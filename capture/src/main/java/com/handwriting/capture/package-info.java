/**
 * Learning the user's handwriting: from photos of filled-in template sheets to
 * {@link com.handwriting.core.glyph.GlyphSample}s.
 *
 * <p>Pipeline: print a {@link com.handwriting.capture.TemplateSheet} -> user fills every cell
 * with one pen -> photograph -> {@link com.handwriting.capture.SampleExtractor} finds the
 * corner markers, flattens the photo, cuts out each cell, separates ink from paper,
 * thins the ink to a one pixel skeleton, traces it into strokes, and measures the ink width
 * along each stroke.
 */
package com.handwriting.capture;
