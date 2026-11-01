package com.handwriting.core.ink;

import com.handwriting.core.pen.PenProfile;
import com.handwriting.core.stroke.Stroke;

/** A stroke that synthesis has positioned on the page, in page space (millimetres). */
public record PlacedStroke(Stroke stroke, PenProfile pen) {
}
