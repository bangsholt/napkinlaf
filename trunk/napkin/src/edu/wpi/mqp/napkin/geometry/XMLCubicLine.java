/*
 * Created on Nov 12, 2004 by peterg
 * XMLCubicLine.java in edu.wpi.mqp.napkin for MQP
 */
package edu.wpi.mqp.napkin.geometry;

import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;

import org.jdom.DefaultJDOMFactory;
import org.jdom.Element;

import edu.wpi.mqp.napkin.XMLUtility;

/**
 * XMLCubicLine: A CubicCurve which knows how to represent itself
 * as XML
 * 
 * @author Peter Goodspeed
 * @author Justin Crafford
 */
public class XMLCubicLine extends CubicLine implements XMLShape {
	/**
	 * 
	 */
	public XMLCubicLine() {
		super();
	}
	
	/**
	 * Creates a new CubicLine identical to the CubicCurve2D passed into it
	 * 
	 * @param clone the CubicCurve2D to duplicate
	 */
	public XMLCubicLine(CubicCurve2D clone) {
		this(clone.getP1(),clone.getCtrlP1(),clone.getCtrlP2(),clone.getP2());
	}

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
	public XMLCubicLine(double x1, double y1, double ctlx1, double ctly1, double ctlx2,
			double ctly2, double x2, double y2) {
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
	
	
	/**
	 * @see edu.wpi.mqp.napkin.geometry.XMLShape#produceXML()
	 */
	public Element produceXML() {
		DefaultJDOMFactory f = new DefaultJDOMFactory();
		Element ret = f.element("cubicLine");
		
		ret.addContent(XMLUtility.pointToXML(this.getP1(),"start"));
		ret.addContent(XMLUtility.pointToXML(this.getCtrlP1(),"control"));
		ret.addContent(XMLUtility.pointToXML(this.getCtrlP2(),"control"));
		ret.addContent(XMLUtility.pointToXML(this.getP2(),"end"));
		
		return ret;
	}

}
