package net.sourceforge.napkinlaf.sketch;

import net.sourceforge.napkinlaf.util.NapkinIcon;

import javax.swing.*;
import java.awt.image.*;
import static java.awt.image.BufferedImage.*;

/** @author Alex Lam Sze Lok */
@SuppressWarnings({"WeakerAccess"})
public class SketchifiedIcon extends ImageIcon implements NapkinIcon {
    /**
     * Creates a new instance of {@link SketchifiedIcon}.
     *
     * @param component The component on which the icon is to be rendered.
     * @param icon      The icon to sketch.
     */
    public SketchifiedIcon(JComponent component, Icon icon) {
        super(sketchify(component, icon));
    }

    @SuppressWarnings({"ParameterHidesMemberVariable"})
    private static BufferedImage sketchify(JComponent component, Icon icon) {
        BufferedImage image = new BufferedImage(
                icon.getIconWidth(), icon.getIconHeight(), TYPE_INT_ARGB);
        icon.paintIcon(component, image.getGraphics(), 0, 0);
        return SketchifiedImage.sketchify(image);
    }
}
