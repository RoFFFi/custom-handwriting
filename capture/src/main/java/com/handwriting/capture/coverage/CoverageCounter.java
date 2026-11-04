package com.handwriting.capture.coverage;

import com.handwriting.capture.transcript.PageTranscript;
import com.handwriting.capture.transcript.Span;
import com.handwriting.core.glyph.GlyphKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Counts the glyphs in a writer's transcripts. Only plainly written text counts: a crossed-out
 * word is distorted by the line through it, and a guessed or unreadable word may not contain
 * the letters it seems to, so those spans are skipped (and reported as skipped).
 *
 * <p>Every character counts as a single glyph, spaces aside. Clusters are counted inside a
 * word only, matching case exactly, because a capital T joins differently from a small t.
 * Overlapping clusters each count: "the" adds one "th", one "he" and one "the".
 */
public final class CoverageCounter {

    private static final Set<String> CLUSTERS = CoverageCategory.CLUSTERS.keys().stream()
            .map(GlyphKey::text)
            .collect(Collectors.toUnmodifiableSet());

    private CoverageCounter() {}

    public static Coverage count(List<PageTranscript> transcripts) {
        Map<GlyphKey, Integer> counts = new HashMap<>();
        int skipped = 0;
        for (PageTranscript transcript : transcripts) {
            for (Span span : transcript.spans()) {
                if (span.isClean()) {
                    countText(span.text(), counts);
                } else {
                    skipped++;
                }
            }
        }
        return new Coverage(counts, transcripts.size(), skipped);
    }

    private static void countText(String text, Map<GlyphKey, Integer> counts) {
        text.codePoints()
                .filter(c -> !Character.isWhitespace(c))
                .forEach(c -> counts.merge(GlyphKey.of(Character.toString(c)), 1, Integer::sum));
        for (String word : text.split("[^\\p{L}]+")) {
            for (int start = 0; start < word.length(); start++) {
                for (int end = start + 2; end <= Math.min(word.length(), start + 3); end++) {
                    String piece = word.substring(start, end);
                    if (CLUSTERS.contains(piece)) {
                        counts.merge(GlyphKey.of(piece), 1, Integer::sum);
                    }
                }
            }
        }
    }
}
