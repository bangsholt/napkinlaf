package net.sourceforge.napkinlaf.fonts;

import javax.swing.plaf.*;
import java.awt.*;

/**
 * This is a {@link FontUIResource} that works around a bug in some Java
 * releases.
 *
 * @author Alex Lam Sze Lok
 * @see MergedFont#workaround6313541(Font, Font)
 */
public class PatchedFontUIResource extends FontUIResource {
    /**
     * Creates a new instance of {@link PatchedFontUIResource}.
     *
     * @param font The font for the resource.
     */
    public PatchedFontUIResource(Font font) {
        super(font);
        MergedFont.workaround6313541(font, this);
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
}
