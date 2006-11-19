package net.sourceforge.napkinlaf.sketch;

import net.sourceforge.napkinlaf.util.NapkinUtil;
import org.jdom.DefaultJDOMFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * The <tt>Template</tt> class represents an "ideal" depiction of an image by
 * maintaining a list of graphical components. The components are used, in
 * conjuction with this class's other attributes, to specify what is to be drawn
 * by the sketcher.
 *
 * @author Justin Crafford
 * @author Peter Goodspeed
 */
@SuppressWarnings(
        {"CloneDoesntDeclareCloneNotSupportedException", "WeakerAccess", "InstanceMethodNamingConvention"})
public class Template implements Cloneable {
    private String title; // The title of the template
    private String description; // A description of the template
    private Rectangle clippingBounds; // The area that defines which portion of the image to draw
    private int width; // The width of the image with all the template components combined
    private int height; // the height of the image with all the template components combined
    @SuppressWarnings("CollectionDeclaredAsConcreteClass")
    private LinkedList<TemplateItem> templateItems; // A list of all template components

    /**
     * Constructs a new template with the given values.
     *
     * @param origin     The upper-left corner of the rectangle specifying the
     *                   clipping bounds
     * @param dimensions The width and height of the rectangle specifying the
     */
    public Template(Point origin, Dimension dimensions) {
        title = "";
        description = "";
        templateItems = new LinkedList<TemplateItem>();
        clippingBounds = new Rectangle(origin, dimensions);
    }

    /** Constructs a new Template with default values. */
    public Template() {
        this(new Point(), new Dimension());
    }

    /**
     * Reads an XML file located at the path indicated, and returns a {@link
     * Template} object that instantiates the object represented in that
     * document, so long as the document is in fact a valid XML document
     * according to the schema <tt>net.sourceforge.napkinlaf.resources.Template.xsd</tt>.
     *
     * @param path The path of the file to read.
     *
     * @return A {@link Template} described by the XML document located at
     *         <tt>path</tt>.
     *
     * @throws TemplateReadException The template file has an error.
     * @throws IOException           A problem with the file.
     * @see Template#produceXMLString()
     */
    public static Template createFromXML(String path)
            throws TemplateReadException, IOException {

        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(path));
            return createFromXML(in);
        } finally {
            NapkinUtil.tryClose(in);
        }
    }

    /**
     * Reads an XML from the given input stream, and returns a {@link Template}
     * object that instantiates the object represented in that document, so long
     * as the document is in fact a valid XML document according to the schema
     * <tt>net.sourceforge.napkinlaf.resources.Template.xsd</tt>.
     *
     * @param in The stream to read from.
     *
     * @return a Template described by the XML document located at
     *         <tt>path</tt>.
     *
     * @throws TemplateReadException The template file has an error.
     * @see Template#produceXMLString()
     */
    public static Template createFromXML(InputStream in)
            throws TemplateReadException {
        return new XMLTemplateExtractor().createTemplate(in);
    }

    /**
     * Adds a template component to the template.
     *
     * @param templateItem The template component to be added.
     */
    public void add(TemplateItem templateItem) {
        templateItems.add(templateItem);
        computeWidthAndHeight();
    }

    /**
     * Computes the width and height of a template using the boundaries of the
     * template items' shapes.
     */
    public void computeWidthAndHeight() {
        int minx = 0;
        int maxx = 0;
        int miny = 0;
        int maxy = 0;

        for (TemplateItem t : templateItems) {
            Shape s = t.getShape();

            double tminx = s.getBounds2D().getMinX();
            double tmaxx = s.getBounds2D().getMaxX();
            double tminy = s.getBounds2D().getMinY();
            double tmaxy = s.getBounds2D().getMaxY();

            if (tminx < minx) {
                minx = (int) Math.floor(tminx);
            }
            if (tminy < miny) {
                miny = (int) Math.floor(tminy);
            }
            if (tmaxx > maxx) {
                maxx = (int) Math.ceil(tmaxx);
            }
            if (tmaxy > maxy) {
                maxy = (int) Math.ceil(tmaxy);
            }
        }

        width = maxx - minx;
        height = maxy - miny;
    }

    /**
     * Gets a list-iterator of the template-items in this list, starting at the
     * first element.
     *
     * @return a listIterator of the elements in this list.
     */
    public ListIterator<TemplateItem> getListIterator() {
        return templateItems.listIterator();
    }

    /** @return Returns the title. */
    public String getTitle() {
        return title;
    }

    /** @param title The title to set. */
    public void setTitle(String title) {
        this.title = title;
    }

    /** @return Returns the description. */
    public String getDescription() {
        return description;
    }

    /** @param description The description to set. */
    public void setDescription(String description) {
        this.description = description;
    }

    /** @return Returns the width. */
    public int getWidth() {
        return width;
    }

    /** @return Returns the height. */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the field outside of which items should not be sketched. Note
     * that a null clip mask is allowable and indicates that nothing should be
     * clipped.
     *
     * @return Returns the clipping bounds.
     */
    public Rectangle getClippingBounds() {
        return clippingBounds;
    }

    /** @param clippingBounds The clipping bounds to set. */
    public void setClippingBounds(Rectangle clippingBounds) {
        this.clippingBounds = clippingBounds;
    }

    /**
     * Produces a String which contains a pretty-printed XML representation of
     * this Template. This is a bit too computationally intensive to be used to
     * replace serialization for interprocess communication or transactions
     * which occur at machine-speed; it is more for long-term storage of
     * Templates in files which a user might edit manually.
     *
     * @return a String containing the pretty-printed textual XML document
     *         representing this Template
     *
     * @see Template#createFromXML(String)
     */
    public String produceXMLString() {
        StringWriter stringWriter = new StringWriter();
        try {
            new XMLOutputter(Format.getPrettyFormat()).output(produceXML(),
                    stringWriter);
        } catch (IOException e) {
            // There was an error creating the XML output
            e.printStackTrace();
        }

        return stringWriter.toString();
    }

    /**
     * Creates an XML Document representing the internal state of this
     * Template.
     *
     * @return an XML Document containing the state of this
     */
    public Document produceXML() {
        DefaultJDOMFactory f = new DefaultJDOMFactory();
        Element ret = f.element("template");

        for (TemplateItem templateItem : templateItems) {
            ret.addContent((templateItem).produceXML());
        }

        return f.document(ret);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public Template clone() {
        try {
            Template ret = (Template) super.clone();
            ret.templateItems =
                    (LinkedList<TemplateItem>) templateItems.clone();
            // make it a deep copy
            ret.templateItems.clear();
            for (TemplateItem item : templateItems)
                ret.templateItems.add(item.clone());
            ret.clippingBounds = (Rectangle) clippingBounds.clone();
            return ret;
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("cannot clone?", e);
        }
    }
}
