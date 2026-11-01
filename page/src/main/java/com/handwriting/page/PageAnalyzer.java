package com.handwriting.page;

import java.awt.image.BufferedImage;

/**
 * Week 6: find the sheet's outline, estimate the mapping, detect ruling (grid lines show up
 * as strong peaks in a frequency spectrum or a Hough transform), and estimate illumination
 * by blurring away the ink and lines. Expected to use OpenCV.
 */
public interface PageAnalyzer {

    PageAnalysis analyze(BufferedImage photo);
}
