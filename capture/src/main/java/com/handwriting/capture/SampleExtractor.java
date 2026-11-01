package com.handwriting.capture;

import com.handwriting.core.glyph.GlyphSample;
import java.awt.image.BufferedImage;
import java.util.List;

/** Week 3: photo of a filled template -> glyph samples in glyph space. */
public interface SampleExtractor {

    List<GlyphSample> extract(BufferedImage photo, TemplateSheet sheet, String penId);
}
