// $Id$

package napkin;

import java.awt.*;
import java.awt.geom.*;
import java.util.HashMap;
import java.util.Map;

public class DrawnBoxGenerator extends DrawnShapeGenerator {

    private final RandomValue begX;
    private final RandomValue endY;
    private double adjustmentX;
    private double adjustmentY;
    private final RandomValue startAdjust;
    private final RandomValue sizeX;
    private final RandomValue sizeY;
    private int breakSide;
    private final Point2D breakBeg;
    private final Point2D breakEnd;
    private final Shape[] sides;
    private final DrawnShapeGenerator[] gens;
    private final Map generators;

    private static boolean DEBUG = false;

    public static final String[] SIDE_NAMES = {
        null, "top", "left", "bottom", "right"
    };

    public static final DrawnBoxGenerator INSTANCE = new DrawnBoxGenerator();

    private class SideSize extends RandomValue {
        private final int s1;
        private final int s2;

        SideSize(double val, int s1, int s2) {
            super(val);
            this.s1 = s1;
            this.s2 = s2;
            setSideType(val);
        }

        public void setMid(double mid) {
            super.setMid(mid);
            setSideType(mid);
        }

        private void setSideType(double mid) {
            Class type = defaultLineType(mid);
            setGenerator(s1, type);
            setGenerator(s2, type);
        }
    }

    public DrawnBoxGenerator() {
        this(DrawnCubicLineGenerator.INSTANCE, DrawnQuadLineGenerator.INSTANCE);
    }

    public DrawnBoxGenerator(DrawnCubicLineGenerator cubic,
            DrawnQuadLineGenerator quad) {
        generators = new HashMap(3);
        generators.put(DrawnCubicLineGenerator.class, cubic);
        generators.put(DrawnQuadLineGenerator.class, quad);

        // TOP ... BOTTOM runs from 1 to 4
        sides = new Shape[5];
        gens = new DrawnShapeGenerator[5];
        for (int i = 1; i < 5; i++)
            setGenerator(i, DrawnCubicLineGenerator.class);

        begX = new RandomValue(-1, 3);
        endY = new RandomValue(0, 2.5);
        startAdjust = new RandomValue(5);
        sizeX = new SideSize(LENGTH, LEFT, RIGHT);
        sizeY = new SideSize(LENGTH * 0.618, TOP, BOTTOM);
        breakSide = NO_SIDE;
        breakBeg = new Point2D.Double(0, 0);
        breakEnd = new Point2D.Double(0, 0);
    }

    public Shape generate(AffineTransform matrix) {
        GeneralPath shape = new GeneralPath();

        double xSize = sizeX.generate();
        double ySize = sizeY.generate();
        double xScale = xSize / LENGTH;
        double yScale = ySize / LENGTH;

        double xBeg = adjustStartOffset(begX, xScale);
        double yEnd = adjustStartOffset(endY, yScale);
        adjustmentX = xBeg - begX.get();
        adjustmentY = yEnd - endY.get();

        AffineTransform smat;
        double scale;

        scale = (xSize - xBeg) / LENGTH;
        smat = NapkinUtil.copy(matrix);
        smat.translate(xBeg, 0);
        smat.scale(scale, 1);
        sides[TOP] = addSide(shape, smat, TOP, scale);

        scale = xScale;
        smat = NapkinUtil.copy(matrix);
        smat.translate(xSize, ySize);
        smat.rotate(Math.PI);
        smat.scale(scale, 1);
        sides[BOTTOM] = addSide(shape, smat, BOTTOM, scale);

        scale = yScale;
        smat = NapkinUtil.copy(matrix);
        smat.translate(xSize, 0);
        smat.rotate(Math.PI / 2);
        smat.scale(scale, 1);
        sides[RIGHT] = addSide(shape, smat, RIGHT, scale);

        scale = (ySize - yEnd) / LENGTH;
        smat = NapkinUtil.copy(matrix);
        smat.translate(0, ySize);
        smat.rotate(-Math.PI / 2);
        smat.scale(scale, 1);
        sides[LEFT] = addSide(shape, smat, LEFT, scale);

        return shape;
    }

    private Shape addSide(GeneralPath shape, AffineTransform smat, int side,
            double scale) {
        if (side != breakSide)
            return addLine(shape, smat, gens[side]);

        GeneralPath line = new GeneralPath();
        if (side == BOTTOM || side == LEFT)
            scale = -scale; // this ones are drawn backwards
        if (side == TOP || side == BOTTOM)
            addWithXBreak(smat, line, scale);
        else
            addWithYBreak(smat, line, scale);

        shape.append(line, false);
        return line;
    }

    private void addWithXBreak(AffineTransform smat, GeneralPath line,
            double scale) {
        // Need to transalate the absolute positions into positions on the line
        double xOff = smat.getTranslateX();
        double xSize = sizeX.get() - (begX.get() + adjustmentX);
        if (scale < 0)
            xSize = -xSize;
        double xBeg = breakBeg.getX() - xOff;
        double xEnd = breakEnd.getX() - xOff;

        dumpValues(smat, xBeg, xEnd);

        addSegment(line, smat, 0, 0, xBeg / scale);
        addSegment(line, smat, xEnd / scale, 0, (xSize - xEnd) / scale);
    }

    // I wish I could figure out a way to share code here -- one of those
    // places where the C macro preprocessor would really help.
    private void addWithYBreak(AffineTransform smat, GeneralPath line,
            double scale) {
        // Need to transalate the absolute positions into positions on the line
        double yOff = smat.getTranslateY();
        double ySize = sizeY.get() - (endY.get() + adjustmentY);
        if (scale < 0)
            ySize = -ySize;
        double yBeg = breakBeg.getY() - yOff;
        double yEnd = breakEnd.getY() - yOff;

        dumpValues(smat, yBeg, yEnd);

        addSegment(line, smat, 0, 0, yBeg / scale);
        addSegment(line, smat, yEnd / scale, 0, (ySize - yEnd) / scale);
    }

    private void dumpValues(AffineTransform smat, double beg, double end) {
        if (DEBUG) {
            System.out.println();
            NapkinUtil.printPair("translate", smat.getTranslateX(),
                    smat.getTranslateY());
            NapkinUtil.printPair("scale", smat.getScaleX(), smat.getScaleY());
            NapkinUtil.printPair("breakBeg", breakBeg.getX(), breakBeg.getY());
            NapkinUtil.printPair("breakEnd", breakEnd.getX(), breakEnd.getY());
            NapkinUtil.printPair("size", sizeX.get(), sizeY.get());
            NapkinUtil.printPair("adjustment", adjustmentX, adjustmentY);
            NapkinUtil.printPair("beg/end", begX.get(), endY.get());
            NapkinUtil.printPair("break beg/end", beg, end);
        }
    }

    private void addSegment(GeneralPath side, AffineTransform smat, double xBeg,
            double yBeg, double len) {

        if (DEBUG)
            NapkinUtil.printPair("addSeg (len " + len + ")", xBeg, yBeg);
        if (len > 0) {
            AffineTransform mat = NapkinUtil.copy(smat);
            mat.translate(xBeg, yBeg);
            mat.scale(len / LENGTH, 1);
            addLine(side, mat, toGenerator(defaultLineType(len)));
        }
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

    public RandomValue getBegX() {
        return begX;
    }

    public RandomValue getEndY() {
        return endY;
    }

    public RandomValue getSizeX() {
        return sizeX;
    }

    public RandomValue getSizeY() {
        return sizeY;
    }

    public RandomValue getStartAdjust() {
        return startAdjust;
    }

    public Shape getSide(int side) {
        return sides[side];
    }

    public void setBreak(int side, double begX, double begY, double endX,
            double endY) {
        breakSide = side;
        breakBeg.setLocation(begX, begY);
        breakEnd.setLocation(endX, endY);
    }

    public void setNoBreak() {
        breakSide = NO_SIDE;
    }

    public void setGenerator(int side, Class type) {
        gens[side] = toGenerator(type);
    }

    public Class getGenerator(int side) {
        return fromGenerator(gens[side]);
    }

    private static Class fromGenerator(DrawnShapeGenerator gen) {
        if (gen == null)
            return null;
        return gen.getClass();
    }

    private DrawnShapeGenerator toGenerator(Class type) {
        if (type == null)
            return null;
        DrawnShapeGenerator gen = (DrawnShapeGenerator) generators.get(type);
        if (gen == null)
            throw new IllegalArgumentException("Unknown type: " + type);
        return gen;
    }

    public DrawnQuadLineGenerator getQuadGenerator() {
        return (DrawnQuadLineGenerator) generators.get(
                DrawnQuadLineGenerator.class);
    }

    public DrawnCubicLineGenerator getCubicGenerator() {
        return (DrawnCubicLineGenerator) generators.get(
                DrawnCubicLineGenerator.class);
    }
}
