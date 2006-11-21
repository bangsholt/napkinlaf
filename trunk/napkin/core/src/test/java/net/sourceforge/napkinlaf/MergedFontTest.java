package net.sourceforge.napkinlaf;

import junit.framework.TestCase;
import net.sourceforge.napkinlaf.fonts.MergedFont;

import java.awt.*;

public class MergedFontTest extends TestCase {

    public void testTwoFonts() {
        Font fixedFont = getFixedFont();
        Font serifFont = new Font("serif", Font.PLAIN, 10);
        Font mergedFont = new MergedFont(fixedFont, serifFont);
        boolean hasDifferences = false;
        int i = Character.MIN_CODE_POINT;
        while (i <= Character.MAX_CODE_POINT) {
            boolean fixed = fixedFont.canDisplay(i);
            boolean serif = serifFont.canDisplay(i);
            boolean napkin = mergedFont.canDisplay(i);
            boolean merged = fixed || serif;
            // avoid building the string unless we need it
            if (merged != napkin) {
                assertEquals(stringFromCode(i), merged, napkin);
            }
            hasDifferences |= (fixed != serif);

            if (i <= Character.MAX_VALUE) {
                char ch = (char) i;
                fixed = fixedFont.canDisplay(ch);
                serif = serifFont.canDisplay(ch);
                napkin = mergedFont.canDisplay(ch);
                merged = fixed || serif;
                if (merged != napkin) {
                    assertEquals(stringFromChar(ch), merged, napkin);
                }
            }
            if (i < 256) {
                i++;
            } else if (i <= Character.MAX_VALUE) {
                i += 231;   // arbitrary number, not a power of two
            } else {
                i += 1010;  // arbitrary number, not a power of two
            }
        }
        assertTrue("No differences in fonts", hasDifferences);
    }

    private static String stringFromChar(char ch) {
        return "char 0x" + Integer.toHexString((int) ch) + ": " + ch;
    }

    private static String stringFromCode(int codePoint) {
        return "char 0x" + Integer.toHexString(codePoint) + ": " + new String(
                new int[]{codePoint}, 0, 1);
    }

    private static Font getFixedFont() {
        return NapkinTheme.Manager.tryToLoadFont("FeltTipRoman.ttf");
    }
}