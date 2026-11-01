package com.handwriting.core.sheet;

import com.handwriting.core.geom.Box2;
import java.util.List;

/**
 * The writing surface as the writer sees it, in flat page space (mm). Built by the page
 * module from the photo, then handed to the writer. Camera angle and page curl are not
 * here: they are undone by the page mapping, so the writer always works on a flat sheet.
 *
 * @param widthMm       sheet width
 * @param heightMm      sheet height
 * @param leftMarginMm  where lines start (a printed margin line, or a habit)
 * @param rightMarginMm where lines should end; the writer may overshoot slightly, like people do
 * @param ruledLinesMm  y of every printed line or grid row, top to bottom. For blank paper,
 *                      imaginary lines at the writer's natural spacing.
 * @param occupiedMm    areas that already have ink on the photo (a title, earlier notes),
 *                      which the writer must avoid
 */
public record Sheet(
        double widthMm,
        double heightMm,
        double leftMarginMm,
        double rightMarginMm,
        List<Double> ruledLinesMm,
        List<Box2> occupiedMm) {

    public Sheet {
        ruledLinesMm = List.copyOf(ruledLinesMm);
        occupiedMm = List.copyOf(occupiedMm);
        if (rightMarginMm <= leftMarginMm) {
            throw new IllegalArgumentException("rightMarginMm must be > leftMarginMm");
        }
    }
}
