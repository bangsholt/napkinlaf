package napkin;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javax.swing.plaf.*;

public interface NapkinTheme {
    String name();

    String description();

    Color textColor();

    Color drawColor();

    Color checkColor();

    Color radioColor();

    Color highlightColor();

    FontUIResource textFont();

    FontUIResource boldTextFont();

    FontUIResource fixedFont();

    NapkinBackground paper();

    NapkinBackground popup();

    NapkinBackground erasureMask();

    class Simple implements NapkinTheme {
        private final String name;
        private final String description;
        private final Color textColor;
        private final Color drawColor;
        private final Color checkColor;
        private final Color radioColor;
        private final Color highlightColor;
        private final FontUIResource text;
        private final FontUIResource boldText;
        private final FontUIResource fixed;
        private final NapkinBackground paper;
        private final NapkinBackground popup;
        private final NapkinBackground erasure;

        public Simple(String name, String description, Color textColor,
                Color drawColor, Color checkColor, Color radioColor,
                Color highlightColor, FontUIResource text, FontUIResource boldText,
                FontUIResource fixed, NapkinBackground paper,
                NapkinBackground popup, NapkinBackground erasure) {

            this.checkColor = checkColor;
            this.description = description;
            this.drawColor = drawColor;
            this.erasure = erasure;
            this.fixed = fixed;
            this.name = name;
            this.paper = paper;
            this.popup = popup;
            this.radioColor = radioColor;
            this.highlightColor = highlightColor;
            this.text = text;
            this.boldText = boldText;
            this.textColor = textColor;
        }

        public Simple(String name, String description, Color textColor,
                Color drawColor, Color checkColor, Color radioColor,
                Color highlightColor, FontUIResource text, FontUIResource fixed,
                NapkinBackground paper, NapkinBackground popup,
                NapkinBackground erasure) {

            this(name, description, textColor, drawColor, checkColor,
                    radioColor, highlightColor, text, fixed,
                    new FontUIResource(text.deriveFont(Font.BOLD)),
                    paper, popup, erasure);
        }

        public Simple(String name, String description, Color textColor,
                Color drawColor, Color checkColor, Color radioColor,
                Color highlightColor, Font text, Font boldText, Font fixed,
                NapkinBackground paper, NapkinBackground popup,
                NapkinBackground erasure) {

            this(name, description, textColor, drawColor, checkColor,
                    radioColor, highlightColor, new FontUIResource(text),
                    new FontUIResource(boldText), new FontUIResource(fixed),
                    paper, popup, erasure);
        }

        public Simple(String name, String description, Color textColor,
                Color drawColor, Color checkColor, Color radioColor,
                Color highlightColor, Font text, Font fixed,
                NapkinBackground paper, NapkinBackground popup,
                NapkinBackground erasure) {

            this(name, description, textColor, drawColor, checkColor,
                    radioColor, highlightColor, new FontUIResource(text),
                    new FontUIResource(fixed), paper, popup, erasure);
        }

        public String name() {
            return name;
        }

        public String description() {
            return description;
        }

        public Color textColor() {
            return textColor;
        }

        public Color drawColor() {
            return drawColor;
        }

        public Color checkColor() {
            return checkColor;
        }

        public Color radioColor() {
            return radioColor;
        }

        public Color highlightColor() {
            return highlightColor;
        }

        public FontUIResource textFont() {
            return text;
        }

        public FontUIResource boldTextFont() {
            return boldText;
        }

        public FontUIResource fixedFont() {
            return fixed;
        }

        public NapkinBackground paper() {
            return paper;
        }

        public NapkinBackground popup() {
            return popup;
        }

        public NapkinBackground erasureMask() {
            return erasure;
        }
    }

    class Manager {
        private static Map themes = new HashMap();
        private static NapkinTheme currentTheme;

        private static final String DEFAULT_THEME = "napkin";
        private static boolean gotFonts;
        private static Font scrawl;
        private static Font scrawlBold;
        private static Font fixed;
        //!! Want to make this selectable

//    private static final String SCRAWL_NAME = "nigmaScrawl5bBRK";

        private static final Class THIS_CLASS = NapkinLookAndFeel.class;
        private static final Logger LOG =
                LogManager.getLogManager().getLogger(THIS_CLASS.getName());

        static {
            getFonts();

//    private static final String SCRAWL_NAME = "nigmaScrawl5bBRK";
            addTheme(new Simple(DEFAULT_THEME, "Default theme",
                    Color.BLACK, Color.BLACK, Color.GREEN.darker(),
                    new Color(0xF50000), new Color(0x00, 0xff, 0xff, 0xff / 2),
                    scrawl.deriveFont(Font.PLAIN, 15),
                    scrawlBold.deriveFont(Font.PLAIN, 15),
                    fixed.deriveFont(Font.PLAIN, 15),
                    new NapkinBackground("resources/napkin.jpg"),
                    new NapkinBackground("resources/postit01.jpg",
                            15, 15, 38, 32),
                    new NapkinBackground("resources/erasure.png")));

            NapkinTheme def = getTheme(DEFAULT_THEME);
            addTheme(new Simple("debug", "Debug theme", def.textColor(),
                    def.drawColor(), def.checkColor(), def.radioColor(),
                    def.highlightColor(), def.textFont(), def.boldTextFont(),
                    def.fixedFont(),
                    new NapkinBackground("resources/testPaper.jpg",
                            0, 0, 10, 10),
                    new NapkinBackground("resources/testPostit.jpg",
                            0, 0, 10, 10),
                    def.erasureMask()));

            String themeName;
            try {
                themeName = (String)
                        AccessController.doPrivileged(new PrivilegedAction() {
                            public Object run() {
                                return System.getProperty("napkin.theme",
                                        DEFAULT_THEME);
                            }
                        });
            } catch (SecurityException e) {
                themeName = null;
            }
            if (themeName == null)
                themeName = DEFAULT_THEME;

            currentTheme = getTheme(themeName);
            if (currentTheme == null)
                currentTheme = getTheme(DEFAULT_THEME);
        }

        public static String[] themeNames() {
            return (String[])
                    themes.keySet().toArray(new String[themes.size()]);
        }

        public static NapkinTheme getTheme(String name) {
            return (NapkinTheme) themes.get(name);
        }

        public static NapkinTheme getDefaultTheme() {
            return getTheme(DEFAULT_THEME);
        }

        public static NapkinTheme getCurrentTheme() {
            return currentTheme;
        }

        public static void setCurrentTheme(NapkinTheme theme) {
            currentTheme = (theme != null ? theme : getDefaultTheme());
        }

        public static void addTheme(NapkinTheme theme) {
            themes.put(theme.name(), theme);
        }

        private static synchronized void getFonts() {
            if (gotFonts)
                return;
            //!! Make this selectable
//        scrawl = tryToLoadFont("aescr5b.ttf");
            scrawl = tryToLoadFont("FeltTipRoman.ttf");
            scrawlBold = tryToLoadFont("FeltTipRoman-Bold.ttf");
            fixed = tryToLoadFont("Mcgf____.ttf");
            gotFonts = true;
        }

        private static Font tryToLoadFont(String fontName) {
            try {
                ClassLoader cl = NapkinLookAndFeel.class.getClassLoader();
                String fontRes = "napkin/resources/" + fontName;
                InputStream fontDef = cl.getResourceAsStream(fontRes);
                if (fontDef == null)
                    System.err.println("Could not find font " + fontName);
                else
                    return Font.createFont(Font.TRUETYPE_FONT, fontDef);
            } catch (FontFormatException e) {
                LOG.log(Level.WARNING, "getting font " + fontName, e);
            } catch (IOException e) {
                LOG.log(Level.WARNING, "getting font " + fontName, e);
            }
            return null;
        }
    }
}