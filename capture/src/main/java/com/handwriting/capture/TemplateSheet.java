package com.handwriting.capture;

import java.util.List;

/**
 * A printable capture sheet. Week 2: generate these as PDF or PNG with corner markers
 * (ArUco style) so photos can be flattened exactly, and a guide baseline and x-height line
 * in every cell. Each key should appear 3 to 5 times across sheets to capture variation.
 */
public record TemplateSheet(String id, double widthMm, double heightMm, List<TemplateCell> cells) {

    public TemplateSheet {
        cells = List.copyOf(cells);
    }
}
