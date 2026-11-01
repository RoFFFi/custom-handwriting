package com.handwriting.page;

import com.handwriting.core.geom.Vec2;

/**
 * Maps flat page space (mm) to pixel positions in the photo. Week 6 v1: a homography from
 * the four page corners (handles the camera angle). Later: a mesh warp for page curl and the
 * bulge near a spiral binding.
 */
public interface PageMapping {

    Vec2 toPhotoPixel(Vec2 pageMm);

    double pageWidthMm();

    double pageHeightMm();
}
