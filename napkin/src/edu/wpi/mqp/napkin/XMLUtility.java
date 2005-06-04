// $Id$

package edu.wpi.mqp.napkin;

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
public class XMLUtility {
    /**
     * @param c
     * @param name
     *
     * @return an XML representation of the color with the specified element
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
     * @param p
     * @param name
     *
     * @return an XML representation of the specified Point2D
     */
    public static Element pointToXML(Point2D p, String name) {
        DefaultJDOMFactory f = new DefaultJDOMFactory();
        Element ret = f.element(name);

        Element x = f.element("x");
        Element y = f.element("y");

        int xval = new Long(Math.round(p.getX())).intValue();
        int yval = new Long(Math.round(p.getY())).intValue();

        x.addContent(f.text(Integer.toString(xval)));
        y.addContent(f.text(Integer.toString(yval)));

        ret.addContent(x);
        ret.addContent(y);

        return ret;
    }
}
