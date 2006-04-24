package net.sourceforge.napkinlaf.shapes;

import net.sourceforge.napkinlaf.util.RandomValue;
import net.sourceforge.napkinlaf.util.RandomValueSource;
import net.sourceforge.napkinlaf.util.RandomXY;

import java.awt.*;
import java.awt.geom.*;

public class DrawnTriangleGenerator extends AbstractDrawnGenerator {
    private final RandomXY mid;
    private final RandomXY l;
    private final RandomXY r;
    private final RandomValue startAdjust;
    private final double rotate;

    public DrawnTriangleGenerator(double rotate) {
        super();

        this.rotate = rotate;

        double shimmy = 0.05;
        mid = new RandomXY(0.5, shimmy, 0, shimmy);
        l = new RandomXY(0, shimmy, 1, shimmy);
        r = new RandomXY(1, shimmy, 1, shimmy);

        startAdjust = new RandomValue(0.07);
    }

    @Override
    public Shape generate(AffineTransform matrix) {
        GeneralPath t = new GeneralPath();

        double xScale = matrix.getScaleX();
        double yScale = matrix.getScaleY();

        Point2D midAt = mid.generate();
        Point2D leftAt = l.generate();
        Point2D rightAt = r.generate();
        double xMid = midAt.getX();
        double yMid = midAt.getY();
        double xV1 = leftAt.getX();
        double yV1 = leftAt.getY();
        double xV2 = rightAt.getX();
        double yV2 = rightAt.getY();

        if (rotate != 0) {
            matrix = (AffineTransform) matrix.clone();
            matrix.rotate(rotate, 0.5, 0.5);
        }
        double[] points = {xMid, yMid, xV1, yV1, xV2, yV2};
        matrix.transform(points, 0, points, 0, 3);

        double minDist = Double.MAX_VALUE;
        int minPoint = 0;
        for (int i = 0; i < points.length; i += 2) {
            double x = points[i];
            double y = points[i + 1];
            double dist = Math.sqrt(x * x + y * y);
            if (dist < minDist) {
                minDist = dist;
                minPoint = i;
            }
        }

        float[] drawPoints = new float[points.length + 2];
        for (int i = 0; i < points.length; i++)
            drawPoints[i] = (float) points[(i + minPoint) % points.length];
        drawPoints[6] = drawPoints[0];
        drawPoints[7] = drawPoints[1];

        drawPoints[0] += adjustStartOffset(startAdjust, xScale);
        drawPoints[1] += adjustStartOffset(startAdjust, yScale);
        drawPoints[6] += adjustStartOffset(startAdjust, xScale);
        drawPoints[7] += adjustStartOffset(startAdjust, yScale);

        t.moveTo(drawPoints[0], drawPoints[1]);
        for (int i = 2; i < drawPoints.length; i += 2)
            t.lineTo(drawPoints[i], drawPoints[i + 1]);
        return t;
    }

    private double adjustStartOffset(RandomValueSource off, double scale) {
        double result = off.generate();
        if (scale < 1d) {
            double delta = 1 - scale;
            double exp = startAdjust.generate();
            double adjusted = Math.pow(delta, exp);
            double startScale = 1 - adjusted;
            result *= startScale;
        }
        return result;
    }

    public RandomValue getStartAdjust() {
        return startAdjust;
    }
}
