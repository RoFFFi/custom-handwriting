package com.handwriting.render;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import javax.imageio.ImageIO;

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

    /** Saves the layer as a PNG with its transparency, for looking at while debugging. */
    public void savePng(Path file) throws IOException {
        if (!ImageIO.write(image, "png", file.toFile())) {
            throw new IOException("no PNG writer available");
        }
    }
}
