package net.sourceforge.napkinlaf.sketch;

import org.jdom.DefaultJDOMFactory;
import org.jdom.Element;

import java.awt.*;
import java.awt.geom.*;

/**
 * The <tt>XMLUtility</tt> class provides some utility methods for dealing with
 * XML information.
 *
 * @author Peter Goodspeed
 * @author Justin Crafford
 */
@SuppressWarnings({"StaticMethodOnlyUsedInOneClass"})
public class XMLUtility {

    private XMLUtility() {
    }

    /**
     * @param name The name of the color.
     * @param c    A color to represent.
     *
     * @return An XML representation of the color with the specified element
     *         name
     */
    public static Element colorToXML(String name, Color c) {
        DefaultJDOMFactory f = new DefaultJDOMFactory();
        Element ret = f.element(name);

        String colorName = TemplateColor.nameFor(c);
        if (colorName != null) {
            Element which = f.element("which");
            which.addContent(f.text(colorName));
            ret.addContent(which);
        } else {
            Element red = f.element("r");
            Element green = f.element("g");
            Element blue = f.element("b");

            red.addContent(f.text(Integer.toString(c.getRed())));
            green.addContent(f.text(Integer.toString(c.getGreen())));
            blue.addContent(f.text(Integer.toString(c.getBlue())));

            ret.addContent(red);
            ret.addContent(green);
            ret.addContent(blue);
        }
        return ret;
    }

    /**
     * @param name The name of the point.
     * @param p    Point to represent.
     *
     * @return An XML representation of the specified Point2D
     */
    public static Element pointToXML(String name, Point2D p) {
        DefaultJDOMFactory f = new DefaultJDOMFactory();
        Element ret = f.element(name);

        Element x = f.element("x");
        Element y = f.element("y");

        int xval = (int) Math.round(p.getX());
        int yval = (int) Math.round(p.getY());

        x.addContent(f.text(Integer.toString(xval)));
        y.addContent(f.text(Integer.toString(yval)));

        ret.addContent(x);
        ret.addContent(y);

        return ret;
    }
}
