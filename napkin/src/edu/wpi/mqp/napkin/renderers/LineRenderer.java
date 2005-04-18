/*
 * Created on Dec 2, 2004 by peterg LineRenderer.java in
 * edu.wpi.mqp.napkin.renderers for MQP
 */
package edu.wpi.mqp.napkin.renderers;

import edu.wpi.mqp.napkin.Renderer;
import edu.wpi.mqp.napkin.geometry.*;

/**
 * LineRenderer: Renders things as straight lines.
 * 
 * @author Peter Goodspeed
 * @author Justin Crafford
 */
public class LineRenderer extends Renderer {

	/**
	 * @see edu.wpi.mqp.napkin.Renderer#deformLine(edu.wpi.mqp.napkin.geometry.StraightLine)
	 */
	public UtilityShape deformLine(StraightLine l) {
		return l;
	}

	/**
	 * @see edu.wpi.mqp.napkin.Renderer#deformQuad(edu.wpi.mqp.napkin.geometry.QuadLine)
	 */
	public UtilityShape deformQuad(QuadLine q) {
		return formPath(q.transformToLine());
	}

	/**
	 * @see edu.wpi.mqp.napkin.Renderer#deformCubic(edu.wpi.mqp.napkin.geometry.CubicLine)
	 */
	public UtilityShape deformCubic(CubicLine c) {
		return formPath(c.transformToLine());
	}

	/**
	 * @see edu.wpi.mqp.napkin.Renderer#deformPath(edu.wpi.mqp.napkin.geometry.Path)
	 */
	public UtilityShape deformPath(Path p) {
		Path ret = new Path();
		UtilityShape[] elements = p.simplify();

		for (int i = 0; i < elements.length; ++i) {
			ret.append(elements[i].deform(this), false);
		}

		return ret;
	}
}