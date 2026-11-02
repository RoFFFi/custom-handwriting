package com.handwriting.capture.transcript;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class TranscriptParserTest {

    private static final String HEADER = "# photo: p.jpg\n# writer: w\n";

    private static PageTranscript parse(String body) {
        return TranscriptParser.parse("test.txt", HEADER + body);
    }

    private static List<Span> onlyLine(String body) {
        PageTranscript t = parse(body);
        assertThat(t.lines()).hasSize(1);
        return t.lines().get(0).spans();
    }

    @Test
    void readsHeader() {
        PageTranscript t = TranscriptParser.parse("t", """
                # photo: IMG_1.jpg
                # writer: someone
                # pen: black-ballpoint
                # Document: Journal 1, page 1
                Hello
                """);
        assertThat(t.header()).isEqualTo(new TranscriptHeader(
                "IMG_1.jpg", "someone", Optional.of("black-ballpoint"), Optional.of("Journal 1, page 1"), Optional.empty()));
        assertThat(t.lines()).containsExactly(new TextLine(5, List.of(Span.plain("Hello"))));
    }

    @Test
    void parsesTheExampleFromTheDocs() {
        PageTranscript t = parse("""
                Listening environment: I am alone in my room,
                [box]FIRST LISTEN[/box]

                The words that Strozzi repeats are primarily ~~tentatively~~
                """);
        assertThat(t.lines()).containsExactly(
                new TextLine(3, List.of(Span.plain("Listening environment: I am alone in my room,"))),
                new TextLine(4, List.of(Span.marked("FIRST LISTEN", Mark.BOXED))),
                new BlankLine(5),
                new TextLine(6, List.of(
                        Span.plain("The words that Strozzi repeats are primarily "),
                        Span.marked("tentatively", Mark.CROSSED_OUT))));
    }

    @Test
    void eachMarkupBecomesAMark() {
        assertThat(onlyLine("a _b_ c")).containsExactly(
                Span.plain("a "), Span.marked("b", Mark.UNDERLINED), Span.plain(" c"));
        assertThat(onlyLine("a ^b^")).containsExactly(Span.plain("a "), Span.marked("b", Mark.INSERTED));
        assertThat(onlyLine("[?] x")).containsExactly(Span.marked("", Mark.UNREADABLE), Span.plain(" x"));
        assertThat(onlyLine("I [wondred?] why")).containsExactly(
                Span.plain("I "), Span.marked("wondred", Mark.GUESS), Span.plain(" why"));
    }

    @Test
    void marksCombineAndSpanSeveralWords() {
        assertThat(onlyLine("_~~two words~~_")).containsExactly(
                Span.marked("two words", Mark.UNDERLINED, Mark.CROSSED_OUT));
        assertThat(onlyLine("[box]a [maybe?] b[/box]")).containsExactly(
                Span.marked("a ", Mark.BOXED),
                Span.marked("maybe", Mark.BOXED, Mark.GUESS),
                Span.marked(" b", Mark.BOXED));
    }

    @Test
    void backslashEscapesMarkupCharacters() {
        assertThat(onlyLine("\\[at least\\] snake\\_case \\~\\~ 2\\^3"))
                .containsExactly(Span.plain("[at least] snake_case ~~ 2^3"));
    }

    @Test
    void hashAfterTheHeaderIsHandwriting() {
        PageTranscript t = parse("Oboe response # 2\n# not a header\n");
        assertThat(t.lines()).extracting(line -> line.spans().get(0).text())
                .containsExactly("Oboe response # 2", "# not a header");
        assertThat(TranscriptParser.parse("t", HEADER + "\\# 1 first").lines().get(0).spans())
                .containsExactly(Span.plain("# 1 first"));
    }

    @Test
    void blankLinesCollapseAndAreDroppedAtTheEdges() {
        PageTranscript t = parse("\n\na\n\n\n\nb\n\n\n");
        assertThat(t.lines()).containsExactly(
                new TextLine(5, List.of(Span.plain("a"))),
                new BlankLine(6),
                new TextLine(9, List.of(Span.plain("b"))));
    }

    @Test
    void readsDrawingsTablesAndMarginNotes() {
        PageTranscript t = parse("""
                [drawing]
                [drawing] Flute, _Oboe_
                [table]
                Time | Sound | Idea
                2:35 \\| 2:49 | | ~~loud~~
                [/table]
                [margin] see notes
                """);
        assertThat(t.lines()).containsExactly(
                new DrawingLine(3, List.of()),
                new DrawingLine(4, List.of(Span.plain("Flute, "), Span.marked("Oboe", Mark.UNDERLINED))),
                new TableRow(6, List.of(
                        List.of(Span.plain("Time")), List.of(Span.plain("Sound")), List.of(Span.plain("Idea")))),
                new TableRow(7, List.of(
                        List.of(Span.plain("2:35 | 2:49")), List.of(), List.of(Span.marked("loud", Mark.CROSSED_OUT)))),
                new MarginNote(9, List.of(Span.plain("see notes"))));
        assertThat(t.spans()).extracting(Span::text)
                .containsExactly("Flute, ", "Oboe", "Time", "Sound", "Idea", "2:35 | 2:49", "loud", "see notes");
    }

    @Test
    void cleanSpansExcludeCrossedOutGuessedAndUnreadable() {
        assertThat(Span.plain("a").isClean()).isTrue();
        assertThat(Span.marked("a", Mark.UNDERLINED, Mark.BOXED, Mark.INSERTED).isClean()).isTrue();
        assertThat(Span.marked("a", Mark.CROSSED_OUT).isClean()).isFalse();
        assertThat(Span.marked("a", Mark.GUESS).isClean()).isFalse();
        assertThat(Span.marked("", Mark.UNREADABLE).isClean()).isFalse();
    }

    @Test
    void rejectsBadHeaders() {
        assertError("# writer: w\nhi", "missing required header '# photo: ...'");
        assertError("# photo: p.jpg\nhi", "missing required header '# writer: ...'");
        assertError("# photo: p.jpg\n# writter: w\nhi", "line 2: unknown header key 'writter'");
        assertError("# photo: p.jpg\n# photo: q.jpg\n# writer: w", "line 2: header 'photo' appears twice");
        assertError("# photo:\n# writer: w", "line 1: header 'photo' has no value");
        assertError("# photo p.jpg\n# writer: w", "line 1: header lines look like '# key: value'");
    }

    @Test
    void rejectsBadMarkupWithTheLineNumber() {
        assertError(HEADER + "ok\n~~open", "test.txt line 4: unclosed ~~");
        assertError(HEADER + "_a", "line 3: unclosed _");
        assertError(HEADER + "[box]a", "line 3: unclosed [box]");
        assertError(HEADER + "a[/box]", "line 3: [/box] without an open [box]");
        assertError(HEADER + "[box][box]a[/box][/box]", "line 3: [box] inside another [box]");
        assertError(HEADER + "[at least, a biker]", "line 3: unexpected '[' at column 1");
        assertError(HEADER + "a \\", "line 3: a line cannot end with a lone backslash");
        assertError(HEADER + "[table]\na | b", "line 3: [table] is never closed");
        assertError(HEADER + "[table]\na\n\nb\n[/table]", "line 5: blank line inside [table]");
        assertError(HEADER + "[/table]", "line 3: [/table] without an open [table]");
        assertError(HEADER + "[margin]", "line 3: [margin] needs the text");
    }

    private static void assertError(String text, String message) {
        assertThatThrownBy(() -> TranscriptParser.parse("test.txt", text))
                .isInstanceOf(TranscriptFormatException.class)
                .hasMessageContaining(message);
    }
}
