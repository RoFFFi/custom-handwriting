package com.handwriting.core.stroke;

/**
 * One sample along a pen stroke.
 *
 * @param x        position in the stroke's coordinate space
 * @param y        position in the stroke's coordinate space (+y down)
 * @param pressure 0..1, how hard the pen pressed. Use 1.0 when unknown (e.g. traced from a photo).
 * @param width    local ink width relative to the pen's nominal width (1.0 = nominal).
 *                 Lets a traced sample keep the thick/thin shape of the real ink.
 */
public record StrokePoint(double x, double y, double pressure, double width) {

    public StrokePoint {
        if (pressure < 0 || pressure > 1) {
            throw new IllegalArgumentException("pressure must be in [0,1]: " + pressure);
        }
        if (width < 0) {
            throw new IllegalArgumentException("width must be >= 0: " + width);
        }
    }

    public static StrokePoint of(double x, double y) {
        return new StrokePoint(x, y, 1.0, 1.0);
    }
}
