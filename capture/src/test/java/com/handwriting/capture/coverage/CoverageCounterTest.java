package com.handwriting.capture.coverage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.handwriting.capture.transcript.PageTranscript;
import com.handwriting.capture.transcript.TranscriptParser;
import com.handwriting.core.glyph.GlyphKey;
import java.util.List;
import org.junit.jupiter.api.Test;

class CoverageCounterTest {

    private static PageTranscript page(String body) {
        return TranscriptParser.parse("test", "# photo: p.jpg\n# writer: w\n" + body);
    }

    private static int count(Coverage coverage, String key) {
        return coverage.count(GlyphKey.of(key));
    }

    @Test
    void countsEveryCharacterButNotSpaces() {
        Coverage coverage = CoverageCounter.count(List.of(page("Sad, sad day.\n")));

        assertThat(count(coverage, "a")).isEqualTo(3);
        assertThat(count(coverage, "S")).isEqualTo(1);
        assertThat(count(coverage, "s")).isEqualTo(1);
        assertThat(count(coverage, ",")).isEqualTo(1);
        assertThat(count(coverage, ".")).isEqualTo(1);
        assertThat(count(coverage, " ")).isZero();
        assertThat(coverage.pages()).isEqualTo(1);
    }

    @Test
    void countsOverlappingClustersInsideWordsOnly() {
        Coverage coverage = CoverageCounter.count(List.of(page("the thing\nThe an-d\n")));

        assertThat(count(coverage, "th")).isEqualTo(2);
        assertThat(count(coverage, "he")).isEqualTo(2);
        assertThat(count(coverage, "the")).isEqualTo(1);
        assertThat(count(coverage, "ing")).isEqualTo(1);
        assertThat(count(coverage, "ng")).isEqualTo(1);
        assertThat(count(coverage, "nd")).as("split by a hyphen").isZero();
        assertThat(count(coverage, "eth")).as("across a space, and not tracked").isZero();
    }

    @Test
    void skipsCrossedOutGuessedAndUnreadableSpans() {
        Coverage coverage = CoverageCounter.count(List.of(page("zap ~~zip~~ [zoo?] [?] _zed_ ^z^\n")));

        assertThat(count(coverage, "z")).as("zap, zed and the inserted z").isEqualTo(3);
        assertThat(coverage.skippedSpans()).isEqualTo(3);
    }

    @Test
    void countsDrawingLabelsTableCellsAndMarginNotes() {
        Coverage coverage = CoverageCounter.count(List.of(page(
                "[drawing] Oboe\n[table]\nq | Q\n[/table]\n[margin] 9/12\n")));

        assertThat(count(coverage, "O")).isEqualTo(1);
        assertThat(count(coverage, "q")).isEqualTo(1);
        assertThat(count(coverage, "Q")).isEqualTo(1);
        assertThat(count(coverage, "9")).isEqualTo(1);
        assertThat(count(coverage, "/")).isEqualTo(1);
    }

    @Test
    void sortsTrackedGlyphsIntoLevelsAndListsUntrackedCharacters() {
        Coverage coverage = CoverageCounter.count(List.of(page("eeeeeeeeee x →\n")));

        assertThat(coverage.keys(CoverageCategory.LOWERCASE, CoverageLevel.WELL_COVERED))
                .containsExactly(GlyphKey.of("e"));
        assertThat(coverage.keys(CoverageCategory.LOWERCASE, CoverageLevel.THIN))
                .containsExactly(GlyphKey.of("x"));
        assertThat(coverage.keys(CoverageCategory.LOWERCASE, CoverageLevel.MISSING)).hasSize(24);
        assertThat(coverage.untracked()).containsExactly(GlyphKey.of("→"));
    }

    @Test
    void levelsFollowTheThreshold() {
        assertThat(CoverageLevel.of(0)).isEqualTo(CoverageLevel.MISSING);
        assertThat(CoverageLevel.of(CoverageLevel.WELL_COVERED_FROM - 1)).isEqualTo(CoverageLevel.THIN);
        assertThat(CoverageLevel.of(CoverageLevel.WELL_COVERED_FROM)).isEqualTo(CoverageLevel.WELL_COVERED);
        assertThatThrownBy(() -> CoverageLevel.of(-1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void reportListsEachCategoryWithCounts() {
        Coverage coverage = CoverageCounter.count(List.of(page("eeeeeeeeee x\n")));

        String report = CoverageReport.format("w", coverage);

        assertThat(report)
                .contains("Coverage for w: 1 pages, 0 crossed-out, guessed or unreadable spans not counted")
                .contains("Lowercase letters")
                .contains("well covered  (1) e 10")
                .contains("thin          (1) x 1")
                .contains("missing       (26) A, B")
                .doesNotContain("Other characters");
    }
}
