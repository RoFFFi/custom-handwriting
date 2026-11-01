package com.handwriting.capture;

import com.handwriting.core.geom.Box2;
import com.handwriting.core.glyph.GlyphKey;

/**
 * One box on the template where the user writes a given key.
 *
 * @param key        what to write in this cell
 * @param cellMm     the cell's rectangle on the sheet, in page space
 * @param baselineMm y of the printed guide baseline inside the cell
 */
public record TemplateCell(GlyphKey key, Box2 cellMm, double baselineMm) {
}
