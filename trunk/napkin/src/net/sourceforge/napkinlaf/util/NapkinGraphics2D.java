/*
 * NapkinGraphics2D.java
 *
 * Created on 31 March 2006, 00:46
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sourceforge.napkinlaf.util;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

/**
 *
 * @author Alex Lam Sze Lok
 */
public class NapkinGraphics2D extends Graphics2D {

    private final Graphics2D g2d;
    
    /** Creates a new instance of NapkinGraphics2D */
    public NapkinGraphics2D(Graphics2D g2d) {
        this.g2d = g2d;
    }

    public void draw(Shape s) {
        g2d.draw(s);
    }

    public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
        return g2d.drawImage(img, xform, obs);
    }

    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
        g2d.drawImage(img, op, x, y);
    }

    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
        g2d.drawRenderedImage(img, xform);
    }

    public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
        g2d.drawRenderableImage(img, xform);
    }

    public void drawString(String str, int x, int y) {
        drawString(str, (float) x, (float) y);
    }

    public void drawString(String s, float x, float y) {
        Font font = getFont();
        FontRenderContext frc = getFontRenderContext();
        GlyphVector gVector = font.createGlyphVector(frc, s);
        g2d.drawGlyphVector(gVector, x, y);
    }

    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        drawString(iterator, (float) x, (float) y);
    }

    public void drawString(AttributedCharacterIterator iterator, float x, float y) {
        Font font = getFont();
        FontRenderContext frc = getFontRenderContext();
        GlyphVector gVector = font.createGlyphVector(frc, iterator);
        g2d.drawGlyphVector(gVector, x, y);
    }

    public void drawGlyphVector(GlyphVector g, float x, float y) {
        g2d.fill(g.getOutline(x, y));
    }

    public void fill(Shape s) {
        g2d.fill(s);
    }

    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        return g2d.hit(rect, s, onStroke);
    }

    public GraphicsConfiguration getDeviceConfiguration() {
        return g2d.getDeviceConfiguration();
    }

    public void setComposite(Composite comp) {
        g2d.setComposite(comp);
    }

    public void setPaint(Paint paint) {
        g2d.setPaint(paint);
    }

    public void setStroke(Stroke s) {
        g2d.setStroke(s);
    }

    public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) {
        g2d.setRenderingHint(hintKey, hintValue);
    }

    public Object getRenderingHint(RenderingHints.Key hintKey) {
        return g2d.getRenderingHint(hintKey);
    }

    public void setRenderingHints(Map<?, ?> hints) {
        g2d.setRenderingHints(hints);
    }

    public void addRenderingHints(Map<?, ?> hints) {
        g2d.addRenderingHints(hints);
    }

    public RenderingHints getRenderingHints() {
        return g2d.getRenderingHints();
    }

    public void translate(int x, int y) {
        g2d.translate(x, y);
    }

    public void translate(double tx, double ty) {
        g2d.translate(tx, ty);
    }

    public void rotate(double theta) {
        g2d.rotate(theta);
    }

    public void rotate(double theta, double x, double y) {
        g2d.rotate(theta, x, y);
    }

    public void scale(double sx, double sy) {
        g2d.scale(sx, sy);
    }

    public void shear(double shx, double shy) {
        g2d.shear(shx, shy);
    }

    public void transform(AffineTransform Tx) {
        g2d.transform(Tx);
    }

    public void setTransform(AffineTransform Tx) {
        g2d.setTransform(Tx);
    }

    public AffineTransform getTransform() {
        return g2d.getTransform();
    }

    public Paint getPaint() {
        return g2d.getPaint();
    }

    public Composite getComposite() {
        return g2d.getComposite();
    }

    public void setBackground(Color color) {
        g2d.setBackground(color);
    }

    public Color getBackground() {
        return g2d.getBackground();
    }

    public Stroke getStroke() {
        return g2d.getStroke();
    }

    public void clip(Shape s) {
        g2d.clip(s);
    }

    public FontRenderContext getFontRenderContext() {
        return g2d.getFontRenderContext();
    }

    public Graphics create() {
        return new NapkinGraphics2D((Graphics2D) g2d.create());
    }

    public Color getColor() {
        return g2d.getColor();
    }

    public void setColor(Color c) {
        g2d.setColor(c);
    }

    public void setPaintMode() {
        g2d.setPaintMode();
    }

    public void setXORMode(Color c1) {
        g2d.setXORMode(c1);
    }

    public Font getFont() {
        return g2d.getFont();
    }

    public void setFont(Font font) {
        g2d.setFont(font);
    }

    public FontMetrics getFontMetrics(Font f) {
        return g2d.getFontMetrics(f);
    }

    public Rectangle getClipBounds() {
        return g2d.getClipBounds();
    }

    public void clipRect(int x, int y, int width, int height) {
        g2d.clipRect(x, y, width, height);
    }

    public void setClip(int x, int y, int width, int height) {
        g2d.setClip(x, y, width, height);
    }

    public Shape getClip() {
        return g2d.getClip();
    }

    public void setClip(Shape clip) {
        g2d.setClip(clip);
    }

    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        g2d.copyArea(x, y, width, height, dx, dy);
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        g2d.drawLine(x1, y1, x2, y2);
    }

    public void fillRect(int x, int y, int width, int height) {
        g2d.fillRect(x, y, width, height);
    }

    public void clearRect(int x, int y, int width, int height) {
        g2d.clearRect(x, y, width, height);
    }

    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        g2d.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        g2d.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    public void drawOval(int x, int y, int width, int height) {
        g2d.drawOval(x, y, width, height);
    }

    public void fillOval(int x, int y, int width, int height) {
        g2d.fillOval(x, y, width, height);
    }

    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        g2d.drawArc(x, y, width, height, startAngle, arcAngle);
    }

    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        g2d.fillArc(x, y, width, height, startAngle, arcAngle);
    }

    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
        g2d.drawPolyline(xPoints, yPoints, nPoints);
    }

    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        g2d.drawPolygon(xPoints, yPoints, nPoints);
    }

    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        g2d.fillPolygon(xPoints, yPoints, nPoints);
    }

    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        return g2d.drawImage(img, x, y, observer);
    }

    public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
        return g2d.drawImage(img, x, y, width, height, observer);
    }

    public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
        return g2d.drawImage(img, x, y, bgcolor, observer);
    }

    public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
        return g2d.drawImage(img, x, y, width, height, bgcolor, observer);
    }

    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        return g2d.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
    }

    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
        return g2d.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);
    }

    public void dispose() {
        g2d.dispose();
    }
    
}
