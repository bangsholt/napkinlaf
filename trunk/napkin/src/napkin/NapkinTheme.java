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
    private final Color[] colors = new Color[4];
    private final Font textFont;
    private final Font boldTextFont;
    private final Font fixedFont;
    private final NapkinBackground paper;
    private final NapkinBackground erasure;
    private final NapkinTheme popupTheme;

    public static final int PEN_COLOR = 0;
    public static final int CHECK_COLOR = 1;
    public static final int RADIO_COLOR = 2;
    public static final int HIGHLIGHT_COLOR = 3;

    public NapkinTheme(String name, String description, Color penColor,
            Color checkColor, Color radioColor, Color highlightColor,
            Font textFont, Font boldTextFont, Font fixedFont,
            NapkinBackground paper, NapkinBackground erasure,
            NapkinBackground popupPaper) {

        this(name, description, penColor, checkColor, radioColor,
                highlightColor, textFont, boldTextFont, fixedFont, paper,
                erasure, new NapkinTheme(name + "Popup",
                        description + " (popup)", penColor, checkColor,
                        radioColor, highlightColor, textFont, boldTextFont,
                        fixedFont, popupPaper, erasure, (NapkinTheme) null));
    }

    public NapkinTheme(String name, String description, Color penColor,
            Color checkColor, Color radioColor, Color highlightColor,
            Font textFont, Font boldTextFont, Font fixedFont,
            NapkinBackground paper, NapkinBackground erasure,
            NapkinTheme popupTheme) {

        this.name = name;
        this.description = description;
        colors[PEN_COLOR] = uiResource(penColor);
        colors[CHECK_COLOR] = uiResource(checkColor);
        colors[RADIO_COLOR] = uiResource(radioColor);
        colors[HIGHLIGHT_COLOR] = uiResource(highlightColor);
        this.textFont = uiResource(textFont);
        this.boldTextFont = uiResource(boldTextFont);
        this.fixedFont = uiResource(fixedFont);
        this.paper = paper;
        this.erasure = erasure;
        this.popupTheme = popupTheme;
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
        return colors[PEN_COLOR];
    }

    public Color getCheckColor() {
        return colors[CHECK_COLOR];
    }

    public Color getRadioColor() {
        return colors[RADIO_COLOR];
    }

    public Color getHighlightColor() {
        return colors[HIGHLIGHT_COLOR];
    }

    public Color getColor(int which) {
        return colors[which];
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

    public NapkinBackground getErasureMask() {
        return erasure;
    }

    public NapkinTheme getPopupTheme() {
        return popupTheme;
    }

    public String toString() {
        return name;
    }

    public static class Manager {
        private static final Map themes = new HashMap();
        private static NapkinTheme currentTheme;

        private static final String DEFAULT_THEME = "napkin";
        private static boolean gotFonts;
        private static Font scrawl;
        private static Font scrawlBold;
        private static Font fixed;
        private static Font augie;
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
                    new NapkinBackground("resources/erasure.png"),
                    new NapkinBackground("resources/postit01.jpg",
                            15, 15, 38, 32));
            addTheme(def);

            addTheme(new NapkinTheme("debug", "Debug theme", def.getPenColor(),
                    def.getCheckColor(), def.getRadioColor(),
                    def.getHighlightColor(), def.getTextFont(),
                    def.getBoldTextFont(), def.getFixedFont(),
                    new NapkinBackground("resources/testPaper.jpg",
                            0, 0, 10, 10),
                    def.getErasureMask(), new NapkinBackground(
                            "resources/testPostit.jpg", 0, 0, 10, 10)));

            Color blueprintInk = new Color(0xe7edf2);
            Color blueprintHighlight = new Color(0x89b5ed);
            addTheme(new NapkinTheme("blueprint", "Blueprint", Color.white,
                    blueprintInk, blueprintInk, blueprintHighlight,
                    augie.deriveFont(Font.PLAIN, 13),
                    augie.deriveFont(Font.BOLD, 13), def.getFixedFont(),
                    new NapkinBackground("resources/blueprint-bg.jpg"),
                    def.getErasureMask(), def.getPopupTheme()));

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
            augie = tryToLoadFont("augie.ttf");
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
