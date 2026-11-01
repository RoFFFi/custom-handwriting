package com.handwriting.core.glyph;

import static org.assertj.core.api.Assertions.assertThat;

import com.handwriting.core.geom.Vec2;
import com.handwriting.core.stroke.Stroke;
import com.handwriting.core.stroke.StrokePoint;
import java.util.List;
import org.junit.jupiter.api.Test;

class GlyphLibraryTest {

    private static GlyphSample sample(String id, String key) {
        Stroke s = new Stroke(List.of(StrokePoint.of(0, 0), StrokePoint.of(1, -1)));
        return new GlyphSample(id, GlyphKey.of(key), List.of(s), new GlyphMetrics(1, Vec2.ZERO, new Vec2(1, 0)), "test");
    }

    @Test
    void groupsSamplesByKeyAndTracksLongestCluster() {
        GlyphLibrary lib = GlyphLibrary.of(List.of(
                sample("a1", "a"), sample("a2", "a"), sample("th1", "th"), sample("ing1", "ing")));

        assertThat(lib.variants(GlyphKey.of("a"))).hasSize(2);
        assertThat(lib.variants(GlyphKey.of("z"))).isEmpty();
        assertThat(lib.longestSpan()).isEqualTo(3);
        assertThat(lib.sampleCount()).isEqualTo(4);
        assertThat(GlyphKey.of("th").isCluster()).isTrue();
    }
}
