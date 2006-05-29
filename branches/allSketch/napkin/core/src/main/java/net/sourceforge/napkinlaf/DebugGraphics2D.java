package net.sourceforge.napkinlaf;

import javax.swing.*;
import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.image.renderable.*;
import java.lang.reflect.Field;
import java.text.AttributedCharacterIterator;
import java.util.Map;

@SuppressWarnings({"deprecation"})
public class DebugGraphics2D extends Graphics2D {
    private final DebugGraphics debugGraphics;
    private final Graphics2D g;

    private static final Field GRAPHICS_FIELD;

    static {
        try {
            Class<DebugGraphics> dgClass = DebugGraphics.class;
            GRAPHICS_FIELD = dgClass.getDeclaredField("graphics");
            GRAPHICS_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public DebugGraphics2D(DebugGraphics dg) {
        if (dg == null)
            throw new NullPointerException("dg");
        debugGraphics = dg;
        g = extractGraphics(dg);
        if (g == null)
            throw new NullPointerException("g");
    }

    private static Graphics2D extractGraphics(DebugGraphics dg) {
        try {
            return (Graphics2D) GRAPHICS_FIELD.get(dg);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    public boolean isDrawingBuffer() {
        return debugGraphics.isDrawingBuffer();
    }

    public void setDebugOptions(int options) {
        debugGraphics.setDebugOptions(options);
    }

    public int getDebugOptions() {
        return debugGraphics.getDebugOptions();
    }

    public Graphics create() {
        return new DebugGraphics2D((DebugGraphics) debugGraphics.create());
    }

    public Graphics create(int x, int y, int width, int height) {
        return new DebugGraphics2D(
                (DebugGraphics) debugGraphics.create(x, y, width, height));
    }

    public void translate(int x, int y) {
        debugGraphics.translate(x, y);
    }

    public Color getColor() {
        return debugGraphics.getColor();
    }

    public void setColor(Color c) {
        debugGraphics.setColor(c);
    }

    public void setPaintMode() {
        debugGraphics.setPaintMode();
    }

    public void setXORMode(Color c1) {
        debugGraphics.setXORMode(c1);
    }

    public Font getFont() {
        return debugGraphics.getFont();
    }

    public void setFont(Font font) {
        debugGraphics.setFont(font);
    }

    public FontMetrics getFontMetrics() {
        return debugGraphics.getFontMetrics();
    }

    public FontMetrics getFontMetrics(Font f) {
        return debugGraphics.getFontMetrics(f);
    }

    public Rectangle getClipBounds() {
        return debugGraphics.getClipBounds();
    }

    public void clipRect(int x, int y, int width, int height) {
        debugGraphics.clipRect(x, y, width, height);
    }

    public void setClip(int x, int y, int width, int height) {
        debugGraphics.setClip(x, y, width, height);
    }

    public Shape getClip() {
        return debugGraphics.getClip();
    }

    public void setClip(Shape clip) {
        debugGraphics.setClip(clip);
    }

    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        debugGraphics.copyArea(x, y, width, height, dx, dy);
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        debugGraphics.drawLine(x1, y1, x2, y2);
    }

    public void fillRect(int x, int y, int width, int height) {
        debugGraphics.fillRect(x, y, width, height);
    }

    public void drawRect(int x, int y, int width, int height) {
        debugGraphics.drawRect(x, y, width, height);
    }

    public void clearRect(int x, int y, int width, int height) {
        debugGraphics.clearRect(x, y, width, height);
    }

    public void drawRoundRect(int x, int y, int width, int height, int arcWidth,
            int arcHeight) {
        debugGraphics.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    public void fillRoundRect(int x, int y, int width, int height, int arcWidth,
            int arcHeight) {
        debugGraphics.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    public void draw3DRect(int x, int y, int width, int height,
            boolean raised) {
        debugGraphics.draw3DRect(x, y, width, height, raised);
    }

    public void fill3DRect(int x, int y, int width, int height,
            boolean raised) {
        debugGraphics.fill3DRect(x, y, width, height, raised);
    }

    public void drawOval(int x, int y, int width, int height) {
        debugGraphics.drawOval(x, y, width, height);
    }

    public void fillOval(int x, int y, int width, int height) {
        debugGraphics.fillOval(x, y, width, height);
    }

    public void drawArc(int x, int y, int width, int height, int startAngle,
            int arcAngle) {
        debugGraphics.drawArc(x, y, width, height, startAngle, arcAngle);
    }

    public void fillArc(int x, int y, int width, int height, int startAngle,
            int arcAngle) {
        debugGraphics.fillArc(x, y, width, height, startAngle, arcAngle);
    }

    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
        debugGraphics.drawPolyline(xPoints, yPoints, nPoints);
    }

    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        debugGraphics.drawPolygon(xPoints, yPoints, nPoints);
    }

    public void drawPolygon(Polygon p) {
        debugGraphics.drawPolygon(p);
    }

    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        debugGraphics.fillPolygon(xPoints, yPoints, nPoints);
    }

    public void fillPolygon(Polygon p) {
        debugGraphics.fillPolygon(p);
    }

    public void drawString(String str, int x, int y) {
        debugGraphics.drawString(str, x, y);
    }

    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        debugGraphics.drawString(iterator, x, y);
    }

    public void drawChars(char[] data, int offset, int length, int x, int y) {
        debugGraphics.drawChars(data, offset, length, x, y);
    }

    public void drawBytes(byte[] data, int offset, int length, int x, int y) {
        debugGraphics.drawBytes(data, offset, length, x, y);
    }

    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        return debugGraphics.drawImage(img, x, y, observer);
    }

    public boolean drawImage(Image img, int x, int y, int width, int height,
            ImageObserver observer) {
        return debugGraphics.drawImage(img, x, y, width, height, observer);
    }

    public boolean drawImage(Image img, int x, int y, Color bgcolor,
            ImageObserver observer) {
        return debugGraphics.drawImage(img, x, y, bgcolor, observer);
    }

    public boolean drawImage(Image img, int x, int y, int width, int height,
            Color bgcolor, ImageObserver observer) {
        return debugGraphics.drawImage(img, x, y, width, height, bgcolor, observer);
    }

    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
            int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        return debugGraphics.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2,
                observer);
    }

    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
            int sx1, int sy1, int sx2, int sy2, Color bgcolor,
            ImageObserver observer) {
        return debugGraphics.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor,
                observer);
    }

    public void dispose() {
        debugGraphics.dispose();
    }

    public Rectangle getClipRect() {
        return debugGraphics.getClipRect();
    }

    public boolean hitClip(int x, int y, int width, int height) {
        return debugGraphics.hitClip(x, y, width, height);
    }

    public Rectangle getClipBounds(Rectangle r) {
        return debugGraphics.getClipBounds(r);
    }

    public void draw(Shape s) {
        g.draw(s);
    }

    public boolean drawImage(Image img, AffineTransform xform,
            ImageObserver obs) {
        return g.drawImage(img, xform, obs);
    }

    public void drawImage(BufferedImage img, BufferedImageOp op, int x,
            int y) {
        g.drawImage(img, op, x, y);
    }

    public void drawRenderedImage(RenderedImage img,
            AffineTransform xform) {
        g.drawRenderedImage(img, xform);
    }

    public void drawRenderableImage(RenderableImage img,
            AffineTransform xform) {
        g.drawRenderableImage(img, xform);
    }

    public void drawString(String s, float x, float y) {
        g.drawString(s, x, y);
    }

    public void drawString(AttributedCharacterIterator iterator, float x,
            float y) {
        g.drawString(iterator, x, y);
    }

    public void drawGlyphVector(GlyphVector g, float x, float y) {
        this.g.drawGlyphVector(g, x, y);
    }

    public void fill(Shape s) {
        g.fill(s);
    }

    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        return g.hit(rect, s, onStroke);
    }

    public GraphicsConfiguration getDeviceConfiguration() {
        return g.getDeviceConfiguration();
    }

    public void setComposite(Composite comp) {
        g.setComposite(comp);
    }

    public void setPaint(Paint paint) {
        g.setPaint(paint);
    }

    public void setStroke(Stroke s) {
        g.setStroke(s);
    }

    public void setRenderingHint(RenderingHints.Key hintKey,
            Object hintValue) {
        g.setRenderingHint(hintKey, hintValue);
    }

    public Object getRenderingHint(RenderingHints.Key hintKey) {
        return g.getRenderingHint(hintKey);
    }

    public void setRenderingHints(Map<?, ?> hints) {
        g.setRenderingHints(hints);
    }

    public void addRenderingHints(Map<?, ?> hints) {
        g.addRenderingHints(hints);
    }

    public RenderingHints getRenderingHints() {
        return g.getRenderingHints();
    }

    public void translate(double tx, double ty) {
        g.translate(tx, ty);
    }

    public void rotate(double theta) {
        g.rotate(theta);
    }

    public void rotate(double theta, double x, double y) {
        g.rotate(theta, x, y);
    }

    public void scale(double sx, double sy) {
        g.scale(sx, sy);
    }

    public void shear(double shx, double shy) {
        g.shear(shx, shy);
    }

    public void transform(AffineTransform Tx) {
        g.transform(Tx);
    }

    public void setTransform(AffineTransform Tx) {
        g.setTransform(Tx);
    }

    public AffineTransform getTransform() {
        return g.getTransform();
    }

    public Paint getPaint() {
        return g.getPaint();
    }

    public Composite getComposite() {
        return g.getComposite();
    }

    public void setBackground(Color color) {
        g.setBackground(color);
    }

    public Color getBackground() {
        return g.getBackground();
    }

    public Stroke getStroke() {
        return g.getStroke();
    }

    public void clip(Shape s) {
        g.clip(s);
    }

    public FontRenderContext getFontRenderContext() {
        return g.getFontRenderContext();
    }
}
