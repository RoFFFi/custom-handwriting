package com.handwriting.capture.transcript;

import java.util.List;

/** A note written in the margin, placed where it sits vertically on the page. */
public record MarginNote(int sourceLine, List<Span> spans) implements TranscriptLine {

    public MarginNote {
        spans = List.copyOf(spans);
    }
}
