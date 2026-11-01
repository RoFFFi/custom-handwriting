package com.handwriting.synth;

import com.handwriting.core.glyph.GlyphSample;
import com.handwriting.core.hand.StyleParams;
import com.handwriting.core.random.Rng;

/**
 * Makes a fresh, slightly different instance of a sample so no two letters on the page are
 * identical. Must stay inside the writer's real range of variation.
 *
 * <p>Week 4 (v1): per-glyph affine jitter: scale, slant, small rotation, from
 * {@link StyleParams}. Week 7 (v2): smooth elastic deformation of the stroke points plus
 * per-point tremor. Later: learn the variation range from the spread between real samples.
 */
public interface VariationModel {

    GlyphSample vary(GlyphSample sample, StyleParams style, Rng rng);
}
