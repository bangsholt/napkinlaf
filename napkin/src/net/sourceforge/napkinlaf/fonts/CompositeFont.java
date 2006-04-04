package net.sourceforge.napkinlaf.fonts;

import java.text.AttributedCharacterIterator.Attribute;
import java.util.Map;
import javax.swing.plaf.*;
import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.lang.reflect.Field;
import java.text.CharacterIterator;
import java.util.Arrays;

/**
 * This class defines a psuedo font that satisfies glyphs along a search path
 * through a list of other fonts.  When a glyph vector is being produced, it is
 * first gathered from the first font.  If the resutling vector contains any
 * undefined glyphs, these are looked for in the second font, and so on until
 * all fonts have been searched for missing glyphs.
 * <p/>
 * We use this because handwritten fonts are rarely complete, so when people use
 * Napkin for text it is easy to find characters that are missing from the
 * fonts. Rather than look illegible, we can use one or more similar fonts as
 * supplement, or let a complete but possibly non-handwritten font to back up
 * the handwritten one.
 *
 * @author Alex Lam Sze Lok
 */
public class CompositeFont extends Font implements UIResource {

    private final Font backingFont;

    public CompositeFont(String name, int style, int size) {
        super(name, style, size);
        backingFont = null;
    }

    public CompositeFont(Font font) {
        this(font, null);
    }

    public CompositeFont(Font topFont, Font backingFont) {
        super(topFont.getAttributes());
        this.backingFont = backingFont;

        /*
         * Bug 6313541 (fixed in Mustang) prevents the bundled fonts loading
         * (because the font2DHandle field is not transferred when calling
         * FontUIResource).  The workaround uses reflection, which might not
         * work for applets and Web Start applications, so here I've put in
         * checks so the workaround is used only when needed.
         */
        if (!getFontName().equals(topFont.getFontName())) {
            try {
                // transfer private field font2DHandle
                Field field = Font.class.getDeclaredField("font2DHandle");
                field.setAccessible(true);
                field.set(this, field.get(topFont));
                field.setAccessible(false);
                // transfer private field createdFont
                field = Font.class.getDeclaredField("createdFont");
                field.setAccessible(true);
                field.set(this, field.get(topFont));
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

    public static CompositeFont
            newInstance(Font topFont, Font ... backingFonts) {
        CompositeFont cFont = null;
        for (int i = backingFonts.length; --i >= 0;) {
            cFont = new CompositeFont(backingFonts[i], cFont);
        }
        return new CompositeFont(topFont, cFont);
    }

    public boolean isComposite() {
        return backingFont != null;
    }

    public Font getBackingFont() {
        return backingFont;
    }

    private GlyphVector processGlyphVector(FontRenderContext frc,
            GlyphVector gVector, GlyphVector gVector2) {

        int glyphCount = gVector.getNumGlyphs();
        // if no glyphs or we only have a single font, just return
        if (glyphCount == 0) {
            return gVector;
        }

        // we do have bad glyphs; scan through the font chain for replacement
        int badCode = getMissingGlyphCode();
        int badCode2 = backingFont.getMissingGlyphCode();
        CompositeGlyphVector result = new CompositeGlyphVector(this, frc);
        Point2D curPos, origPos;
        GlyphVector curGVector;
        boolean replaced = false;
        for (int i = 0; i < glyphCount; i++) {
            /**
             * Look for the GlyphVector with non-bad glyph.
             * Fall back to top font's bad glyph if failed.
             */
            curGVector = gVector;
            if (gVector.getGlyphCode(i) == badCode
                    && gVector2.getGlyphCode(i) != badCode2) {
                curGVector = gVector2;
                replaced = true;
            }
            // prepare matrix for glyph paremater transformation
            origPos = curGVector.getGlyphPosition(i);
            curPos = gVector.getGlyphPosition(i);
            AffineTransform matrix = AffineTransform.getTranslateInstance(
                    curPos.getX() - origPos.getX(),
                    curPos.getY() - origPos.getY());
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
        /**
         * if no replacements were done, i.e. the backing font does not have
         * the missing glyphs as well, the original GlyphVector is returned.
         */
        return replaced ? result : gVector;
    }

    private boolean isTopFontSufficient(String str) {
        int i, n = str.length();
        for (i = 0; i < n && super.canDisplay(str.charAt(i)); i++) {
        }
        return i == n;
    }

    private boolean isTopFontSufficient(char[] text, int start, int limit) {
        int i;
        for (i = start; i < limit && super.canDisplay(text[i]); i++) {
        }
        return i == limit;
    }

    private boolean isTopFontSufficient(CharacterIterator iter) {
        int limit = iter.getEndIndex();
        for (char c = iter.setIndex(iter.getBeginIndex());
                iter.getIndex() < limit && super.canDisplay(c);
                c = iter.next()) {
        }
        return iter.getIndex() == limit;
    }

    @Override
    public GlyphVector createGlyphVector(FontRenderContext frc, char[] chars) {
        GlyphVector gVector = super.createGlyphVector(frc, chars);
        /**
         * if this is not a composite font or if we don't have any bad glyphs,
         * just return the simple result.
         */
        if (!isComposite() || isTopFontSufficient(chars, 0, chars.length)) {
            return gVector;
        }
        return processGlyphVector(frc, gVector,
                backingFont.createGlyphVector(frc, chars));
    }

    @Override
    public GlyphVector createGlyphVector(FontRenderContext frc, String str) {
        GlyphVector gVector = super.createGlyphVector(frc, str);
        /**
         * if this is not a composite font or if we don't have any bad glyphs,
         * just return the simple result.
         */
        if (!isComposite() || isTopFontSufficient(str)) {
            return gVector;
        }
        return processGlyphVector(frc, gVector,
                backingFont.createGlyphVector(frc, str));
    }

    @Override
    public GlyphVector
            createGlyphVector(FontRenderContext frc, CharacterIterator ci) {
        GlyphVector gVector = super.createGlyphVector(frc, ci);
        /**
         * if this is not a composite font or if we don't have any bad glyphs,
         * just return the simple result.
         */
        if (!isComposite() || isTopFontSufficient(ci)) {
            return gVector;
        }
        return processGlyphVector(frc, gVector,
                backingFont.createGlyphVector(frc, ci));
    }

    @Override
    public GlyphVector
            createGlyphVector(FontRenderContext frc, int[] glyphCodes) {
        GlyphVector gVector = super.createGlyphVector(frc, glyphCodes);
        // if this is not a composite font just return the simple result.
        if (!isComposite()) {
            return gVector;
        }
        return processGlyphVector(frc, gVector,
                backingFont.createGlyphVector(frc, glyphCodes));
    }

    @Override
    public GlyphVector layoutGlyphVector(FontRenderContext frc,
            char[] text, int start, int limit, int flags) {
        GlyphVector gVector = super.layoutGlyphVector(
                frc, text, start, limit, flags);
        /**
         * if this is not a composite font or if we don't have any bad glyphs,
         * just return the simple result.
         */
        if (!isComposite() || isTopFontSufficient(text, start, limit)) {
            return gVector;
        }
        return processGlyphVector(frc, gVector,
                backingFont.layoutGlyphVector(frc, text, start, limit, flags));
    }

    @Override
    public boolean canDisplay(char c) {
        return super.canDisplay(c) || backingFont.canDisplay(c);
    }

    @Override
    public boolean canDisplay(int codePoint) {
        return super.canDisplay(codePoint) || backingFont.canDisplay(codePoint);
    }

    @Override
    public String toString() {
        if (!isComposite())
            return super.toString();
        StringBuilder result = new StringBuilder("CompositeFont{");
        result.append(super.toString());
        Font font = backingFont;
        do {
            result.append("; ").append(font.toString());
            if (font instanceof CompositeFont) {
                font = ((CompositeFont) font).getBackingFont();
            } else {
                break;
            }
        } while (font != null);
        return result.append("}").toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        if (isComposite()) {
            return obj instanceof CompositeFont &&
                    backingFont.equals(((CompositeFont) obj).getBackingFont());
        } else {
            return true;
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ (isComposite() ? backingFont.hashCode() : 0);
    }

    @Override
    public byte getBaselineFor(char c) {
        if (isComposite() && !super.canDisplay(c) && canDisplay(c)) {
            return backingFont.getBaselineFor(c);
        } else {
            return super.getBaselineFor(c);
        }
    }

    @Override
    public int getNumGlyphs() {
        if (isComposite()) {
            return Math.max(super.getNumGlyphs(), backingFont.getNumGlyphs());
        } else {
            return super.getNumGlyphs();
        }
    }

    /**!!
     * Here comes the difficult part of the game - backing font does not
     * necessarily share the same properties with the top font, i.e. you can
     * have a plain, 16pt, hand-written top font backed by an italic, 14.5pt,
     * unicode font.
     *
     * So the challenge with deriveFont()s is to find out how to maintain these
     * invisible links; the approach that I take for now is to:
     * 1) change in sizes in the top font will lead to proportional scaling in
     *    the backing font.
     * 2) any changes in other attribute in the top font will simply write
     *    through to the backing font.
     */

    @Override
    public Font deriveFont(float size) {
        Font topFont = super.deriveFont(size);
        if (isComposite()) {
            float backSize = size * backingFont.getSize2D() / getSize2D();
            return new CompositeFont(topFont, backingFont.deriveFont(backSize));
        } else {
            return topFont;
        }
    }

    @Override
    public Font deriveFont(int style) {
        Font topFont = super.deriveFont(style);
        if (isComposite()) {
            // find the differing bits, i.e. the styles which will be overriden
            int styleMask = getStyle() ^ style;
            // prepare the overriding bits (styles)
            int overridingStyles = style & styleMask;
            // prepare to calculate for the new backing font's styles
            int backStyle = backingFont.getStyle();
            // clears away the fields we are going to write in
            backStyle &= ~styleMask;
            // write in the fields
            backStyle |= overridingStyles;
            return new CompositeFont(topFont, backingFont.deriveFont(backStyle));
        } else {
            return topFont;
        }
    }

    @Override
    public Font deriveFont(AffineTransform trans) {
        Font topFont = super.deriveFont(trans);
        if (isComposite()) {
            AffineTransform topTrans = getTransform();
            if (trans == topTrans
                    || (trans != null && trans.equals(topTrans))) {
                return this;
            } else {
                return new CompositeFont(
                        topFont, backingFont.deriveFont(trans));
            }
        } else {
            return topFont;
        }
    }

    @Override
    public Font deriveFont(Map<? extends Attribute, ?> attributes) {
        Font topFont = super.deriveFont(attributes);
        if (isComposite()) {
            Map<? extends Attribute, ?> topAttributes = getAttributes();
            if (attributes == topAttributes ||
                    (attributes != null && attributes.equals(topAttributes))) {
                return this;
            } else {
                return new CompositeFont(
                        topFont, backingFont.deriveFont(attributes));
            }
        } else {
            return topFont;
        }
    }

    /**!!
     * These are simply divided into seperate stages
     */

    @Override
    public Font deriveFont(int style, AffineTransform trans) {
        return deriveFont(style).deriveFont(trans);
    }

    @Override
    public Font deriveFont(int style, float size) {
        return deriveFont(size).deriveFont(style);
    }

}
