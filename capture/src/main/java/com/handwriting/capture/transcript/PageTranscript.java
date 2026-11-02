package com.handwriting.capture.transcript;

import java.util.List;

/**
 * Exactly what is handwritten on one photo, line by line, as written (misspellings and all).
 * This is how the tool knows which marks on the page are which letters. The text format is
 * described in docs/DATA_FORMATS.md; {@link TranscriptParser} reads it.
 */
public record PageTranscript(TranscriptHeader header, List<TranscriptLine> lines) {

    public PageTranscript {
        lines = List.copyOf(lines);
    }

    /** Every handwritten span on the page, in reading order. */
    public List<Span> spans() {
        return lines.stream().flatMap(line -> line.spans().stream()).toList();
    }
}
