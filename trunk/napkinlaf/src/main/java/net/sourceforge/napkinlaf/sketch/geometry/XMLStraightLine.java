package net.sourceforge.napkinlaf.sketch.geometry;

import net.sourceforge.napkinlaf.sketch.XMLUtility;
import org.jdom.DefaultJDOMFactory;
import org.jdom.Element;

import java.awt.geom.*;

/**
 * XMLStraightLine: A line which can represent itself as XML.
 *
 * @author Peter Goodspeed
 * @author Justin Crafford
 */
public class XMLStraightLine extends StraightLine implements XMLShape {
    public XMLStraightLine(Point2D p1, Point2D p2) {
        super(p1, p2);
    }

    /** {@inheritDoc} */
    @Override
    public Element produceXML() {
        DefaultJDOMFactory f = new DefaultJDOMFactory();
        Element ret = f.element("straightLine");

        ret.addContent(XMLUtility.pointToXML("start", getP1()));
        ret.addContent(XMLUtility.pointToXML("end", getP2()));

        return ret;
    }
}
