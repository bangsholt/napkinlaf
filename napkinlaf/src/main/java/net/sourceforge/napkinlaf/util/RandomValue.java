package net.sourceforge.napkinlaf.util;

public class RandomValue implements RandomValueSource {
    private double adjust;
    private double mid;
    private double range;

    public RandomValue(double mid) {
        this(mid, 0);
    }

    public RandomValue(double mid, double range) {
        this.mid = mid;
        this.range = range;
    }

    public void randomize() {
        double factor = getRange();
        if (factor == 0) {
            adjust = 0;
        } else {
            adjust = NapkinRandom.triangularCubeRoot(0.9d * factor);
        }
    }

    public double get() {
        return getMid() + adjust;
    }

    public double generate() {
        randomize();
        return get();
    }

    public double getMid() {
        return mid;
    }

    public void setMid(double mid) {
        this.mid = mid;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public double getAdjust() {
        return adjust;
    }

    /** @return The minimum normative value (mid point minus range). */
    public double min() {
        return mid - range;
    }

    /** @return The maximum normative value (mid point plus range). */
    public double max() {
        return mid + range;
    }
}

