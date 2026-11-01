package com.handwriting.core.random;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RngTest {

    @Test
    void sameSeedGivesSameSequence() {
        Rng a = new Rng(42);
        Rng b = new Rng(42);
        for (int i = 0; i < 100; i++) {
            assertThat(a.gaussian(0, 1)).isEqualTo(b.gaussian(0, 1));
        }
    }

    @Test
    void forksAreIndependentOfParentUsage() {
        Rng a = new Rng(7);
        Rng forkA = a.fork();
        a.uniform(); // drawing more from the parent must not change the fork

        Rng b = new Rng(7);
        Rng forkB = b.fork();

        assertThat(forkA.uniform()).isEqualTo(forkB.uniform());
    }

    @Test
    void gaussianHasRoughlyRightMeanAndSpread() {
        Rng rng = new Rng(1);
        int n = 20_000;
        double sum = 0, sumSq = 0;
        for (int i = 0; i < n; i++) {
            double v = rng.gaussian(5, 2);
            sum += v;
            sumSq += v * v;
        }
        double mean = sum / n;
        double sd = Math.sqrt(sumSq / n - mean * mean);
        assertThat(mean).isBetween(4.95, 5.05);
        assertThat(sd).isBetween(1.95, 2.05);
    }
}
