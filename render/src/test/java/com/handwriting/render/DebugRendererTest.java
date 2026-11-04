package com.handwriting.render;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.handwriting.core.ink.InkDocument;
import com.handwriting.core.ink.PlacedStroke;
import com.handwriting.core.pen.PenProfile;
import com.handwriting.core.pen.PenType;
import com.handwriting.core.random.Rng;
import com.handwriting.core.stroke.Stroke;
import com.handwriting.core.stroke.StrokePoint;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.List;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DebugRendererTest {

    private static final PenProfile BLUE = new PenProfile("test-blue", PenType.BALLPOINT, 0x1030C0, 0.5, 1.0);

    private static PlacedStroke stroke(StrokePoint... points) {
        return new PlacedStroke(new Stroke(List.of(points)), BLUE);
    }

    private static InkLayer render(PlacedStroke... strokes) {
        return new DebugRenderer().render(new InkDocument(20, 10, List.of(strokes)), 10, new Rng(1));
    }

    private static int alpha(InkLayer layer, int x, int y) {
        return layer.image().getRGB(x, y) >>> 24;
    }

    @Test
    void sizesTheImageFromThePageAndResolution() {
        InkLayer layer = new DebugRenderer().render(new InkDocument(20.05, 10, List.of()), 10, new Rng(1));

        assertThat(layer.image().getWidth()).isEqualTo(201);
        assertThat(layer.image().getHeight()).isEqualTo(100);
        assertThat(layer.pixelsPerMm()).isEqualTo(10);
    }

    @Test
    void drawsAStrokeInThePenColourAndLeavesTheRestTransparent() {
        InkLayer layer = render(stroke(StrokePoint.of(2, 5), StrokePoint.of(18, 5)));

        assertThat(layer.image().getRGB(100, 50)).isEqualTo(0xFF1030C0);
        assertThat(alpha(layer, 100, 20)).as("well above the line").isZero();
        assertThat(alpha(layer, 5, 50)).as("before the line starts").isZero();
    }

    @Test
    void usesThePenWidthInMillimetres() {
        InkLayer layer = render(stroke(StrokePoint.of(2, 5), StrokePoint.of(18, 5)));

        // 0.5 mm at 10 px/mm is 5 px, so ink reaches about 2.5 px each side of y = 50.
        assertThat(alpha(layer, 100, 48)).isPositive();
        assertThat(alpha(layer, 100, 52)).isPositive();
        assertThat(alpha(layer, 100, 54)).isZero();
    }

    @Test
    void drawsASinglePointAsADot() {
        InkLayer layer = render(stroke(StrokePoint.of(10, 5)));

        assertThat(alpha(layer, 100, 50)).isPositive();
        assertThat(alpha(layer, 110, 50)).isZero();
    }

    @Test
    void rejectsANonPositiveResolution() {
        assertThatThrownBy(() -> new DebugRenderer().render(new InkDocument(1, 1, List.of()), 0, new Rng(1)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void savesAPngThatReadsBackTheSame(@TempDir Path dir) throws Exception {
        InkLayer layer = render(stroke(StrokePoint.of(2, 5), StrokePoint.of(18, 5)));
        Path file = dir.resolve("ink.png");

        layer.savePng(file);

        BufferedImage read = ImageIO.read(file.toFile());
        assertThat(read.getWidth()).isEqualTo(200);
        assertThat(read.getRGB(100, 50)).isEqualTo(0xFF1030C0);
        assertThat(read.getRGB(100, 20) >>> 24).isZero();
    }
}
