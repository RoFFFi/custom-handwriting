package com.handwriting.core.glyph;

/**
 * What a glyph sample represents: a single character ("a") or a cluster of characters the
 * writer tends to merge together ("th", "ing", "ll"). Clusters are what make the output
 * look written instead of typeset.
 */
public record GlyphKey(String text) {

    public GlyphKey {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("glyph key text must not be empty");
        }
    }

    public static GlyphKey of(String text) {
        return new GlyphKey(text);
    }

    public boolean isCluster() {
        return text.codePointCount(0, text.length()) > 1;
    }

    /** Number of characters (code points) this key covers. */
    public int span() {
        return text.codePointCount(0, text.length());
    }
}
