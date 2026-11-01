package com.handwriting.core.hand;

/**
 * Writer-level habits measured from samples (or tuned by hand at first). Synthesis draws
 * per-letter and per-line values around these means. All lengths are millimetres on paper
 * unless the name says otherwise.
 *
 * <p>Expect this record to grow. Add fields with sensible defaults in {@link #defaults()}.
 *
 * @param xHeightMm          height of a lowercase "x"
 * @param slantDeg           average slant, positive leans right
 * @param slantJitterDeg     standard deviation of slant between letters
 * @param sizeJitter         standard deviation of letter scale, as a fraction (0.05 = 5%)
 * @param letterGapXh        extra gap between glyphs, in x-heights
 * @param wordGapXh          gap between words, in x-heights
 * @param baselineWanderMm   how far the baseline drifts up and down along a line
 * @param shapeNoise         strength of smooth shape deformation, in x-heights
 */
public record StyleParams(
        double xHeightMm,
        double slantDeg,
        double slantJitterDeg,
        double sizeJitter,
        double letterGapXh,
        double wordGapXh,
        double baselineWanderMm,
        double shapeNoise) {

    public static StyleParams defaults() {
        return new StyleParams(2.6, 8.0, 2.0, 0.05, 0.08, 0.9, 0.4, 0.03);
    }
}
