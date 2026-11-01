package com.handwriting.core.random;

import java.util.List;
import java.util.SplittableRandom;

/**
 * The only source of randomness in the project. Never call {@code Math.random()} or
 * {@code new Random()} elsewhere: with one seeded source, the same seed and input always
 * produce the same page, which makes bugs reproducible and tests possible.
 * See docs/DECISIONS.md, D4.
 */
public final class Rng {

    private final SplittableRandom random;

    public Rng(long seed) {
        this.random = new SplittableRandom(seed);
    }

    private Rng(SplittableRandom random) {
        this.random = random;
    }

    /**
     * An independent child stream. Give each subsystem its own fork (layout, variation, ink)
     * so changing how many numbers one of them draws does not reshuffle the others.
     */
    public Rng fork() {
        return new Rng(random.split());
    }

    public double uniform() {
        return random.nextDouble();
    }

    public double uniform(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }

    public int nextInt(int boundExclusive) {
        return random.nextInt(boundExclusive);
    }

    public boolean chance(double probability) {
        return random.nextDouble() < probability;
    }

    /** Normally distributed value (Box-Muller). */
    public double gaussian(double mean, double stdDev) {
        double u1 = 1.0 - random.nextDouble(); // (0,1], avoids log(0)
        double u2 = random.nextDouble();
        double z = Math.sqrt(-2.0 * Math.log(u1)) * Math.cos(2.0 * Math.PI * u2);
        return mean + stdDev * z;
    }

    public <T> T pick(List<T> items) {
        if (items.isEmpty()) {
            throw new IllegalArgumentException("cannot pick from an empty list");
        }
        return items.get(random.nextInt(items.size()));
    }
}
