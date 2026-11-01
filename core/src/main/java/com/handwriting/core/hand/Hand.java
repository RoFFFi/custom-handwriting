package com.handwriting.core.hand;

import com.handwriting.core.glyph.GlyphLibrary;
import com.handwriting.core.pen.PenProfile;

/**
 * One writer using one pen. The same person writes differently with a pencil than with a
 * gel pen, so samples and style are stored per (writer, pen) pair. See docs/DECISIONS.md, D3.
 *
 * @param id       e.g. "raphael/bic-blue"
 * @param writerId the person
 * @param pen      the pen used for these samples
 * @param style    measured habits for this pen
 * @param glyphs   captured samples for this pen
 */
public record Hand(String id, String writerId, PenProfile pen, StyleParams style, GlyphLibrary glyphs) {
}
