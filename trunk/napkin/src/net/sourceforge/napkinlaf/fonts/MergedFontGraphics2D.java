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
        assert !(g2d instanceof MergedFontGraphics2D) : "double delegation";
        this.g2d = g2d;
        // setFont so as to set the intial state of isCompositeFont
        setFont(g2d.getFont());
    }

    /** {@inheritDoc} */
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
    public void drawString(String str, int x, int y) {
        if (isMergedFont) {
            drawString(str, (float) x, (float) y);
        } else {
            g2d.drawString(str, x, y);
        }
    }

    /** {@inheritDoc} */
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
    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        if (isMergedFont) {
            drawString(iterator, (float) x, (float) y);
        } else {
            g2d.drawString(iterator, x, y);
        }
    }

    /** {@inheritDoc} */
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

    /** Invokes {@link Graphics2D#dispose()} on the underlying object. */
    public void dispose() {
        g2d.dispose();
    }

    @Override
    public boolean equals(Object that) {
        if (this == that)
            return true;
        if (that instanceof MergedFontGraphics2D)
            return g2d.equals(((MergedFontGraphics2D) that).g2d);
        return false;
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
    @Override
    @Deprecated
    public Rectangle getClipRect() {
        return g2d.getClipRect();
    }

    /** {@inheritDoc} */
    public void draw(Shape s) {
        g2d.draw(s);
    }

    /** {@inheritDoc} */
    public boolean
            drawImage(Image img, AffineTransform xform, ImageObserver obs) {
        return g2d.drawImage(img, xform, obs);
    }

    /** {@inheritDoc} */
    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
        g2d.drawImage(img, op, x, y);
    }

    /** {@inheritDoc} */
    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
        g2d.drawRenderedImage(img, xform);
    }

    /** {@inheritDoc} */
    public void
            drawRenderableImage(RenderableImage img, AffineTransform xform) {
        g2d.drawRenderableImage(img, xform);
    }

    /** {@inheritDoc} */
    public void fill(Shape s) {
        g2d.fill(s);
    }

    /** {@inheritDoc} */
    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        return g2d.hit(rect, s, onStroke);
    }

    /** {@inheritDoc} */
    public GraphicsConfiguration getDeviceConfiguration() {
        return g2d.getDeviceConfiguration();
    }

    /** {@inheritDoc} */
    public void setComposite(Composite comp) {
        g2d.setComposite(comp);
    }

    /** {@inheritDoc} */
    public void setPaint(Paint paint) {
        g2d.setPaint(paint);
    }

    /** {@inheritDoc} */
    public void setStroke(Stroke s) {
        g2d.setStroke(s);
    }

    /** {@inheritDoc} */
    public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) {
        g2d.setRenderingHint(hintKey, hintValue);
    }

    /** {@inheritDoc} */
    public Object getRenderingHint(RenderingHints.Key hintKey) {
        return g2d.getRenderingHint(hintKey);
    }

    /** {@inheritDoc} */
    public void setRenderingHints(Map<?, ?> hints) {
        g2d.setRenderingHints(hints);
    }

    /** {@inheritDoc} */
    public void addRenderingHints(Map<?, ?> hints) {
        g2d.addRenderingHints(hints);
    }

    /** {@inheritDoc} */
    public RenderingHints getRenderingHints() {
        return g2d.getRenderingHints();
    }

    /** {@inheritDoc} */
    public void translate(int x, int y) {
        g2d.translate(x, y);
    }

    /** {@inheritDoc} */
    public void translate(double tx, double ty) {
        g2d.translate(tx, ty);
    }

    /** {@inheritDoc} */
    public void rotate(double theta) {
        g2d.rotate(theta);
    }

    /** {@inheritDoc} */
    public void rotate(double theta, double x, double y) {
        g2d.rotate(theta, x, y);
    }

    /** {@inheritDoc} */
    public void scale(double sx, double sy) {
        g2d.scale(sx, sy);
    }

    /** {@inheritDoc} */
    public void shear(double shx, double shy) {
        g2d.shear(shx, shy);
    }

    /** {@inheritDoc} */
    public void transform(AffineTransform Tx) {
        g2d.transform(Tx);
    }

    /** {@inheritDoc} */
    public void setTransform(AffineTransform Tx) {
        g2d.setTransform(Tx);
    }

    /** {@inheritDoc} */
    public AffineTransform getTransform() {
        return g2d.getTransform();
    }

    /** {@inheritDoc} */
    public Paint getPaint() {
        return g2d.getPaint();
    }

    /** {@inheritDoc} */
    public Composite getComposite() {
        return g2d.getComposite();
    }

    /** {@inheritDoc} */
    public void setBackground(Color color) {
        g2d.setBackground(color);
    }

    /** {@inheritDoc} */
    public Color getBackground() {
        return g2d.getBackground();
    }

    /** {@inheritDoc} */
    public Stroke getStroke() {
        return g2d.getStroke();
    }

    /** {@inheritDoc} */
    public void clip(Shape s) {
        g2d.clip(s);
    }

    /** {@inheritDoc} */
    public FontRenderContext getFontRenderContext() {
        return g2d.getFontRenderContext();
    }

    /** {@inheritDoc} */
    public Graphics create() {
        return new MergedFontGraphics2D((Graphics2D) g2d.create());
    }

    /** {@inheritDoc} */
    public Color getColor() {
        return g2d.getColor();
    }

    /** {@inheritDoc} */
    public void setColor(Color c) {
        g2d.setColor(c);
    }

    /** {@inheritDoc} */
    public void setPaintMode() {
        g2d.setPaintMode();
    }

    /** {@inheritDoc} */
    public void setXORMode(Color c1) {
        g2d.setXORMode(c1);
    }

    /** {@inheritDoc} */
    public Font getFont() {
        return g2d.getFont();
    }

    /** {@inheritDoc} */
    public FontMetrics getFontMetrics(Font f) {
        return g2d.getFontMetrics(f);
    }

    /** {@inheritDoc} */
    public Rectangle getClipBounds() {
        return g2d.getClipBounds();
    }

    /** {@inheritDoc} */
    public void clipRect(int x, int y, int width, int height) {
        g2d.clipRect(x, y, width, height);
    }

    /** {@inheritDoc} */
    public void setClip(int x, int y, int width, int height) {
        g2d.setClip(x, y, width, height);
    }

    /** {@inheritDoc} */
    public Shape getClip() {
        return g2d.getClip();
    }

    /** {@inheritDoc} */
    public void setClip(Shape clip) {
        g2d.setClip(clip);
    }

    /** {@inheritDoc} */
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        g2d.copyArea(x, y, width, height, dx, dy);
    }

    /** {@inheritDoc} */
    public void drawLine(int x1, int y1, int x2, int y2) {
        g2d.drawLine(x1, y1, x2, y2);
    }

    /** {@inheritDoc} */
    public void fillRect(int x, int y, int width, int height) {
        g2d.fillRect(x, y, width, height);
    }

    /** {@inheritDoc} */
    public void clearRect(int x, int y, int width, int height) {
        g2d.clearRect(x, y, width, height);
    }

    /** {@inheritDoc} */
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth,
            int arcHeight) {
        g2d.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    /** {@inheritDoc} */
    public void fillRoundRect(int x, int y, int width, int height, int arcWidth,
            int arcHeight) {
        g2d.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    /** {@inheritDoc} */
    public void drawOval(int x, int y, int width, int height) {
        g2d.drawOval(x, y, width, height);
    }

    /** {@inheritDoc} */
    public void fillOval(int x, int y, int width, int height) {
        g2d.fillOval(x, y, width, height);
    }

    /** {@inheritDoc} */
    public void drawArc(int x, int y, int width, int height, int startAngle,
            int arcAngle) {
        g2d.drawArc(x, y, width, height, startAngle, arcAngle);
    }

    /** {@inheritDoc} */
    public void fillArc(int x, int y, int width, int height, int startAngle,
            int arcAngle) {
        g2d.fillArc(x, y, width, height, startAngle, arcAngle);
    }

    /** {@inheritDoc} */
    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
        g2d.drawPolyline(xPoints, yPoints, nPoints);
    }

    /** {@inheritDoc} */
    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        g2d.drawPolygon(xPoints, yPoints, nPoints);
    }

    /** {@inheritDoc} */
    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        g2d.fillPolygon(xPoints, yPoints, nPoints);
    }

    /** {@inheritDoc} */
    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        return g2d.drawImage(img, x, y, observer);
    }

    /** {@inheritDoc} */
    public boolean drawImage(Image img, int x, int y, int width, int height,
            ImageObserver observer) {
        return g2d.drawImage(img, x, y, width, height, observer);
    }

    /** {@inheritDoc} */
    public boolean drawImage(Image img, int x, int y, Color bgcolor,
            ImageObserver observer) {
        return g2d.drawImage(img, x, y, bgcolor, observer);
    }

    /** {@inheritDoc} */
    public boolean drawImage(Image img, int x, int y, int width, int height,
            Color bgcolor, ImageObserver observer) {
        return g2d.drawImage(img, x, y, width, height, bgcolor, observer);
    }

    /** {@inheritDoc} */
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
            int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        return g2d.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2,
                observer);
    }

    /** {@inheritDoc} */
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
            int sx1, int sy1, int sx2, int sy2, Color bgcolor,
            ImageObserver observer) {
        return g2d.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2,
                bgcolor, observer);
    }
}
