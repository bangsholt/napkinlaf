// $Id$

package napkin;

import java.awt.*;
import java.awt.geom.*;

public class CheckGenerator extends ShapeGenerator {
    private final ValueSource widthVal;
    private final ValueSource heightVal;
    private final QuadGenerator leftLineGen;
    private final QuadGenerator rightLineGen;

    private final Value midXScale;
    private final Value midYScale;
    private final Value leftXScale;
    private final Value leftYScale;
    private final Value rightXScale;
    private final Value rightYScale;

    public static final CheckGenerator INSTANCE = new CheckGenerator();

    public CheckGenerator() {
        this(10);
    }

    public CheckGenerator(double size) {
        this(new Value(size), new Value(size));
    }

    public CheckGenerator(ValueSource widthVal, ValueSource heightVal) {
        this.widthVal = widthVal;
        this.heightVal = heightVal;

        leftLineGen = new QuadGenerator();
        leftLineGen.getCtlY().setMid(+2);
        leftLineGen.getCtlY().setRange(0.3);

        rightLineGen = new QuadGenerator();
        rightLineGen.getCtlY().setMid(-2);
        rightLineGen.getCtlY().setRange(0.3);

        leftXScale = new Value(0.5, 0.075);
        leftYScale = new Value(0.5, 0.075);
        midXScale = new Value(0.5, 0.1);
        midYScale = new Value(0.1, 0.05);
        rightXScale = new Value(1.1, 0.1);
        rightYScale = new Value(0.9, 0.1);
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
            double rot, ShapeGenerator lineGen) {

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

    public Value getLeftXScale() {
        return leftXScale;
    }

    public Value getLeftYScale() {
        return leftYScale;
    }

    public Value getMidXScale() {
        return midXScale;
    }

    public Value getMidYScale() {
        return midYScale;
    }

    public Value getRightXScale() {
        return rightXScale;
    }

    public Value getRightYScale() {
        return rightYScale;
    }
}

