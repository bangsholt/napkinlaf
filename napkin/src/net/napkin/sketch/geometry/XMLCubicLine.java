// $Id$

package napkin.sketch.geometry;

import napkin.sketch.XMLUtility;
import org.jdom.DefaultJDOMFactory;
import org.jdom.Element;

/**
 * XMLCubicLine: A CubicCurve which knows how to represent itself as XML.
 *
 * @author Peter Goodspeed
 * @author Justin Crafford
 */
public class XMLCubicLine extends CubicLine implements XMLShape {
    public XMLCubicLine(double x1, double y1, double ctlx1, double ctly1,
            double ctlx2, double ctly2, double x2, double y2) {
        super(x1, y1, ctlx1, ctly1, ctlx2, ctly2, x2, y2);
    }

    /** {@inheritDoc} */
    public Element produceXML() {
        DefaultJDOMFactory f = new DefaultJDOMFactory();
        Element ret = f.element("cubicLine");

        ret.addContent(XMLUtility.pointToXML(getP1(), "start"));
        ret.addContent(XMLUtility.pointToXML(getCtrlP1(), "control"));
        ret.addContent(XMLUtility.pointToXML(getCtrlP2(), "control"));
        ret.addContent(XMLUtility.pointToXML(getP2(), "end"));

        return ret;
    }
}
