// $Id$

package napkin;

import napkin.sketch.SketchedIcon;
import napkin.sketch.Template;
import napkin.sketch.TemplateReadException;

import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public class NapkinIconFactory implements NapkinConstants {
    private static final Map<String, Template> tmplMap =
            new HashMap<String, Template>();

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
            DrawnQuadLineGenerator gen = new DrawnQuadLineGenerator();
            gen.getCtl().getY().setMid(1);
            return gen;
        }

        /** @noinspection AssignmentToStaticFieldFromInstanceMethod */
        protected DrawnShapeGenerator createMarkGenerator() {
            if (checkGen == null)
                checkGen = new DrawnCheckGenerator(size - midInset);
            return checkGen;
        }

        protected int calcWidth() {
            RandomValue lx = checkGen.getLeftScale().getX();
            RandomValue mx = checkGen.getMidScale().getX();
            RandomValue rx = checkGen.getRightScale().getX();
            double l = mx.min() - lx.min();
            double r = mx.max() + rx.max();
            return (int) Math.round(size * (r - l));
        }

        protected int calcHeight() {
            RandomValue my = checkGen.getMidScale().getY();
            RandomValue ry = checkGen.getRightScale().getY();
            // the "2" is for the underline if it loops down a bit
            return (int) Math.round(size * (my.max() + ry.max()) + 2);
        }

        protected void doPaint(Graphics2D placeG, Graphics2D markG, int x,
                int y) {
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
        private final double scale;

        private static final int DEF_SIZE = 13;
        private static final double DEF_SCALE =
                (double) DEF_SIZE / NapkinConstants.LENGTH;
        private static final AffineTransform DEF_SCALE_MAT =
                NapkinUtil.scaleMat(DEF_SCALE);
        private static DrawnCircleGenerator circleGen;

        public RadioButtonIcon() {
            super(NapkinTheme.RADIO_COLOR, DEF_SCALE_MAT);
            scale = DEF_SCALE;
            init();
        }

        public RadioButtonIcon(int themeColor, double size) {
            super(themeColor,
                    NapkinUtil.scaleMat(size / NapkinConstants.LENGTH));
            scale = size / NapkinConstants.LENGTH;
            init();
        }

        /** @noinspection AssignmentToStaticFieldFromInstanceMethod */
        protected DrawnShapeGenerator createPlaceGenerator() {
            if (circleGen == null)
                circleGen = new DrawnCircleGenerator();
            return circleGen;
        }

        protected DrawnShapeGenerator createMarkGenerator() {
            DrawnCircleGenerator gen = new DrawnCircleGenerator(true);
            double skew = LENGTH / 3.0;
            RandomXY tl = gen.getTL();
            RandomXY br = gen.getBR();
            Point2D tlMid = tl.getMid();
            Point2D brMid = br.getMid();
            tl.setMid(tlMid.getX() + skew, tlMid.getY() + skew);
            br.setMid(brMid.getX() + skew, brMid.getY() + skew);
            return gen;
        }

        public int calcWidth() {
            double max = circleGen.getBR().max().getX();
            double min = circleGen.getBL().min().getX();
            return (int) Math.ceil(scale * (max - min));
        }

        public int calcHeight() {
            double max = circleGen.getBR().max().getY();
            double min = circleGen.getTR().min().getY();
            return (int) Math.ceil(scale * (max - min));
        }
    }

    public static class WarnIcon implements Icon {
        private final Icon arrow1;
        private final Icon arrow2;

        public WarnIcon() {
            arrow1 = createArrowIcon(NORTH, 25);
            arrow2 = createArrowIcon(NORTH, 25);
        }

        public int getIconHeight() {
            return arrow1.getIconHeight();
        }

        public int getIconWidth() {
            return arrow1.getIconWidth();
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            arrow1.paintIcon(c, g, x, y);
            arrow2.paintIcon(c, g, x, y);
            float midX = x + getIconWidth() / 2.0f;
            float midY = y + 2 * getIconHeight() / 3.0f;
            NapkinUtil.centerBoldText(c, (Graphics2D) g, midX, midY, 20, "!");
        }
    }

    public static class ErrorIcon implements Icon {
        private final Icon circle1;
        private final Icon circle2;
        private final Icon cross1;
        private final Icon cross2;

        private static final int CIRCLE_SIZE = 30;
        private static final int CROSS_SIZE = 20;
        private static final int CROSS_OFFSET = (CIRCLE_SIZE - CROSS_SIZE) / 2;

        public ErrorIcon() {
            circle1 = new RadioButtonIcon(NapkinTheme.PEN_COLOR, CIRCLE_SIZE);
            circle2 = new RadioButtonIcon(NapkinTheme.PEN_COLOR, CIRCLE_SIZE);
            cross1 = new XIcon(CROSS_SIZE);
            cross2 = new XIcon(CROSS_SIZE);
        }

        public int getIconHeight() {
            return circle1.getIconHeight();
        }

        public int getIconWidth() {
            return circle1.getIconWidth();
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            circle1.paintIcon(c, g, x, y);
            circle2.paintIcon(c, g, x, y);
            cross1.paintIcon(c, g, x + CROSS_OFFSET, y + CROSS_OFFSET);
            cross2.paintIcon(c, g, x + CROSS_OFFSET, y + CROSS_OFFSET);
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

        /** @param pointTowards One of NORTH, EAST, WEST, or SOUTH. */
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
            return c.isFocusOwner();
        }
    }

    public static class XIcon implements Icon {
        private final int size;
        private final DrawnBoxHolder mark;

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
            Graphics2D g = NapkinUtil
                    .lineGraphics(g1, NapkinConstants.CHECK_WIDTH);
            mark.shapeUpToDate(new Rectangle(x, y, size, size));
            g.translate(x, y);
            mark.draw(g);
            g.translate(-x, -y);
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

    public static Icon createXIcon(int size) {
        return new XIcon(size);
    }

    public static Icon createWarnIcon() {
        return new WarnIcon();
    }

    public static Icon createErrorIcon() {
        return new ErrorIcon();
    }

    /**
     * @param templatePath the path to an XML representation of a Template
     *
     * @return a new DrawnIcon in the Jot style representing the Template at the
     *         given path
     */
    public static Icon createSketchedIcon(String templatePath) {
        NapkinTheme theme = NapkinTheme.Manager.getCurrentTheme();
        Template template = getTemplate(templatePath);
        if (template == null)
            return createArrowIcon(WEST);   // just to have *something*
        else
            return new SketchedIcon(template, theme.getSketcher());
    }

    public static Template getTemplate(String templatePath) {
        Template template = tmplMap.get(templatePath);
        if (template == null) {
            String subpath = "resources/templates/" + templatePath + ".xml";
            InputStream in = NapkinIconFactory.class
                    .getResourceAsStream(subpath);
            if (in == null)
                throw new IllegalArgumentException(
                        "unknown template: " + subpath);

            try {
                template = Template.createFromXML(in);
                tmplMap.put(templatePath, template);
            } catch (TemplateReadException e) {
                e.printStackTrace();
                return null;
            }
        }
        return template;
    }
}
