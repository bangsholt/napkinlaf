// $Id$

package napkin;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

public class NapkinIconFactory implements NapkinConstants {
    public static class CheckBoxIcon extends NapkinIcon {
        private final int size;
        private final int midInset;

        protected static final int SIZE = 13;
        protected static final int MID_INSET = 3;

        private static DrawnCheckGenerator checkGen;

        public CheckBoxIcon() {
            this(SIZE);
        }

        public CheckBoxIcon(int size) {
            super(NapkinTheme.CHECK_COLOR, null);
            this.size = size;
            midInset = size * Math.round(MID_INSET / (float) SIZE);
            init();
        }

        protected DrawnShapeGenerator createPlaceGenerator() {
            DrawnQuadLineGenerator placeGen = new DrawnQuadLineGenerator();
            placeGen.getCtlY().setMid(1);
            return placeGen;
        }

        protected DrawnShapeGenerator createMarkGenerator() {
            return (checkGen = new DrawnCheckGenerator(size - midInset));
        }

        protected int calcWidth() {
            RandomValue lx = checkGen.getLeftXScale();
            RandomValue mx = checkGen.getMidXScale();
            RandomValue rx = checkGen.getRightXScale();
            double l = mx.min() - lx.min();
            double r = mx.max() + rx.max();
            return (int) Math.round(size * (r - l));
        }

        protected int calcHeight() {
            RandomValue my = checkGen.getMidYScale();
            RandomValue ry = checkGen.getRightYScale();
            // the "2" is for the underline if it loops down a bit
            return (int) Math.round(size * (my.max() + ry.max()) + 2);
        }

        protected void
                doPaint(Graphics2D placeG, Graphics2D markG, int x, int y) {
            FontMetrics fm = placeG.getFontMetrics();
            int ypos = y + fm.getAscent();
            placeG.translate(x, ypos);
            placeG.scale((double) size / NapkinConstants.LENGTH, 1);
            placeG.draw(place);

            if (markG != null) {
                markG.translate(x, ypos - size);
                markG.draw(mark);
            }
        }
    }

    public static class RadioButtonIcon extends NapkinIcon {
        private static final int SIZE = 13;
        private static final double SCALE =
                (double) SIZE / NapkinConstants.LENGTH;
        private static final AffineTransform SCALE_MAT =
                NapkinUtil.scaleMat(SCALE);
        private static DrawnCircleGenerator placeGen;

        public RadioButtonIcon() {
            super(NapkinTheme.RADIO_COLOR, SCALE_MAT);
            init();
        }

        protected DrawnShapeGenerator createPlaceGenerator() {
            return (placeGen = new DrawnCircleGenerator());
        }

        protected DrawnShapeGenerator createMarkGenerator() {
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
    }

    public static class ArrowIcon extends NapkinIcon {
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
        public ArrowIcon(int pointTowards, int size) {
            super(NapkinTheme.CHECK_COLOR, NapkinUtil.scaleMat(size));
            genNum = pointTowards / 2;
            this.size = size;
            init();
        }

        protected DrawnShapeGenerator createPlaceGenerator() {
            return ARROW_GEN[genNum];
        }

        protected DrawnShapeGenerator createMarkGenerator() {
            return ARROW_GEN[genNum];
        }

        protected int calcHeight() {
            return size;
        }

        protected int calcWidth() {
            return size;
        }

        protected boolean shouldUseMark(Component c) {
            if (super.shouldUseMark(c))
                return true;
            if (c.isFocusOwner())
                return true;
            return false;
        }
    }

    public static class XIcon implements Icon {
        private final int size;
        private final DrawnBoxHolder mark;

        protected static final int SIZE = 15;
        protected static final int MID_INSET = 3;

        public XIcon() {
            this(SIZE);
        }

        public XIcon(int size) {
            DrawnBoxGenerator box = new DrawnBoxGenerator();
            box.setAsX(true);
            mark = new DrawnBoxHolder(box);
            this.size = size;
        }

        public int getIconHeight() {
            return size;
        }

        public int getIconWidth() {
            return size;
        }

        public void paintIcon(Component c, Graphics g1, int x, int y) {
            Graphics2D g =
                    NapkinUtil.lineGraphics(g1, NapkinConstants.CHECK_WIDTH);
            mark.shapeUpToDate(new Rectangle(x, y, size, size));
            mark.draw(g);
        }
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

    public static Icon createUnderlineIcon(int size) {
        return new CheckBoxIcon(size);
    }

    public static Icon createXIcon(int size) {
        return new XIcon(size);
    }
}
