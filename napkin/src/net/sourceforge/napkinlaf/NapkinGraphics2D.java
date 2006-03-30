package net.sourceforge.napkinlaf;

import java.awt.*;
import java.awt.image.*;

public abstract class NapkinGraphics2D extends Graphics2D {
    /**
     * Draws a 3-D highlighted outline of the specified rectangle. The edges of
     * the rectangle are highlighted so that they appear to be beveled and lit
     * from the upper left corner.
     * <p/>
     * The colors used for the highlighting effect are determined based on the
     * current color. The resulting rectangle covers an area that is
     * <code>width&nbsp;+&nbsp;1</code> pixels wide by <code>height&nbsp;+&nbsp;1</code>
     * pixels tall.  This method uses the current <code>Color</code> exclusively
     * and ignores the current <code>Paint</code>.
     *
     * @param x      the x coordinate of the rectangle to be drawn.
     * @param y      the y coordinate of the rectangle to be drawn.
     * @param width  the width of the rectangle to be drawn.
     * @param height the height of the rectangle to be drawn.
     * @param raised a boolean that determines whether the rectangle appears to
     *               be raised above the surface or sunk into the surface.
     *
     * @see Graphics#fill3DRect
     */
    @Override
    public void draw3DRect(int x, int y, int width, int height,
            boolean raised) {
        super.draw3DRect(x, y, width, height, raised);
    }

    /**
     * Paints a 3-D highlighted rectangle filled with the current color. The
     * edges of the rectangle are highlighted so that it appears as if the edges
     * were beveled and lit from the upper left corner. The colors used for the
     * highlighting effect and for filling are determined from the current
     * <code>Color</code>.  This method uses the current <code>Color</code>
     * exclusively and ignores the current <code>Paint</code>.
     *
     * @param x      the x coordinate of the rectangle to be filled.
     * @param y      the y coordinate of the rectangle to be filled.
     * @param width  the width of the rectangle to be filled.
     * @param height the height of the rectangle to be filled.
     * @param raised a boolean value that determines whether the rectangle
     *               appears to be raised above the surface or etched into the
     *               surface.
     *
     * @see Graphics#draw3DRect
     */
    @Override
    public void fill3DRect(int x, int y, int width, int height,
            boolean raised) {
        super.fill3DRect(x, y, width, height, raised);
    }

    /**
     * Constructs a new <code>Graphics2D</code> object.  Since
     * <code>Graphics2D</code> is an abstract class, and since it must be
     * customized by subclasses for different output devices,
     * <code>Graphics2D</code> objects cannot be created directly. Instead,
     * <code>Graphics2D</code> objects must be obtained from another
     * <code>Graphics2D</code> object, created by a <code>Component</code>, or
     * obtained from images such as {@link BufferedImage} objects.
     *
     * @see Component#getGraphics
     * @see Graphics#create
     */
    protected NapkinGraphics2D() {
        super();
    }

    /**
     * Creates a new <code>Graphics</code> object based on this
     * <code>Graphics</code> object, but with a new translation and clip area.
     * The new <code>Graphics</code> object has its origin translated to the
     * specified point (<i>x</i>,&nbsp;<i>y</i>). Its clip area is determined by
     * the intersection of the original clip area with the specified rectangle.
     * The arguments are all interpreted in the coordinate system of the
     * original <code>Graphics</code> object. The new graphics context is
     * identical to the original, except in two respects:
     * <p/>
     * <ul> <li> The new graphics context is translated by
     * (<i>x</i>,&nbsp;<i>y</i>). That is to say, the point
     * (<code>0</code>,&nbsp;<code>0</code>) in the new graphics context is the
     * same as (<i>x</i>,&nbsp;<i>y</i>) in the original graphics context. <li>
     * The new graphics context has an additional clipping rectangle, in
     * addition to whatever (translated) clipping rectangle it inherited from
     * the original graphics context. The origin of the new clipping rectangle
     * is at (<code>0</code>,&nbsp;<code>0</code>), and its size is specified by
     * the <code>width</code> and <code>height</code> arguments. </ul>
     * <p/>
     *
     * @param x      the <i>x</i> coordinate.
     * @param y      the <i>y</i> coordinate.
     * @param width  the width of the clipping rectangle.
     * @param height the height of the clipping rectangle.
     *
     * @return a new graphics context.
     *
     * @see Graphics#translate
     * @see Graphics#clipRect
     */
    @Override
    public Graphics create(int x, int y, int width, int height) {
        return super.create(x, y, width, height);
    }

    /**
     * Draws the text given by the specified byte array, using this graphics
     * context's current font and color. The baseline of the first character is
     * at position (<i>x</i>,&nbsp;<i>y</i>) in this graphics context's
     * coordinate system.
     *
     * @param data   the data to be drawn
     * @param offset the start offset in the data
     * @param length the number of bytes that are drawn
     * @param x      the <i>x</i> coordinate of the baseline of the text
     * @param y      the <i>y</i> coordinate of the baseline of the text
     *
     * @see Graphics#drawChars
     * @see Graphics#drawString
     */
    @Override
    public void drawBytes(byte data[], int offset, int length, int x, int y) {
        super.drawBytes(data, offset, length, x, y);
    }

    /**
     * Draws the text given by the specified character array, using this
     * graphics context's current font and color. The baseline of the first
     * character is at position (<i>x</i>,&nbsp;<i>y</i>) in this graphics
     * context's coordinate system.
     *
     * @param data   the array of characters to be drawn
     * @param offset the start offset in the data
     * @param length the number of characters to be drawn
     * @param x      the <i>x</i> coordinate of the baseline of the text
     * @param y      the <i>y</i> coordinate of the baseline of the text
     *
     * @see Graphics#drawBytes
     * @see Graphics#drawString
     */
    @Override
    public void drawChars(char data[], int offset, int length, int x, int y) {
        super.drawChars(data, offset, length, x, y);
    }

    /**
     * Draws the outline of a polygon defined by the specified
     * <code>Polygon</code> object.
     *
     * @param p the polygon to draw.
     *
     * @see Graphics#fillPolygon
     * @see Graphics#drawPolyline
     */
    @Override
    public void drawPolygon(Polygon p) {
        super.drawPolygon(p);
    }

    /**
     * Draws the outline of the specified rectangle. The left and right edges of
     * the rectangle are at <code>x</code> and <code>x&nbsp;+&nbsp;width</code>.
     * The top and bottom edges are at <code>y</code> and
     * <code>y&nbsp;+&nbsp;height</code>. The rectangle is drawn using the
     * graphics context's current color.
     *
     * @param x      the <i>x</i> coordinate of the rectangle to be drawn.
     * @param y      the <i>y</i> coordinate of the rectangle to be drawn.
     * @param width  the width of the rectangle to be drawn.
     * @param height the height of the rectangle to be drawn.
     *
     * @see Graphics#fillRect
     * @see Graphics#clearRect
     */
    @Override
    public void drawRect(int x, int y, int width, int height) {
        super.drawRect(x, y, width, height);
    }

    /**
     * Fills the polygon defined by the specified Polygon object with the
     * graphics context's current color.
     * <p/>
     * The area inside the polygon is defined using an even-odd fill rule, also
     * known as the alternating rule.
     *
     * @param p the polygon to fill.
     *
     * @see Graphics#drawPolygon(int[], int[], int)
     */
    @Override
    public void fillPolygon(Polygon p) {
        super.fillPolygon(p);
    }

    /**
     * Disposes of this graphics context once it is no longer referenced.
     *
     * @see #dispose
     */
    @Override
    public void finalize() {
        super.finalize();
    }

    /**
     * Returns the bounding rectangle of the current clipping area. The
     * coordinates in the rectangle are relative to the coordinate system origin
     * of this graphics context.  This method differs from {@link
     * #getClipBounds() getClipBounds} in that an existing rectangle is used
     * instead of allocating a new one. This method refers to the user clip,
     * which is independent of the clipping associated with device bounds and
     * window visibility. If no clip has previously been set, or if the clip has
     * been cleared using <code>setClip(null)</code>, this method returns the
     * specified <code>Rectangle</code>.
     *
     * @param r the rectangle where the current clipping area is copied to.  Any
     *          current values in this rectangle are overwritten.
     *
     * @return the bounding rectangle of the current clipping area.
     */
    @Override
    public Rectangle getClipBounds(Rectangle r) {
        return super.getClipBounds(r);
    }

    /**
     * Returns the bounding rectangle of the current clipping area.
     *
     * @return the bounding rectangle of the current clipping area or
     *         <code>null</code> if no clip is set.
     *
     * @deprecated As of JDK version 1.1, replaced by <code>getClipBounds()</code>.
     */
    @Override
    @Deprecated
    public Rectangle getClipRect() {
        return super.getClipRect();
    }

    /**
     * Gets the font metrics of the current font.
     *
     * @return the font metrics of this graphics context's current font.
     *
     * @see Graphics#getFont
     * @see FontMetrics
     * @see Graphics#getFontMetrics(Font)
     */
    @Override
    public FontMetrics getFontMetrics() {
        return super.getFontMetrics();
    }

    /**
     * Returns true if the specified rectangular area might intersect the
     * current clipping area. The coordinates of the specified rectangular area
     * are in the user coordinate space and are relative to the coordinate
     * system origin of this graphics context. This method may use an algorithm
     * that calculates a result quickly but which sometimes might return true
     * even if the specified rectangular area does not intersect the clipping
     * area. The specific algorithm employed may thus trade off accuracy for
     * speed, but it will never return false unless it can guarantee that the
     * specified rectangular area does not intersect the current clipping area.
     * The clipping area used by this method can represent the intersection of
     * the user clip as specified through the clip methods of this graphics
     * context as well as the clipping associated with the device or image
     * bounds and window visibility.
     *
     * @param x      the x coordinate of the rectangle to test against the clip
     * @param y      the y coordinate of the rectangle to test against the clip
     * @param width  the width of the rectangle to test against the clip
     * @param height the height of the rectangle to test against the clip
     *
     * @return <code>true</code> if the specified rectangle intersects the
     *         bounds of the current clip; <code>false</code> otherwise.
     */
    @Override
    public boolean hitClip(int x, int y, int width, int height) {
        return super.hitClip(x, y, width, height);
    }

    /**
     * Returns a <code>String</code> object representing this
     * <code>Graphics</code> object's value.
     *
     * @return a string representation of this graphics context.
     */
    @Override
    public String toString() {
        return super.toString();
    }
}