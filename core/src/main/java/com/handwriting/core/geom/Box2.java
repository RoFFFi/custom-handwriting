package com.handwriting.core.geom;

/** Axis aligned rectangle, inclusive of its edges. */
public record Box2(double minX, double minY, double maxX, double maxY) {

    public Box2 {
        if (maxX < minX || maxY < minY) {
            throw new IllegalArgumentException("max must be >= min: " + minX + "," + minY + " " + maxX + "," + maxY);
        }
    }

    public double width() {
        return maxX - minX;
    }

    public double height() {
        return maxY - minY;
    }

    public Box2 union(Box2 o) {
        return new Box2(Math.min(minX, o.minX), Math.min(minY, o.minY), Math.max(maxX, o.maxX), Math.max(maxY, o.maxY));
    }
}
