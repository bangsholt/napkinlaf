// $Id$

package napkin;

public class Value implements ValueSource {

    private double adjust;
    private double mid;
    private double range;

    public Value(double val) {
        this(val, 0);
    }

    public Value(double val, double range) {
        this.mid = val;
        this.range = range;
    }

    public void randomize() {
        adjust = adjustment();
    }

    private double adjustment() {
        double factor = getRange();
        if (factor == 0)
            return 0;
        else
            return NapkinUtil.random.nextGaussian() * factor;
    }

    public double get() {
        return getMid() + adjust;
    }

    public double generate() {
        return getMid() + adjustment();
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

