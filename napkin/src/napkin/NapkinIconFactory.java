package napkin;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

public class NapkinIconFactory implements NapkinConstants {
    static class CheckBoxIcon extends NapkinIcon {

        protected static final int SIZE = 13;
        protected static final int MID_INSET = 3;
        private static DrawnCheckGenerator checkGen;

        CheckBoxIcon() {
            super(NapkinTheme.CHECK_COLOR, null);
            init();
        }

        DrawnShapeGenerator createPlaceGenerator() {
            DrawnQuadLineGenerator placeGen = new DrawnQuadLineGenerator();
            placeGen.getCtlY().setMid(1);
            return placeGen;
        }

        DrawnShapeGenerator createMarkGenerator() {
            return (checkGen = new DrawnCheckGenerator(SIZE - MID_INSET));
        }

        int calcWidth() {
            RandomValue lx = checkGen.getLeftXScale();
            RandomValue mx = checkGen.getMidXScale();
            RandomValue rx = checkGen.getRightXScale();
            double l = mx.min() - lx.min();
            double r = mx.max() + rx.max();
            return (int) Math.round(SIZE * (r - l));
        }

        int calcHeight() {
            RandomValue my = checkGen.getMidYScale();
            RandomValue ry = checkGen.getRightYScale();
            // the "2" is for the underline if it loops down a bit
            return (int) Math.round(SIZE * (my.max() + ry.max()) + 2);
        }

        void doPaint(Graphics2D placeG, Graphics2D markG, int x, int y) {
            FontMetrics fm = placeG.getFontMetrics();
            int ypos = y + fm.getAscent();
            placeG.translate(x, ypos);
            placeG.scale((double) SIZE / NapkinConstants.LENGTH, 1);
            placeG.draw(place);

            if (markG != null) {
                markG.translate(x, ypos - SIZE);
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
        private static DrawnCircleGenerator placeGen;

        RadioButtonIcon() {
            super(NapkinTheme.RADIO_COLOR, SCALE_MAT);
            init();
        }

        DrawnShapeGenerator createPlaceGenerator() {
            return (placeGen = new DrawnCircleGenerator());
        }

        DrawnShapeGenerator createMarkGenerator() {
            DrawnCircleGenerator markGen = new DrawnCircleGenerator(true);
            double skew = LENGTH / 3;
            RandomValue tlX = markGen.getTlX();
            RandomValue blX = markGen.getBlX();
            RandomValue trX = markGen.getTrX();
            RandomValue brX = markGen.getBrX();
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
        private static final DrawnTriangleGenerator[] ARROW_GEN = {
            new DrawnTriangleGenerator(0),
            new DrawnTriangleGenerator(Math.PI / 2),
            new DrawnTriangleGenerator(Math.PI),
            new DrawnTriangleGenerator(-Math.PI / 2),
        };

        /**
         * @param pointTowards One of NORTH, EAST, WEST, or SOUTH.
         */
        ArrowIcon(int pointTowards, int size) {
            super(NapkinTheme.PEN_COLOR, NapkinUtil.scaleMat(size));
            genNum = pointTowards / 2;
            this.size = size;
            init();
        }

        DrawnShapeGenerator createPlaceGenerator() {
            return ARROW_GEN[genNum];
        }

        DrawnShapeGenerator createMarkGenerator() {
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