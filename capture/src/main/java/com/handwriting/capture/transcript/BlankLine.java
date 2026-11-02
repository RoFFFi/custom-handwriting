package com.handwriting.capture.transcript;

import java.util.List;

/** A gap of one or more empty ruled lines, usually a paragraph break. */
public record BlankLine(int sourceLine) implements TranscriptLine {

    @Override
    public List<Span> spans() {
        return List.of();
    }
}
