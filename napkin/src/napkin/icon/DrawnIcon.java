// $Id$

package napkin.icon;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

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
    private final Template template;    // The template object to render
    private Renderer renderer;    // The renderer used to create icon's image
    private BufferedImage bimage; // The stored image of the final render
    private boolean isRendered;   // Has this icon's template has been rendered?

    /**
     * Creates a new DrawnIcon with specified template and render style
     *
     * @param template
     * @param renderer
     */
    public DrawnIcon(Template template, Renderer renderer) {
        this.template = template;
        this.renderer = renderer;
        isRendered = false;
    }

    /**
     * Sets the current renderer to <tt>renderStyle</tt>
     *
     * @param renderStyle
     */
    public void setRenderStyle(Renderer renderStyle) {
        renderer = renderStyle;
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

    /** @return the title of the underlying template */
    public String getTemplateTitle() {
        return template.getTitle();
    }

    /** @return the internal description of the underlying template. */
    public String getTemplateDescription() {
        return template.getDescription();
    }

    /** @see Icon#getIconHeight() */
    public int getIconHeight() {
        return (int) template.getClippingBounds().getHeight();
    }

    /** @see Icon#getIconWidth() */
    public int getIconWidth() {
        return (int) template.getClippingBounds().getWidth();
    }

    /** @see Icon#paintIcon(Component, Graphics, int, int) */
    public void paintIcon(Component c, Graphics g, int x, int y) {
        int width = template.getClippingBounds().width;
        int height = template.getClippingBounds().height;

        Graphics2D g2d = (Graphics2D) g.create(x, y, width, height);

        if (!isRendered) {
            bimage = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_ARGB);
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