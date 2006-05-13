/*
 * SketchifiedIcon.java
 *
 * Created on 27 April 2006, 23:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sourceforge.napkinlaf.sketch;

import net.sourceforge.napkinlaf.util.NapkinIcon;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import static java.awt.image.BufferedImage.*;
import static java.awt.image.DataBuffer.TYPE_BYTE;
import static java.awt.RenderingHints.*;

/**
 *
 * @author Alex Lam Sze Lok
 */
public class SketchifiedIcon extends ImageIcon implements NapkinIcon {
    
    /**
     * Creates a new instance of SketchifiedIcon
     */
    public SketchifiedIcon(JComponent component, Icon icon) {
        super(sketchify(component, icon));
    }

    private static BufferedImage sketchify(JComponent component, Icon icon) {
        BufferedImage image = new BufferedImage(
                icon.getIconWidth(), icon.getIconHeight(), TYPE_INT_ARGB);
        icon.paintIcon(component, image.getGraphics(), 0, 0);
        return SketchifiedImage.sketchify(image);
    }
}
