// $Id$

package napkin;

import java.awt.*;
import java.awt.geom.*;

public class DrawnCheckGenerator extends DrawnShapeGenerator {
    private final RandomValueSource widthVal;
    private final RandomValueSource heightVal;
    private final DrawnQuadLineGenerator leftLineGen;
    private final DrawnQuadLineGenerator rightLineGen;

    private final RandomValue midXScale;
    private final RandomValue midYScale;
    private final RandomValue leftXScale;
    private final RandomValue leftYScale;
    private final RandomValue rightXScale;
    private final RandomValue rightYScale;

    public static final DrawnCheckGenerator INSTANCE = new DrawnCheckGenerator();

    public DrawnCheckGenerator() {
        this(10);
    }

    public DrawnCheckGenerator(double size) {
        this(new RandomValue(size), new RandomValue(size));
    }

    public DrawnCheckGenerator(RandomValueSource widthVal, RandomValueSource heightVal) {
        this.widthVal = widthVal;
        this.heightVal = heightVal;

        leftLineGen = new DrawnQuadLineGenerator();
        leftLineGen.getCtlY().setMid(+2);
        leftLineGen.getCtlY().setRange(0.3);

        rightLineGen = new DrawnQuadLineGenerator();
        rightLineGen.getCtlY().setMid(-2);
        rightLineGen.getCtlY().setRange(0.3);

        leftXScale = new RandomValue(0.5, 0.075);
        leftYScale = new RandomValue(0.5, 0.075);
        midXScale = new RandomValue(0.5, 0.1);
        midYScale = new RandomValue(0.1, 0.05);
        rightXScale = new RandomValue(1.1, 0.1);
        rightYScale = new RandomValue(0.9, 0.1);
    }

    public Shape generate(AffineTransform matrix) {
        GeneralPath check = new GeneralPath();

        double lxScale = leftXScale.generate();
        double lyScale = leftYScale.generate();
        double mxScale = midXScale.generate();
        double myScale = midYScale.generate();
        double rxScale = rightXScale.generate();
        double ryScale = rightYScale.generate();
        double boxWidth = widthVal.getMid();
        double boxHeight = heightVal.getMid();

        double mx = mxScale * boxWidth;
        double my = boxHeight - myScale * boxHeight;
        double lx = mx - lxScale * boxWidth;
        double ly = my - lyScale * boxHeight;
        double rx = mx + rxScale * boxWidth;
        double ry = my - ryScale * boxHeight;

        drawStroke(check, matrix, mx, my, lx, ly, -Math.PI, leftLineGen);
        drawStroke(check, matrix, mx, my, rx, ry, 0, rightLineGen);

        return check;
    }

    private static void drawStroke(GeneralPath check, AffineTransform matrix,
            double mx, double my, double ex, double ey,
            double rot, DrawnShapeGenerator lineGen) {

        double xDelta = mx - ex;
        double yDelta = my - ey;
        double angle = Math.atan2(xDelta, yDelta);
        AffineTransform mat = NapkinUtil.copy(matrix);
        mat.translate(mx, my);
        mat.rotate(rot + angle);
        double len = Math.sqrt(xDelta * xDelta + yDelta * yDelta);
        mat.scale(len / LENGTH, 1);
        addLine(check, mat, lineGen);
    }

    public RandomValue getLeftXScale() {
        return leftXScale;
    }

    public RandomValue getLeftYScale() {
        return leftYScale;
    }

    public RandomValue getMidXScale() {
        return midXScale;
    }

    public RandomValue getMidYScale() {
        return midYScale;
    }

    public RandomValue getRightXScale() {
        return rightXScale;
    }

    public RandomValue getRightYScale() {
        return rightYScale;
    }
}

