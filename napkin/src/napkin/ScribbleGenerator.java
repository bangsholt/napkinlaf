
package napkin;

import java.awt.*;
import java.awt.geom.*;

public class ScribbleGenerator extends ShapeGenerator {
    private final Value position;
    private final Value side;
    private final float[] point;
    private final float minShow;
    private int shown;
    private double range;
    private GeneralPath shape;
    private int orientation;
    private boolean done;

    private static final float PER_STROKE = 4;
    private double max;

    ScribbleGenerator(float minShow) {
        this.minShow = minShow;
        position = new Value(0, 0.4);
        side = new Value(0, 0.4);
        point = new float[2];
    }
    public Shape generate(AffineTransform matrix) {
        if (shape == null) {
            shape = new GeneralPath();
            done = false;
            position.setMid(0);
            side.setMid(0);
            convert(matrix, 0, 0);
            shape.moveTo(convertedX(), convertedY());
        } else {
            if (side.getMid() > 0)
                side.setMid(range); // in case it has changed
        }

        if (shown < minShow || done)
            return shape;

        for (double pos = position.getMid(); pos <= shown; pos += PER_STROKE) {
            position.setMid(pos);
            line(matrix);
        }

        if (position.getMid() + PER_STROKE >= max) {
            position.setMid(max);
            line(matrix);   // draw from last angled to end pos
            line(matrix);   // draw from end pos to opposite side of bar
            done = true;
        }
        return shape;
    }

    private void line(AffineTransform matrix) {
        side.setMid(side.getMid() == 0 ? range : 0);
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
        if (shown < this.shown)
            shape = null;
        this.shown = shown;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public void setMax(double max) {
        this.max = max;
    }
}