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
     * @param c    A color to represent.
     * @param name The name of the color.
     *
     * @return An XML representation of the color with the specified element
     *         name
     */
    public static Element colorToXML(Color c, String name) {
        DefaultJDOMFactory f = new DefaultJDOMFactory();
        Element ret = f.element(name);

        Element red = f.element("r");
        Element green = f.element("g");
        Element blue = f.element("b");

        red.addContent(f.text(Integer.toString(c.getRed())));
        green.addContent(f.text(Integer.toString(c.getGreen())));
        blue.addContent(f.text(Integer.toString(c.getBlue())));

        ret.addContent(red);
        ret.addContent(green);
        ret.addContent(blue);

        return ret;
    }

    /**
     * @param p    Point to represent.
     * @param name The name of the point.
     *
     * @return An XML representation of the specified Point2D
     */
    public static Element pointToXML(Point2D p, String name) {
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
