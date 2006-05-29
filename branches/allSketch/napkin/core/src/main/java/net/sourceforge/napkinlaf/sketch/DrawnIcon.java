package net.sourceforge.napkinlaf.sketch;

import net.sourceforge.napkinlaf.util.NapkinIcon;
import net.sourceforge.napkinlaf.util.NapkinUtil;

import java.awt.*;
import java.awt.image.*;

/**
 * The <tt>DrawnIcon</tt> class is an implmentation of the <tt>Icon</tt>
 * interface type. This class is responsible for providing the height and width
 * of the icon and painting the sketched image on screen using the given XML
 * template and sketching style.
 *
 * @author Justin Crafford
 * @author Peter Goodspeed
 */
public class DrawnIcon implements NapkinIcon {
    private final Template template;    // The template object to sketch
    private Sketcher sketcher;    // The sketcher used to create icon's image
    private BufferedImage image; // The stored image of the final sketch

    /** Creates a new DrawnIcon with specified template and sketch style. */
    public DrawnIcon(Template template, Sketcher sketcher) {
        this.template = template;
        this.sketcher = sketcher;
    }

    /** Sets the current sketcher to <tt>sketchStyle</tt>. */
    public void setSketchStyle(Sketcher sketchStyle) {
        sketcher = sketchStyle;
    }

    /**
     * Invalidate the current sketch so it will be redone next time it is
     * needed.
     */
    public void invalidate() {
        image = null;
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
        paint(g2d);
        g2d.dispose();
    }

    public void paint(Graphics2D g2d) {
        int width = template.getWidth();
        int height = template.getHeight();
        width += 6;
        height += 6;

        if (image == null) {
            image = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D ig = image.createGraphics();
            ig.translate(3, 3);
            NapkinUtil.copySettings(g2d, ig);
            sketcher.sketch(template, ig);
            ig.dispose();
        }

        g2d.drawImage(image, -3, -3, width, height, null);
    }
}
