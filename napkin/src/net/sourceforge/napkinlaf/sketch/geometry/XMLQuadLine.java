package net.sourceforge.napkinlaf.sketch.geometry;

import net.sourceforge.napkinlaf.sketch.XMLUtility;
import org.jdom.DefaultJDOMFactory;
import org.jdom.Element;

/**
 * XMLQuadLine: A QuadCurve which knows how to export itself to XML.
 *
 * @author Peter Goodspeed
 * @author Justin Crafford
 */
public class XMLQuadLine extends QuadLine implements XMLShape {
    public XMLQuadLine(double x1, double y1, double ctrlx, double ctrly,
            double x2, double y2) {
        super(x1, y1, ctrlx, ctrly, x2, y2);
    }

    /** {@inheritDoc} */
    public Element produceXML() {
        DefaultJDOMFactory f = new DefaultJDOMFactory();
        Element ret = f.element("quadLine");

        ret.addContent(XMLUtility.pointToXML(getP1(), "start"));
        ret.addContent(XMLUtility.pointToXML(getCtrlPt(), "control"));
        ret.addContent(XMLUtility.pointToXML(getP2(), "end"));

        return ret;
    }
}
