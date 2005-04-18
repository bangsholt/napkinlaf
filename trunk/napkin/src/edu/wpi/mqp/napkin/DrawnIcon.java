/*
 * Created on Nov 17, 2004 by justin DrawnIcon.java in edu.wpi.mqp.napkin for
 * MQP
 */
package edu.wpi.mqp.napkin;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.*;

import javax.swing.Icon;

import edu.wpi.mqp.napkin.renderers.IdealRenderer;

/**
 * The <tt>DrawnIcon</tt> class is an implmentation of the <tt>Icon</tt>
 * interface type. This class is responsible for providing the height and width
 * of the icon and painting the rendered image on screen using the given XML
 * template and rendering style.
 * 
 * @author Justin Crafford
 * @author Peter Goodspeed
 */
public class DrawnIcon implements Icon {

	private Template template; // The template object to render
	private Renderer renderer; // The renderer to use to create the icon's image
	private BufferedImage bimage; // The stored image of the final render
	private boolean isRendered; // Determines whether this icon's template has

	// been rendered

	/**
	 * Constructs a new <tt>DrawnIcon</tt> using the provided template and
	 * rendering style
	 * 
	 * @param path
	 *           The path on the local filesystem to the XML template document
	 * @param renderStyle
	 *           The renderer to use to create the icon's image
	 * @throws TemplateReadException
	 * @deprecated This is not the place to be loading the XML files
	 */
	public DrawnIcon(String path, Renderer renderStyle) throws TemplateReadException {
		this(Template.produceFromXMLDocument(path));
		this.renderer = renderStyle;
	}
	
	/**
	 * Creates a new DrawnIcon with Ideal render style. Typical syntax
	 * will resemble the following:<br />
	 * <code>new DrawnIcon(Template.produceFromXMLDocument(path));</code>
	 * 
	 * @param template The Template to draw
	 * @see Template#produceFromXMLDocument(String)
	 * @see TemplateReadException
	 */
	public DrawnIcon(Template template) {
		this.template = template;
		this.renderer = new IdealRenderer();
		isRendered = false;
	}
	
	/**
	 * Creates a new DrawnIcon with specified template and render style 
	 * @param template
	 * @param renderer
	 * @see DrawnIcon#DrawnIcon(Template)
	 */
	public DrawnIcon(Template template, Renderer renderer) {
		this(template);
		this.renderer = renderer;
	}

	/**
	 * Sets the current renderer to <tt>renderStyle</tt>
	 * 
	 * @param renderStyle
	 */
	public void setRenderStyle(Renderer renderStyle) {
		this.renderer = renderStyle;
		isRendered = false;
	}

	/**
	 * Set the rendered status of this icon. When the rendered status is false,
	 * the next paint command will generate a new rendering according to the
	 * renderer. This mainly matters on non-deterministic underlying renderers.
	 * 
	 * @param isRendered
	 */
	public void setRendered(boolean isRendered) {
		this.isRendered = isRendered;
	}

	/**
	 * @return the title of the underlying template
	 */
	public String getTemplateTitle() {
		return template.getTitle();
	}

	/**
	 * @return the internal description of the underlying template.
	 */
	public String getTemplateDescription() {
		return template.getDescription();
	}

	/**
	 * @see javax.swing.Icon#getIconHeight()
	 */
	public int getIconHeight() {
		return (int) template.getClippingBounds().getHeight();
	}

	/**
	 * @see javax.swing.Icon#getIconWidth()
	 */
	public int getIconWidth() {
		return (int) template.getClippingBounds().getWidth();
	}

	/**
	 * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics,
	 *      int, int)
	 */
	public void paintIcon(Component c, Graphics g, int x, int y) {
		int width = template.getClippingBounds().width;
		int height = template.getClippingBounds().height;

		Graphics2D g2d = (Graphics2D) g.create(x, y, width, height);

		if (!isRendered) {
			bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D imageGraphics = bimage.createGraphics();

			renderer.render((Template) template.clone(), imageGraphics);
			g2d.drawImage(bimage, 0, 0, width, height, null);
			isRendered = true;
		} else if (bimage != null) {
			g2d.drawImage(bimage, 0, 0, width, height, null);
		}

		g2d.dispose();
	}
}