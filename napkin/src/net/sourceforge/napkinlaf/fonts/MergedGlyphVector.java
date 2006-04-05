package net.sourceforge.napkinlaf.fonts;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.util.ArrayList;

/**
 * This is used in the implementation of {@link MergedFont}.
 *
 * @see MergedFont
 */
class MergedGlyphVector extends GlyphVector {
    private final Font font;
    private final FontRenderContext frc;
    private final java.util.List<GlyphInfo> glyphs = new ArrayList<GlyphInfo>();

    private static class GlyphInfo {
        public final int code;
        public final Shape outline;
        public Point2D position;
        public AffineTransform transform;
        public final Shape logicalBounds;
        public final Shape visualBounds;
        public final GlyphMetrics metrics;
        public final GlyphJustificationInfo info;
        public final Font font;

        public GlyphInfo(int code, Shape outline, Point2D position,
                AffineTransform transform, Shape logicalBounds,
                Shape visualBounds, GlyphMetrics metrics,
                GlyphJustificationInfo info, Font font) {

            this.code = code;
            this.outline = outline;
            this.position = position;
            this.transform = transform;
            this.logicalBounds = logicalBounds;
            this.visualBounds = visualBounds;
            this.metrics = metrics;
            this.info = info;
            this.font = font;
        }
    }

    public MergedGlyphVector(Font font, FontRenderContext frc) {
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
            result[i * 2] = (float) point.getX();
            result[i * 2 + 1] = (float) point.getY();
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

    public GlyphJustificationInfo getGlyphJustificationInfo(
            int glyphIndex) {
        return glyphs.get(glyphIndex).info;
    }

    public boolean equals(GlyphVector set) {
        for (int i = 0, n = getNumGlyphs(); i < n; i++) {
            if (getGlyphCode(i) != set.getGlyphCode(i))
                return false;
        }
        return true;
    }

    public Font getGlyphFont(int glyphIndex) {
        return glyphs.get(glyphIndex).font;
    }

    public void appendGlyph(int code, Shape outline, Point2D position,
            AffineTransform transform, Shape logicalBounds,
            Shape visualBounds, GlyphMetrics metrics,
            GlyphJustificationInfo info, Font font) {
        GlyphInfo glyph = new GlyphInfo(code, outline, position, transform,
                logicalBounds, visualBounds, metrics, info, font);
        glyphs.add(glyph);
    }

    public java.util.List<MergedGlyphVector> split() {
        java.util.List<MergedGlyphVector> list =
                new ArrayList<MergedGlyphVector>();
        final int glyphCount = glyphs.size();
        if (glyphCount > 0) {
            int topStartIndex = 0;
            for (int i = 0; i < glyphCount; i++) {
                GlyphInfo info = glyphs.get(i);
                if (!font.equals(info.font)) {
                    if (topStartIndex < i) {
                        MergedGlyphVector cgv =
                                new MergedGlyphVector(font, frc);
                        cgv.glyphs.addAll(glyphs.subList(topStartIndex, i));
                        list.add(cgv);
                    }
                    MergedGlyphVector cgv =
                            new MergedGlyphVector(info.font, frc);
                    cgv.glyphs.add(glyphs.get(i));
                    list.add(cgv);
                    topStartIndex = i + 1;
                }
            }
            if (topStartIndex < glyphCount) {
                MergedGlyphVector cgv = new MergedGlyphVector(font, frc);
                cgv.glyphs.addAll(glyphs.subList(topStartIndex, glyphCount));
                list.add(cgv);
            }
        }
        return list;
    }
}