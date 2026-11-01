package com.handwriting.core.glyph;

import com.handwriting.core.geom.Box2;
import com.handwriting.core.stroke.Stroke;
import java.util.List;

/**
 * One real instance of the user writing a character or cluster, in glyph space.
 * The library keeps several samples per key; synthesis picks one and then varies it.
 *
 * @param id        stable identifier, e.g. "ballpoint-blue/a/003"
 * @param key       what was written
 * @param strokes   the strokes in drawing order
 * @param metrics   baseline and joining information
 * @param sourceRef where it came from (photo file + cell), for debugging bad samples
 */
public record GlyphSample(String id, GlyphKey key, List<Stroke> strokes, GlyphMetrics metrics, String sourceRef) {

    public GlyphSample {
        strokes = List.copyOf(strokes);
        if (strokes.isEmpty()) {
            throw new IllegalArgumentException("glyph sample " + id + " has no strokes");
        }
    }

    public Box2 bounds() {
        Box2 box = strokes.get(0).bounds();
        for (int i = 1; i < strokes.size(); i++) {
            box = box.union(strokes.get(i).bounds());
        }
        return box;
    }
}
