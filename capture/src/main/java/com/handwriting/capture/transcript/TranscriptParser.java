package com.handwriting.capture.transcript;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads the transcript text format of docs/DATA_FORMATS.md into a {@link PageTranscript}.
 *
 * <p>It works in two passes over each line. The line pass decides what kind of line it is
 * (header, blank, table, drawing, margin note, text). The inline pass walks the characters
 * of a line, switching marks on and off as it meets markup, and cuts a new {@link Span}
 * every time the set of active marks changes.
 *
 * <p>The parser is strict: anything it does not understand is an error with a line number,
 * because a silently misread transcript would teach the tool the wrong letters.
 */
public final class TranscriptParser {

    private static final Set<String> KNOWN_KEYS = Set.of("photo", "writer", "pen", "document", "notes");
    private static final Pattern HEADER = Pattern.compile("#\\s*([A-Za-z]+)\\s*:\\s*(.*?)\\s*");

    private TranscriptParser() {}

    /**
     * @param source file name or other label, used only in error messages
     * @param text   the whole transcript
     * @throws TranscriptFormatException when the text does not follow the format
     */
    public static PageTranscript parse(String source, String text) {
        if (text.startsWith("﻿")) {
            text = text.substring(1);
        }
        String[] raw = text.replace("\r\n", "\n").replace('\r', '\n').split("\n", -1);

        // Header: '# key: value' lines at the top. The first other non-blank line ends it,
        // so a '#' further down is ordinary handwriting.
        Map<String, String> fields = new LinkedHashMap<>();
        int i = 0;
        for (; i < raw.length; i++) {
            String line = raw[i].strip();
            if (line.isEmpty()) {
                continue;
            }
            if (!line.startsWith("#")) {
                break;
            }
            Matcher m = HEADER.matcher(line);
            if (!m.matches()) {
                throw new TranscriptFormatException(source, i + 1,
                        "header lines look like '# key: value' (write \\# for a '#' that starts the handwriting)");
            }
            String key = m.group(1).toLowerCase();
            if (!KNOWN_KEYS.contains(key)) {
                throw new TranscriptFormatException(source, i + 1,
                        "unknown header key '" + key + "', expected one of photo, writer, pen, document, notes");
            }
            if (m.group(2).isEmpty()) {
                throw new TranscriptFormatException(source, i + 1, "header '" + key + "' has no value");
            }
            if (fields.putIfAbsent(key, m.group(2)) != null) {
                throw new TranscriptFormatException(source, i + 1, "header '" + key + "' appears twice");
            }
        }
        for (String required : List.of("photo", "writer")) {
            if (!fields.containsKey(required)) {
                throw new TranscriptFormatException(source, 0, "missing required header '# " + required + ": ...'");
            }
        }
        TranscriptHeader header = new TranscriptHeader(
                fields.get("photo"),
                fields.get("writer"),
                Optional.ofNullable(fields.get("pen")),
                Optional.ofNullable(fields.get("document")),
                Optional.ofNullable(fields.get("notes")));

        // Body.
        List<TranscriptLine> lines = new ArrayList<>();
        int tableStart = 0; // line number of the open [table], or 0 when not in a table
        int blankStart = 0; // first line of a run of blank lines not yet recorded, or 0
        for (; i < raw.length; i++) {
            int n = i + 1;
            String line = raw[i].strip();

            if (tableStart > 0) {
                if (line.equals("[/table]")) {
                    tableStart = 0;
                } else if (line.isEmpty()) {
                    throw new TranscriptFormatException(source, n, "blank line inside [table]; close the table with [/table] first");
                } else {
                    List<List<Span>> cells = new ArrayList<>();
                    for (String cell : splitCells(line)) {
                        cells.add(parseInline(source, n, cell.strip()));
                    }
                    lines.add(new TableRow(n, cells));
                }
                continue;
            }

            if (line.isEmpty()) {
                // Leading blank lines are not a gap; runs of blank lines count once.
                if (!lines.isEmpty() && blankStart == 0) {
                    blankStart = n;
                }
                continue;
            }
            if (blankStart > 0) {
                lines.add(new BlankLine(blankStart));
                blankStart = 0;
            }

            if (line.equals("[table]")) {
                tableStart = n;
            } else if (line.equals("[/table]")) {
                throw new TranscriptFormatException(source, n, "[/table] without an open [table]");
            } else if (line.startsWith("[drawing]")) {
                lines.add(new DrawingLine(n, parseInline(source, n, line.substring("[drawing]".length()).strip())));
            } else if (line.startsWith("[margin]")) {
                String note = line.substring("[margin]".length()).strip();
                if (note.isEmpty()) {
                    throw new TranscriptFormatException(source, n, "[margin] needs the text of the note after it");
                }
                lines.add(new MarginNote(n, parseInline(source, n, note)));
            } else {
                lines.add(new TextLine(n, parseInline(source, n, line)));
            }
        }
        if (tableStart > 0) {
            throw new TranscriptFormatException(source, tableStart, "[table] is never closed with [/table]");
        }
        return new PageTranscript(header, lines);
    }

    /** Splits a table row at each '|' that is not escaped with a backslash. Escapes are kept. */
    private static List<String> splitCells(String row) {
        List<String> cells = new ArrayList<>();
        StringBuilder cell = new StringBuilder();
        for (int p = 0; p < row.length(); p++) {
            char c = row.charAt(p);
            if (c == '\\' && p + 1 < row.length()) {
                cell.append(c).append(row.charAt(++p));
            } else if (c == '|') {
                cells.add(cell.toString());
                cell.setLength(0);
            } else {
                cell.append(c);
            }
        }
        cells.add(cell.toString());
        return cells;
    }

    /** The inline pass: turns one line of text with markup into spans. */
    static List<Span> parseInline(String source, int n, String s) {
        List<Span> spans = new ArrayList<>();
        EnumSet<Mark> active = EnumSet.noneOf(Mark.class);
        StringBuilder text = new StringBuilder();
        int p = 0;
        while (p < s.length()) {
            char c = s.charAt(p);
            if (c == '\\') {
                if (p + 1 >= s.length()) {
                    throw new TranscriptFormatException(source, n, "a line cannot end with a lone backslash");
                }
                text.append(s.charAt(p + 1));
                p += 2;
            } else if (s.startsWith("~~", p)) {
                flush(spans, text, active);
                toggle(active, Mark.CROSSED_OUT);
                p += 2;
            } else if (c == '_') {
                flush(spans, text, active);
                toggle(active, Mark.UNDERLINED);
                p++;
            } else if (c == '^') {
                flush(spans, text, active);
                toggle(active, Mark.INSERTED);
                p++;
            } else if (s.startsWith("[box]", p)) {
                if (active.contains(Mark.BOXED)) {
                    throw new TranscriptFormatException(source, n, "[box] inside another [box]");
                }
                flush(spans, text, active);
                active.add(Mark.BOXED);
                p += "[box]".length();
            } else if (s.startsWith("[/box]", p)) {
                if (!active.contains(Mark.BOXED)) {
                    throw new TranscriptFormatException(source, n, "[/box] without an open [box]");
                }
                flush(spans, text, active);
                active.remove(Mark.BOXED);
                p += "[/box]".length();
            } else if (s.startsWith("[?]", p)) {
                flush(spans, text, active);
                spans.add(new Span("", with(active, Mark.UNREADABLE)));
                p += "[?]".length();
            } else if (c == '[') {
                int end = s.indexOf(']', p + 1);
                String inside = end < 0 ? "" : s.substring(p + 1, end);
                if (!inside.endsWith("?") || inside.contains("[") || inside.length() < 2) {
                    throw new TranscriptFormatException(source, n,
                            "unexpected '[' at column " + (p + 1) + "; markup is [box], [/box], [?] or [word?], "
                                    + "and a bracket in the handwriting is written \\[");
                }
                flush(spans, text, active);
                spans.add(new Span(inside.substring(0, inside.length() - 1), with(active, Mark.GUESS)));
                p = end + 1;
            } else {
                text.append(c);
                p++;
            }
        }
        flush(spans, text, active);
        if (!active.isEmpty()) {
            throw new TranscriptFormatException(source, n,
                    "unclosed " + active.iterator().next().markup() + " (marks must open and close on the same line)");
        }
        return spans;
    }

    private static void flush(List<Span> spans, StringBuilder text, Set<Mark> active) {
        if (!text.isEmpty()) {
            spans.add(new Span(text.toString(), active));
            text.setLength(0);
        }
    }

    private static void toggle(Set<Mark> active, Mark mark) {
        if (!active.remove(mark)) {
            active.add(mark);
        }
    }

    private static Set<Mark> with(Set<Mark> active, Mark extra) {
        EnumSet<Mark> marks = EnumSet.of(extra);
        marks.addAll(active);
        return marks;
    }
}
