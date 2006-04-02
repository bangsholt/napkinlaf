// $Id$

/*
 * NapkinFont.java
 *
 * Created on 25 February 2006, 10:42
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sourceforge.napkinlaf.util;

import java.awt.font.FontRenderContext;
import java.awt.font.GlyphJustificationInfo;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.CharacterIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.plaf.*;
import java.awt.*;
import java.lang.reflect.Field;

/**
 * Until Mustang there is Bug 6313541 which prevents the bundled fonts to load
 * (because font2DHandle is not transferred when calling FontUIResource)
 * <p/>
 * Fixed by a workaround using Reflection - which might not work for applets and
 * Web Start applications, so here I've put in checks so workaround is used only
 * when needed.
 *
 * @author Alex Lam Sze Lok
 */
public class NapkinFont extends Font implements UIResource {

    static class CompositeGlyphVector extends GlyphVector {
        private static class GlyphInfo {
            public final int code;
            public final Shape outline;
            public Point2D position;
            public AffineTransform transform;
            public final Shape logicalBounds;
            public final Shape visualBounds;
            public final GlyphMetrics metrics;
            public final GlyphJustificationInfo info;
            public GlyphInfo(int code, Shape outline, Point2D position,
                    AffineTransform transform, Shape logicalBounds,
                    Shape visualBounds, GlyphMetrics metrics,
                    GlyphJustificationInfo info) {
                this.code = code;
                this.outline = outline;
                this.position = position;
                this.transform = transform;
                this.logicalBounds = logicalBounds;
                this.visualBounds = visualBounds;
                this.metrics = metrics;
                this.info = info;
            }
        }

        private final Font font;
        private final FontRenderContext frc;
        private final List<GlyphInfo> glyphs = new ArrayList<GlyphInfo>();

        public CompositeGlyphVector(Font font, FontRenderContext frc) {
            this.font = font;
            this.frc = frc;
        }

        public Font getFont() {
            return font;
        }

        public FontRenderContext getFontRenderContext() {
            return frc;
        }

        public void performDefaultLayout() {
        }

        public int getNumGlyphs() {
            return glyphs.size();
        }

        public int getGlyphCode(int glyphIndex) {
            return glyphs.get(glyphIndex).code;
        }

        public int[] getGlyphCodes(int beginGlyphIndex, int numEntries,
                int[] codeReturn) {
            int[] result = codeReturn == null ?
                    new int[numEntries] : codeReturn;
            for (int i = 0; i < numEntries; i++) {
                result[i] = getGlyphCode(beginGlyphIndex + i);
            }
            return result;
        }

        public Rectangle2D getLogicalBounds() {
            if (getNumGlyphs() == 0)
                return new Rectangle();
            return getGlyphLogicalBounds(0).getBounds2D().createUnion(
                    getGlyphLogicalBounds(getNumGlyphs() - 1).getBounds2D());
        }

        public Rectangle2D getVisualBounds() {
            if (getNumGlyphs() == 0)
                return new Rectangle();
            return getGlyphVisualBounds(0).getBounds2D().createUnion(
                    getGlyphVisualBounds(getNumGlyphs() - 1).getBounds2D());
        }

        public Shape getOutline() {
            return getOutline(0f, 0f);
        }

        public Shape getOutline(float x, float y) {
            GeneralPath result = new GeneralPath();
            Point2D point;
            for (int i = 0, n = getNumGlyphs(); i < n; i++) {
                point = getGlyphPosition(i);
                result.append(getGlyphOutline(i, x, y), false);
            }
            return result;
        }

        public Shape getGlyphOutline(int glyphIndex) {
            return glyphs.get(glyphIndex).outline;
        }

        public Point2D getGlyphPosition(int glyphIndex) {
            return glyphs.get(glyphIndex).position;
        }

        public void setGlyphPosition(int glyphIndex, Point2D newPos) {
            glyphs.get(glyphIndex).position = newPos;
        }

        public AffineTransform getGlyphTransform(int glyphIndex) {
            return glyphs.get(glyphIndex).transform;
        }

        public void setGlyphTransform(int glyphIndex, AffineTransform newTX) {
            glyphs.get(glyphIndex).transform = newTX;
        }

        public float[] getGlyphPositions(int beginGlyphIndex, int numEntries,
                float[] positionReturn) {
            float[] result = positionReturn == null ?
                    new float[numEntries * 2] : positionReturn;
            Point2D point;
            for (int i = 0; i < numEntries; i++) {
                point = getGlyphPosition(beginGlyphIndex + i);
                result[i*2] = (float) point.getX();
                result[i*2 + 1] = (float) point.getY();
            }
            return result;
        }

        public Shape getGlyphLogicalBounds(int glyphIndex) {
            return glyphs.get(glyphIndex).logicalBounds;
        }

        public Shape getGlyphVisualBounds(int glyphIndex) {
            return glyphs.get(glyphIndex).visualBounds;
        }

        public GlyphMetrics getGlyphMetrics(int glyphIndex) {
            return glyphs.get(glyphIndex).metrics;
        }

        public GlyphJustificationInfo getGlyphJustificationInfo(int glyphIndex) {
            return glyphs.get(glyphIndex).info;
        }

        public boolean equals(GlyphVector set) {
            for (int i = 0, n = getNumGlyphs(); i < n; i++) {
                if (getGlyphCode(i) != set.getGlyphCode(i))
                    return false;
            }
            return true;
        }

        public void appendGlyph(int code, Shape outline, Point2D position,
                AffineTransform transform, Shape logicalBounds,
                Shape visualBounds, GlyphMetrics metrics,
                GlyphJustificationInfo info) {
            GlyphInfo glyph = new GlyphInfo(code, outline, position, transform,
                    logicalBounds, visualBounds, metrics, info);
            glyphs.add(glyph);
        }
    }

    private final List<Font> fonts = new ArrayList<Font>();

    public NapkinFont(String name, int style, int size) {
        super(name, style, size);
    }

    public NapkinFont(Font font) {
        super(font.getAttributes());
        if (!getFontName().equals(font.getFontName())) {
            try {
                Field field = Font.class.getDeclaredField("font2DHandle");
                field.setAccessible(true);
                field.set(this, field.get(font));
                field.setAccessible(false);
                field = Font.class.getDeclaredField("createdFont");
                field.setAccessible(true);
                field.set(this, field.get(font));
                field.setAccessible(false);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            } catch (SecurityException ex) {
                ex.printStackTrace();
            } catch (NoSuchFieldException ex) {
                ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
    }

    public NapkinFont(Font font, Font ... fonts) {
        this(font);
        this.fonts.addAll(Arrays.asList(fonts));
    }

    private GlyphVector processGlyphVector(FontRenderContext frc,
            GlyphVector gVector, GlyphVector[] gVectors) {
        int i, j;
        final int l = gVector.getNumGlyphs(), n = fonts.size();
        // if no glyphs or we only have a single font, just return
        if (l == 0 || n == 0) { // (n == 0) is not strictly necessary
            return gVector;
        }
        int badCode = getMissingGlyphCode();
        for (i = 0; i < l; i++) {
            if (gVector.getGlyphCode(i) == badCode) {
                break;
            }
        }
        // if we don't have any bad glyphs, just return
        if (i == l) {
            return gVector;
        }
        
        // we do have bad glyphs; scan through the font chain for replacement
        int[] badCodes = new int[n];
        for (j = 0; j < n; j++) {
            badCodes[j] = fonts.get(j).getMissingGlyphCode();
        }
        CompositeGlyphVector result = new CompositeGlyphVector(this, frc);
        Point2D curPos, origPos;
        GlyphVector curGVector;
        boolean replaced = false;
        for (i = 0; i < l; i++) {
            // look for the GlyphVector with non-bad glyph
            // fall back to top font's bad glyph if failed
            curGVector = gVector;
            if (gVector.getGlyphCode(i) == badCode) {
                for (j = 0; j < n; j++) {
                    if (gVectors[j].getGlyphCode(i) != badCodes[j]) {
                        curGVector = gVectors[j];
                        replaced = true;
                        break;
                    }
                }
            }
            // prepare matrix for glyph paremater transformation
            origPos = curGVector.getGlyphPosition(i);
            curPos = gVector.getGlyphPosition(i);
            AffineTransform matrix = AffineTransform.getTranslateInstance(
                    curPos.getX() - origPos.getX(), curPos.getY() - origPos.getY());
            // transform glyph parameters to its designated position
            Shape outline = curGVector.getGlyphOutline(i);
            Shape logicalBounds = curGVector.getGlyphLogicalBounds(i);
            Shape visualBounds = curGVector.getGlyphVisualBounds(i);
            outline = matrix.createTransformedShape(outline);
            logicalBounds = matrix.createTransformedShape(logicalBounds);
            visualBounds = matrix.createTransformedShape(visualBounds);
            // transform GlyphMetrics
            GlyphMetrics metrics = curGVector.getGlyphMetrics(i);
            float advanceX = metrics.getAdvanceX();
            float advanceY = metrics.getAdvanceY();
            Rectangle2D metricsBounds = metrics.getBounds2D();
            metricsBounds.setRect(curPos.getX(), curPos.getY(),
                    metricsBounds.getWidth(), metricsBounds.getHeight());
            metrics = new GlyphMetrics(metrics.getAdvance() == advanceX,
                    advanceX, metrics.getAdvanceY(), metricsBounds,
                    (byte) metrics.getType());
            // add transformed glyph into our GlyphVector
            result.appendGlyph(curGVector.getGlyphCode(i),
                    outline, curPos, curGVector.getGlyphTransform(i),
                    logicalBounds, visualBounds, metrics,
                    curGVector.getGlyphJustificationInfo(i));
        }
        return replaced ? result : gVector;
    }
    
    @Override
    public GlyphVector createGlyphVector(FontRenderContext frc, char[] chars) {
        GlyphVector gVector = super.createGlyphVector(frc, chars);
        int n = fonts.size();
        if (n == 0) {
            return gVector;
        }
        GlyphVector[] gVectors = new GlyphVector[n];
        for (int i = 0; i < n; i++) {
            gVectors[i] = fonts.get(i).createGlyphVector(frc, chars);
        }
        return processGlyphVector(frc, gVector, gVectors);
    }

    @Override
    public GlyphVector createGlyphVector(FontRenderContext frc, String str) {
        GlyphVector gVector = super.createGlyphVector(frc, str);
        int n = fonts.size();
        if (n == 0) {
            return gVector;
        }
        GlyphVector[] gVectors = new GlyphVector[n];
        for (int i = 0; i < n; i++) {
            gVectors[i] = fonts.get(i).createGlyphVector(frc, str);
        }
        return processGlyphVector(frc, gVector, gVectors);
    }

    @Override
    public GlyphVector createGlyphVector(FontRenderContext frc,
            CharacterIterator ci) {
        GlyphVector gVector = super.createGlyphVector(frc, ci);
        int n = fonts.size();
        if (n == 0) {
            return gVector;
        }
        GlyphVector[] gVectors = new GlyphVector[n];
        for (int i = 0; i < n; i++) {
            gVectors[i] = fonts.get(i).createGlyphVector(frc, ci);
        }
        return processGlyphVector(frc, gVector, gVectors);
    }

    @Override
    public GlyphVector createGlyphVector(FontRenderContext frc,
            int[] glyphCodes) {
        GlyphVector gVector = super.createGlyphVector(frc, glyphCodes);
        int n = fonts.size();
        if (n == 0) {
            return gVector;
        }
        GlyphVector[] gVectors = new GlyphVector[n];
        for (int i = 0; i < n; i++) {
            gVectors[i] = fonts.get(i).createGlyphVector(frc, glyphCodes);
        }
        return processGlyphVector(frc, gVector, gVectors);
    }

    @Override
    public GlyphVector layoutGlyphVector(FontRenderContext frc,
            char[] text, int start, int limit, int flags) {
        GlyphVector gVector = super.layoutGlyphVector(
                frc, text, start, limit, flags);
        int n = fonts.size();
        if (n == 0) {
            return gVector;
        }
        GlyphVector[] gVectors = new GlyphVector[n];
        for (int i = 0; i < n; i++) {
            gVectors[i] = fonts.get(i).layoutGlyphVector(
                    frc, text, start, limit, flags);
        }
        return processGlyphVector(frc, gVector, gVectors);
    }

    public boolean canDisplay(char c) {
        if (super.canDisplay(c)) {
            return true;
        }
        for (Font font : fonts) {
            if (font.canDisplay(c)) {
                return true;
            }
        }
        return false;
    }

    public boolean canDisplay(int codePoint) {
        if (super.canDisplay(codePoint)) {
            return true;
        }
        for (Font font : fonts) {
            if (font.canDisplay(codePoint)) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        if (!isComposite())
            return super.toString();
        StringBuilder result = new StringBuilder("CompositeFont{");
        result.append(super.toString());
        for (Font font : fonts) {
            result.append("; ").append(font.toString());
        }
        return result.append("}").toString();
    }

    public boolean isComposite() {
        return !fonts.isEmpty();
    }

}
