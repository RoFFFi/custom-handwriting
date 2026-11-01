package com.handwriting.synth.layout;

import com.handwriting.core.glyph.GlyphKey;
import java.util.List;

/**
 * Layout is a tree of boxes (the same idea TeX uses). Plain text is just a vertical list of
 * lines, each a horizontal list of glyphs and gaps. Building on a tree from day one is what
 * lets math slot in later: a fraction is a box holding two boxes stacked around a rule, an
 * exponent is a small raised box. See docs/DECISIONS.md, D5.
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
