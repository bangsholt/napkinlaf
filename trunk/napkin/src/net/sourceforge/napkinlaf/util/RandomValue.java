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
        if (factor == 0)
            adjust = 0;
        else
            adjust = NapkinRandom.triCbRt(0.9d * factor); //gaussian(factor);
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

    public double min() {
        return mid - range;
    }

    public double max() {
        return mid + range;
    }
}

