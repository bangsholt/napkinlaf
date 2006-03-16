// $Id$

package napkin.sketch;

import napkin.NapkinLookAndFeel;
import static napkin.sketch.XMLActions.*;
import napkin.sketch.geometry.CubicLine;
import napkin.sketch.geometry.Path;
import napkin.sketch.geometry.QuadLine;
import napkin.sketch.geometry.SketchShape;
import napkin.sketch.geometry.StraightLine;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.awt.*;
import java.awt.geom.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * The <tt>XMLTemplateExtractor</tt> class parses an XML document to create a
 * <tt>Template<tt> object.
 *
 * @author Justin Crafford
 * @author Peter Goodspeed
 */
@SuppressWarnings({"WeakerAccess"})
public class XMLTemplateExtractor extends DefaultHandler {

    // Template-related objects
    private final Template template;
    private TemplateItem templateItem;
    private Dimension dimensions;
    private SketchShape shape;

    // Shape-related objects
    private StraightLine straightLine;
    private CubicLine cubicLine;
    private QuadLine quadLine;
    private Path path;

    // Data-related objects
    private Point2D point; // Holds point information
    private final Point2D.Float[] points; // Holds points for a particular line
    private int i; // Used to iterate through all the points
    private int[] rgb; // Holds r, g, b color information

    private XMLActions currentAction; // Specifies information that needs parsing
    private float curX;

    /** Constructs a new XMLTemplateExtractor. */
    public XMLTemplateExtractor() {
        super();
        template = new Template();
        i = 0;
        point = new Point2D.Float();
        points = new Point2D.Float[4];
        points[0] = new Point2D.Float();
        points[1] = new Point2D.Float();
        points[2] = new Point2D.Float();
        points[3] = new Point2D.Float();
        currentAction = XMLActions.NO_ACTION;
    }

    /**
     * SAX event that provides the namespace, name, and all attributes
     * associated with the Element.
     */
    @Override
    public void startElement(String namespace, String localName,
            String qualifiedName, Attributes attribs) {
        // Extract data from all leaf elements or set currentAction so
        // that the characters method can extract the data
        if ("title".equals(localName)) {
            currentAction = GET_TEMPLATE_TITLE;
        } else if ("description".equals(localName)) {
            currentAction = GET_TEMPLATE_DESCRIPTION;
        } else if ("clippingBounds".equals(localName)) {
            dimensions = new Dimension();
        } else if ("width".equals(localName)) {
            currentAction = GET_TEMPLATE_CLIP_WIDTH;
        } else if ("height".equals(localName)) {
            currentAction = GET_TEMPLATE_CLIP_HEIGHT;
        } else if ("templateItem".equals(localName)) {
            templateItem = new TemplateItem();
        } else if ("drawStroke".equals(localName)) {
            currentAction = GET_DRAW_STROKE;
        } else if ("drawFill".equals(localName)) {
            currentAction = GET_DRAW_FILL;
        } else if ("strokeWeight".equals(localName)) {
            currentAction = GET_STROKE_WEIGHT;
        } else if ("strokeColor".equals(localName)) {
            rgb = new int[3];
        } else if ("fillColor".equals(localName)) {
            rgb = new int[3];
        } else if ("straightLine".equals(localName)) {
            i = 0;
            straightLine = new StraightLine();
        } else if ("quadLine".equals(localName)) {
            i = 0;
            quadLine = new QuadLine();
        } else if ("cubicLine".equals(localName)) {
            i = 0;
            cubicLine = new CubicLine();
        } else if ("path".equals(localName)) {
            i = 0;
            path = new Path();
        } else if ("moveTo".equals(localName)) {
            i = 0;
        } else if ("quadTo".equals(localName)) {
            i = 0;
        } else if ("cubicTo".equals(localName)) {
            i = 0;
        } else if ("r".equals(localName)) {
            currentAction = GET_R_VALUE;
        } else if ("g".equals(localName)) {
            currentAction = GET_G_VALUE;
        } else if ("b".equals(localName)) {
            currentAction = GET_B_VALUE;
        } else if ("x".equals(localName)) {
            currentAction = GET_X_VALUE;
        } else if ("y".equals(localName)) {
            currentAction = GET_Y_VALUE;
        }
    }

    /** SAX event that provides the content of Elements. */
    @Override
    public void characters(char[] ch, int start, int length) {
        if (currentAction != NO_ACTION) {
            // Create a string from the character data
            String value = new String(ch, start, length);

            // Convert the string into data that the current action requires
            switch (currentAction) {
            case GET_TEMPLATE_TITLE:
                template.setTitle(value);
                break;
            case GET_TEMPLATE_DESCRIPTION:
                template.setDescription(value);
                break;
            case GET_TEMPLATE_CLIP_WIDTH:
                dimensions.width = Integer.parseInt(value);
                break;
            case GET_TEMPLATE_CLIP_HEIGHT:
                dimensions.height = Integer.parseInt(value);
                break;
            case GET_DRAW_STROKE:
                templateItem.setDrawStroke(Boolean.valueOf(value));
                break;
            case GET_DRAW_FILL:
                templateItem.setDrawFill(Boolean.valueOf(value));
                break;
            case GET_STROKE_WEIGHT:
                templateItem.setStrokeWeight(Float.parseFloat(value));
                break;
            case GET_R_VALUE:
                rgb[0] = Integer.parseInt(value);
                break;
            case GET_G_VALUE:
                rgb[1] = Integer.parseInt(value);
                break;
            case GET_B_VALUE:
                rgb[2] = Integer.parseInt(value);
                break;
            case GET_X_VALUE:
                curX = Float.parseFloat(value);
                break;
            case GET_Y_VALUE:
                point = new Point2D.Float(curX, Float.parseFloat(value));
                break;
            case NO_ACTION:
                break;
            default:
                throw new IllegalStateException(currentAction + ": unknown");
            }
        }
    }

    /** SAX event that indicates that the end of an element has been reached. */
    @Override
    public void endElement(String namespaceURI, String localName,
            String qualifiedName) {
        if ("clippingBounds".equals(localName)) {
            Rectangle clippingBounds = new Rectangle(dimensions);
            template.setClippingBounds(clippingBounds);
        } else if ("templateItem".equals(localName)) {
            templateItem.setShape(shape);
            template.add(templateItem);
        } else if ("strokeColor".equals(localName)) {
            templateItem.setStrokeColor(new Color(rgb[0], rgb[1], rgb[2]));
        } else if ("fillColor".equals(localName)) {
            templateItem.setFillColor(new Color(rgb[0], rgb[1], rgb[2]));
        } else if ("straightLine".equals(localName)) {
            straightLine.x1 = points[0].x;
            straightLine.y1 = points[0].y;
            straightLine.x2 = points[1].x;
            straightLine.y2 = points[1].y;
            shape = straightLine;
        } else if ("quadLine".equals(localName)) {
            quadLine.x1 = points[0].x;
            quadLine.y1 = points[0].y;
            quadLine.ctrlx = points[1].x;
            quadLine.ctrly = points[1].y;
            quadLine.x2 = points[2].x;
            quadLine.y2 = points[2].y;
            shape = quadLine;
        } else if ("cubicLine".equals(localName)) {
            cubicLine.x1 = points[0].x;
            cubicLine.y1 = points[0].y;
            cubicLine.ctrlx1 = points[1].x;
            cubicLine.ctrly1 = points[1].y;
            cubicLine.ctrlx2 = points[2].x;
            cubicLine.ctrly2 = points[2].y;
            cubicLine.x2 = points[3].x;
            cubicLine.y2 = points[3].y;
            shape = cubicLine;
        } else if ("path".equals(localName)) {
            shape = path;
        } else if ("moveTo".equals(localName)) {
            path.moveTo(points[0].x, points[0].y);
            i = 0;
        } else if ("lineTo".equals(localName)) {
            path.lineTo(points[0].x, points[0].y);
            i = 0;
        } else if ("quadTo".equals(localName)) {
            path.quadTo(points[0].x, points[0].y, points[1].x, points[1].y);
            i = 0;
        } else if ("cubicTo".equals(localName)) {
            path.curveTo(points[0].x, points[0].y, points[1].x, points[1].y,
                    points[2].x, points[2].y);
            i = 0;
        } else if ("close".equals(localName)) {
            path.closePath();
        } else if ("start".equals(localName) || "point".equals(localName) ||
                "control".equals(localName) || "end".equals(localName)) {
            points[i++] = (Point2D.Float) point.clone();
        }
        currentAction = NO_ACTION;
    }

    // The three methods of the ErrorHandler interface
    @Override
    public void error(SAXParseException e) {
        printError("Error", e);
    }

    @Override
    public void warning(SAXParseException e) {
        printError("Warning", e);
    }

    @Override
    public void fatalError(SAXParseException e) {
        printError("Fatal Error", e);
    }

    /** Prints a detailed error message. */
    @SuppressWarnings(
            {"WeakerAccess", "UseOfSystemOutOrSystemErr", "HardcodedFileSeparator"})
    protected static void printError(String type, SAXParseException e) {
        System.err.print("[");
        System.err.print(type);
        System.err.print("] ");
        String systemId = e.getSystemId();
        if (systemId != null) {
            int index = systemId.lastIndexOf('/');
            if (index != -1)
                systemId = systemId.substring(index + 1);
            System.err.print(systemId);
        }
        System.err.print(':');
        System.err.print(e.getLineNumber());
        System.err.print(':');
        System.err.print(e.getColumnNumber());
        System.err.print(": ");
        System.err.print(e.getMessage());
        System.err.println();
        System.err.flush();
    }

    @SuppressWarnings({"HardcodedFileSeparator"})
    private static final String SCHEMA_URL = NapkinLookAndFeel.class.
            getResource("resources/templates/Template.xsd").toString();

    /**
     * Creates a Template object from an XML document.
     *
     * @param in An input stream containing the XML document to parse.
     *
     * @return A Template.
     */
    public Template createTemplate(InputStream in)
            throws TemplateReadException {

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();

            // set parser features
            try {
                reader.setFeature("http://xml.org/sax/features/namespaces",
                        true);
                reader.setFeature("http://xml.org/sax/features/validation",
                        true);
                reader.setFeature(
                        "http://apache.org/xml/features/validation/schema",
                        true);
                reader.setFeature(
                        "http://apache.org/xml/features/validation/schema-full-checking",
                        true);
                reader.setProperty(
                        "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation",
                        SCHEMA_URL);
            } catch (SAXException e) {
                e.printStackTrace();
                System.out.println(
                        "Warning: Parser does not support schema validation");
            }

            parser.parse(in, this);
        } catch (ParserConfigurationException e) {
            throw new TemplateReadException(e);
        } catch (SAXException e) {
            throw new TemplateReadException(e);
        } catch (IOException e) {
            throw new TemplateReadException(e);
        }

        return template;
    }
}
