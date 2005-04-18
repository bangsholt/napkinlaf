/*
 * Created on Nov 12, 2004 by peterg
 * XMLStraightLine.java in edu.wpi.mqp.napkin for MQP
 * 
 */
package edu.wpi.mqp.napkin.geometry;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import org.jdom.DefaultJDOMFactory;
import org.jdom.Element;

import edu.wpi.mqp.napkin.XMLUtility;

/**
 * XMLStraightLine: A line which can represent itself as XML
 * 
 * @author Peter Goodspeed
 * @author Justin Crafford
 */
public class XMLStraightLine extends StraightLine implements XMLShape {
	/**
	 * 
	 */
	public XMLStraightLine() {
		super();
	}

	/**
	 * @param l
	 */
	public XMLStraightLine(Line2D l) {
		super(l.getP1(), l.getP2());
	}

	/**
	 * @param p1
	 * @param p2
	 */
	public XMLStraightLine(Point2D p1, Point2D p2) {
		super(p1, p2);
	}

	/**
	 * Constructs a new StraightLine given a start point, an angle, and a length
	 * 
	 * @param start
	 *           a point
	 * @param angle
	 *           an angle in radians
	 * @param length
	 *           a length
	 */
	public XMLStraightLine(Point2D start, double angle, double length) {
		super(start, new Point2D.Double(start.getX() + (length * Math.cos(angle)),
				start.getY() + (length * Math.sin(angle))));
	}

	/**
	 * @see edu.wpi.mqp.napkin.geometry.XMLShape#produceXML()
	 */
	public Element produceXML() {
		DefaultJDOMFactory f = new DefaultJDOMFactory();
		Element ret = f.element("straightLine");
		
		ret.addContent(XMLUtility.pointToXML(this.getP1(),"start"));
		ret.addContent(XMLUtility.pointToXML(this.getP2(),"end"));
		
		return ret;
	}
}
