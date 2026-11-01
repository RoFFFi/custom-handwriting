package com.handwriting.core.ink;

import java.util.List;

/**
 * Everything that will be drawn on one sheet, in page space, before any pixels exist.
 * This is the hand-off point between {@code synth} (decides what to write where) and
 * {@code render} (decides what the ink looks like). Keeping it pixel free means the same
 * document can be rendered at any resolution or onto any page photo.
 */
public record InkDocument(double pageWidthMm, double pageHeightMm, List<PlacedStroke> strokes) {

    public InkDocument {
        strokes = List.copyOf(strokes);
    }
}
