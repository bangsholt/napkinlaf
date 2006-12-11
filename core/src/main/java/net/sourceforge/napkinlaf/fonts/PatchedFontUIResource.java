package net.sourceforge.napkinlaf.fonts;

import javax.swing.plaf.*;
import java.awt.*;
import java.lang.reflect.Field;

/**
 * This is a {@link FontUIResource} that works around a bug in some Java
 * releases.
 *
 * @author Alex Lam Sze Lok
 * @see PatchedFontUIResource#workaround6313541(Font,Font)
 */
@SuppressWarnings({"WeakerAccess", "UnusedCatchParameter"})
public class PatchedFontUIResource extends Font implements UIResource {

    private static final Field font2DHandleField;
    private static final Field createdFontField;

    static {
        Field fField = null;
        Field cField = null;
        try {
            // transfer private field font2DHandle
            fField = Font.class.getDeclaredField("font2DHandle");
            fField.setAccessible(true);
            // transfer private field createdFont
            cField = Font.class.getDeclaredField("createdFont");
            cField.setAccessible(true);
        } catch (Exception ex) {
            /*
             * Could be IllegalArgumentException,
             * SecurityException or NoSuchFieldException
             */
            // fall through
        }
        font2DHandleField = fField;
        createdFontField = cField;
    }

    /**
     * Creates a new instance of {@link PatchedFontUIResource}.
     *
     * @param font The font for the resource.
     */
    public PatchedFontUIResource(Font font) {
        super(font.getAttributes());
        workaround6313541(font, this);
    }

    public static Font wrapIfPossible(Font font) {
        return doesPatchWork() ? new PatchedFontUIResource(font) : font;
    }

    public static boolean doesPatchWork() {
        return font2DHandleField != null;
    }

    /**
     * Creates a new instance of {@link PatchedFontUIResource}.
     *
     * @param name  The name of the font for the resource.
     * @param style The style of the font for the resource.
     * @param size  The size of the font for the resource.
     */
    public PatchedFontUIResource(String name, int style, int size) {
        super(name, style, size);
    }

    /**
     * Bug 6313541 (fixed in the Mustang (1.6) release) prevents the bundled
     * fonts loading, because the <tt>font2DHandle</tt> field is not transferred
     * when calling <tt>Font(attributes)</tt>.  The workaround uses reflection,
     * which might not work for applets and Web Start applications, so I've put
     * in checks so the workaround is used only when needed.
     *
     * @param src The font being copied.
     * @param dst The font that has been copied and may need to be patched.
     */
    @SuppressWarnings({"UnusedCatchParameter"})
    protected static void workaround6313541(Font src, Font dst) {
        // check for the effect of the bug -- don't do it if it's not needed
        if (doesPatchWork() && !dst.getFontName().equals(src.getFontName())) {
            try {
                font2DHandleField.set(dst, font2DHandleField.get(src));
                createdFontField.set(dst, createdFontField.get(src));
            } catch (Exception ex) {
                /*
                 * Could be IllegalArgumentException,
                 * SecurityException or IllegalAccessException
                 */
                // fall through
            }
        }
    }
}
