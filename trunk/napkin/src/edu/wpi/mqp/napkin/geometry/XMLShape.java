// $Id$

package edu.wpi.mqp.napkin.geometry;

import org.jdom.Element;

/**
 * @author Peter Goodspeed
 * @author Justin Crafford
 */
public interface XMLShape extends UtilityShape {
    /** @return the XML representation of this Shape */
    Element produceXML();
}
