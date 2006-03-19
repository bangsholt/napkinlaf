// $Id$

package net.sourceforge.napkinlaf.sketch;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;

/**
 * The <tt>SketchedIcon</tt> class is an implmentation of the <tt>Icon</tt>
 * interface type. This class is responsible for providing the height and width
 * of the icon and painting the sketched image on screen using the given XML
 * template and sketching style.
 *
 * @author Justin Crafford
 * @author Peter Goodspeed
 */
public class SketchedIcon implements Icon {
    private final Template template;    // The template object to sketch
    private AbstractSketcher sketcher;    // The sketcher used to create icon's image
    private BufferedImage bimage; // The stored image of the final sketch
    private boolean isSketched;   // Has this icon's template has been sketched?

    /** Creates a new DrawnIcon with specified template and sketch style. */
    public SketchedIcon(Template template, AbstractSketcher sketcher) {
        this.template = template;
        this.sketcher = sketcher;
        isSketched = false;
    }

    /** Sets the current sketcher to <tt>sketchStyle</tt>. */
    public void setSketchStyle(AbstractSketcher sketchStyle) {
        sketcher = sketchStyle;
        isSketched = false;
    }

    /**
     * Set the sketched status of this icon. When the sketched status is false,
     * the next paint command will generate a new sketching according to the
     * sketcher. This mainly matters on non-deterministic underlying sketchers.
     */
    public void setSketched(boolean isSketched) {
        this.isSketched = isSketched;
    }

    /** @return The title of the underlying template. */
    public String getTemplateTitle() {
        return template.getTitle();
    }

    /** @return The internal description of the underlying template. */
    public String getTemplateDescription() {
        return template.getDescription();
    }

    /** {@inheritDoc} */
    public int getIconHeight() {
        return (int) template.getClippingBounds().getHeight();
    }

    /** {@inheritDoc} */
    public int getIconWidth() {
        return (int) template.getClippingBounds().getWidth();
    }

    /** {@inheritDoc} */
    public void paintIcon(Component c, Graphics g, int x, int y) {
        int width = template.getClippingBounds().width;
        int height = template.getClippingBounds().height;

        Graphics2D g2d = (Graphics2D) g.create(x, y, width, height);

        if (!isSketched) {
            bimage = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D imageGraphics = bimage.createGraphics();

            sketcher.sketch(template.clone(), imageGraphics);
            g2d.drawImage(bimage, 0, 0, width, height, null);
            isSketched = true;
        } else if (bimage != null) {
            g2d.drawImage(bimage, 0, 0, width, height, null);
        }

        g2d.dispose();
    }
}