package net.sourceforge.napkinlaf.shapes;

import static net.sourceforge.napkinlaf.util.NapkinConstants.*;
import net.sourceforge.napkinlaf.util.NapkinUtil;
import net.sourceforge.napkinlaf.util.RandomValue;
import net.sourceforge.napkinlaf.util.RandomValueSource;
import net.sourceforge.napkinlaf.util.RandomXY;

import java.awt.*;
import java.awt.geom.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings({"WeakerAccess"})
public class DrawnBoxGenerator extends AbstractDrawnGenerator {
    private final RandomXY corner;
    private double adjustmentX;
    private double adjustmentY;
    private final RandomValue startAdjust;
    private final RandomXY size;
    private int breakSide;
    private final Point2D breakBeg;
    private final Point2D breakEnd;
    private final Shape[] sides;
    private final AbstractDrawnGenerator[] gens;
    private final Map<Class<?>, AbstractDrawnGenerator> generators;
    private boolean asX;

    private static final Logger logger = Logger.getLogger(
            DrawnBoxGenerator.class.getName());

    private class SideSize extends RandomValue {
        private final int s1;
        private final int s2;

        SideSize(double val, int s1, int s2) {
            super(val);
            this.s1 = s1;
            this.s2 = s2;
            setSideType(val);
        }

        @Override
        public void setMid(double mid) {
            super.setMid(mid);
            setSideType(mid);
        }

        private void setSideType(double mid) {
            Class<?> type = defaultLineType(mid);
            setGenerator(s1, type);
            setGenerator(s2, type);
        }
    }

    public DrawnBoxGenerator() {
        this(DrawnCubicLineGenerator.INSTANCE, DrawnQuadLineGenerator.INSTANCE);
    }

    public DrawnBoxGenerator(DrawnCubicLineGenerator cubic,
            DrawnQuadLineGenerator quad) {

        generators = new HashMap<Class<?>, AbstractDrawnGenerator>(3);
        generators.put(DrawnCubicLineGenerator.class, cubic);
        generators.put(DrawnQuadLineGenerator.class, quad);

        // TOP ... RIGHT runs from 1 to 4
        sides = new Shape[5];
        gens = new AbstractDrawnGenerator[5];
        for (int i = 1; i < 5; i++) {
            setGenerator(i, DrawnCubicLineGenerator.class);
        }

        corner = new RandomXY(-1, 3, 0, 2.5);
        startAdjust = new RandomValue(5);
        size = new RandomXY(new SideSize(BASE_LINE_LENGTH, LEFT, RIGHT),
                new SideSize(BASE_LINE_LENGTH * 0.618, TOP, BOTTOM));
        breakSide = NO_SIDE;
        breakBeg = new Point2D.Double(0, 0);
        breakEnd = new Point2D.Double(0, 0);
    }

    @Override
    public Shape generate(AffineTransform matrix) {
        matrix = (matrix != null ? matrix : new AffineTransform());
        GeneralPath shape = new GeneralPath();
        double xSize = size.getX().generate();
        double ySize = size.getY().generate();
        double xScale = xSize / BASE_LINE_LENGTH;
        double yScale = ySize / BASE_LINE_LENGTH;
        double xCorner = adjustStartOffset(corner.getX(), xScale);
        double yCorner = adjustStartOffset(corner.getY(), yScale);

        adjustmentX = xCorner - corner.getX().get();
        adjustmentY = yCorner - corner.getY().get();
        if (asX) {
            NapkinUtil.drawStroke(shape, matrix, xCorner, 0, xSize, ySize,
                    Math.PI, gens[0]);
            NapkinUtil.drawStroke(shape, matrix, 0, ySize, xSize, yCorner, 0,
                    gens[0]);
            return shape;
        }
        double scale = (xSize - xCorner) / BASE_LINE_LENGTH;
        AffineTransform smat = (AffineTransform) matrix.clone();

        smat.translate(xCorner, 0);
        smat.scale(scale, 1);
        sides[TOP] = addSide(shape, smat, TOP, scale);
        scale = xScale;
        smat = (AffineTransform) matrix.clone();
        smat.translate(xSize, ySize);
        smat.rotate(Math.PI);
        smat.scale(scale, 1);
        sides[BOTTOM] = addSide(shape, smat, BOTTOM, scale);
        scale = yScale;
        smat = (AffineTransform) matrix.clone();
        smat.translate(xSize, 0);
        smat.rotate(Math.PI / 2);
        smat.scale(scale, 1);
        sides[RIGHT] = addSide(shape, smat, RIGHT, scale);
        scale = (ySize - yCorner) / BASE_LINE_LENGTH;
        smat = (AffineTransform) matrix.clone();
        smat.translate(0, ySize);
        smat.rotate(-Math.PI / 2);
        smat.scale(scale, 1);
        sides[LEFT] = addSide(shape, smat, LEFT, scale);
        return shape;
    }

    private Shape addSide(GeneralPath shape, AffineTransform smat, int side,
            double scale) {

        Shape result;
        if (side == breakSide) {
            GeneralPath line = new GeneralPath();
            if (side == BOTTOM || side == LEFT) {
                scale = -scale; // this ones are drawn backwards
            }
            if (side == TOP || side == BOTTOM) {
                addWithXBreak(smat, line, scale);
            } else {
                addWithYBreak(smat, line, scale);
            }

            shape.append(line, false);
            result = line;
        } else {
            result = addLine(shape, smat, gens[side]);
        }
        return result;
    }

    private void addWithXBreak(AffineTransform smat, GeneralPath line,
            double scale) {
        // Need to transalate the absolute positions into positions on the line
        double xOff = smat.getTranslateX();
        double xSize = size.getX().get() - (corner.getX().get() + adjustmentX);
        if (scale < 0) {
            xSize = -xSize;
        }
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
        double ySize = size.getY().get() - (corner.getY().get() + adjustmentY);
        if (scale < 0) {
            ySize = -ySize;
        }
        double yBeg = breakBeg.getY() - yOff;
        double yEnd = breakEnd.getY() - yOff;

        dumpValues(smat, yBeg, yEnd);

        addSegment(line, smat, 0, 0, yBeg / scale);
        addSegment(line, smat, yEnd / scale, 0, (ySize - yEnd) / scale);
    }

    @SuppressWarnings({"HardcodedFileSeparator"})
    private void dumpValues(AffineTransform smat, double beg, double end) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "");
            NapkinUtil.printPair(logger, Level.FINE, "translate",
                    smat.getTranslateX(), smat.getTranslateY());
            NapkinUtil.printPair(logger, Level.FINE, "scale", smat.getScaleX(),
                    smat.getScaleY());
            NapkinUtil.printPair(logger, Level.FINE, "breakBeg",
                    breakBeg.getX(), breakBeg.getY());
            NapkinUtil.printPair(logger, Level.FINE, "breakEnd",
                    breakEnd.getX(), breakEnd.getY());
            NapkinUtil.printPair(logger, Level.FINE, "size", size.getX().get(),
                    size.getY().get());
            NapkinUtil.printPair(logger, Level.FINE, "adjustment", adjustmentX,
                    adjustmentY);
            NapkinUtil.printPair(logger, Level.FINE, "beg/end",
                    corner.getX().get(), corner.getY().get());
            NapkinUtil.printPair(logger, Level.FINE, "break beg/end", beg, end);
        }
    }

    @SuppressWarnings({"SameParameterValue"})
    private void addSegment(GeneralPath side, AffineTransform smat, double xBeg,
            double yBeg, double len) {

        if (logger.isLoggable(Level.FINE)) {
            NapkinUtil.printPair(logger, Level.FINE, "addSeg (len " + len + ")",
                    xBeg, yBeg);
        }
        if (len > 0) {
            AffineTransform mat = (AffineTransform) smat.clone();
            mat.translate(xBeg, yBeg);
            mat.scale(len / BASE_LINE_LENGTH, 1);
            addLine(side, mat, toGenerator(defaultLineType(len)));
        }
    }

    private double adjustStartOffset(RandomValueSource off, double scale) {
        double result = off.generate();
        if (scale < 1.0) {
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

    public void setGenerator(int side, Class<?> type) {
        gens[side] = toGenerator(type);
    }

    public Class<? extends AbstractDrawnGenerator> getGenerator(int side) {
        return fromGenerator(gens[side]);
    }

    private static Class<? extends AbstractDrawnGenerator> fromGenerator(
            AbstractDrawnGenerator gen) {

        return gen == null ? null : gen.getClass();
    }

    private AbstractDrawnGenerator toGenerator(Class<?> type) {
        AbstractDrawnGenerator result = null;
        if (type != null) {
            result = generators.get(type);
            if (result == null) {
                throw new IllegalArgumentException("Unknown type: " + type);
            }
        }
        return result;
    }

    public DrawnQuadLineGenerator getQuadGenerator() {
        return (DrawnQuadLineGenerator) generators.get(
                DrawnQuadLineGenerator.class);
    }

    public DrawnCubicLineGenerator getCubicGenerator() {
        return (DrawnCubicLineGenerator) generators.get(
                DrawnCubicLineGenerator.class);
    }

    public void setAsX(boolean asX) {
        this.asX = asX;
    }

    public boolean isAsX() {
        return asX;
    }

    public RandomXY getSize() {
        return size;
    }

    public RandomXY getCorner() {
        return corner;
    }
}
