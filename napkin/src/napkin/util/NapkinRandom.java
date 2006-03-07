// $Header$

package napkin.util;

import java.util.Random;

public class NapkinRandom {
    public static final Random random = new Random();

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
}