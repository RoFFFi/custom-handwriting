/**
 * Domain model shared by every module.
 *
 * <h2>Coordinate conventions (read this before touching geometry)</h2>
 * <ul>
 *   <li><b>Glyph space</b>: where captured letters live. Unit 1.0 = the writer's x-height.
 *       Origin is the left end of the baseline. +x goes right, +y goes <i>down</i>
 *       (same as Java2D), so ascenders have negative y and descenders positive y.</li>
 *   <li><b>Page space</b>: where finished writing is placed. Millimetres, origin at the
 *       top-left corner of the flat (unwarped) sheet, +y down.</li>
 *   <li><b>Pixel space</b>: only exists in {@code render} and {@code page}. Never store pixels
 *       in the model.</li>
 * </ul>
 *
 * <p>This module has no third party dependencies on purpose.
 */
package com.handwriting.core;
