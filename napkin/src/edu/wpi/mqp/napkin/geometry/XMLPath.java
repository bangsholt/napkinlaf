// $Id$

package edu.wpi.mqp.napkin.geometry;

import org.jdom.Element;

import java.awt.*;

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

    /** @param rule  */
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

    /** @param s  */
    public XMLPath(Shape s) {
        super(s);
    }

    /** @return an XML representation of this Element */
    public Element produceXML() {
        //TODO: this should not be neglected
        return null;
    }
}
