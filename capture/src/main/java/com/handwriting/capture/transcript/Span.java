package com.handwriting.capture.transcript;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * A run of handwritten text that carries the same marks, for example {@code "tentatively"}
 * crossed out. A plain line is usually a single span with no marks.
 *
 * <p>An unreadable word is a span with empty text and the {@link Mark#UNREADABLE} mark, so
 * it keeps its place in the line without pretending to know its letters.
 */
public record Span(String text, Set<Mark> marks) {

    public Span {
        Objects.requireNonNull(text, "text");
        marks = marks.isEmpty() ? Set.of() : Set.copyOf(EnumSet.copyOf(marks));
        if (marks.contains(Mark.UNREADABLE) != text.isEmpty()) {
            throw new IllegalArgumentException(
                    "a span has empty text exactly when it is unreadable, got \"" + text + "\" " + marks);
        }
    }

    public static Span plain(String text) {
        return new Span(text, Set.of());
    }

    public static Span marked(String text, Mark... marks) {
        return new Span(text, marks.length == 0 ? Set.of() : EnumSet.of(marks[0], marks));
    }

    public boolean has(Mark mark) {
        return marks.contains(mark);
    }

    /** True when the text is written plainly: not crossed out, not guessed, not unreadable. */
    public boolean isClean() {
        return !has(Mark.CROSSED_OUT) && !has(Mark.GUESS) && !has(Mark.UNREADABLE);
    }
}
