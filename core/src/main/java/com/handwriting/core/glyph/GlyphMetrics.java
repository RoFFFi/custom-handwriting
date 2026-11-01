package com.handwriting.core.glyph;

import com.handwriting.core.geom.Vec2;

/**
 * How a glyph sample sits on the baseline and connects to neighbours, in glyph space.
 *
 * @param advance how far the pen moves right before the next glyph starts
 * @param entry   where a connecting stroke from the previous letter would arrive
 * @param exit    where this glyph's ending stroke leaves, for joining to the next letter
 */
public record GlyphMetrics(double advance, Vec2 entry, Vec2 exit) {
}
