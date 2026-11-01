package com.handwriting.core.ink;

import java.util.List;

/**
 * Everything written on one sheet, in page space, before any pixels exist. Strokes are in
 * the order they were written, so the renderer can replay them (ink running low, smudging
 * earlier wet ink, or an animation of the page being written).
 * This is the hand-off point between {@code synth} (decides what to write where) and
 * {@code render} (decides what the ink looks like). Keeping it pixel free means the same
 * document can be rendered at any resolution or onto any page photo.
 */
public record InkDocument(double pageWidthMm, double pageHeightMm, List<PlacedStroke> strokes) {

    public InkDocument {
        strokes = List.copyOf(strokes);
    }
}
