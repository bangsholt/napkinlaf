package net.sourceforge.napkinlaf.fonts;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This is used in the implementation of {@link MergedFont}.
 *
 * @see MergedFont
 */
// GlyphVector doesn't really implement clone
@SuppressWarnings({"CloneableClassWithoutClone"})
class MergedGlyphVector extends GlyphVector {
    private final Font font;
    private final FontRenderContext frc;
    private final List<GlyphInfo> glyphs = new ArrayList<GlyphInfo>();

    private static class GlyphInfo {
        final int code;
        final Shape outline;
        Point2D position;
        AffineTransform transform;
        final Shape logicalBounds;
        final Shape visualBounds;
        final GlyphMetrics metrics;
        final GlyphJustificationInfo info;
        final Font font;

        GlyphInfo(int code, Shape outline, Point2D position,
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

    MergedGlyphVector(Font font, FontRenderContext frc) {
        this.font = font;
        this.frc = frc;
    }

    @Override
    public Font getFont() {
        return font;
    }

    @Override
    public FontRenderContext getFontRenderContext() {
        return frc;
    }

    @Override
    public void performDefaultLayout() {
    }

    @Override
    public int getNumGlyphs() {
        return glyphs.size();
    }

    @Override
    public int getGlyphCode(int glyphIndex) {
        return glyphs.get(glyphIndex).code;
    }

    @Override
    public int[] getGlyphCodes(int beginGlyphIndex, int numEntries,
            int[] codeReturn) {
        int[] result = codeReturn == null ? new int[numEntries] : codeReturn;
        for (int i = 0; i < numEntries; i++) {
            result[i] = getGlyphCode(beginGlyphIndex + i);
        }
        return result;
    }

    @Override
    public Rectangle2D getLogicalBounds() {
        if (getNumGlyphs() == 0) {
            return new Rectangle();
        }
        return getGlyphLogicalBounds(0).getBounds2D().createUnion(
                getGlyphLogicalBounds(getNumGlyphs() - 1).getBounds2D());
    }

    @Override
    public Rectangle2D getVisualBounds() {
        if (getNumGlyphs() == 0) {
            return new Rectangle();
        }
        return getGlyphVisualBounds(0).getBounds2D().createUnion(
                getGlyphVisualBounds(getNumGlyphs() - 1).getBounds2D());
    }

    @Override
    public Shape getOutline() {
        return getOutline(0.0f, 0.0f);
    }

    @Override
    public Shape getOutline(float x, float y) {
        GeneralPath result = new GeneralPath();
        for (int i = 0, n = getNumGlyphs(); i < n; i++) {
            result.append(getGlyphOutline(i, x, y), false);
        }
        return result;
    }

    @Override
    public Shape getGlyphOutline(int glyphIndex) {
        return glyphs.get(glyphIndex).outline;
    }

    @Override
    public Point2D getGlyphPosition(int glyphIndex) {
        return glyphs.get(glyphIndex).position;
    }

    @Override
    public void setGlyphPosition(int glyphIndex, Point2D newPos) {
        glyphs.get(glyphIndex).position = newPos;
    }

    @Override
    public AffineTransform getGlyphTransform(int glyphIndex) {
        return glyphs.get(glyphIndex).transform;
    }

    @Override
    public void setGlyphTransform(int glyphIndex, AffineTransform newTX) {
        glyphs.get(glyphIndex).transform = newTX;
    }

    @Override
    public float[] getGlyphPositions(int beginGlyphIndex, int numEntries,
            float[] positionReturn) {
        float[] result = positionReturn == null ?
                new float[numEntries * 2] :
                positionReturn;
        for (int i = 0; i < numEntries; i++) {
            Point2D point = getGlyphPosition(beginGlyphIndex + i);
            int aPos = i * 2;
            result[aPos] = (float) point.getX();
            result[aPos + 1] = (float) point.getY();
        }
        return result;
    }

    @Override
    public Shape getGlyphLogicalBounds(int glyphIndex) {
        return glyphs.get(glyphIndex).logicalBounds;
    }

    @Override
    public Shape getGlyphVisualBounds(int glyphIndex) {
        return glyphs.get(glyphIndex).visualBounds;
    }

    @Override
    public GlyphMetrics getGlyphMetrics(int glyphIndex) {
        return glyphs.get(glyphIndex).metrics;
    }

    @Override
    public GlyphJustificationInfo getGlyphJustificationInfo(int glyphIndex) {
        return glyphs.get(glyphIndex).info;
    }

    // GlphyVector stupidly re-uses equals() for this method, or stupidly it
    // doesn't provide equals(Object) and hashCode -- take your pick...
    @SuppressWarnings({"CovariantEquals"})
    @Override
    public boolean equals(GlyphVector that) {
        for (int i = 0, n = getNumGlyphs(); i < n; i++) {
            if (getGlyphCode(i) != that.getGlyphCode(i)) {
                return false;
            }
        }
        return true;
    }

    public Font getGlyphFont(int glyphIndex) {
        return glyphs.get(glyphIndex).font;
    }

    public void appendGlyph(int code, Shape outline, Point2D position,
            AffineTransform transform, Shape logicalBounds, Shape visualBounds,
            GlyphMetrics metrics, GlyphJustificationInfo info, Font glyphFont) {

        GlyphInfo glyph = new GlyphInfo(code, outline, position, transform,
                logicalBounds, visualBounds, metrics, info, glyphFont);
        glyphs.add(glyph);
    }

    public List<MergedGlyphVector> split() {
        List<MergedGlyphVector> list = new ArrayList<MergedGlyphVector>();
        int glyphCount = glyphs.size();
        if (glyphCount > 0) {
            int topStartIndex = 0;
            for (int i = 0; i < glyphCount; i++) {
                GlyphInfo info = glyphs.get(i);
                if (!font.equals(info.font)) {
                    if (topStartIndex < i) {
                        MergedGlyphVector cgv = new MergedGlyphVector(font,
                                frc);
                        cgv.glyphs.addAll(glyphs.subList(topStartIndex, i));
                        list.add(cgv);
                    }
                    MergedGlyphVector cgv = new MergedGlyphVector(info.font,
                            frc);
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