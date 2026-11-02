package com.handwriting.capture.transcript;

import java.util.List;

/** One row of a handwritten table, cells left to right. A cell may be empty. */
public record TableRow(int sourceLine, List<List<Span>> cells) implements TranscriptLine {

    public TableRow {
        cells = cells.stream().map(List::copyOf).toList();
    }

    @Override
    public List<Span> spans() {
        return cells.stream().flatMap(List::stream).toList();
    }
}
