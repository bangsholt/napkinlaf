// $Id$

package napkin.sketch.geometry;

import napkin.sketch.XMLUtility;
import org.jdom.DefaultJDOMFactory;
import org.jdom.Element;

import java.awt.geom.*;

/**
 * XMLCubicLine: A CubicCurve which knows how to represent itself as XML
 *
 * @author Peter Goodspeed
 * @author Justin Crafford
 */
public class XMLCubicLine extends CubicLine implements XMLShape {
    /**
     * @param x1
     * @param y1
     * @param ctlx1
     * @param ctly1
     * @param ctlx2
     * @param ctly2
     * @param x2
     * @param y2
     */
    public XMLCubicLine(double x1, double y1, double ctlx1, double ctly1,
            double ctlx2, double ctly2, double x2, double y2) {
        super(x1, y1, ctlx1, ctly1, ctlx2, ctly2, x2, y2);
    }

    /**
     * @param p1
     * @param ctl1
     * @param ctl2
     * @param p2
     */
    public XMLCubicLine(Point2D p1, Point2D ctl1, Point2D ctl2, Point2D p2) {
        super(p1.getX(), p1.getY(), ctl1.getX(), ctl1.getY(), ctl2.getX(), ctl2
                .getY(), p2.getX(), p2.getY());
    }

    /** {@inheritDoc} */
    public Element produceXML() {
        DefaultJDOMFactory f = new DefaultJDOMFactory();
        Element ret = f.element("cubicLine");

        ret.addContent(napkin.sketch.XMLUtility.pointToXML(getP1(), "start"));
        ret.addContent(XMLUtility.pointToXML(getCtrlP1(), "control"));
        ret.addContent(XMLUtility.pointToXML(getCtrlP2(), "control"));
        ret.addContent(XMLUtility.pointToXML(getP2(), "end"));

        return ret;
    }
}
