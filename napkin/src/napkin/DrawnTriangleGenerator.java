// $Id$

package napkin;

import java.awt.*;
import java.awt.geom.*;

public class DrawnTriangleGenerator extends DrawnShapeGenerator {
    private final RandomValue midX, midY;
    private final RandomValue lX, lY;
    private final RandomValue rX, rY;
    private final RandomValue startAdjust;
    private final double rotate;

    public static final DrawnTriangleGenerator INSTANCE = new DrawnTriangleGenerator();

    public DrawnTriangleGenerator() {
        this(0);
    }

    public DrawnTriangleGenerator(double rotate) {
        this.rotate = rotate;

        double shimmy = 0.05;
        midX = new RandomValue(0.5, shimmy);
        midY = new RandomValue(0, shimmy);
        lX = new RandomValue(0, shimmy);
        lY = new RandomValue(1, shimmy);
        rX = new RandomValue(1, shimmy);
        rY = new RandomValue(1, shimmy);

        startAdjust = new RandomValue(0.07);
    }

    public Shape generate(AffineTransform matrix) {
        GeneralPath t = new GeneralPath();

        double xScale = (matrix == null ? 1 : matrix.getScaleX());
        double yScale = (matrix == null ? 1 : matrix.getScaleY());

        double xMid = midX.generate();
        double yMid = midY.generate();
        double xV1 = lX.generate();
        double yV1 = lY.generate();
        double xV2 = rX.generate();
        double yV2 = rY.generate();

        if (rotate != 0) {
            matrix = NapkinUtil.copy(matrix);
            matrix.rotate(rotate, 0.5, 0.5);
        }
        double[] points = {xMid, yMid, xV1, yV1, xV2, yV2};
        NapkinUtil.transform(matrix, points);

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
        if (scale >= 1)
            return off.generate();
        double delta = 1 - scale;
        double exp = startAdjust.generate();
        double adjusted = Math.pow(delta, exp);
        double startScale = 1 - adjusted;
        return off.generate() * startScale;
    }

    public RandomValue getMidX() {
        return midX;
    }

    public RandomValue getMidY() {
        return midY;
    }

    public RandomValue getStartAdjust() {
        return startAdjust;
    }
}
