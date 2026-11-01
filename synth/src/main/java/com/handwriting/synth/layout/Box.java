package com.handwriting.synth.layout;

import com.handwriting.core.glyph.GlyphKey;
import java.util.List;

/**
 * The shape of one unit the writer is about to put down: a word now, a math expression
 * later. It is a tree of boxes (the same idea TeX uses): a word is a row of glyphs; a fraction
 * is a column of numerator, rule and denominator; an exponent is a small raised box. The
 * writer places each unit on the page one at a time (D6); the tree only describes the unit's
 * inner shape. See docs/DECISIONS.md, D5.
 */
public sealed interface Box permits Box.Glyph, Box.Gap, Box.Row, Box.Column {

    /** One glyph key to be drawn; the concrete sample is chosen later. */
    record Glyph(GlyphKey key, double scale) implements Box {
    }

    /** Horizontal or vertical space, in x-heights. */
    record Gap(double sizeXh) implements Box {
    }

    /** Children placed left to right on a shared baseline (a word, a line). */
    record Row(List<Box> children) implements Box {
        public Row {
            children = List.copyOf(children);
        }
    }

    /** Children stacked top to bottom (lines of a paragraph; later, numerator over denominator). */
    record Column(List<Box> children) implements Box {
        public Column {
            children = List.copyOf(children);
        }
    }
}
