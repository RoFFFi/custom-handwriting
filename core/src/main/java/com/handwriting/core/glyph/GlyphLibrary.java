package com.handwriting.core.glyph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** All captured samples for one hand, grouped by key. Immutable. */
public final class GlyphLibrary {

    private final Map<GlyphKey, List<GlyphSample>> samplesByKey;
    private final int longestSpan;

    private GlyphLibrary(Map<GlyphKey, List<GlyphSample>> samplesByKey) {
        this.samplesByKey = samplesByKey;
        this.longestSpan = samplesByKey.keySet().stream().mapToInt(GlyphKey::span).max().orElse(0);
    }

    public static GlyphLibrary of(Collection<GlyphSample> samples) {
        Map<GlyphKey, List<GlyphSample>> grouped = new LinkedHashMap<>();
        for (GlyphSample s : samples) {
            grouped.computeIfAbsent(s.key(), k -> new ArrayList<>()).add(s);
        }
        grouped.replaceAll((k, v) -> List.copyOf(v));
        return new GlyphLibrary(Map.copyOf(grouped));
    }

    /** All samples for a key, or an empty list when the user never wrote it. */
    public List<GlyphSample> variants(GlyphKey key) {
        return samplesByKey.getOrDefault(key, List.of());
    }

    public boolean contains(GlyphKey key) {
        return samplesByKey.containsKey(key);
    }

    public Set<GlyphKey> keys() {
        return samplesByKey.keySet();
    }

    /** Longest cluster length in characters; tokenizers use it to bound their search. */
    public int longestSpan() {
        return longestSpan;
    }

    public int sampleCount() {
        return samplesByKey.values().stream().mapToInt(List::size).sum();
    }
}
