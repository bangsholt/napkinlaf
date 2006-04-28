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
import java.awt.image.*;
import javax.swing.*;
import static java.awt.RenderingHints.*;

/**
 *
 * @author Alex Lam Sze Lok
 */
public class SketchedImageIcon extends ImageIcon implements NapkinIcon {
    
    private static final RenderingHints hints = new RenderingHints(null);
    static {
        hints.put(KEY_ALPHA_INTERPOLATION, VALUE_ALPHA_INTERPOLATION_QUALITY);
        hints.put(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        hints.put(KEY_INTERPOLATION, VALUE_INTERPOLATION_BICUBIC);
    }

    private static final BandCombineOp desaturateOp = new BandCombineOp(
            new float[][] {
                {0.299f, 0.587f, 0.114f,     0f},
            }, hints
        );

    private static final BandCombineOp invertOp = new BandCombineOp(
            new float[][] {
                {-1f,  255f},
            }, hints
        );
    
    private static final ConvolveOp blurOp = new ConvolveOp(
            new Kernel(3, 3,
                new float[] {
                    0.0625f, 0.1250f, 0.0625f,
                    0.1250f, 0.2500f, 0.1250f,
                    0.0625f, 0.1250f, 0.0625f,
                }
            ),
            ConvolveOp.EDGE_NO_OP, hints
        );

    private static final ConvolveOp sharpenOp = new ConvolveOp(
            new Kernel(3, 3,
                new float[] {
                    -1f, -1f, -1f,
                    -1f,  9f, -1f,
                    -1f, -1f, -1f,
                }
            ),
            ConvolveOp.EDGE_NO_OP, hints
        );

    /** Creates a new instance of SketchedImageIcon */
    public SketchedImageIcon(JComponent component, Icon icon) {
        super(sketchIcon(component, icon));
    }
    
    private static Image sketchIcon(JComponent component, Icon icon) {
        int width = icon.getIconWidth();
        int height = icon.getIconHeight();
        BufferedImage image =
                new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        ((Graphics2D) g).setRenderingHints(hints);
        icon.paintIcon(component, g, 0, 0);
        // create a desaturated image
        BufferedImage grayImage =
                new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        desaturateOp.filter(image.getRaster(), grayImage.getRaster());
        // invert and blur the desaturated image
        BufferedImage grayInvertedImage =
                new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        invertOp.filter(grayImage.getRaster(), grayInvertedImage.getRaster());
        grayInvertedImage
                .setData(blurOp.filter(grayInvertedImage.getRaster(), null));
        // create an edge image out of the 2 layers
        BufferedImage edgeImage =
                new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        WritableRaster gRaster = grayImage.getRaster();
        WritableRaster gIRaster = grayInvertedImage.getRaster();
        WritableRaster aRaster = image.getAlphaRaster();
        WritableRaster dstRaster = edgeImage.getRaster();
        int[] src1 = new int[1];
        int[] src2 = new int[1];
        int[] alpha = new int[1];
        int[] color = new int[4];
        for (int x, y = 0; y < height; y++) {
            for (x = 0; x < width; x++) {
                gRaster.getPixel(x, y, src1);
                gIRaster.getPixel(x, y, src2);
                aRaster.getPixel(x, y, alpha);
                color[0] = src1[0] + src2[0] < 0xF0 ? 0x00 : 0xFF;
                color[2] = color[1] = color[0];
                color[3] = (color[0] == 0x00) ? alpha[0] : 0x00;
                dstRaster.setPixel(x, y, color);
            }
        }
        // smoothen the edges
        edgeImage.setData(blurOp.filter(edgeImage.getRaster(), null));
        edgeImage.setData(sharpenOp.filter(edgeImage.getRaster(), null));
        // overlay the edge image onto the original
        g.drawImage(edgeImage, 0, 0, null);
        return image;
    }
    
}
