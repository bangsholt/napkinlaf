package net.sourceforge.napkinlaf;

import static net.sourceforge.napkinlaf.NapkinKnownTheme.BASIC_THEME;
import static net.sourceforge.napkinlaf.NapkinKnownTheme.POPUP_THEME;
import static net.sourceforge.napkinlaf.NapkinThemeColor.*;
import net.sourceforge.napkinlaf.fonts.MergedFont;
import net.sourceforge.napkinlaf.sketch.AbstractSketcher;
import net.sourceforge.napkinlaf.sketch.sketchers.DraftSketcher;
import net.sourceforge.napkinlaf.sketch.sketchers.JotSketcher;
import net.sourceforge.napkinlaf.util.AlphaColorUIResource;
import net.sourceforge.napkinlaf.util.NapkinBackground;
import net.sourceforge.napkinlaf.util.NapkinConstants;

import javax.swing.plaf.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class NapkinTheme {
    private final String name;
    private final String description;
    private final Map<NapkinThemeColor, Color> colors =
            new EnumMap<NapkinThemeColor, Color>(NapkinThemeColor.class);
    private final Font textFont;
    private final Font boldTextFont;
    private final Font fixedFont;
    private final AbstractSketcher sketcher;
    private final NapkinBackground paper;
    private final NapkinBackground erasure;
    private Map<NapkinKnownTheme, NapkinTheme> variants =
            new EnumMap<NapkinKnownTheme, NapkinTheme>(NapkinKnownTheme.class);

    /**
     * Creates a new theme with a popup theme derived from the specified one,
     * but with a different background.  If the name of this them is
     * <tt>"Foo"</tt>, the name of the derived background will be
     * <tt>"FooPopup"</tt>.
     */
    public NapkinTheme(String name, String description, Color penColor,
            Color checkColor, Color radioColor, Color highlightColor,
            Color selectionColor, Color rollOverColor, Color popupRollOverColor,
            Font textFont, Font boldTextFont, Font fixedFont,
            AbstractSketcher sketcher, NapkinBackground paper,
            NapkinBackground erasure, NapkinBackground popupPaper) {

        this(name, description, penColor, checkColor, radioColor,
                highlightColor, selectionColor, rollOverColor, textFont,
                boldTextFont, fixedFont, sketcher, paper,
                erasure, new NapkinTheme(name + "Popup",
                description + " (popup)", penColor, checkColor,
                radioColor, highlightColor, selectionColor, popupRollOverColor,
                textFont, boldTextFont, fixedFont, sketcher, popupPaper,
                erasure, (NapkinTheme) null));
    }

    public NapkinTheme(String name, String description, Color penColor,
            Color checkColor, Color radioColor, Color highlightColor,
            Color selectionColor, Color rollOverColor, Font textFont,
            Font boldTextFont, Font fixedFont, AbstractSketcher sketcher,
            NapkinBackground paper, NapkinBackground erasure,
            NapkinTheme popupTheme) {
        this.name = name;
        this.description = description;
        colors.put(PEN_COLOR, uiResource(penColor));
        colors.put(CHECK_COLOR, uiResource(checkColor));
        colors.put(RADIO_COLOR, uiResource(radioColor));
        colors.put(HIGHLIGHT_COLOR, uiResource(highlightColor));
        colors.put(SELECTION_COLOR, uiResource(selectionColor));
        colors.put(BACKGROUND_COLOR,
                uiResource(paper.getMeanColor()));
        colors.put(ROLLOVER_COLOR, uiResource(rollOverColor));
        this.textFont = uiResource(textFont);
        this.boldTextFont = uiResource(boldTextFont);
        this.fixedFont = uiResource(fixedFont);
        this.sketcher = sketcher;
        this.paper = paper;
        this.erasure = erasure;
        variants.put(BASIC_THEME, this);
        variants.put(POPUP_THEME, popupTheme);
        if (popupTheme != null)
            popupTheme.variants = variants;
    }

    private static Color uiResource(Color color) {
        if (color instanceof UIResource)
            return color;
        else
            return new AlphaColorUIResource(color);
    }

    private static Font uiResource(Font font) {
        if (font instanceof UIResource)
            return font;
        else {
            return new net.sourceforge.napkinlaf.fonts.FontUIResource(font);
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Color getPenColor() {
        return colors.get(PEN_COLOR);
    }

    public Color getCheckColor() {
        return colors.get(CHECK_COLOR);
    }

    public Color getRadioColor() {
        return colors.get(RADIO_COLOR);
    }

    public Color getHighlightColor() {
        return colors.get(HIGHLIGHT_COLOR);
    }

    public Color getSelectionColor() {
        return colors.get(SELECTION_COLOR);
    }

    public Color getBackgroundColor() {
        return colors.get(BACKGROUND_COLOR);
    }

    public Color getRollOverColor() {
        return colors.get(ROLLOVER_COLOR);
    }

    public Color getColor(NapkinThemeColor which) {
        return colors.get(which);
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

    public AbstractSketcher getSketcher() {
        return sketcher;
    }

    public NapkinBackground getPaper() {
        return paper;
    }

    public NapkinBackground getErasureMask() {
        return erasure;
    }

    public NapkinTheme getPopupTheme() {
        return getTheme(POPUP_THEME);
    }

    public NapkinTheme getBasicTheme() {
        return getTheme(BASIC_THEME);
    }

    public NapkinTheme getTheme(NapkinKnownTheme which) {
        return variants.get(which);
    }

    @Override
    public String toString() {
        return name;
    }

    public static class Manager {
        private static final Map<String, NapkinTheme> themes =
                new HashMap<String, NapkinTheme>();
        private static NapkinTheme currentTheme;

        private static final String DEFAULT_THEME = "napkin";
        private static final String RESOURCE_PATH = "resources/";

        private static final Class<NapkinLookAndFeel> THIS_CLASS =
                NapkinLookAndFeel.class;
        private static final Logger LOG =
                LogManager.getLogManager().getLogger(THIS_CLASS.getName());

        static {
            AccessController.doPrivileged(new PrivilegedAction<Object>() {
                public Object run() {
                    setup();
                    return null;
                }
            });
        }

        @SuppressWarnings(
                {"NonThreadSafeLazyInitialization", "AccessOfSystemProperties"})
        private static void setup() {
            Color checkGreen = Color.GREEN.darker();
            //!! Make this selectable
            //            scrawl = tryToLoadFont("aescr5b.ttf");
            Font scrawl = tryToLoadFont("FeltTipRoman.ttf");
            Font scrawlBold = tryToLoadFont("FeltTipRoman-Bold.ttf");
            Font fixed = tryToLoadFont("1942.ttf");
            Font augie = tryToLoadFont("augie.ttf");

            NapkinTheme def = new NapkinTheme(DEFAULT_THEME, "Default theme",
                    Color.BLACK, checkGreen, new Color(0xf50000),
                    new Color(0x00, 0xff, 0xff, 0x80), checkGreen,
                    new Color(0xff, 0xff, 0x00, 0x80),
                    new Color(0xff, 0x00, 0xff, 0x80),
                    scrawl.deriveFont(Font.PLAIN, 15),
                    scrawlBold.deriveFont(Font.BOLD, 15),
                    fixed.deriveFont(Font.PLAIN, 15),
                    new JotSketcher(),
                    new NapkinBackground(RESOURCE_PATH + "napkin.jpg"),
                    new NapkinBackground(RESOURCE_PATH + "erasure.png"),
                    new NapkinBackground(RESOURCE_PATH + "postit.jpg",
                            80, 80, 50, 40));
            addTheme(def);

            addTheme(new NapkinTheme("debug", "Debug theme", def.getPenColor(),
                    def.getCheckColor(), def.getRadioColor(),
                    def.getHighlightColor(),
                    def.getPopupTheme().getHighlightColor(),
                    def.getSelectionColor(), def.getRollOverColor(),
                    def.getTextFont(), def.getBoldTextFont(),
                    def.getFixedFont(), new JotSketcher(),
                    new NapkinBackground(RESOURCE_PATH + "testPaper.jpg",
                            0, 0, 10, 10),
                    def.getErasureMask(), new NapkinBackground(
                    RESOURCE_PATH + "testPostit.jpg", 0, 0, 10, 10)));

            Color blueprintInk = new Color(0xe7edf2);
            Color blueprintHighlight = new Color(0x89b5ed);
            // We're using the same font for plain and bold because the current
            // font has no bold, so it's better to do this than let the graphics
            // system pick a replacement "best match" for the bold font.
            Font blueFont = augie.deriveFont(Font.PLAIN, 13);
            addTheme(new NapkinTheme("blueprint", "Blueprint", Color.white,
                    blueprintInk, blueprintInk, blueprintHighlight,
                    blueprintInk, blueprintHighlight, blueFont, blueFont,
                    def.getFixedFont(), new DraftSketcher(),
                    new NapkinBackground(RESOURCE_PATH + "blueprint-bg.jpg"),
                    def.getErasureMask(), def.getPopupTheme()));

            String themeName;
            try {
                themeName = AccessController.doPrivileged(
                        new PrivilegedAction<String>() {
                            public String run() {
                                return System.getProperty(
                                        NapkinConstants.THEME_KEY,
                                        DEFAULT_THEME);
                            }
                        });
            } catch (SecurityException e) {
                e.printStackTrace();
                themeName = null;
            }
            if (themeName == null)
                themeName = DEFAULT_THEME;

            currentTheme = getTheme(themeName);
            if (currentTheme == null)
                currentTheme = getTheme(DEFAULT_THEME);
        }

        public static String[] themeNames() {
            return themes.keySet().toArray(new String[themes.size()]);
        }

        public static NapkinTheme getTheme(String name) {
            return themes.get(name);
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

        static Font tryToLoadFont(String fontName) {
            try {
                String fontRes = RESOURCE_PATH + fontName;
                InputStream fontDef =
                        NapkinLookAndFeel.class.getResourceAsStream(fontRes);
                if (fontDef != null) {
                    return Font.createFont(Font.TRUETYPE_FONT, fontDef);
                } else {
                    throw new NullPointerException(
                            "Could not find font resource \"" + fontName +
                                    "\"\n\t\tin \"" + fontRes +
                                    "\"\n\t\tfor \"" + NapkinLookAndFeel.class
                                    .getName() +
                                    "\"\n\t\ttry: " + NapkinLookAndFeel.class
                                    .getResource(fontRes));
                }
            } catch (FontFormatException e) {
                LOG.log(Level.WARNING, "getting font " + fontName, e);
            } catch (IOException e) {
                LOG.log(Level.WARNING, "getting font " + fontName, e);
            }
            return null;
        }
    }
}
