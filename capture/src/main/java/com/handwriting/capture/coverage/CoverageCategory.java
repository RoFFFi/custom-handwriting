package com.handwriting.capture.coverage;

import com.handwriting.core.glyph.GlyphKey;
import java.util.List;
import java.util.stream.IntStream;

/**
 * A group of glyphs the coverage report tracks, with the full list of glyphs in it. The list
 * is what lets the report say a glyph is missing: a glyph that never appears in any
 * transcript is still listed here.
 */
public enum CoverageCategory {
    LOWERCASE("Lowercase letters", range('a', 'z')),
    CAPITALS("Capitals", range('A', 'Z')),
    DIGITS("Digits", range('0', '9')),
    PUNCTUATION("Punctuation", keys(". , ; : ! ? ' \" ( ) - / & + = %")),
    /**
     * Common English letter pairs and triples. A writer often joins these into one shape, so
     * each needs its own samples. Version 1 uses a fixed list; later the list can come from
     * the clusters a writer is seen to merge.
     */
    CLUSTERS("Clusters", keys("th he in er an re on at en nd ti es or te of ed is it al ar "
            + "st to nt ng se ha as ou io le ve co me de hi ri ro ic ne ea ra ce li ch ll "
            + "the ing and ion tio ent her for tha ter"));

    private final String title;
    private final List<GlyphKey> keys;

    CoverageCategory(String title, List<GlyphKey> keys) {
        this.title = title;
        this.keys = keys;
    }

    /** Heading used in the printed report. */
    public String title() {
        return title;
    }

    /** Every glyph in this category, in report order. */
    public List<GlyphKey> keys() {
        return keys;
    }

    private static List<GlyphKey> range(char first, char last) {
        return IntStream.rangeClosed(first, last).mapToObj(c -> GlyphKey.of(Character.toString(c))).toList();
    }

    private static List<GlyphKey> keys(String spaceSeparated) {
        return List.of(spaceSeparated.split(" ")).stream().map(GlyphKey::of).toList();
    }
}
