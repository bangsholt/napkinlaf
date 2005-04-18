/*
 * Created on Nov 12, 2004 by peterg
 * XMLQuadLine.java in edu.wpi.mqp.napkin for MQP
 * 
 */
package edu.wpi.mqp.napkin.geometry;

import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;

import org.jdom.DefaultJDOMFactory;
import org.jdom.Element;

import edu.wpi.mqp.napkin.XMLUtility;

/**
 * XMLQuadLine: A QuadCurve which knows how to export itself to XML
 * 
 * @author Peter Goodspeed
 * @author Justin Crafford
 */
public class XMLQuadLine extends QuadLine implements XMLShape {

	/**
	 *  
	 */
	public XMLQuadLine() {
		super();
	}

	/**
	 * @param x1
	 * @param y1
	 * @param ctrlx
	 * @param ctrly
	 * @param x2
	 * @param y2
	 */
	public XMLQuadLine(double x1, double y1, double ctrlx, double ctrly, double x2,
			double y2) {
		super(x1, y1, ctrlx, ctrly, x2, y2);
	}

	/**
	 * @param p1
	 * @param ctrlpt
	 * @param p2
	 */
	public XMLQuadLine(Point2D p1, Point2D ctrlpt, Point2D p2) {
		super(p1.getX(), p1.getY(), ctrlpt.getX(), ctrlpt.getY(), p2.getX(), p2
				.getY());
	}

	/**
	 * Creates a QuadLine identical to the QuadCurve2D passed in
	 * 
	 * @param q
	 */
	public XMLQuadLine(QuadCurve2D q) {
		this(q.getP1(), q.getCtrlPt(), q.getP2());
	}
	/**
	 * @see edu.wpi.mqp.napkin.geometry.XMLShape#produceXML()
	 */
	public Element produceXML() {
		DefaultJDOMFactory f = new DefaultJDOMFactory();
		Element ret = f.element("quadLine");
		
		ret.addContent(XMLUtility.pointToXML(this.getP1(),"start"));
		ret.addContent(XMLUtility.pointToXML(this.getCtrlPt(),"control"));
		ret.addContent(XMLUtility.pointToXML(this.getP2(),"end"));
		
		return ret;
	}
}
