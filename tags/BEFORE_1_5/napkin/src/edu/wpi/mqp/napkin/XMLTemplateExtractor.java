/*
 * Created on Nov 19, 2004 by justin 
 * XMLTemplateExtractor.java in edu.wpi.mqp.napkin for MQP
 */
package edu.wpi.mqp.napkin;

import java.awt.*;
import java.awt.Point;
import java.io.IOException;

import javax.xml.parsers.*;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import edu.wpi.mqp.napkin.geometry.*;

/**
 * The <tt>XMLTemplateExtractor</tt> class parses an XML document to create a 
 * <tt>Template<tt> object.
 * 
 * @author Justin Crafford
 * @author Peter Goodspeed
 */
public class XMLTemplateExtractor extends DefaultHandler {

	// Constants indicating what data needs to be extracted
	protected static final int NO_ACTION = 0;
	protected static final int GET_TEMPLATE_TITLE = 1;
	protected static final int GET_TEMPLATE_DESCRIPTION = 2;
	protected static final int GET_TEMPLATE_CLIP_WIDTH = 3;
	protected static final int GET_TEMPLATE_CLIP_HEIGHT = 4;
	protected static final int GET_DRAW_STROKE = 5;
	protected static final int GET_DRAW_FILL = 6;
	protected static final int GET_STROKE_WEIGHT = 7;
	protected static final int GET_R_VALUE = 8;
	protected static final int GET_G_VALUE = 9;
	protected static final int GET_B_VALUE = 10;
	protected static final int GET_X_VALUE = 11;
	protected static final int GET_Y_VALUE = 12;

	// Template-related objects
	private Template template;
	private TemplateItem templateItem;
	private Dimension dimensions;
	private UtilityShape shape;

	// Shape-related objects
	private StraightLine straightLine;
	private CubicLine cubicLine;
	private QuadLine quadLine;
	private Path path;

	// Data-related objects
	private Point point; // Holds point information
	private Point[] points; // Holds all points for a particular line
	private int i; // Used to iterate through all the points
	private int[] rgb; // Holds r, g, b color information
	
	private int currentAction; // Specifies information that needs parsing

	/**
	 * Constructs a new XMLTemplateExtractor
	 */
	public XMLTemplateExtractor() {
		template = new Template();
		i = 0;
		point = new Point();
		points = new Point[4];
		points[0] = new Point();
		points[1] = new Point();
		points[2] = new Point();
		points[3] = new Point();
		currentAction = NO_ACTION;
	}

	/**
	 * SAX event that provides the namespace, name, and all attributes
	 * associated with the Element.
	 */
	public void startElement(String namespace, String localName,
			String qualifiedName, Attributes attribs) {
		// Extract data from all leaf elements or set currentAction so
		// that the characters method can extract the data
		if (localName.equals("title")) {
			currentAction = GET_TEMPLATE_TITLE;
		} else if (localName.equals("description")) {
			currentAction = GET_TEMPLATE_DESCRIPTION;
		} else if (localName.equals("clippingBounds")) {
			dimensions = new Dimension();
		} else if (localName.equals("width")) {
			currentAction = GET_TEMPLATE_CLIP_WIDTH;
		} else if (localName.equals("height")) {
			currentAction = GET_TEMPLATE_CLIP_HEIGHT;
		} else if (localName.equals("templateItem")) {
			templateItem = new TemplateItem();
		} else if (localName.equals("drawStroke")) {
			currentAction = GET_DRAW_STROKE;
		} else if (localName.equals("drawFill")) {
			currentAction = GET_DRAW_FILL;
		} else if (localName.equals("strokeWeight")) {
			currentAction = GET_STROKE_WEIGHT;
		} else if (localName.equals("strokeColor")) {
			rgb = new int[3];
		} else if (localName.equals("fillColor")) {
			rgb = new int[3];
		} else if (localName.equals("straightLine")) {
			i = 0;
			straightLine = new StraightLine();
		} else if (localName.equals("quadLine")) {
			i = 0;
			quadLine = new QuadLine();
		} else if (localName.equals("cubicLine")) {
			i = 0;
			cubicLine = new CubicLine();
		} else if (localName.equals("path")) {
			i = 0;
			path = new Path();
		} else if (localName.equals("moveTo")) {
			i = 0;
		} else if (localName.equals("quadTo")) {
			i = 0;
		} else if (localName.equals("cubicTo")) {
			i = 0;
		} else if (localName.equals("r")) {
			currentAction = GET_R_VALUE;
		} else if (localName.equals("g")) {
			currentAction = GET_G_VALUE;
		} else if (localName.equals("b")) {
			currentAction = GET_B_VALUE;
		} else if (localName.equals("x")) {
			currentAction = GET_X_VALUE;
		} else if (localName.equals("y")) {
			currentAction = GET_Y_VALUE;
		}
	}

	/**
	 * SAX event that provides the content of Elements.
	 */
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
				dimensions.width = new Integer(value).intValue();
				break;
			case GET_TEMPLATE_CLIP_HEIGHT:
				dimensions.height = new Integer(value).intValue();
				break;
			case GET_DRAW_STROKE:
				templateItem.setDrawStroke(new Boolean(value).booleanValue());
				break;
			case GET_DRAW_FILL:
				templateItem.setDrawFill(new Boolean(value).booleanValue());
				break;
			case GET_STROKE_WEIGHT:
				templateItem.setStrokeWeight(new Float(value).floatValue());
				break;
			case GET_R_VALUE:
				rgb[0] = new Integer(value).intValue();
				break;
			case GET_G_VALUE:
				rgb[1] = new Integer(value).intValue();
				break;
			case GET_B_VALUE:
				rgb[2] = new Integer(value).intValue();
				break;
			case GET_X_VALUE:
				point = new Point();
				point.x = new Integer(value).intValue();
				break;
			case GET_Y_VALUE:
				point.y = new Integer(value).intValue();
				break;
			}
		}
	}

	/**
	 * SAX event that indicates that the end of an element has been reached.
	 */
	public void endElement(String namespaceURI, String localName,
			String qualifiedName) {
		if (localName.equals("clippingBounds")) {
			Rectangle clippingBounds = new Rectangle(point, dimensions);
			template.setClippingBounds(clippingBounds);
		} else if (localName.equals("templateItem")) {
			templateItem.setShape(shape);
			template.add(templateItem);
		} else if (localName.equals("strokeColor")) {
			templateItem.setStrokeColor(new Color(rgb[0], rgb[1], rgb[2]));
		} else if (localName.equals("fillColor")) {
			templateItem.setFillColor(new Color(rgb[0], rgb[1], rgb[2]));
		} else if (localName.equals("straightLine")) {
			straightLine.x1 = points[0].x;
			straightLine.y1 = points[0].y;
			straightLine.x2 = points[1].x;
			straightLine.y2 = points[1].y;
			shape = straightLine;
		} else if (localName.equals("quadLine")) {
			quadLine.x1 = points[0].x;
			quadLine.y1 = points[0].y;
			quadLine.ctrlx = points[1].x;
			quadLine.ctrly = points[1].y;
			quadLine.x2 = points[2].x;
			quadLine.y2 = points[2].y;
			shape = quadLine;
		} else if (localName.equals("cubicLine")) {
			cubicLine.x1 = points[0].x;
			cubicLine.y1 = points[0].y;
			cubicLine.ctrlx1 = points[1].x;
			cubicLine.ctrly1 = points[1].y;
			cubicLine.ctrlx2 = points[2].x;
			cubicLine.ctrly2 = points[2].y;
			cubicLine.x2 = points[3].x;
			cubicLine.y2 = points[3].y;
			shape = cubicLine;
		} else if (localName.equals("path")) {
			shape = path;
		} else if (localName.equals("moveTo")) {
			path.moveTo(points[0].x, points[0].y);
			i = 0;
		} else if (localName.equals("lineTo")) {
			path.lineTo(points[0].x, points[0].y);
			i = 0;
		} else if (localName.equals("quadTo")) {
			path.quadTo(points[0].x, points[0].y, points[1].x, points[1].y);
			i = 0;
		} else if (localName.equals("cubicTo")) {
			path.curveTo(points[0].x, points[0].y, points[1].x, points[1].y,
					points[2].x, points[2].y);
			i = 0;
		} else if (localName.equals("close")) {
			path.closePath();
		} else if (localName.equals("start") || localName.equals("point")
				|| localName.equals("control") || localName.equals("end")) {
			points[i++] = new Point(point);
		}
		currentAction = NO_ACTION;
	}

	// The three methods of the ErrorHandler interface
	public void error(SAXParseException e) {
		printError("Error", e);
	}

	public void warning(SAXParseException e) {
		printError("Warning", e);
	}

	public void fatalError(SAXParseException e) {
		printError("Fatal Error", e);
	}

	/**
	 * Prints a detailed error message.
	 */
	protected void printError(String type, SAXParseException e) {
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

	/**
	 * Creates a Template object from an XML document
	 * 
	 * @param path
	 *            A string describing the location of the XML document to parse
	 * @return a Template
	 */
	public Template createTemplate(String path) throws TemplateReadException{
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
			} catch (SAXException e) {
				System.out.println("Warning: Parser does not support schema validation");
			}

			parser.parse(path, this);
		} catch (FactoryConfigurationError e) {
			System.out.println("Factory configuration error: " + e);
		} catch (ParserConfigurationException e) {
			System.out.println("Parser configuration error: " + e);
		} catch (SAXException e) {
			System.out.println("Parsing error: " + e);
			throw new TemplateReadException("Parsing Exception", e);
		} catch (IOException e) {
			System.out.println("I/O error: " + e);
			throw new TemplateReadException("I/O Exception", e);
		}

		return template;
	}
}