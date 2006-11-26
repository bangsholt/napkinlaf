package net.sourceforge.napkinlaf.sketch;

import net.sourceforge.napkinlaf.NapkinThemeColor;
import net.sourceforge.napkinlaf.sketch.geometry.SketchShape;
import net.sourceforge.napkinlaf.sketch.geometry.XMLShape;
import org.jdom.DefaultJDOMFactory;
import org.jdom.Element;

import java.awt.*;

/**
 * The <tt>TemplateItem</tt> class represents one component of a template. It
 * contains information about the geometry, colors, and line weight of this
 * component for use by the sketcher.
 *
 * @author Justin Crafford
 * @author Peter Goodspeed
 */
@SuppressWarnings({"CloneDoesntDeclareCloneNotSupportedException"})
public class TemplateItem implements Cloneable {
    private boolean drawStroke = true; // Is the shape's stroke is drawn?
    private boolean drawFill = true; // Is the shape's fill is drawn?
    private Color strokeColor; // Color of this object's boundary line
    private float strokeWeight = 1; // Thickness of this object's boundary line
    private Color fillColor; // Color of this object's internal fill
    private SketchShape shape; // Geometry that specifies this object's shape

    /**
     * Constructs a new TemplateItem with default values. The stroke will be
     * drawn in a line weight of one, and will not be filled.
     */
    public TemplateItem() {
        drawStroke = true;
        drawFill = false;
        strokeWeight = 1;
    }

    /**
     * @param c The component to sketch on, which is used for abstract colors
     *          such as "pen". No drawing is done here.
     *
     * @return The stroke color. If the stroke color was set to <tt>null</tt>
     *         the default color specified by the current theme is returned.
     */
    public Color getStrokeColor(Component c) {
        return TemplateColor.colorFor(strokeColor, c,
                NapkinThemeColor.PEN_COLOR);
    }

    /**
     * Set the stroke color. By default, the stroke color is <tt>null</tt>. This
     * has the effect that the default sketch color is that of the current
     * theme.
     *
     * @param strokeColor The new stroke color.
     */
    public void setStrokeColor(Color strokeColor) {
        this.strokeColor = strokeColor;
    }

    /** @return The stroke weight. */
    public float getStrokeWeight() {
        return strokeWeight;
    }

    /** @param strokeWeight The new stroke weight. */
    public void setStrokeWeight(float strokeWeight) {
        this.strokeWeight = strokeWeight;
    }

    /**
     * @param c The component to sketch on, which is used for abstract colors
     *          such as "pen". No drawing is done here.
     *
     * @return The fill color. If the fill color is <t,t>null</tt> the default
     *         color specified by the Napkin's current theme is returned.
     */
    public Color getFillColor(Component c) {
        return TemplateColor.colorFor(fillColor, c,
                NapkinThemeColor.HIGHLIGHT_COLOR);
    }

    /**
     * Set the fille color. By default, the fille color is <tt>null</tt>. This
     * has the effect that the default sketch color is that of the current
     * theme.
     *
     * @param fillColor The new fill color.
     */
    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    /** @return The shape. */
    public SketchShape getShape() {
        return shape;
    }

    /** @param shape The new shape. */
    public void setShape(SketchShape shape) {
        if (shape == null) {
            throw new NullPointerException("shape cannot be null");
        }
        this.shape = shape;
    }

    /** @return <tt>true</tt> if the shape should be filled. */
    public boolean isDrawFill() {
        return drawFill;
    }

    /** @param drawFill <tt>true</tt> if the shape should be filled. */
    public void setDrawFill(boolean drawFill) {
        this.drawFill = drawFill;
    }

    /** @return The draw stroke. */
    public boolean isDrawStroke() {
        return drawStroke;
    }

    /** @param drawStroke The new draw stroke. */
    public void setDrawStroke(boolean drawStroke) {
        this.drawStroke = drawStroke;
    }

    /**
     * @return An XML representation of the information contained in this
     *         template item.
     */
    public Element produceXML() {
        Element result = null;
        if (shape instanceof XMLShape) {
            DefaultJDOMFactory f = new DefaultJDOMFactory();

            result = f.element("templateItem");

            if (strokeColor != null) {
                result.addContent(XMLUtility
                        .colorToXML("strokeColor", strokeColor));
            }
            if (strokeWeight != 1) {
                Element t = f.element("strokeWeight");
                t.addContent(f.text(Float.toString(strokeWeight)));
                result.addContent(t);
            }
            if (fillColor != null) {
                result.addContent(XMLUtility.colorToXML("fillColor",
                        fillColor));
            }

            result.addContent(((XMLShape) shape).produceXML());
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public TemplateItem clone() {
        try {
            TemplateItem item = (TemplateItem) super.clone();
            item.shape = shape.clone();
            return item;
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("cannot clone?", e);
        }
    }
}
