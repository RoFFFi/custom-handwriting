package com.handwriting.core.pen;

/**
 * Physical description of a pen, used by the renderer. It says nothing about the writer;
 * that lives in {@link com.handwriting.core.hand.Hand}.
 *
 * @param id            stable identifier, e.g. "bic-blue"
 * @param type          pen family
 * @param inkRgb        ink colour as 0xRRGGBB
 * @param nominalWidthMm typical line width on paper
 * @param opacity       0..1, how much ink covers the paper (pencil is low, gel is high)
 */
public record PenProfile(String id, PenType type, int inkRgb, double nominalWidthMm, double opacity) {

    public PenProfile {
        if (nominalWidthMm <= 0) {
            throw new IllegalArgumentException("nominalWidthMm must be > 0");
        }
        if (opacity <= 0 || opacity > 1) {
            throw new IllegalArgumentException("opacity must be in (0,1]");
        }
    }
}
