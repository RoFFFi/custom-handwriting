package com.handwriting.core.stroke;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.handwriting.core.geom.Box2;
import java.util.List;
import org.junit.jupiter.api.Test;

class StrokeTest {

    @Test
    void boundsAndLength() {
        Stroke s = new Stroke(List.of(StrokePoint.of(0, 0), StrokePoint.of(3, 0), StrokePoint.of(3, 4)));
        assertThat(s.bounds()).isEqualTo(new Box2(0, 0, 3, 4));
        assertThat(s.length()).isEqualTo(7.0);
    }

    @Test
    void rejectsEmptyStroke() {
        assertThatThrownBy(() -> new Stroke(List.of())).isInstanceOf(IllegalArgumentException.class);
    }
}
