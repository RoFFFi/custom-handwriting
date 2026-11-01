package com.handwriting.page;

import java.awt.image.BufferedImage;

/**
 * Everything learned about a page photo.
 *
 * @param mapping          flat page to photo pixels
 * @param ruling           what is printed on the paper
 * @param rulingPitchMm    line or grid spacing (0 for blank)
 * @param rulingOffsetMm   y of the first printed line in page space
 * @param illumination     per pixel brightness of the bare paper, same size as the photo;
 *                         ink is darkened or lightened by it so it matches the light
 */
public record PageAnalysis(
        PageMapping mapping,
        RulingKind ruling,
        double rulingPitchMm,
        double rulingOffsetMm,
        BufferedImage illumination) {
}
