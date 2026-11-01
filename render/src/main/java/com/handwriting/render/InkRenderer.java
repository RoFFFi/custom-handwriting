package com.handwriting.render;

import com.handwriting.core.ink.InkDocument;
import com.handwriting.core.random.Rng;

/**
 * Week 1: {@code DebugRenderer}: plain Java2D polylines, just to see strokes.
 * Week 5: {@code PenRenderer}: variable width along the stroke from pressure and speed,
 * per-pen behaviour (ballpoint skips and blobs, pencil grain, felt-tip bleed),
 * supersampling for clean edges.
 */
public interface InkRenderer {

    InkLayer render(InkDocument document, double pixelsPerMm, Rng rng);
}
