// $Id$

package napkin;

import java.awt.*;
import java.awt.geom.*;
import java.util.HashMap;
import java.util.Map;

public class BoxGenerator extends ShapeGenerator {

    private final Value begX;
    private final Value endY;
    private final Value startAdjust;
    private final Value sizeX;
    private final Value sizeY;
    private final Shape[] sides;
    private final ShapeGenerator[] gens;

    private final Map generators;

    public static final int TOP_SIDE = 0;
    public static final int RIGHT_SIDE = 1;
    public static final int LEFT_SIDE = 2;
    public static final int BOTTOM_SIDE = 3;

    public static final String[] SIDE_NAMES = {
        "top", "right", "left", "bottom"
    };

    public static final BoxGenerator INSTANCE = new BoxGenerator();

    private class SideSize extends Value {
        private final int s1;
        private final int s2;

        public SideSize(double val, int s1, int s2) {
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
            Class type = defaultLineGenerator(mid);
            setGenerator(s1, type);
            setGenerator(s2, type);
        }
    }

    public BoxGenerator() {
        this(CubicGenerator.INSTANCE, QuadGenerator.INSTANCE);
    }

    public BoxGenerator(CubicGenerator cubic, QuadGenerator quad) {
        generators = new HashMap(3);
        generators.put(CubicGenerator.class, cubic);
        generators.put(QuadGenerator.class, quad);

        sides = new Shape[4];
        gens = new ShapeGenerator[4];
        for (int i = 0; i < 4; i++)
            setGenerator(i, CubicGenerator.class);

        begX = new Value(-1, 3);
        endY = new Value(0, 2.5);
        startAdjust = new Value(5);
        sizeX = new SideSize(LENGTH, LEFT_SIDE, RIGHT_SIDE);
        sizeY = new SideSize(LENGTH * 0.618, TOP_SIDE, BOTTOM_SIDE);
    }

    public Shape generate(AffineTransform matrix) {
        GeneralPath shape = new GeneralPath();

        double xSize = sizeX.generate();
        double ySize = sizeY.generate();
        double xScale = xSize / LENGTH;
        double yScale = ySize / LENGTH;

        double xBeg = adjustStartOffset(begX, xScale);
        double yEnd = adjustStartOffset(endY, yScale);

        AffineTransform smat;

        smat = NapkinUtil.copy(matrix);
        smat.translate(xBeg, 0);
        smat.scale((xSize - xBeg) / LENGTH, 1);
        sides[TOP_SIDE] = addLine(shape, smat, gens[TOP_SIDE]);

        smat = NapkinUtil.copy(matrix);
        smat.translate(xSize, 0);
        smat.rotate(Math.PI / 2);
        smat.scale(yScale, 1);
        sides[RIGHT_SIDE] = addLine(shape, smat, gens[RIGHT_SIDE]);

        smat = NapkinUtil.copy(matrix);
        smat.translate(0, ySize);
        smat.rotate(-Math.PI / 2);
        smat.scale((ySize - yEnd) / LENGTH, 1);
        sides[LEFT_SIDE] = addLine(shape, smat, gens[LEFT_SIDE]);

        smat = NapkinUtil.copy(matrix);
        smat.translate(xSize, ySize);
        smat.rotate(Math.PI);
        smat.scale(xScale, 1);
        sides[BOTTOM_SIDE] = addLine(shape, smat, gens[BOTTOM_SIDE]);

        return shape;
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

    public Value getBegX() {
        return begX;
    }

    public Value getEndY() {
        return endY;
    }

    public Value getSizeX() {
        return sizeX;
    }

    public Value getSizeY() {
        return sizeY;
    }

    public Value getStartAdjust() {
        return startAdjust;
    }

    public Shape getSide(int side) {
        return sides[side];
    }

    public void setGenerator(int side, Class type) {
        gens[side] = toGenerator(type);
    }

    public Class getGenerator(int side) {
        return fromGenerator(gens[side]);
    }

    private static Class fromGenerator(ShapeGenerator gen) {
        if (gen == null)
            return null;
        return gen.getClass();
    }

    private ShapeGenerator toGenerator(Class type) {
        if (type == null)
            return null;
        ShapeGenerator gen = (ShapeGenerator) generators.get(type);
        if (gen == null)
            throw new IllegalArgumentException("Unknown type: " + type);
        return gen;
    }

    public QuadGenerator getQuadGenerator() {
        return (QuadGenerator) generators.get(QuadGenerator.class);
    }

    public CubicGenerator getCubicGenerator() {
        return (CubicGenerator) generators.get(CubicGenerator.class);
    }
}

