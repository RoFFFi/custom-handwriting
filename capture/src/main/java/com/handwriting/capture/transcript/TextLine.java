package com.handwriting.capture.transcript;

import java.util.List;

/** An ordinary line of handwriting in the main text. */
public record TextLine(int sourceLine, List<Span> spans) implements TranscriptLine {

    public TextLine {
        spans = List.copyOf(spans);
    }
}
