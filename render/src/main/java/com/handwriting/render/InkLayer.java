package com.handwriting.render;

import java.awt.image.BufferedImage;

/**
 * Rendered ink for a whole flat page. ARGB: colour is the ink, alpha is how much ink is
 * there. Pixel (0,0) is the page's top-left corner.
 */
public record InkLayer(BufferedImage image, double pixelsPerMm) {

    public InkLayer {
        if (image.getType() != BufferedImage.TYPE_INT_ARGB) {
            throw new IllegalArgumentException("ink layer must be TYPE_INT_ARGB");
        }
    }
}
