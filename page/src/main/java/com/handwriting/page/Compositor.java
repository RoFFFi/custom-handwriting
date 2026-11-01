package com.handwriting.page;

import com.handwriting.render.InkLayer;
import java.awt.image.BufferedImage;

/**
 * Warps the flat ink layer into the photo and blends it in. Week 6 v1: warp with the page
 * mapping and multiply-blend. Week 7: match the photo's blur, noise, colour cast and JPEG
 * softness so the ink is not sharper than the paper around it.
 */
public interface Compositor {

    BufferedImage composite(BufferedImage photo, PageAnalysis analysis, InkLayer ink);
}
