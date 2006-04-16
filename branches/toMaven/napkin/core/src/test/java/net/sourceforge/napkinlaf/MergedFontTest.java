package net.sourceforge.napkinlaf;

import javax.swing.UIManager;
import junit.framework.TestCase;
import net.sourceforge.napkinlaf.fonts.MergedFont;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class MergedFontTest extends TestCase {

    private boolean SKIP_TEST = false;

    public void testCurrentUI() {
        if (UIManager.getLookAndFeel().getClass() == NapkinLookAndFeel.class) {
            System.err.println("Tests cannot run when using Napkin!");
            SKIP_TEST = true;
        }
    }
    
    public void testTwoFonts() {
        if (SKIP_TEST) return;

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
        }
        assertTrue("No differences in fonts", hasDifferences);
    }

    private String stringFromChar(char ch) {
        return "char 0x" + Integer.toHexString((int) ch) + ": " + ch;
    }

    private String stringFromCode(int codePoint) {
        return "char 0x" + Integer.toHexString(codePoint) + ": " +
                new String(new int[]{codePoint}, 0, 1);
    }

    private static Font getFixedFont() {
        return NapkinTheme.Manager.tryToLoadFont("FeltTipRoman.ttf");
    }
}