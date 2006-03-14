// $Id$

package napkin.util;

import napkin.NapkinTheme;
import napkin.NapkinThemeColor;
import napkin.shapes.AbstractDrawnGenerator;
import napkin.shapes.DrawnBoxGenerator;
import napkin.shapes.DrawnBoxHolder;
import napkin.shapes.DrawnCheckGenerator;
import napkin.shapes.DrawnCircleGenerator;
import napkin.shapes.DrawnQuadLineGenerator;
import napkin.shapes.DrawnTriangleGenerator;
import napkin.sketch.SketchedIcon;
import napkin.sketch.Template;
import napkin.sketch.TemplateReadException;
import static napkin.util.NapkinConstants.LENGTH;
import static napkin.util.NapkinConstants.RESOURCE_PATH;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class NapkinIconFactory {
    private static final Map<String, Template> tmplMap =
            new HashMap<String, Template>();

    public static class CheckBoxIcon extends AbstractNapkinIcon {
        protected static final double MID_INSET_RATIO = 3d / 13d;

        private final int size;
        private final int midInset;
        private DrawnCheckGenerator checkGen;

        public CheckBoxIcon(int size) {
            super(NapkinThemeColor.CHECK_COLOR, null);
            this.size = size;
            midInset = (int) (size * MID_INSET_RATIO + 0.5d);
            init();
        }

        protected AbstractDrawnGenerator createPlaceGenerator() {
            DrawnQuadLineGenerator gen = new DrawnQuadLineGenerator();
            gen.getCtl().getY().setMid(1);
            return gen;
        }

        /** @noinspection AssignmentToStaticFieldFromInstanceMethod */
        protected AbstractDrawnGenerator createMarkGenerator() {
            if (checkGen == null)
                checkGen = new DrawnCheckGenerator(size - midInset);
            return checkGen;
        }

        protected int calcWidth() {
            return (int) ((size - midInset) * checkGen.getMaxWidth() + 0.5d);
        }

        protected int calcHeight() {
            // the "2" is for the underline if it loops down a bit
            return (int) ((size - midInset) * checkGen.getMaxHeight() + 2.5d);
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

    public static class RadioButtonIcon extends AbstractNapkinIcon {
        private final double scale;

        private static final int DEF_SIZE = 13;
        private static final double DEF_SCALE =
                (double) DEF_SIZE / NapkinConstants.LENGTH;
        private static final AffineTransform DEF_SCALE_MAT =
                NapkinUtil.scaleMat(DEF_SCALE);
        private static DrawnCircleGenerator circleGen;

        public RadioButtonIcon() {
            super(NapkinThemeColor.RADIO_COLOR, DEF_SCALE_MAT);
            scale = DEF_SCALE;
            init();
        }

        /** @noinspection AssignmentToStaticFieldFromInstanceMethod */
        protected AbstractDrawnGenerator createPlaceGenerator() {
            if (circleGen == null)
                circleGen = new DrawnCircleGenerator();
            return circleGen;
        }

        protected AbstractDrawnGenerator createMarkGenerator() {
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

    public static class ArrowIcon extends AbstractNapkinIcon {
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
            super(NapkinThemeColor.CHECK_COLOR, NapkinUtil.scaleMat(size));
            genNum = pointTowards / 2;
            this.size = size;
            init();
        }

        protected AbstractDrawnGenerator createPlaceGenerator() {
            return ARROW_GEN[genNum];
        }

        protected AbstractDrawnGenerator createMarkGenerator() {
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
        return new CheckBoxIcon(13);
    }

    public static Icon createCheckedMenuItemIcon() {
        return new CheckBoxIcon(9);
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
            return createXIcon(20);   // just to have *something*
        else
            return new SketchedIcon(template, theme.getSketcher());
    }

    public static Template getTemplate(String templatePath) {
        Template template = tmplMap.get(templatePath);
        if (template == null) {
            String subpath =
                    RESOURCE_PATH + "templates/" + templatePath + ".xml";
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
