package com.handwriting.capture.coverage;

import com.handwriting.core.glyph.GlyphKey;
import java.util.List;
import java.util.stream.Collectors;

/** Turns a {@link Coverage} into the plain text report printed by the {@code coverage} command. */
public final class CoverageReport {

    private CoverageReport() {}

    public static String format(String writer, Coverage coverage) {
        StringBuilder out = new StringBuilder();
        out.append("Coverage for ").append(writer).append(": ")
                .append(coverage.pages()).append(" pages, ")
                .append(coverage.skippedSpans()).append(" crossed-out, guessed or unreadable spans not counted\n")
                .append("Well covered means at least ").append(CoverageLevel.WELL_COVERED_FROM).append(" samples.\n");
        for (CoverageCategory category : CoverageCategory.values()) {
            out.append('\n').append(category.title()).append('\n');
            line(out, "well covered", coverage, coverage.keys(category, CoverageLevel.WELL_COVERED));
            line(out, "thin", coverage, coverage.keys(category, CoverageLevel.THIN));
            line(out, "missing", coverage, coverage.keys(category, CoverageLevel.MISSING));
        }
        List<GlyphKey> untracked = coverage.untracked();
        if (!untracked.isEmpty()) {
            out.append('\n').append("Other characters\n");
            line(out, "seen", coverage, untracked);
        }
        return out.toString();
    }

    private static void line(StringBuilder out, String label, Coverage coverage, List<GlyphKey> keys) {
        String items = keys.isEmpty() ? "none" : keys.stream()
                .map(key -> coverage.count(key) == 0 ? key.text() : key.text() + " " + coverage.count(key))
                .collect(Collectors.joining(", "));
        out.append(String.format("  %-13s (%d) %s%n", label, keys.size(), items));
    }
}
