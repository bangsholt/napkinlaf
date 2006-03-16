// $Id: XMLShape.java 292 2006-03-06 14:15:37Z kcrca $

package net.sourceforge.napkinlaf.sketch.geometry;

import org.jdom.Element;

/**
 * @author Peter Goodspeed
 * @author Justin Crafford
 */
public interface XMLShape extends SketchShape {
    /** @return the XML representation of this Shape. */
    Element produceXML();
}
