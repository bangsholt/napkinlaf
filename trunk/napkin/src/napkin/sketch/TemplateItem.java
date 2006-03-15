// $Id$

package napkin.sketch;

import napkin.NapkinTheme;
import napkin.sketch.geometry.SketchShape;
import napkin.sketch.geometry.XMLShape;
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
public class TemplateItem implements Cloneable {
    private boolean drawStroke = true; // Is the shape's stroke is drawn?
    private boolean drawFill = true; // Is the shape's fill is drawn?
    private Color strokeColor; // Color of this object's boundary line
    private float strokeWeight = 1; // Thickness of this object's boundary line
    private Color fillColor; // Color of this object's internal fill
    private SketchShape shape; // Geometry that specifies this object's shape

    /** Constructs a new TemplateItem with default values. */
    public TemplateItem() {
        drawStroke = true;
        drawFill = false;
        strokeColor = null;
        strokeWeight = 1;
        fillColor = null;
        shape = null;
    }

    /**
     * @return Returns the strokeColor. If the stroke color was set to null the
     *         default color specified by the Napkin's current theme is
     *         returned.
     */
    public Color getStrokeColor() {
        if (strokeColor == null) {
            return NapkinTheme.Manager.getCurrentTheme().getPenColor();
        } else
            return strokeColor;
    }

    /**
     * Note that by default, the strokeColor is null. This has the effect that
     * the default sketch color is that of the Look and Feel. Setting a
     * strokeColor will override the Look and Feel's default stroke color.
     *
     * @param strokeColor The strokeColor to set.
     */
    public void setStrokeColor(Color strokeColor) {
        this.strokeColor = strokeColor;
    }

    /** @return Returns the strokeWeight. */
    public float getStrokeWeight() {
        return strokeWeight;
    }

    /** @param strokeWeight The strokeWeight to set. */
    public void setStrokeWeight(float strokeWeight) {
        this.strokeWeight = strokeWeight;
    }

    /**
     * @return Returns the fillColor. If the fill color was set to null the
     *         default color specified by the Napkin's current theme is
     *         returned.
     */
    public Color getFillColor() {
        if (fillColor == null)
            return NapkinTheme.Manager.getCurrentTheme().getHighlightColor();
        else
            return fillColor;
    }

    /**
     * Note that by default, the fillColor is null. This has the effect that the
     * default sketch color is that of the Look and Feel. Setting a fillColor
     * will override the Look and Feel's default fill color.
     *
     * @param fillColor The fillColor to set.
     */
    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    /** @return Returns the shape. */
    public SketchShape getShape() {
        return shape;
    }

    /** @param shape The shape to set. */
    public void setShape(SketchShape shape) {
        if (shape == null)
            throw new NullPointerException();
        this.shape = shape;
    }

    /** @return Returns the drawFill. */
    public boolean isDrawFill() {
        return drawFill;
    }

    /** @param drawFill The drawFill to set. */
    public void setDrawFill(boolean drawFill) {
        this.drawFill = drawFill;
    }

    /** @return Returns the drawStroke. */
    public boolean isDrawStroke() {
        return drawStroke;
    }

    /** @param drawStroke The drawStroke to set. */
    public void setDrawStroke(boolean drawStroke) {
        this.drawStroke = drawStroke;
    }

    /**
     * @return an XML representation of the information contained in this
     *         TemplateItem.
     */
    public Element produceXML() {
        if (shape instanceof XMLShape) {
            DefaultJDOMFactory f = new DefaultJDOMFactory();

            Element ret = f.element("templateItem");

            if (strokeColor != null) {
                ret.addContent(XMLUtility
                        .colorToXML(strokeColor, "strokeColor"));
            }
            if (strokeWeight != 1) {
                Element t = f.element("strokeWeight");
                t.addContent(f.text(Float.toString(strokeWeight)));
                ret.addContent(t);
            }
            if (fillColor != null) {
                ret.addContent(XMLUtility.colorToXML(fillColor, "fillColor"));
            }

            ret.addContent(((XMLShape) shape).produceXML());

            return ret;
        } else {
            return null;
        }
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public TemplateItem clone() {
        try {
            TemplateItem item = (TemplateItem) super.clone();
            item.drawFill = drawFill;
            item.drawStroke = drawStroke;
            item.fillColor = fillColor;
            item.shape = shape.clone();
            item.strokeColor = strokeColor;
            item.strokeWeight = strokeWeight;
            return item;
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("cannot clone?", e);
        }
    }
}