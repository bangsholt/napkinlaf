package net.sourceforge.napkinlaf.shapes;

import static net.sourceforge.napkinlaf.util.NapkinConstants.HORIZONTAL;
import net.sourceforge.napkinlaf.util.RandomValue;

import java.awt.*;
import java.awt.geom.*;

public class DrawnScribbleGenerator extends AbstractDrawnGenerator {
    private final RandomValue position;
    private final RandomValue side;
    private final float[] point;
    private final float minShow;
    private int shown;
    private double range;
    private double swingRange;
    private float midRange;
    private GeneralPath shape;
    private int orientation;
    private boolean done;
    private double max;

    private static final float PER_STROKE = 1.8f;
    private static final double HEIGHT_ADJUST = 0.85;

    @SuppressWarnings({"SameParameterValue"})
    DrawnScribbleGenerator(float minShow) {
        this.minShow = minShow;
        position = new RandomValue(0, 0.4);
        side = new RandomValue(0, 0.4);
        point = new float[2];
    }

    @Override
    public Shape generate(AffineTransform matrix) {
        boolean first = (shape == null);
        if (first) {
            shape = new GeneralPath();
            done = false;
            position.setMid(0);
            setSideRange();
            if (orientation == HORIZONTAL) {
                convert(matrix, 0, (float) side.getMid());
            } else {
                convert(matrix, (float) side.getMid(), 0);
            }
            shape.moveTo(convertedX(), convertedY());
        }

        if (shown > minShow && !done) {
            if (first) {
                line(matrix);
            }

            double pos = position.get();
            while (pos <= shown) {
                position.setMid(pos + PER_STROKE);
                line(matrix);
                pos = position.get();
            }

            if (position.getMid() + PER_STROKE >= max) {
                position.setMid(max);
                line(matrix);   // draw from last angled to end pos
                line(matrix);   // draw from end pos to opposite side of bar
                done = true;
            }
        }
        return shape;
    }

    private void line(AffineTransform matrix) {
        // alternate sides of the bar
        setSideRange();
        double x, y;
        if (orientation == HORIZONTAL) {
            x = position.generate();
            y = side.generate();
        } else {
            y = position.generate();
            x = side.generate();
        }
        convert(matrix, (float) x, (float) y);
        shape.lineTo(convertedX(), convertedY());
    }

    private void setSideRange() {
        side.setMid(side.getMid() < midRange ?
                midRange + swingRange :
                midRange - swingRange);
    }

    private void convert(AffineTransform matrix, float x, float y) {
        point[0] = x;
        point[1] = y;
        matrix.transform(point, 0, point, 0, 1);
    }

    private float convertedX() {
        return point[0];
    }

    private float convertedY() {
        return point[1];
    }

    public void setShown(int shown) {
        if (shown < this.shown) {
            shape = null;
        }
        this.shown = shown;
    }

    public void setRange(double range) {
        if (this.range != range) {
            this.range = range;
            midRange = (float) (this.range / 2.0);
            swingRange = (range * HEIGHT_ADJUST) / 2;
            shape = null;
        }
    }

    public void setOrientation(int orientation) {
        if (this.orientation != orientation) {
            this.orientation = orientation;
            shape = null;
        }
    }

    public void setMax(double max) {
        if (this.max != max) {
            this.max = max;
            shape = null;
        }
    }
}
