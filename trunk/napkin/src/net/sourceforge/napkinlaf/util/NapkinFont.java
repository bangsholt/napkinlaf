package net.sourceforge.napkinlaf.util;

import javax.swing.plaf.*;
import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.lang.reflect.Field;
import java.text.CharacterIterator;

/**
 * This class defines a psuedo font that satisfies glyphs along a search path
 * through a list of other fonts.  When a glyph vector is being produced, it is
 * first gathered from the first font.  If the resutling vector contains any
 * undefined glyphs, these are looked for in the second font, and so on until
 * all fonts have been searched for missing glyphs.
 * <p/>
 * We use this because handwritten fonts are rarely complete, so when people use
 * Napkin for text it is easy to find characters that are missing from the
 * fonts. Rather than look illegible, we let a complete but possibly
 * non-handwritten font to back up the handwritten one.
 *
 * @author Alex Lam Sze Lok
 */
public class NapkinFont extends Font implements UIResource {
    private static Font[] NO_ADDED_FONTS = new Font[0];

    private Font[] fonts = NO_ADDED_FONTS;

    public NapkinFont(String name, int style, int size) {
        super(name, style, size);
    }

    public NapkinFont(Font font) {
        super(font.getAttributes());

        /*
         * Bug 6313541 (fixed in Mustang) prevents the bundled fonts loading
         * (because the font2DHandle field is not transferred when calling
         * FontUIResource.  The workaround uses reflection, which might not work
         * for applets and Web Start applications, so here I've put in checks so
         * the workaround is used only when needed.
         */
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
        this.fonts = fonts.clone();
    }

    private GlyphVector processGlyphVector(FontRenderContext frc,
            GlyphVector gVector, GlyphVector[] gVectors) {

        int glyphCount = gVector.getNumGlyphs();
        int fontCount = fonts.length;
        // if no glyphs or we only have a single font, just return
        // (fontCount == 0) is not strictly necessary
        if (glyphCount == 0 || fontCount == 0) {
            return gVector;
        }
        int badCode = getMissingGlyphCode();
        int pos;
        for (pos = 0; pos < glyphCount; pos++) {
            if (gVector.getGlyphCode(pos) == badCode) {
                break;
            }
        }
        // if we don't have any bad glyphs, just return
        if (pos == glyphCount) {
            return gVector;
        }

        // we do have bad glyphs; scan through the font chain for replacement
        int[] badCodes = new int[fontCount];
        for (int j = 0; j < fontCount; j++) {
            badCodes[j] = fonts[j].getMissingGlyphCode();
        }
        NapkinGlyphVector result = new NapkinGlyphVector(this, frc);
        Point2D curPos, origPos;
        GlyphVector curGVector;
        boolean replaced = false;
        for (int i = 0; i < glyphCount; i++) {
            // look for the GlyphVector with non-bad glyph
            // fall back to top font's bad glyph if failed
            curGVector = gVector;
            if (gVector.getGlyphCode(i) == badCode) {
                for (int j = 0; j < fontCount; j++) {
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
        return replaced ? result : gVector;
    }

    @Override
    public GlyphVector createGlyphVector(FontRenderContext frc, char[] chars) {
        GlyphVector gVector = super.createGlyphVector(frc, chars);
        if (fonts.length == 0) {
            return gVector;
        }
        GlyphVector[] gVectors = new GlyphVector[fonts.length];
        for (int i = 0; i < fonts.length; i++) {
            gVectors[i] = fonts[i].createGlyphVector(frc, chars);
        }
        return processGlyphVector(frc, gVector, gVectors);
    }

    @Override
    public GlyphVector createGlyphVector(FontRenderContext frc, String str) {
        GlyphVector gVector = super.createGlyphVector(frc, str);
        if (fonts.length == 0) {
            return gVector;
        }
        GlyphVector[] gVectors = new GlyphVector[fonts.length];
        for (int i = 0; i < fonts.length; i++) {
            gVectors[i] = fonts[i].createGlyphVector(frc, str);
        }
        return processGlyphVector(frc, gVector, gVectors);
    }

    @Override
    public GlyphVector
            createGlyphVector(FontRenderContext frc, CharacterIterator ci) {

        GlyphVector gVector = super.createGlyphVector(frc, ci);
        if (fonts.length == 0) {
            return gVector;
        }
        GlyphVector[] gVectors = new GlyphVector[fonts.length];
        for (int i = 0; i < fonts.length; i++) {
            gVectors[i] = fonts[i].createGlyphVector(frc, ci);
        }
        return processGlyphVector(frc, gVector, gVectors);
    }

    @Override
    public GlyphVector
            createGlyphVector(FontRenderContext frc, int[] glyphCodes) {

        GlyphVector gVector = super.createGlyphVector(frc, glyphCodes);
        if (fonts.length == 0) {
            return gVector;
        }
        GlyphVector[] gVectors = new GlyphVector[fonts.length];
        for (int i = 0; i < fonts.length; i++) {
            gVectors[i] = fonts[i].createGlyphVector(frc, glyphCodes);
        }
        return processGlyphVector(frc, gVector, gVectors);
    }

    @Override
    public GlyphVector layoutGlyphVector(FontRenderContext frc,
            char[] text, int start, int limit, int flags) {
        GlyphVector gVector = super.layoutGlyphVector(
                frc, text, start, limit, flags);
        if (fonts.length == 0) {
            return gVector;
        }
        GlyphVector[] gVectors = new GlyphVector[fonts.length];
        for (int i = 0; i < fonts.length; i++) {
            gVectors[i] = fonts[i].layoutGlyphVector(
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
        return fonts.length != 0;
    }
}
