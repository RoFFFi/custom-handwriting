package com.handwriting.capture.transcript;

import java.util.List;

/**
 * A sketch, graph or diagram. {@code labels} holds any words handwritten inside it
 * ({@code [drawing] Flute, Oboe}); they are real handwriting even though they are not
 * on a ruled line. Empty when the drawing has no words.
 */
public record DrawingLine(int sourceLine, List<Span> labels) implements TranscriptLine {

    public DrawingLine {
        labels = List.copyOf(labels);
    }

    @Override
    public List<Span> spans() {
        return labels;
    }
}
