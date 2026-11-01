package com.handwriting.core.stroke;

import com.handwriting.core.geom.Box2;
import java.util.List;

/**
 * One continuous pen-down movement, in drawing order. A letter is usually 1 to 3 strokes
 * (an "i" is the body stroke plus the dot stroke).
 *
 * <p>Strokes are vectors, not pixels: this is what makes per-instance variation possible.
 * See docs/DECISIONS.md, D2.
 */
public record Stroke(List<StrokePoint> points) {

    public Stroke {
        points = List.copyOf(points);
        if (points.isEmpty()) {
            throw new IllegalArgumentException("a stroke needs at least one point");
        }
    }

    public Box2 bounds() {
        double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
        for (StrokePoint p : points) {
            minX = Math.min(minX, p.x());
            minY = Math.min(minY, p.y());
            maxX = Math.max(maxX, p.x());
            maxY = Math.max(maxY, p.y());
        }
        return new Box2(minX, minY, maxX, maxY);
    }

    /** Polyline length along the points. */
    public double length() {
        double total = 0;
        for (int i = 1; i < points.size(); i++) {
            StrokePoint a = points.get(i - 1), b = points.get(i);
            total += Math.hypot(b.x() - a.x(), b.y() - a.y());
        }
        return total;
    }
}
