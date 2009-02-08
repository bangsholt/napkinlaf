package net.sourceforge.napkinlaf;

import static net.sourceforge.napkinlaf.NapkinKnownTheme.*;
import static net.sourceforge.napkinlaf.NapkinThemeColor.*;
import net.sourceforge.napkinlaf.fonts.PatchedFontUIResource;
import net.sourceforge.napkinlaf.sketch.AbstractSketcher;
import net.sourceforge.napkinlaf.sketch.sketchers.DraftSketcher;
import net.sourceforge.napkinlaf.sketch.sketchers.JotSketcher;
import net.sourceforge.napkinlaf.util.AlphaColorUIResource;
import net.sourceforge.napkinlaf.util.NapkinBackground;
import net.sourceforge.napkinlaf.util.NapkinConstants;

import javax.swing.*;
import javax.swing.plaf.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * This class describes a theme for the particular drawing style.  You can
 * specify such things as the font, text color, the sketcher that wll be used to
 * render icon templates, and the background paper image.  Defining a new theme
 * can be as simple as creating a <tt>NapkinTheme</tt> object with certain
 * parameters, or you can define your own sketcher to get a complete different
 * style.
 */
@SuppressWarnings({"WeakerAccess"})
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
     * Creates a new theme with a calculated popup theme.  The popup theme will
     * be the same as the main one, but with a different paper and highlight. If
     * the name of this them is <tt>"Foo"</tt>, the name of the derived
     * background will be <tt>"FooPopup"</tt>. If you want to specifiy a full
     * theme for popups, you can use the other constructor.
     *
     * @param name               The short name of the theme.
     * @param description        A human-readable description of the theme (in
     *                           brief).
     * @param penColor           Default pen color for drawing lines.
     * @param checkColor         Color for drawing check-marks. This is also
     *                           used for filling in the progress bar, and to
     *                           underline default buttons.
     * @param radioColor         Color to fill in selected radio buttons.
     * @param highlightColor     Color for highlighting the component with the
     *                           current focus.
     * @param rolloverColor      Color for highlighting during rollover.
     * @param selectionColor     Color for text in selected items, such as in
     *                           lists.
     * @param textFont           Default text font.
     * @param boldTextFont       Font for bold text.
     * @param fixedFont          Font for fixed-width text.
     * @param sketcher           Sketcher to use for turning templates into
     *                           images and icons.
     * @param paper              Background paper to use in normal windows.
     * @param erasure            Image to use as an erasure mask. Disabled
     *                           components are masked through this image when
     *                           drawn.
     * @param popupPaper         Paper to use for popup windows (tool tips,
     *                           popup menus, etc.)
     * @param popupRolloverColor Color for highlighting during rollover on popup
     *                           paer.
     */
    public NapkinTheme(String name, String description, Color penColor,
            Color checkColor, Color radioColor, Color highlightColor,
            Color rolloverColor, Color selectionColor, Font textFont,
            Font boldTextFont, Font fixedFont, AbstractSketcher sketcher,
            NapkinBackground paper, NapkinBackground erasure,
            NapkinBackground popupPaper, Color popupRolloverColor) {

        this(name, description, penColor, checkColor, radioColor,
                highlightColor, rolloverColor, selectionColor, textFont,
                boldTextFont, fixedFont, sketcher, paper, erasure,
                new NapkinTheme(name + "Popup", description + " (popup)",
                        penColor, checkColor, radioColor, highlightColor,
                        popupRolloverColor, selectionColor, textFont,
                        boldTextFont, fixedFont, sketcher, popupPaper, erasure,
                        null));
    }

    /**
     * Creates a new theme with a specified theme for popup windows.  The popup
     * theme of the specified popup theme is ignored.
     *
     * @param name           The short name of the theme.
     * @param description    A human-readable description of the theme (in
     *                       brief).
     * @param penColor       Default pen color for drawing lines.
     * @param checkColor     Color for drawing check-marks. This is also used
     *                       for filling in the progress bar, and to underline
     *                       default buttons.
     * @param radioColor     Color to fill in selected radio buttons.
     * @param highlightColor Color for highlighting the component with the
     *                       current focus.
     * @param rolloverColor  Color for highlighting during rollover.
     * @param selectionColor Color for text in selected items, such as in
     *                       lists.
     * @param textFont       Default text font.
     * @param boldTextFont   Font for bold text.
     * @param fixedFont      Font for fixed-width text.
     * @param sketcher       Sketcher to use for turning templates into images
     *                       and icons.
     * @param paper          Background paper to use in normal windows.
     * @param erasure        Image to use as an erasure mask. Disabled
     *                       components are masked through this image when
     *                       drawn.
     * @param popupTheme     Theme to use for popup windows (tool tips, popup
     *                       menus, etc.)
     */
    public NapkinTheme(String name, String description, Color penColor,
            Color checkColor, Color radioColor, Color highlightColor,
            Color rolloverColor, Color selectionColor, Font textFont,
            Font boldTextFont, Font fixedFont, AbstractSketcher sketcher,
            NapkinBackground paper, NapkinBackground erasure,
            NapkinTheme popupTheme) {

        this.name = name;
        this.description = description;
        colors.put(PEN_COLOR, toColorResource(penColor));
        colors.put(CHECK_COLOR, toColorResource(checkColor));
        colors.put(RADIO_COLOR, toColorResource(radioColor));
        colors.put(HIGHLIGHT_COLOR, toColorResource(highlightColor));
        colors.put(SELECTION_COLOR, toColorResource(selectionColor));
        colors.put(BACKGROUND_COLOR, toColorResource(paper.getMeanColor()));
        colors.put(ROLLOVER_COLOR, toColorResource(rolloverColor));
        this.textFont = toFontResource(textFont);
        this.boldTextFont = toFontResource(boldTextFont);
        this.fixedFont = toFontResource(fixedFont);
        this.sketcher = sketcher;
        this.paper = paper;
        this.erasure = erasure;
        variants.put(BASIC_THEME, this);
        variants.put(POPUP_THEME, popupTheme);
        if (popupTheme != null) {
            popupTheme.variants = variants;
        }
    }

    private static Color toColorResource(Color color) {
        return color instanceof UIResource ? color : new AlphaColorUIResource(
                color);
    }

    private static Font toFontResource(Font font) {
        return font instanceof UIResource ?
                font :
                PatchedFontUIResource.wrapIfPossible(font);
    }

    /** @return The name of the theme. */
    public String getName() {
        return name;
    }

    /** @return The description of the theme. */
    public String getDescription() {
        return description;
    }

    /** @return The pen color. */
    public Color getPenColor() {
        return colors.get(PEN_COLOR);
    }

    /** @return The check mark color. */
    public Color getCheckColor() {
        return colors.get(CHECK_COLOR);
    }

    /** @return The selected radio-button fill-in color. */
    public Color getRadioColor() {
        return colors.get(RADIO_COLOR);
    }

    /** @return The highlighter pen color. */
    public Color getHighlightColor() {
        return colors.get(HIGHLIGHT_COLOR);
    }

    /** @return The rollover highlighter pen color. */
    public Color getRolloverColor() {
        return colors.get(ROLLOVER_COLOR);
    }

    /** @return The text color for selected items. */
    public Color getSelectionColor() {
        return colors.get(SELECTION_COLOR);
    }

    /** @return The background color. */
    public Color getBackgroundColor() {
        return colors.get(BACKGROUND_COLOR);
    }

    /**
     * @param which Which color to return.
     *
     * @return A specified color.
     */
    @SuppressWarnings({"TypeMayBeWeakened"})
    public Color getColor(NapkinThemeColor which) {
        return colors.get(which);
    }

    /** @return The default text font. */
    public Font getTextFont() {
        return textFont;
    }

    /** @return The bold text font. */
    public Font getBoldTextFont() {
        return boldTextFont;
    }

    /** @return The fixed-width text font. */
    public Font getFixedFont() {
        return fixedFont;
    }

    /** @return The sketcher to use. */
    public AbstractSketcher getSketcher() {
        return sketcher;
    }

    /** @return The background paper. */
    public NapkinBackground getPaper() {
        return paper;
    }

    /** @return The erasure mask image. */
    public NapkinBackground getErasureMask() {
        return erasure;
    }

    /** @return The theme for popup windows. */
    public NapkinTheme getPopupTheme() {
        return getTheme(POPUP_THEME);
    }

    /** @return The default theme to use. */
    public NapkinTheme getBasicTheme() {
        return getTheme(BASIC_THEME);
    }

    /**
     * @param which The theme to return.
     *
     * @return The specified theme.
     */
    @SuppressWarnings({"TypeMayBeWeakened"})
    public NapkinTheme getTheme(NapkinKnownTheme which) {
        return variants.get(which);
    }

    /** @return The theme name. */
    @Override
    public String toString() {
        return name;
    }

    /** This class manages the installation and switching of themes. */
    public static class Manager {
        private static final Map<String, NapkinTheme> themes =
                new HashMap<String, NapkinTheme>();
        private static NapkinTheme currentTheme;

        private static final String DEFAULT_THEME = "napkin";
        private static final String RESOURCE_PATH = "resources/";

        private static final Class<NapkinLookAndFeel> THIS_CLASS =
                NapkinLookAndFeel.class;
        private static final Logger LOG = LogManager.getLogManager().getLogger(
                THIS_CLASS.getName());
        private static final String DEBUG_THEME = "debug";
        private static final String BLUEPRINT_THEME = "blueprint";

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

            String image = "postit.jpg";
            int scrawlSize = 14;
            NapkinTheme def = new NapkinTheme(DEFAULT_THEME, "Default theme",
                    Color.BLACK, checkGreen, new Color(0xf50000), new Color(
                            0x00, 0xff, 0xff, 0x80), new Color(0xff, 0xff, 0x00,
                            0x80), checkGreen, scrawl.deriveFont(Font.PLAIN,
                            scrawlSize), scrawlBold.deriveFont(Font.BOLD,
                            scrawlSize), fixed.deriveFont(Font.PLAIN,
                            scrawlSize), new JotSketcher(), background(
                            "napkin.jpg"), background("erasure.png"),
                    background(image, 80, 80, 50, 40), new Color(0xff, 0x00,
                            0xff, 0x80));
            addTheme(def);

            addTheme(new NapkinTheme(DEBUG_THEME, "Debug theme",
                    def.getPenColor(), def.getCheckColor(), def.getRadioColor(),
                    def.getHighlightColor(), def.getSelectionColor(),
                    def.getPopupTheme().getHighlightColor(), def.getTextFont(),
                    def.getBoldTextFont(), def.getFixedFont(),
                    new JotSketcher(), background("testPaper.jpg", 0, 0, 10,
                            10), def.getErasureMask(), background(
                            "testPostit.jpg", 0, 0, 10, 10),
                    def.getRolloverColor()));

            Color blueprintInk = new Color(0xe7edf2);
            Color blueprintHighlight = new Color(0x89b5ed);
            // We're using the same font for plain and bold because the current
            // font has no bold, so it's better to do this than let the graphics
            // system pick a replacement "best match" for the bold font.
            Font blueFont = augie.deriveFont(Font.PLAIN, 11);
            addTheme(new NapkinTheme(BLUEPRINT_THEME, "Blueprint", Color.white,
                    blueprintInk, blueprintInk, blueprintHighlight,
                    blueprintHighlight, blueprintInk, blueFont, blueFont,
                    def.getFixedFont(), new DraftSketcher(), background(
                            "blueprint-bg.jpg"), def.getErasureMask(),
                    def.getPopupTheme()));

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
            if (themeName == null) {
                themeName = DEFAULT_THEME;
            }

            currentTheme = getTheme(themeName);
            if (currentTheme == null) {
                currentTheme = getTheme(DEFAULT_THEME);
            }
        }

        private static NapkinBackground background(String image, int top,
                int left, int bottom, int right) {

            ImageIcon icon = getBackgroundImage(RESOURCE_PATH + image);
            return new NapkinBackground(icon, top, left, bottom, right);
        }

        private static NapkinBackground background(String image) {
            ImageIcon icon = getBackgroundImage(RESOURCE_PATH + image);
            return new NapkinBackground(icon);
        }

        private static ImageIcon getBackgroundImage(String name) {
            URL resource = NapkinLookAndFeel.class.getResource(name);
            if (resource == null) {
                throw new NullPointerException(
                        "no resource found for: " + name);
            }
            Image image = Toolkit.getDefaultToolkit().getImage(resource);
            return new ImageIcon(image, name);
        }

        /**
         * Returns the list of known theme names. This will consist of the
         * default (pre-installed) themes, plus any added via {@link
         * #addTheme(NapkinTheme)}.
         *
         * @return The list of known theme names.
         */
        public static String[] themeNames() {
            return themes.keySet().toArray(new String[themes.size()]);
        }

        /**
         * @param name The theme name to look up.
         *
         * @return The scheme with the given name. If the name is not known,
         *         returns <tt>null</tt>.
         *
         * @see #addTheme(NapkinTheme)
         */
        @SuppressWarnings({"TypeMayBeWeakened"})
        public static NapkinTheme getTheme(String name) {
            return themes.get(name);
        }

        /** @return The default Napkin Look and Feel theme. */
        public static NapkinTheme getDefaultTheme() {
            return getTheme(DEFAULT_THEME);
        }

        /** @return The theme currently in use. */
        public static NapkinTheme getCurrentTheme() {
            return currentTheme;
        }

        /**
         * Sets the current theme.
         *
         * @param theme The new theme to make current.  If <tt>null</tt>, switch
         *              back to the default theme.
         */
        public static void setCurrentTheme(NapkinTheme theme) {
            currentTheme = (theme != null ? theme : getDefaultTheme());
        }

        /**
         * Sets the current theme, looked up by name.  This name must be either
         * in the default lists of themes, or have been added.
         *
         * @param themeName The name of the theme.
         *
         * @see #addTheme(NapkinTheme)
         */
        public static void setCurrentTheme(String themeName) {
            NapkinTheme theme = getTheme(themeName);
            if (theme == null) {
                throw new IllegalArgumentException("unknown theme");
            } else {
                setCurrentTheme(theme);
            }
        }

        /**
         * Add a new theme to the list of known, available themes.
         *
         * @param theme The theme to add.
         */
        public static void addTheme(NapkinTheme theme) {
            themes.put(theme.getName(), theme);
        }

        static Font tryToLoadFont(String fontName) {
            Font result = null;
            try {
                String fontRes = RESOURCE_PATH + fontName;
                InputStream fontDef =
                        NapkinLookAndFeel.class.getResourceAsStream(fontRes);
                if (fontDef != null) {
                    result = Font.createFont(Font.TRUETYPE_FONT, fontDef);
                } else {
                    String msg = "Could not find font resource \"" + fontName +
                            "\"\n\t\tin \"" + fontRes + "\"\n\t\tfor \"" +
                            NapkinLookAndFeel.class.getName() +
                            "\"\n\t\ttry: " +
                            NapkinLookAndFeel.class.getResource(fontRes);
                    System.err.println(msg);
                    throw new NullPointerException(msg);
                }
            } catch (FontFormatException e) {
                LOG.log(Level.WARNING, "getting font " + fontName, e);
            } catch (IOException e) {
                LOG.log(Level.WARNING, "getting font " + fontName, e);
            }
            return result;
        }
    }
}
