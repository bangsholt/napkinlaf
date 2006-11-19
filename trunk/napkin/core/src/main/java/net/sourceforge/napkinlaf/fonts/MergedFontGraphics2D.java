package net.sourceforge.napkinlaf.fonts;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.image.renderable.*;
import java.text.AttributedCharacterIterator;
import java.util.Map;

/**
 * This class is a delegator to an underlying {@link Graphics2D} object that
 * works with {@link MergedFont}.  Almost all methods just forward to the
 * underlying object.
 *
 * @author Alex Lam Sze Lok
 */
public class MergedFontGraphics2D extends Graphics2D {
    private final Graphics2D g2d;
    private boolean isMergedFont;

    private MergedFontGraphics2D(Graphics2D g2d) {
        assert!(g2d instanceof MergedFontGraphics2D) : "double delegation";
        this.g2d = g2d;
        // setFont so as to set the intial state of isCompositeFont
        setFont(g2d.getFont());
    }

    /** {@inheritDoc} */
    @Override
    public void setFont(Font font) {
        isMergedFont = font instanceof MergedFont;
        g2d.setFont(font);
    }

    /**
     * Returns a {@link MergedFontGraphics2D} object for the given graphics
     * object.  If the incoming object is already a {@link MergedFontGraphics2D}
     * it is simply returned; otherwise you will get a {@link
     * MergedFontGraphics2D} object that wraps the incoming object.
     *
     * @param g2d The graphics object that may need wrapping.
     *
     * @return A {@link MergedFontGraphics2D} object.
     */
    public static MergedFontGraphics2D wrap(Graphics2D g2d) {
        return g2d instanceof MergedFontGraphics2D ?
                (MergedFontGraphics2D) g2d : new MergedFontGraphics2D(g2d);
    }

    /** @return The underlying {@link Graphics2D} object. */
    public Graphics2D getGraphics2D() {
        return g2d;
    }

    /** {@inheritDoc} */
    @Override
    public void drawString(String str, int x, int y) {
        if (isMergedFont) {
            drawString(str, (float) x, (float) y);
        } else {
            g2d.drawString(str, x, y);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void drawString(String s, float x, float y) {
        if (isMergedFont) {
            Font font = getFont();
            FontRenderContext frc = getFontRenderContext();
            GlyphVector gVector = font.createGlyphVector(frc, s);
            drawGlyphVector(gVector, x, y);
        } else {
            g2d.drawString(s, x, y);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        if (isMergedFont) {
            drawString(iterator, (float) x, (float) y);
        } else {
            g2d.drawString(iterator, x, y);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void
           drawString(AttributedCharacterIterator iterator, float x, float y) {
        if (isMergedFont) {
            Font font = getFont();
            FontRenderContext frc = getFontRenderContext();
            GlyphVector gVector = font.createGlyphVector(frc, iterator);
            drawGlyphVector(gVector, x, y);
        } else {
            g2d.drawString(iterator, x, y);
        }
    }

    @Override
    /** {@inheritDoc} */
    public void drawBytes(byte[] data, int offset, int length, int x, int y) {
        if (isMergedFont) {
            drawString(new String(data, offset, length), x, y);
        } else {
            g2d.drawBytes(data, offset, length, x, y);
        }
    }

    @Override
    /** {@inheritDoc} */
    public void drawChars(char[] data, int offset, int length, int x, int y) {
        if (isMergedFont) {
            drawString(new String(data, offset, length), x, y);
        } else {
            g2d.drawChars(data, offset, length, x, y);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void drawGlyphVector(GlyphVector g, float x, float y) {
        if (g instanceof MergedGlyphVector) {
            MergedGlyphVector cgv = (MergedGlyphVector) g;
            int glyphIndex = 0;
            for (GlyphVector g2 : cgv.split()) {
                Point2D glyphPos = cgv.getGlyphPosition(glyphIndex);
                g2d.drawGlyphVector(g2, (float) (x + glyphPos.getX()),
                        (float) (y + glyphPos.getY()));
                glyphIndex += g2.getNumGlyphs();
            }
        } else {
            g2d.drawGlyphVector(g, x, y);
        }
    }

    /**
     * {@inheritDoc}
     * <p/>
     * The returned object will be another {@link MergedFontGraphics2D} object.
     */
    @Override
    public Graphics create(int x, int y, int width, int height) {
        return new MergedFontGraphics2D(
                (Graphics2D) g2d.create(x, y, width, height));
    }

    /**
     * Dispose of this object but not the Graphics2D instance that it wraps.
     * This is because one often wraps an instance passed through as a method
     * parameter, and there would almost always be impossible to replace the
     * wrapped instance with this in the caller.
     * <p/>
     * Since it will be the caller's responsibility to dispose of its Graphics2D
     * instance, and the caller would not be able to reference to this wrapper,
     * so the only chance to dispose of a MergedFontGraphics2D instance would be
     * where it was created.
     */
    public void dispose() {
    }

    @Override
    public boolean equals(Object that) {
        return this == that || (that instanceof MergedFontGraphics2D &&
                g2d.equals(((MergedFontGraphics2D) that).getGraphics2D()));
    }

    /** {@inheritDoc} */
    @Override
    public void drawPolygon(Polygon p) {
        g2d.drawPolygon(p);
    }

    /** {@inheritDoc} */
    @Override
    public void fillPolygon(Polygon p) {
        g2d.fillPolygon(p);
    }

    /** {@inheritDoc} */
    @Override
    public Rectangle getClipBounds(Rectangle r) {
        return g2d.getClipBounds(r);
    }

    /** {@inheritDoc} */
    @Override
    public void
           fill3DRect(int x, int y, int width, int height, boolean raised) {
        g2d.fill3DRect(x, y, width, height, raised);
    }

    /** {@inheritDoc} */
    @Override
    public void
           draw3DRect(int x, int y, int width, int height, boolean raised) {
        g2d.draw3DRect(x, y, width, height, raised);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return g2d.hashCode();
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return g2d.toString();
    }

    /** {@inheritDoc} */
    @Override
    public void drawRect(int x, int y, int width, int height) {
        g2d.drawRect(x, y, width, height);
    }

    /** {@inheritDoc} */
    @Override
    public boolean hitClip(int x, int y, int width, int height) {
        return g2d.hitClip(x, y, width, height);
    }

    /** {@inheritDoc} */
    @Override
    public FontMetrics getFontMetrics() {
        return g2d.getFontMetrics();
    }

    /** {@inheritDoc} */
    @SuppressWarnings({"deprecation"})
    @Override
    @Deprecated
    public Rectangle getClipRect() {
        return g2d.getClipRect();
    }

    /** {@inheritDoc} */
    @Override
    public void draw(Shape s) {
        g2d.draw(s);
    }

    /** {@inheritDoc} */
    @Override
    public boolean
           drawImage(Image img, AffineTransform xform, ImageObserver obs) {
        return g2d.drawImage(img, xform, obs);
    }

    /** {@inheritDoc} */
    @Override
    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
        g2d.drawImage(img, op, x, y);
    }

    /** {@inheritDoc} */
    @Override
    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
        g2d.drawRenderedImage(img, xform);
    }

    /** {@inheritDoc} */
    @Override
    public void
           drawRenderableImage(RenderableImage img, AffineTransform xform) {
        g2d.drawRenderableImage(img, xform);
    }

    /** {@inheritDoc} */
    @Override
    public void fill(Shape s) {
        g2d.fill(s);
    }

    /** {@inheritDoc} */
    @Override
    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        return g2d.hit(rect, s, onStroke);
    }

    /** {@inheritDoc} */
    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        return g2d.getDeviceConfiguration();
    }

    /** {@inheritDoc} */
    @Override
    public void setComposite(Composite comp) {
        g2d.setComposite(comp);
    }

    /** {@inheritDoc} */
    @Override
    public void setPaint(Paint paint) {
        g2d.setPaint(paint);
    }

    /** {@inheritDoc} */
    @Override
    public void setStroke(Stroke s) {
        g2d.setStroke(s);
    }

    /** {@inheritDoc} */
    @Override
    public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) {
        g2d.setRenderingHint(hintKey, hintValue);
    }

    /** {@inheritDoc} */
    @Override
    public Object getRenderingHint(RenderingHints.Key hintKey) {
        return g2d.getRenderingHint(hintKey);
    }

    /** {@inheritDoc} */
    @Override
    public void setRenderingHints(Map<?, ?> hints) {
        g2d.setRenderingHints(hints);
    }

    /** {@inheritDoc} */
    @Override
    public void addRenderingHints(Map<?, ?> hints) {
        g2d.addRenderingHints(hints);
    }

    /** {@inheritDoc} */
    @Override
    public RenderingHints getRenderingHints() {
        return g2d.getRenderingHints();
    }

    /** {@inheritDoc} */
    @Override
    public void translate(int x, int y) {
        g2d.translate(x, y);
    }

    /** {@inheritDoc} */
    @Override
    public void translate(double tx, double ty) {
        g2d.translate(tx, ty);
    }

    /** {@inheritDoc} */
    @Override
    public void rotate(double theta) {
        g2d.rotate(theta);
    }

    /** {@inheritDoc} */
    @Override
    public void rotate(double theta, double x, double y) {
        g2d.rotate(theta, x, y);
    }

    /** {@inheritDoc} */
    @Override
    public void scale(double sx, double sy) {
        g2d.scale(sx, sy);
    }

    /** {@inheritDoc} */
    @Override
    public void shear(double shx, double shy) {
        g2d.shear(shx, shy);
    }

    /** {@inheritDoc} */
    @SuppressWarnings({"MethodParameterNamingConvention"})
    @Override
    public void transform(AffineTransform Tx) {
        g2d.transform(Tx);
    }

    /** {@inheritDoc} */
    @SuppressWarnings({"MethodParameterNamingConvention"})
    @Override
    public void setTransform(AffineTransform Tx) {
        g2d.setTransform(Tx);
    }

    /** {@inheritDoc} */
    @Override
    public AffineTransform getTransform() {
        return g2d.getTransform();
    }

    /** {@inheritDoc} */
    @Override
    public Paint getPaint() {
        return g2d.getPaint();
    }

    /** {@inheritDoc} */
    @Override
    public Composite getComposite() {
        return g2d.getComposite();
    }

    /** {@inheritDoc} */
    @Override
    public void setBackground(Color color) {
        g2d.setBackground(color);
    }

    /** {@inheritDoc} */
    @Override
    public Color getBackground() {
        return g2d.getBackground();
    }

    /** {@inheritDoc} */
    @Override
    public Stroke getStroke() {
        return g2d.getStroke();
    }

    /** {@inheritDoc} */
    @Override
    public void clip(Shape s) {
        g2d.clip(s);
    }

    /** {@inheritDoc} */
    @Override
    public FontRenderContext getFontRenderContext() {
        return g2d.getFontRenderContext();
    }

    /** {@inheritDoc} */
    @Override
    public Graphics create() {
        return new MergedFontGraphics2D((Graphics2D) g2d.create());
    }

    /** {@inheritDoc} */
    @Override
    public Color getColor() {
        return g2d.getColor();
    }

    /** {@inheritDoc} */
    @Override
    public void setColor(Color c) {
        g2d.setColor(c);
    }

    /** {@inheritDoc} */
    @Override
    public void setPaintMode() {
        g2d.setPaintMode();
    }

    /** {@inheritDoc} */
    @Override
    public void setXORMode(Color c1) {
        g2d.setXORMode(c1);
    }

    /** {@inheritDoc} */
    @Override
    public Font getFont() {
        return g2d.getFont();
    }

    /** {@inheritDoc} */
    @Override
    public FontMetrics getFontMetrics(Font f) {
        return g2d.getFontMetrics(f);
    }

    /** {@inheritDoc} */
    @Override
    public Rectangle getClipBounds() {
        return g2d.getClipBounds();
    }

    /** {@inheritDoc} */
    @Override
    public void clipRect(int x, int y, int width, int height) {
        g2d.clipRect(x, y, width, height);
    }

    /** {@inheritDoc} */
    @Override
    public void setClip(int x, int y, int width, int height) {
        g2d.setClip(x, y, width, height);
    }

    /** {@inheritDoc} */
    @Override
    public Shape getClip() {
        return g2d.getClip();
    }

    /** {@inheritDoc} */
    @Override
    public void setClip(Shape clip) {
        g2d.setClip(clip);
    }

    /** {@inheritDoc} */
    @Override
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        g2d.copyArea(x, y, width, height, dx, dy);
    }

    /** {@inheritDoc} */
    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        g2d.drawLine(x1, y1, x2, y2);
    }

    /** {@inheritDoc} */
    @Override
    public void fillRect(int x, int y, int width, int height) {
        g2d.fillRect(x, y, width, height);
    }

    /** {@inheritDoc} */
    @Override
    public void clearRect(int x, int y, int width, int height) {
        g2d.clearRect(x, y, width, height);
    }

    /** {@inheritDoc} */
    @Override
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth,
            int arcHeight) {
        g2d.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    /** {@inheritDoc} */
    @Override
    public void fillRoundRect(int x, int y, int width, int height, int arcWidth,
            int arcHeight) {
        g2d.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    /** {@inheritDoc} */
    @Override
    public void drawOval(int x, int y, int width, int height) {
        g2d.drawOval(x, y, width, height);
    }

    /** {@inheritDoc} */
    @Override
    public void fillOval(int x, int y, int width, int height) {
        g2d.fillOval(x, y, width, height);
    }

    /** {@inheritDoc} */
    @Override
    public void drawArc(int x, int y, int width, int height, int startAngle,
            int arcAngle) {
        g2d.drawArc(x, y, width, height, startAngle, arcAngle);
    }

    /** {@inheritDoc} */
    @Override
    public void fillArc(int x, int y, int width, int height, int startAngle,
            int arcAngle) {
        g2d.fillArc(x, y, width, height, startAngle, arcAngle);
    }

    /** {@inheritDoc} */
    @Override
    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
        g2d.drawPolyline(xPoints, yPoints, nPoints);
    }

    /** {@inheritDoc} */
    @Override
    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        g2d.drawPolygon(xPoints, yPoints, nPoints);
    }

    /** {@inheritDoc} */
    @Override
    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        g2d.fillPolygon(xPoints, yPoints, nPoints);
    }

    /** {@inheritDoc} */
    @Override
    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        return g2d.drawImage(img, x, y, observer);
    }

    /** {@inheritDoc} */
    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height,
            ImageObserver observer) {
        return g2d.drawImage(img, x, y, width, height, observer);
    }

    /** {@inheritDoc} */
    @Override
    public boolean drawImage(Image img, int x, int y, Color bgcolor,
            ImageObserver observer) {
        return g2d.drawImage(img, x, y, bgcolor, observer);
    }

    /** {@inheritDoc} */
    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height,
            Color bgcolor, ImageObserver observer) {
        return g2d.drawImage(img, x, y, width, height, bgcolor, observer);
    }

    /** {@inheritDoc} */
    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
            int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        return g2d.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2,
                observer);
    }

    /** {@inheritDoc} */
    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
            int sx1, int sy1, int sx2, int sy2, Color bgcolor,
            ImageObserver observer) {
        return g2d.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2,
                bgcolor, observer);
    }
}
