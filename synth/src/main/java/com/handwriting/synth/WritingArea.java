package com.handwriting.synth;

/**
 * Where on the sheet writing is allowed, in page space (mm). For ruled or grid paper the
 * page module fills this from the detected lines so the baselines land on them.
 *
 * @param leftMm          left margin x
 * @param rightMm         right margin x
 * @param firstBaselineMm y of the first line's baseline
 * @param bottomMm        no baseline below this y
 * @param lineSpacingMm   distance between baselines
 */
public record WritingArea(double leftMm, double rightMm, double firstBaselineMm, double bottomMm, double lineSpacingMm) {

    public WritingArea {
        if (rightMm <= leftMm) {
            throw new IllegalArgumentException("rightMm must be > leftMm");
        }
        if (lineSpacingMm <= 0) {
            throw new IllegalArgumentException("lineSpacingMm must be > 0");
        }
    }

    public double widthMm() {
        return rightMm - leftMm;
    }
}
