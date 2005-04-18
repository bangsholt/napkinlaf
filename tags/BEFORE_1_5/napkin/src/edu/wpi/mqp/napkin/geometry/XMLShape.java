/*
 * Created on Nov 11, 2004 by peterg
 * XMLShape.java in edu.wpi.mqp.napkin for MQP
 */
package edu.wpi.mqp.napkin.geometry;

import org.jdom.Element;

/**
 * @author Peter Goodspeed
 * @author Justin Crafford
 */
public interface XMLShape extends UtilityShape {
	/**
	 * @return the XML representation of this Shape
	 */
	public Element produceXML();
}