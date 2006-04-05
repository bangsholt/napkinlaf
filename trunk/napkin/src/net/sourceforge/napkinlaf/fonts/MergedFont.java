package net.sourceforge.napkinlaf.fonts;

import javax.swing.plaf.*;
import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.lang.reflect.Field;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.CharacterIterator;
import java.util.Map;

/**
 * This class defines a psuedo font that satisfies glyphs from a primary and a
 * backing font.  When a glyph vector is being produced, glyphs are gathered
 * from the primary font.  If the resuting vector contains any undefined glyphs,
 * these are looked for in the backing font.  All other equestions are answered
 * similarly.  For example, {@link #canDisplay(char)} returns <tt>true</tt> if
 * either font can display the character.
 * <p/>
 * You can create a chain of merged fonts if you want to look through a search
 * path of more than one font by making the backing font itself a merged font,
 * and so on as far as you like. Then the chain of fonts will be search in order
 * for missing glyphs.  The {@link #mergedFonts(Font, Font...)} method helps you
 * do this easily.
 * <p/>
 * Deriving fonts raises the following interesting issues: The backing font does
 * not necessarily share the same properties with the top font.  For example,
 * you can have a plain, 16pt, hand-written top font backed by an italic,
 * 14.5pt, unicode font. The challenge is how to maintain these relationships
 * with the backing font while applying the derivation instructions to the
 * primary font.  The answer is that changing the size of the primary font will
 * cause proportional scaling in the backing font, while changes in any other
 * attributes in the primary font will also be simply applied to the backing
 * font.
 * <p/>
 * We use this because handwritten fonts are rarely complete, so when people use
 * Napkin for text it is easy to find characters that are missing from the
 * fonts. Rather than look illegible, we can use one or more similar fonts as
 * supplement, or let a complete but possibly non-handwritten font to back up
 * the handwritten one.
 * <p/>
 * Obviously this might be useful in other situations, and is written to be
 * independent of the Napkin Look & Feel.  In other words, the {@link
 * net.sourceforge.napkinlaf.fonts} package can be used by themselves.
 *
 * @author Alex Lam Sze Lok
 */
public class MergedFont extends Font implements UIResource {
    private final Font backingFont;

    /**
     * Creates a new merged font with the given primary and backing fonts.
     *
     * @param primaryFont The primary font for the merge.
     * @param backingFont The backing font for the merge.
     */
    public MergedFont(Font primaryFont, Font backingFont) {
        super(primaryFont.getAttributes());
        if (backingFont == null)
            throw new NullPointerException("backingFont");
        this.backingFont = backingFont;

        /*
         * Bug 6313541 (fixed in Mustang) prevents the bundled fonts loading
         * (because the font2DHandle field is not transferred when calling
         * FontUIResource).  The workaround uses reflection, which might not
         * work for applets and Web Start applications, so here I've put in
         * checks so the workaround is used only when needed.
         */
        if (!getFontName().equals(primaryFont.getFontName())) {
            try {
                // transfer private field font2DHandle
                Field field = Font.class.getDeclaredField("font2DHandle");
                field.setAccessible(true);
                field.set(this, field.get(primaryFont));
                field.setAccessible(false);
                // transfer private field createdFont
                field = Font.class.getDeclaredField("createdFont");
                field.setAccessible(true);
                field.set(this, field.get(primaryFont));
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

    /**
     * Creates a font chain starting with a parimary font.  The returned font
     * will satisfy glyphs from the fonts in the order they are provided. The
     * degenerate case of providing only one font will return that font.
     *
     * @param primaryFont  The primary font.
     * @param backingFonts Zero or more backing fonts, to be searched in order.
     *
     * @return A font that is the merger of the given fonts.
     */
    public static Font mergedFonts(Font primaryFont, Font... backingFonts) {
        if (backingFonts.length == 0)
            return primaryFont;
        else if (backingFonts.length == 1)
            return new MergedFont(primaryFont, backingFonts[0]);
        else
            return new MergedFont(primaryFont, mergedFonts(backingFonts, 0));
    }

    private static Font mergedFonts(Font[] backingFonts, int pos) {
        Font first = backingFonts[pos];
        if (pos == backingFonts.length - 1)
            return first;
        else
            return new MergedFont(first, mergedFonts(backingFonts, pos + 1));
    }

    /** @return The backing font for this font. */
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
        MergedGlyphVector result = new MergedGlyphVector(this, frc);
        Point2D curPos, origPos;
        GlyphVector curGVector;
        boolean replaced = false;
        for (int i = 0; i < glyphCount; i++) {
            /*
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
            // get the font of this particular glyph
            Font glyphFont = curGVector instanceof MergedGlyphVector ?
                    ((MergedGlyphVector) curGVector).getGlyphFont(i)
                    : curGVector.getFont();
            // add transformed glyph into our GlyphVector
            result.appendGlyph(curGVector.getGlyphCode(i),
                    outline, curPos, curGVector.getGlyphTransform(i),
                    logicalBounds, visualBounds, metrics,
                    curGVector.getGlyphJustificationInfo(i), glyphFont);
        }
        /*
         * if no replacements were done, i.e. the backing font does not have
         * the missing glyphs as well, the original GlyphVector is returned.
         */
        return replaced ? result : gVector;
    }

    private boolean isPrimarySufficient(String str) {
        int i, n = str.length();
        for (i = 0; i < n && super.canDisplay(str.charAt(i)); i++) {
        }
        return i == n;
    }

    private boolean isPrimarySufficient(char[] text, int start, int limit) {
        int i;
        for (i = start; i < limit && super.canDisplay(text[i]); i++) {
        }
        return i == limit;
    }

    private boolean isPrimarySufficient(CharacterIterator iter) {
        int limit = iter.getEndIndex();
        for (char c = iter.setIndex(iter.getBeginIndex());
             iter.getIndex() < limit && super.canDisplay(c);
             c = iter.next()) {
        }
        return iter.getIndex() == limit;
    }

    /** {@inheritDoc} */
    @Override
    public GlyphVector createGlyphVector(FontRenderContext frc, char[] chars) {
        GlyphVector gVector = super.createGlyphVector(frc, chars);
        // If we don't have any bad glyphs, just return the simple result.
        if (isPrimarySufficient(chars, 0, chars.length)) {
            return gVector;
        }
        return processGlyphVector(frc, gVector,
                backingFont.createGlyphVector(frc, chars));
    }

    /** {@inheritDoc} */
    @Override
    public GlyphVector createGlyphVector(FontRenderContext frc, String str) {
        GlyphVector gVector = super.createGlyphVector(frc, str);
        // If we don't have any bad glyphs, just return the simple result.
        if (isPrimarySufficient(str)) {
            return gVector;
        }
        return processGlyphVector(frc, gVector,
                backingFont.createGlyphVector(frc, str));
    }

    /** {@inheritDoc} */
    @Override
    public GlyphVector
            createGlyphVector(FontRenderContext frc, CharacterIterator ci) {
        GlyphVector gVector = super.createGlyphVector(frc, ci);
        /**
         * if this is not a composite font or if we don't have any bad glyphs,
         * just return the simple result.
         */
        if (isPrimarySufficient(ci)) {
            return gVector;
        }
        return processGlyphVector(frc, gVector,
                backingFont.createGlyphVector(frc, ci));
    }

    /** {@inheritDoc} */
    @Override
    public GlyphVector
            createGlyphVector(FontRenderContext frc, int[] glyphCodes) {

        GlyphVector gVector = super.createGlyphVector(frc, glyphCodes);
        return processGlyphVector(frc, gVector,
                backingFont.createGlyphVector(frc, glyphCodes));
    }

    /** {@inheritDoc} */
    @Override
    public GlyphVector layoutGlyphVector(FontRenderContext frc,
            char[] text, int start, int limit, int flags) {
        GlyphVector gVector = super.layoutGlyphVector(
                frc, text, start, limit, flags);
        // If we don't have any bad glyphs, just return the simple result.
        if (isPrimarySufficient(text, start, limit)) {
            return gVector;
        }
        return processGlyphVector(frc, gVector,
                backingFont.layoutGlyphVector(frc, text, start, limit, flags));
    }

    /** {@inheritDoc} */
    @Override
    public boolean canDisplay(char c) {
        return super.canDisplay(c) || backingFont.canDisplay(c);
    }

    /** {@inheritDoc} */
    @Override
    public boolean canDisplay(int codePoint) {
        return super.canDisplay(codePoint) || backingFont.canDisplay(codePoint);
    }

    /** @return A string showing the primary and backing fonts. */
    @Override
    public String toString() {
        return "MergedFont{" + super.toString() + ":" + backingFont + "}";
    }

    /**
     * {@inheritDoc}
     *
     * @param that The object to compare to.
     *
     * @return <tt>true</tt> if both the primary and backing fonts are equal.
     */
    @Override
    public boolean equals(Object that) {
        if (that == this) {
            return true;
        }
        if (!super.equals(that)) {
            return false;
        }
        if (that instanceof MergedFont) {
            return backingFont.equals(((MergedFont) that).backingFont);
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return super.hashCode() ^ backingFont.hashCode();
    }

    /**
     * {@inheritDoc}
     * <p/>
     * If neither font can display the character, the baseline will be the one
     * for the primary font.
     */
    @Override
    public byte getBaselineFor(char c) {
        if (super.canDisplay(c) || !backingFont.canDisplay(c)) {
            return super.getBaselineFor(c);
        } else {
            return backingFont.getBaselineFor(c);
        }
    }

    /** {@inheritDoc} */
    @Override
    public int getNumGlyphs() {
        return Math.max(super.getNumGlyphs(), backingFont.getNumGlyphs());
    }

    /** {@inheritDoc} */
    @Override
    public Font deriveFont(float size) {
        Font topFont = super.deriveFont(size);
        float backSize = size * backingFont.getSize2D() / getSize2D();
        return new MergedFont(topFont, backingFont.deriveFont(backSize));
    }

    /** {@inheritDoc} */
    @Override
    public Font deriveFont(int style) {
        Font topFont = super.deriveFont(style);
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
        return new MergedFont(topFont, backingFont.deriveFont(backStyle));
    }

    /** {@inheritDoc} */
    @Override
    public Font deriveFont(AffineTransform trans) {
        try {
            AffineTransform temp = getTransform().createInverse();
            temp.concatenate(backingFont.getTransform());
            AffineTransform backingTrans = (AffineTransform) trans.clone();
            backingTrans.concatenate(temp);
            return new MergedFont(super.deriveFont(trans),
                    backingFont.deriveFont(backingTrans));
        } catch (NoninvertibleTransformException ex) {
            return new MergedFont(super.deriveFont(trans),
                    backingFont.deriveFont(trans));
        }
    }

    /** {@inheritDoc} */
    @Override
    public Font deriveFont(Map<? extends Attribute, ?> attributes) {
        Map<? extends Attribute, ?> topAttributes = getAttributes();
        if (attributes == topAttributes ||
                (attributes != null && attributes.equals(topAttributes))) {
            return this;
        } else {
            return new MergedFont(super.deriveFont(attributes),
                    backingFont.deriveFont(attributes));
        }
    }

    /** {@inheritDoc} */
    @Override
    public Font deriveFont(int style, AffineTransform trans) {
        return deriveFont(style).deriveFont(trans);
    }

    /** {@inheritDoc} */
    @Override
    public Font deriveFont(int style, float size) {
        return deriveFont(size).deriveFont(style);
    }
}
