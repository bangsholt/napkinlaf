/*
 * SketchedImageIcon.java
 *
 * Created on 27 April 2006, 23:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sourceforge.napkinlaf.sketch;

import net.sourceforge.napkinlaf.util.NapkinIcon;

import java.awt.*;
import java.awt.image.BandCombineOp;
import java.awt.image.BufferedImage;
import javax.swing.*;
import static java.awt.RenderingHints.*;

/**
 *
 * @author Alex Lam Sze Lok
 */
public class SketchedImageIcon extends ImageIcon implements NapkinIcon {
    
    /** Creates a new instance of SketchedImageIcon */
    public SketchedImageIcon(JComponent component, Icon icon) {
        super(sketchIcon(component, icon));
    }
    
    private static Image sketchIcon(JComponent component, Icon icon) {
        BufferedImage image = new BufferedImage(icon.getIconWidth(),
                icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        icon.paintIcon(component, g, 0, 0);
        RenderingHints hints = new RenderingHints(null);
        hints.put(KEY_ALPHA_INTERPOLATION, VALUE_ALPHA_INTERPOLATION_QUALITY);
        hints.put(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        hints.put(KEY_INTERPOLATION, VALUE_INTERPOLATION_BICUBIC);
        BandCombineOp bandOp = new BandCombineOp(
                new float[][] {
                    {0.299f, 0.587f, 0.114f, 0f},
                    {0.299f, 0.587f, 0.114f, 0f},
                    {0.299f, 0.587f, 0.114f, 0f},
                    {0f, 0f, 0f, 1f},
                }, null
            );
        image.setData(bandOp.filter(image.getData(), null));
        return image;
    }
    
}
