package net.sourceforge.napkinlaf.sketch;

import net.sourceforge.napkinlaf.NapkinTheme;
import net.sourceforge.napkinlaf.NapkinThemeColor;
import net.sourceforge.napkinlaf.util.NapkinUtil;

import java.awt.*;
import java.awt.color.*;

public class TemplateColor {
    private static final float[] DUMMY_FLOATS = new float[3];

    private static class SpecialSpace extends ColorSpace {
        private final NapkinThemeColor themeColor;

        public SpecialSpace(NapkinThemeColor themeColor) {
            super(0, 0);
            this.themeColor = themeColor;
        }

        public float[] fromCIEXYZ(float[] colorvalue) {
            return DUMMY_FLOATS;
        }

        public float[] fromRGB(float[] rgbvalue) {
            return DUMMY_FLOATS;
        }

        public float[] toCIEXYZ(float[] colorvalue) {
            return DUMMY_FLOATS;
        }

        public float[] toRGB(float[] colorvalue) {
            return DUMMY_FLOATS;
        }
    }

    public static final Color BACKGROUND = specialColor(
            NapkinThemeColor.BACKGROUND_COLOR);
    public static final Color CHECK = specialColor(
            NapkinThemeColor.CHECK_COLOR);
    public static final Color HIGHLIGHT = specialColor(
            NapkinThemeColor.HIGHLIGHT_COLOR);
    public static final Color PEN = specialColor(NapkinThemeColor.PEN_COLOR);
    public static final Color RADIO = specialColor(
            NapkinThemeColor.RADIO_COLOR);
    public static final Color ROLLOVER = specialColor(
            NapkinThemeColor.ROLLOVER_COLOR);
    public static final Color SELECTION = specialColor(
            NapkinThemeColor.SELECTION_COLOR);

    private static Color specialColor(NapkinThemeColor themeColor) {
        return new Color(new SpecialSpace(themeColor), DUMMY_FLOATS, 1.0f);
    }

    public static Color colorFor(Color color, Component c,
            NapkinThemeColor themeColor) {

        if (color == null) {
            return NapkinUtil.currentTheme(c).getColor(themeColor);
        } else if (!isSpecial(color)) {
            return color;
        } else {
            SpecialSpace space = (SpecialSpace) color.getColorSpace();
            NapkinTheme theme = NapkinUtil.currentTheme(c);
            return theme.getColor(space.themeColor);
        }
    }

    public static boolean isSpecial(Color color) {
        return color.getColorSpace() instanceof SpecialSpace;
    }

    public static String nameFor(Color color) {
        if (!isSpecial(color)) {
            return null;
        } else {
            SpecialSpace space = (SpecialSpace) color.getColorSpace();
            return space.themeColor.toString();
        }
    }

    public static Color colorFor(NapkinThemeColor color) {
        switch (color) {
        case BACKGROUND_COLOR:
            return BACKGROUND;
        case CHECK_COLOR:
            return CHECK;
        case HIGHLIGHT_COLOR:
            return HIGHLIGHT;
        case PEN_COLOR:
            return PEN;
        case RADIO_COLOR:
            return RADIO;
        case ROLLOVER_COLOR:
            return ROLLOVER;
        case SELECTION_COLOR:
            return SELECTION;
        default:
            throw new IllegalArgumentException(color + ": Unknown color type");
        }
    }
}