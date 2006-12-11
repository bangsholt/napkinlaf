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
