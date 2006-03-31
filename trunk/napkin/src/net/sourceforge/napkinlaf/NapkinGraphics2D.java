package net.sourceforge.napkinlaf;

import java.awt.*;
import java.awt.RenderingHints.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.image.renderable.*;
import java.text.AttributedCharacterIterator;
import java.util.Map;

public abstract class NapkinGraphics2D {
    private final Graphics2D graphics2D;

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
    protected NapkinGraphics2D(Graphics2D graphics2D) {
        this.graphics2D = graphics2D;
    }

    public abstract void draw(Shape s);

    public abstract boolean drawImage(Image img,
                                      AffineTransform xform,
                                      ImageObserver obs);

    public abstract void drawImage(BufferedImage img,
                                   BufferedImageOp op,
                                   int x,
                                   int y);

    public abstract void drawRenderedImage(RenderedImage img,
                                           AffineTransform xform);

    public abstract void drawRenderableImage(RenderableImage img,
                                             AffineTransform xform);

    public abstract void drawString(String str, int x, int y);

    public abstract void drawString(String s, float x, float y);

    public abstract void drawString(AttributedCharacterIterator iterator,
                                    int x, int y);

    public abstract void drawString(AttributedCharacterIterator iterator,
                                    float x, float y);

    public abstract void drawGlyphVector(GlyphVector g, float x, float y);

    public abstract void fill(Shape s);

    public abstract boolean hit(Rectangle rect,
                                Shape s,
                                boolean onStroke);

    public abstract GraphicsConfiguration getDeviceConfiguration();

    public abstract void setComposite(Composite comp);

    public abstract void setPaint( Paint paint );

    public abstract void setStroke(Stroke s);

    public abstract void setRenderingHint(Key hintKey, Object hintValue);

    public abstract Object getRenderingHint(Key hintKey);

    public abstract void setRenderingHints(Map<?,?> hints);

    public abstract void addRenderingHints(Map<?,?> hints);

    public abstract RenderingHints getRenderingHints();

    public abstract void translate(int x, int y);

    public abstract void translate(double tx, double ty);

    public abstract void rotate(double theta);

    public abstract void rotate(double theta, double x, double y);

    public abstract void scale(double sx, double sy);

    public abstract void shear(double shx, double shy);

    public abstract void transform(AffineTransform Tx);

    public abstract void setTransform(AffineTransform Tx);

    public abstract AffineTransform getTransform();

    public abstract Paint getPaint();

    public abstract Composite getComposite();

    public abstract void setBackground(Color color);

    public abstract Color getBackground();

    public abstract Stroke getStroke();

    public abstract void clip(Shape s);

    public abstract FontRenderContext getFontRenderContext();

    public abstract Graphics create();

    public abstract Color getColor();

    public abstract void setColor(Color c);

    public abstract void setPaintMode();

    public abstract void setXORMode(Color c1);

    public abstract Font getFont();

    public abstract void setFont(Font font);

    public abstract FontMetrics getFontMetrics(Font f);

    public abstract Rectangle getClipBounds();

    public abstract void clipRect(int x, int y, int width, int height);

    public abstract void setClip(int x, int y, int width, int height);

    public abstract Shape getClip();

    public abstract void setClip(Shape clip);

    public abstract void copyArea(int x, int y, int width, int height,
                                  int dx, int dy);

    public abstract void drawLine(int x1, int y1, int x2, int y2);

    public abstract void fillRect(int x, int y, int width, int height);

    public abstract void clearRect(int x, int y, int width, int height);

    public abstract void drawRoundRect(int x, int y, int width, int height,
                                       int arcWidth, int arcHeight);

    public abstract void fillRoundRect(int x, int y, int width, int height,
                                       int arcWidth, int arcHeight);

    public abstract void drawOval(int x, int y, int width, int height);

    public abstract void fillOval(int x, int y, int width, int height);

    public abstract void drawArc(int x, int y, int width, int height,
                                 int startAngle, int arcAngle);

    public abstract void fillArc(int x, int y, int width, int height,
                                 int startAngle, int arcAngle);

    public abstract void drawPolyline(int xPoints[], int yPoints[],
                                      int nPoints);

    public abstract void drawPolygon(int xPoints[], int yPoints[],
                                     int nPoints);

    public abstract void fillPolygon(int xPoints[], int yPoints[],
                                     int nPoints);

    public abstract boolean drawImage(Image img, int x, int y,
                                      ImageObserver observer);

    public abstract boolean drawImage(Image img, int x, int y,
                                      int width, int height,
                                      ImageObserver observer);

    public abstract boolean drawImage(Image img, int x, int y,
                                      Color bgcolor,
                                      ImageObserver observer);

    public abstract boolean drawImage(Image img, int x, int y,
                                      int width, int height,
                                      Color bgcolor,
                                      ImageObserver observer);

    public abstract boolean drawImage(Image img,
                                      int dx1, int dy1, int dx2, int dy2,
                                      int sx1, int sy1, int sx2, int sy2,
                                      ImageObserver observer);

    public abstract boolean drawImage(Image img,
                                      int dx1, int dy1, int dx2, int dy2,
                                      int sx1, int sy1, int sx2, int sy2,
                                      Color bgcolor,
                                      ImageObserver observer);

    public abstract void dispose();

    public Graphics2D getGraphics2D() {
        return graphics2D;
    }

    public Graphics create(int x, int y, int width, int height) {
        return graphics2D.create(x, y, width, height);
    }

    public FontMetrics getFontMetrics() {
        return graphics2D.getFontMetrics();
    }

    public void drawRect(int x, int y, int width, int height) {
        graphics2D.drawRect(x, y, width, height);
    }

    public void drawPolygon(Polygon p) {
        graphics2D.drawPolygon(p);
    }

    public void fillPolygon(Polygon p) {
        graphics2D.fillPolygon(p);
    }

    public void drawChars(char[] data, int offset, int length, int x, int y) {
        graphics2D.drawChars(data, offset, length, x, y);
    }

    public void drawBytes(byte[] data, int offset, int length, int x, int y) {
        graphics2D.drawBytes(data, offset, length, x, y);
    }

    public String toString() {
        return graphics2D.toString();
    }

    public Rectangle getClipRect() {
        return graphics2D.getClipRect();
    }

    public boolean hitClip(int x, int y, int width, int height) {
        return graphics2D.hitClip(x, y, width, height);
    }

    public Rectangle getClipBounds(Rectangle r) {
        return graphics2D.getClipBounds(r);
    }

    public void draw3DRect(int x, int y, int width, int height,
            boolean raised) {
        graphics2D.draw3DRect(x, y, width, height, raised);
    }

    public void fill3DRect(int x, int y, int width, int height,
            boolean raised) {
        graphics2D.fill3DRect(x, y, width, height, raised);
    }
}