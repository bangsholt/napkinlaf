package net.sourceforge.napkinlaf.util;

import java.util.Random;

@SuppressWarnings({"WeakerAccess"})
public class NapkinRandom {
    private static final Random random = new Random();

    private NapkinRandom() {
    }

    public static double gaussian(double scale) {
        return gaussian() * scale;
    }

    public static double gaussian() {
        return random.nextGaussian();
    }

    public static double nextDouble(double scale) {
        return random.nextDouble() * scale;
    }

    public static double triangular(double scale) {
        return scale * (random.nextDouble() - random.nextDouble());
    }

    public static double triangularCubeRoot(double scale) {
        return scale * Math.cbrt(random.nextDouble() - random.nextDouble());
    }
}
