package napkin;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class NapkinIconFactory implements NapkinConstants {
    static class CheckBoxIcon extends NapkinIcon {

        protected static final int SIZE = 13;
        protected static final int MID_INSET = 3;
        public static final Color MARK_COLOR = Color.green.darker();
        private static CheckGenerator checkGen;

        CheckBoxIcon() {
            super(MARK_COLOR, null);
            init();
        }

        ShapeGenerator createPlaceGenerator() {
            QuadGenerator placeGen = new QuadGenerator();
            placeGen.getCtlY().setMid(1);
            return placeGen;
        }

        ShapeGenerator createMarkGenerator() {
            return (checkGen = new CheckGenerator(SIZE - MID_INSET));
        }

        int calcWidth() {
            Value lx = checkGen.getLeftXScale();
            Value mx = checkGen.getMidXScale();
            Value rx = checkGen.getRightXScale();
            double l = mx.min() - lx.min();
            double r = mx.max() + rx.max();
            return (int) Math.round(SIZE * (r - l));
        }

        int calcHeight() {
            Value my = checkGen.getMidYScale();
            Value ry = checkGen.getRightYScale();
            // the "2" is for the underline if it loops down a bit
            return (int) Math.round(SIZE * (my.max() + ry.max()) + 2);
        }

        void doPaint(Graphics2D placeG, Graphics2D markG,
                     int x, int y) {

            FontMetrics fm = placeG.getFontMetrics();
            int ypos = y + fm.getAscent();
            placeG.translate(x, ypos);
            placeG.scale((double) SIZE / NapkinConstants.LENGTH, 1);
            placeG.draw(place);

            if (markG != null) {
                markG.translate(x, ypos - SIZE);
                markG.setColor(MARK_COLOR);
                markG.draw(mark);
            }
        }
    }

    static class RadioButtonIcon extends NapkinIcon {
        private static final int SIZE = 13;
        private static final double SCALE =
                (double) SIZE / NapkinConstants.LENGTH;
        private static final AffineTransform SCALE_MAT =
                NapkinUtil.scaleMat(SCALE);
        private static CircleGenerator placeGen;
        public static final Color MARK_COLOR = new Color(0xF50000);

        RadioButtonIcon() {
            super(MARK_COLOR, SCALE_MAT);
            init();
        }

        ShapeGenerator createPlaceGenerator() {
            return (placeGen = new CircleGenerator());
        }

        ShapeGenerator createMarkGenerator() {
            CircleGenerator markGen = new CircleGenerator(true);
            double skew = LENGTH / 3;
            Value tlX = markGen.getTlX();
            Value blX = markGen.getBlX();
            Value trX = markGen.getTrX();
            Value brX = markGen.getBrX();
            tlX.setMid(tlX.getMid() + skew);
            trX.setMid(trX.getMid() + skew);
            blX.setMid(blX.getMid() - skew);
            brX.setMid(brX.getMid() - skew);
            return markGen;
        }

        public int calcHeight() {
            double max = placeGen.getBrX().max();
            double min = placeGen.getBlX().min();
            return (int) Math.ceil(SCALE * (max - min));
        }

        public int calcWidth() {
            double max = placeGen.getBrY().max();
            double min = placeGen.getTrY().min();
            return (int) Math.ceil(SCALE * (max - min));
        }

        void doPaint(Graphics2D placeG, Graphics2D markG, int x,
                     int y) {
            if (markG != null) {
                markG.translate(x, y);
                markG.fill(mark);
            }

            placeG.translate(x, y);
            placeG.draw(place);
        }
    }

    static class ArrowIcon extends NapkinIcon {
        private final int genNum;
        private final int size;

        public static final int DEFAULT_SIZE = 10;
        private static final TriangleGenerator[] ARROW_GEN = {
            new TriangleGenerator(0),
            new TriangleGenerator(Math.PI / 2),
            new TriangleGenerator(Math.PI),
            new TriangleGenerator(-Math.PI / 2),
        };

        /**
         * @param pointTowards One of NORTH, EAST, WEST, or SOUTH.
         */
        ArrowIcon(int pointTowards, int size) {
            super(Color.black, NapkinUtil.scaleMat(size));
            genNum = pointTowards / 2;
            this.size = size;
            init();
        }

        ShapeGenerator createPlaceGenerator() {
            return ARROW_GEN[genNum];
        }

        ShapeGenerator createMarkGenerator() {
            return ARROW_GEN[genNum];
        }

        int calcHeight() {
            return size;
        }

        int calcWidth() {
            return size;
        }

        void doPaint(Graphics2D placeG, Graphics2D markG, int x,
                     int y) {
            if (markG != null) {
                markG.translate(x, y);
                markG.fill(mark);
            }

            placeG.translate(x, y);
            placeG.draw(place);
        }
    }

    private NapkinIconFactory() {
    }

    public static Icon createCheckBoxIcon() {
        return new CheckBoxIcon();
    }

    public static Icon createRadioButtonIcon() {
        return new RadioButtonIcon();
    }

    public static Icon createArrowIcon(int pointTowards) {
        return createArrowIcon(pointTowards, ArrowIcon.DEFAULT_SIZE);
    }

    public static Icon createArrowIcon(int pointTowards, int size) {
        return new ArrowIcon(pointTowards, size);
    }
}