
package napkin;

import java.awt.*;
import java.awt.geom.*;

public class TriangleGenerator extends ShapeGenerator {
    private final Value midX, midY;
    private final Value v1X, v1Y;
    private final Value v2X, v2Y;
    private final Value startAdjust;
    private final double rotate;

    public static final TriangleGenerator INSTANCE = new TriangleGenerator();

    public TriangleGenerator() {
        this(0);
    }

    public TriangleGenerator(double rotate) {
        this.rotate = rotate;

        double shimmy = 0.05;
        midX = new Value(0.5, shimmy);
        midY = new Value(0, shimmy);
        v1X = new Value(0, shimmy);
        v1Y = new Value(1, shimmy);
        v2X = new Value(1, shimmy);
        v2Y = new Value(1, shimmy);

        startAdjust = new Value(0.07);
    }

    public Shape generate(AffineTransform matrix) {
        GeneralPath t = new GeneralPath();

        double xScale = (matrix == null ? 1 : matrix.getScaleX());
        double yScale = (matrix == null ? 1 : matrix.getScaleY());

        double xMid = midX.generate();
        double yMid = midY.generate();
        double xV1 = v1X.generate();
        double yV1 = v1Y.generate();
        double xV2 = v2X.generate();
        double yV2 = v2Y.generate();

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

    private double adjustStartOffset(ValueSource off, double scale) {
        if (scale >= 1)
            return off.generate();
        double delta = 1 - scale;
        double exp = startAdjust.generate();
        double adjusted = Math.pow(delta, exp);
        double startScale = 1 - adjusted;
        return off.generate() * startScale;
    }

    public Value getMidX() {
        return midX;
    }

    public Value getMidY() {
        return midY;
    }

    public Value getStartAdjust() {
        return startAdjust;
    }
}