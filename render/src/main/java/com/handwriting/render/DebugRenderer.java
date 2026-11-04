package com.handwriting.render;

import com.handwriting.core.ink.InkDocument;
import com.handwriting.core.ink.PlacedStroke;
import com.handwriting.core.random.Rng;
import com.handwriting.core.stroke.StrokePoint;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * The simplest possible renderer: every stroke is a plain line in its pen's colour and nominal
 * width, with round ends. No pressure, no texture, no randomness. It exists so strokes can be
 * seen on screen while capture and synthesis are being built; the real ink comes in week 5.
 */
public final class DebugRenderer implements InkRenderer {

    @Override
    public InkLayer render(InkDocument document, double pixelsPerMm, Rng rng) {
        if (pixelsPerMm <= 0) {
            throw new IllegalArgumentException("pixelsPerMm must be > 0: " + pixelsPerMm);
        }
        int width = (int) Math.ceil(document.pageWidthMm() * pixelsPerMm);
        int height = (int) Math.ceil(document.pageHeightMm() * pixelsPerMm);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g.scale(pixelsPerMm, pixelsPerMm);
            for (PlacedStroke placed : document.strokes()) {
                draw(g, placed);
            }
        } finally {
            g.dispose();
        }
        return new InkLayer(image, pixelsPerMm);
    }

    private static void draw(Graphics2D g, PlacedStroke placed) {
        double widthMm = placed.pen().nominalWidthMm();
        int alpha = (int) Math.round(placed.pen().opacity() * 255);
        g.setColor(new Color((alpha << 24) | (placed.pen().inkRgb() & 0xFFFFFF), true));
        List<StrokePoint> points = placed.stroke().points();
        if (points.size() == 1) {
            // A dot (the top of an i): a filled circle as wide as the pen.
            StrokePoint p = points.get(0);
            g.fill(new Ellipse2D.Double(p.x() - widthMm / 2, p.y() - widthMm / 2, widthMm, widthMm));
            return;
        }
        Path2D.Double path = new Path2D.Double();
        path.moveTo(points.get(0).x(), points.get(0).y());
        for (StrokePoint p : points.subList(1, points.size())) {
            path.lineTo(p.x(), p.y());
        }
        g.setStroke(new BasicStroke((float) widthMm, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(path);
    }
}
