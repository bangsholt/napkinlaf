/*
 * Created on Dec 9, 2004 by peterg : 
 * XMLPath.java in edu.wpi.mqp.napkin.geometry for MQP
 * 
 */
package edu.wpi.mqp.napkin.geometry;

import java.awt.Shape;

import org.jdom.Element;

/**
 * XMLPath: A Path which knows how to write itself to XML
 * 
 * @author peterg
 */
public class XMLPath extends Path implements XMLShape {

	/**
	 * 
	 */
	public XMLPath() {
		super();
	}

	/**
	 * @param rule
	 */
	public XMLPath(int rule) {
		super(rule);
	}

	/**
	 * @param rule
	 * @param capacity
	 */
	public XMLPath(int rule, int capacity) {
		super(rule, capacity);
	}

	/**
	 * @param s
	 */
	public XMLPath(Shape s) {
		super(s);
	}

	/**
	 * @return an XML representation of this Element
	 */
	public Element produceXML() {
		//TODO: this should not be neglected
		return null;
	}
}
