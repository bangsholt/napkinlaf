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

public class NapkinTheme {
    private final String name;
    private final String description;
    private final Color penColor;
    private final Color checkColor;
    private final Color radioColor;
    private final Color highlightColor;
    private final Font textFont;
    private final Font boldTextFont;
    private final Font fixedFont;
    private final NapkinBackground paper;
    private final NapkinBackground popup;
    private final NapkinBackground erasure;

    public NapkinTheme(String name, String description, Color penColor,
            Color checkColor, Color radioColor, Color highlightColor,
            Font textFont, Font boldTextFont, Font fixedFont,
            NapkinBackground paper, NapkinBackground popup,
            NapkinBackground erasure) {

        this.name = name;
        this.description = description;
        this.penColor = uiResource(penColor);
        this.checkColor = uiResource(checkColor);
        this.radioColor = uiResource(radioColor);
        this.highlightColor = uiResource(highlightColor);
        this.textFont = uiResource(textFont);
        this.boldTextFont = uiResource(boldTextFont);
        this.fixedFont = uiResource(fixedFont);
        this.paper = paper;
        this.popup = popup;
        this.erasure = erasure;
    }

    private Color uiResource(Color color) {
        if (color instanceof UIResource)
            return color;
        else
            return new AlphaColorUIResource(color);
    }

    private Font uiResource(Font font) {
        if (font instanceof UIResource)
            return font;
        else
            return new FontUIResource(font);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Color getPenColor() {
        return penColor;
    }

    public Color getCheckColor() {
        return checkColor;
    }

    public Color getRadioColor() {
        return radioColor;
    }

    public Color getHighlightColor() {
        return highlightColor;
    }

    public Font getTextFont() {
        return textFont;
    }

    public Font getBoldTextFont() {
        return boldTextFont;
    }

    public Font getFixedFont() {
        return fixedFont;
    }

    public NapkinBackground getPaper() {
        return paper;
    }

    public NapkinBackground getPopup() {
        return popup;
    }

    public NapkinBackground getErasureMask() {
        return erasure;
    }

    public static class Manager {
        private static final Map themes = new HashMap();
        private static NapkinTheme currentTheme;

        private static final String DEFAULT_THEME = "napkin";
        private static boolean gotFonts;
        private static Font scrawl;
        private static Font scrawlBold;
        private static Font fixed;
        private static Font blueprint;
        //!! Want to make this selectable

//    private static final String SCRAWL_NAME = "nigmaScrawl5bBRK";

        private static final Class THIS_CLASS = NapkinLookAndFeel.class;
        private static final Logger LOG =
                LogManager.getLogManager().getLogger(THIS_CLASS.getName());

        static {
            getFonts();

//    private static final String SCRAWL_NAME = "nigmaScrawl5bBRK";
            NapkinTheme def = new NapkinTheme(DEFAULT_THEME, "Default theme",
                    Color.BLACK, Color.GREEN.darker(), new Color(0xf50000),
                    new Color(0x00, 0xff, 0xff, 0xff / 2),
                    scrawl.deriveFont(Font.PLAIN, 15),
                    scrawlBold.deriveFont(Font.PLAIN, 15),
                    fixed.deriveFont(Font.PLAIN, 15),
                    new NapkinBackground("resources/napkin.jpg"),
                    new NapkinBackground("resources/postit01.jpg",
                            15, 15, 38, 32),
                    new NapkinBackground("resources/erasure.png"));
            addTheme(def);

            addTheme(new NapkinTheme("debug", "Debug theme", def.getPenColor(),
                    def.getCheckColor(), def.getRadioColor(),
                    def.getHighlightColor(), def.getTextFont(),
                    def.getBoldTextFont(), def.getFixedFont(),
                    new NapkinBackground("resources/testPaper.jpg",
                            0, 0, 10, 10),
                    new NapkinBackground("resources/testPostit.jpg",
                            0, 0, 10, 10),
                    def.getErasureMask()));

            Color blueprintInk = new Color(0xe7edf2);
            Color blueprintHighlight = new Color(0x89b5ed);
            addTheme(new NapkinTheme("blueprint", "Blueprint", Color.white,
                    blueprintInk, blueprintInk, blueprintHighlight,
                    blueprint.deriveFont(Font.PLAIN, 13),
                    blueprint.deriveFont(Font.BOLD, 13), def.getFixedFont(),
                    new NapkinBackground("resources/blueprint-bg.gif"),
                    def.getPopup(), def.getErasureMask()));

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

        public static void setCurrentTheme(String themeName) {
            NapkinTheme theme = getTheme(themeName);
            if (theme == null)
                throw new IllegalArgumentException("unknown theme");
            else
                setCurrentTheme(theme);
        }

        public static void addTheme(NapkinTheme theme) {
            themes.put(theme.getName(), theme);
        }

        private static synchronized void getFonts() {
            if (gotFonts)
                return;
            //!! Make this selectable
//        scrawl = tryToLoadFont("aescr5b.ttf");
            scrawl = tryToLoadFont("FeltTipRoman.ttf");
            scrawlBold = tryToLoadFont("FeltTipRoman-Bold.ttf");
            fixed = tryToLoadFont("Mcgf____.ttf");
            blueprint = tryToLoadFont("blueprin.ttf");
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
