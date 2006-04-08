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
        for (int i = Character.MIN_CODE_POINT;
             i <= Character.MAX_CODE_POINT;
             i += 16) {
            boolean fixed = fixedFont.canDisplay(i);
            boolean serif = serifFont.canDisplay(i);
            boolean napkin = mergedFont.canDisplay(i);
            boolean merged = fixed || serif;
            if (merged != napkin) // avoid building the string unless we need it
                assertEquals(stringFor(i), merged, napkin);
            hasDifferences |= (fixed != serif);

            if (i <= Character.MAX_VALUE) {
                char ch = (char) i;
                fixed = fixedFont.canDisplay(ch);
                serif = serifFont.canDisplay(ch);
                napkin = mergedFont.canDisplay(ch);
                merged = fixed || serif;
                if (merged != napkin)
                    assertEquals(stringFor(ch), merged, napkin);
            }
        }
        assertTrue("No differences in fonts", hasDifferences);
    }

    private String stringFor(char ch) {
        return "char 0x" + Integer.toHexString((int) ch) + ": " + ch;
    }

    private String stringFor(int codePoint) {
        return "char 0x" + Integer.toHexString(codePoint) + ": " +
                new String(new int[]{codePoint}, 0, 1);
    }

    private static Font getFixedFont() {
        return NapkinTheme.Manager.tryToLoadFont("FeltTipRoman.ttf");
    }
}