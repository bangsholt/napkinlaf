// $Id$

package napkin.sketch.geometry;

import org.jdom.DefaultJDOMFactory;
import org.jdom.Element;

import java.awt.geom.*;

/**
 * XMLStraightLine: A line which can represent itself as XML
 *
 * @author Peter Goodspeed
 * @author Justin Crafford
 */
public class XMLStraightLine extends StraightLine implements XMLShape {
    /**
     * @param p1
     * @param p2
     */
    public XMLStraightLine(Point2D p1, Point2D p2) {
        super(p1, p2);
    }

    /** @see XMLShape#produceXML() */
    public Element produceXML() {
        DefaultJDOMFactory f = new DefaultJDOMFactory();
        Element ret = f.element("straightLine");

        ret.addContent(napkin.sketch.XMLUtility.pointToXML(getP1(), "start"));
        ret.addContent(napkin.sketch.XMLUtility.pointToXML(getP2(), "end"));

        return ret;
    }
}
