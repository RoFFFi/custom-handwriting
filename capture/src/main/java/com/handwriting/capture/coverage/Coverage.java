package com.handwriting.capture.coverage;

import com.handwriting.core.glyph.GlyphKey;
import java.util.List;
import java.util.Map;

/**
 * How many plainly written samples of each glyph a writer's transcripts contain.
 *
 * @param counts        samples per glyph, for every glyph seen (tracked or not)
 * @param pages         number of transcripts counted
 * @param skippedSpans  crossed-out, guessed and unreadable spans, which were not counted
 */
public record Coverage(Map<GlyphKey, Integer> counts, int pages, int skippedSpans) {

    public Coverage {
        counts = Map.copyOf(counts);
    }

    public int count(GlyphKey key) {
        return counts.getOrDefault(key, 0);
    }

    public CoverageLevel level(GlyphKey key) {
        return CoverageLevel.of(count(key));
    }

    /** The glyphs of a category at one level, in the category's order. */
    public List<GlyphKey> keys(CoverageCategory category, CoverageLevel level) {
        return category.keys().stream().filter(key -> level(key) == level).toList();
    }

    /**
     * Single characters that were written but belong to no category, such as an accented
     * letter or a hand-drawn arrow, sorted by character.
     */
    public List<GlyphKey> untracked() {
        List<GlyphKey> tracked = List.of(CoverageCategory.values()).stream()
                .flatMap(category -> category.keys().stream())
                .toList();
        return counts.keySet().stream()
                .filter(key -> !key.isCluster() && !tracked.contains(key))
                .sorted((a, b) -> a.text().compareTo(b.text()))
                .toList();
    }
}
