package com.handwriting.capture.coverage;

/**
 * How well one glyph is covered by a writer's pages, judged by how many plainly written
 * samples there are. Ten samples is enough to see how the letter varies; below that the
 * writer will be asked to write it more (the gap-filling passage).
 */
public enum CoverageLevel {
    MISSING,
    THIN,
    WELL_COVERED;

    /** Fewest samples for a glyph to count as well covered. */
    public static final int WELL_COVERED_FROM = 10;

    public static CoverageLevel of(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("count must not be negative: " + count);
        }
        if (count == 0) {
            return MISSING;
        }
        return count < WELL_COVERED_FROM ? THIN : WELL_COVERED;
    }
}
