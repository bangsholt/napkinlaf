// $Id$

package napkin.shapes;

import napkin.util.NapkinUtil;
import napkin.util.RandomValue;
import napkin.util.RandomXY;

import java.awt.*;
import java.awt.geom.*;

public class DrawnCheckGenerator extends AbstractDrawnGenerator {
    private final RandomXY dim;
    private final DrawnQuadLineGenerator leftLineGen;
    private final DrawnQuadLineGenerator rightLineGen;

    private final RandomXY midScale;
    private final RandomXY leftScale;
    private final RandomXY rightScale;

    public DrawnCheckGenerator() {
        this(10);
    }

    public DrawnCheckGenerator(double size) {
        this(new RandomValue(size), new RandomValue(size));
    }

    public DrawnCheckGenerator(RandomValue widthVal, RandomValue heightVal) {
        super();

        dim = new RandomXY(widthVal, heightVal);

        leftLineGen = new DrawnQuadLineGenerator();
        leftLineGen.getCtl().getY().setMid(+2);
        leftLineGen.getCtl().getY().setRange(0.3);

        rightLineGen = new DrawnQuadLineGenerator();
        rightLineGen.getCtl().getY().setMid(-2);
        rightLineGen.getCtl().getY().setRange(0.3);

        leftScale = new RandomXY(0.5, 0.075, 0.5, 0.075);
        midScale = new RandomXY(0.5, 0.1, 0.1, 0.05);
        rightScale = new RandomXY(1.1, 0.1, 0.9, 0.1);
    }

    @Override
    public Shape generate(AffineTransform matrix) {
        GeneralPath check = new GeneralPath();

        Point2D lScale = leftScale.generate();
        Point2D mScale = midScale.generate();
        Point2D rScale = rightScale.generate();
        Point2D size = dim.getMid();
        double boxWidth = size.getX();
        double boxHeight = size.getY();

        double mx = mScale.getX() * boxWidth;
        double my = boxHeight - mScale.getY() * boxHeight;
        double lx = mx - lScale.getX() * boxWidth;
        double ly = my - lScale.getY() * boxHeight;
        double rx = mx + rScale.getX() * boxWidth;
        double ry = my - rScale.getY() * boxHeight;

        NapkinUtil.drawStroke(check, matrix, mx, my, lx, ly, -Math.PI,
                leftLineGen);
        NapkinUtil.drawStroke(check, matrix, mx, my, rx, ry, 0, rightLineGen);

        return check;
    }

    public RandomXY getLeftScale() {
        return leftScale;
    }

    public RandomXY getRightScale() {
        return rightScale;
    }

    public RandomXY getMidScale() {
        return midScale;
    }

    private static double rootOfSquares(double ... numbers) {
        double sum = 0.0;
        for (double number : numbers)
            sum += number * number;
        return Math.sqrt(sum);
    }

    public double getMeanWidth() {
        return getRightScale().getX().getMid() + getLeftScale().getX().getMid();
    }

    public double getWidthRange() {
        // sum of Gaussians is the sum of means and sum of variance (not s.d.)
        return rootOfSquares(
                getRightScale().getX().getRange(),
                getMidScale().getX().getRange(),
                getMidScale().getX().getRange(),
                getLeftScale().getX().getRange()
        );
    }

    public double getMaxWidth() {
        return getMeanWidth() + getWidthRange();
    }

    public double getMinWidth() {
        return getMeanWidth() - getWidthRange();
    }

    public double getMeanHeight() {
        return getRightScale().getY().getMid() + getMidScale().getY().getMid();
    }

    public double getHeightRange() {
        // sum of Gaussians is the sum of means and sum of variance (not s.d.)
        return rootOfSquares(
                getRightScale().getY().getRange(),
                getMidScale().getY().getRange()
        );
    }

    public double getMaxHeight() {
        return getMeanHeight() + getHeightRange();
    }

    public double getMinHeight() {
        return getMeanHeight() - getHeightRange();
    }
}

