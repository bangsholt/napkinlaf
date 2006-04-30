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
import static java.awt.image.BufferedImage.*;
import static java.awt.image.DataBuffer.TYPE_BYTE;
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

    /** Creates a new instance of SketchedImageIcon */
    public SketchedImageIcon(JComponent component, Icon icon) {
        super(sketchIcon(component, icon));
    }
    
    private static Raster
            findEdge(Raster alphaRtr, Raster srcRtr, WritableRaster dstRtr) {
        WritableRaster invRtr = invertOp.filter(srcRtr, null);
        invRtr = blurOp.filter(invRtr, null);
        int width = invRtr.getWidth();
        int height = invRtr.getHeight();
        int[] srcVal = new int[1];
        int[] invVal = new int[1];
        int[] alpha = new int[1];
        int[] retVal = new int[1];
        if (dstRtr == null) {
            dstRtr = WritableRaster
                    .createBandedRaster(TYPE_BYTE, width, height, 1, null);
        }
        for (int x, y = 0; y < height; y++) {
            for (x = 0; x < width; x++) {
                srcRtr.getPixel(x, y, srcVal);
                invRtr.getPixel(x, y, invVal);
                alphaRtr.getPixel(x, y, alpha);
                float c = (srcVal[0] + invVal[0]) / 255f;
                c = Math.max(0f, Math.min(1f, (c - 0.9f) / 0.1f));
                c *= c;
                retVal[0] = (int) (alpha[0] * (1f - c));
                dstRtr.setPixel(x, y, retVal);
            }
        }
        return dstRtr;
    }
    
    private static Image sketchIcon(JComponent component, Icon icon) {
        int width = icon.getIconWidth();
        int height = icon.getIconHeight();
        BufferedImage image =
                new BufferedImage(width, height, TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        ((Graphics2D) g).setRenderingHints(hints);
        icon.paintIcon(component, g, 0, 0);
        // renders the edge image
        WritableRaster imageRtr = image.getRaster();
        WritableRaster alphaRtr = image.getAlphaRaster();
        WritableRaster bands = WritableRaster.createBandedRaster(
                DataBuffer.TYPE_BYTE, width, height, 4, null);
        for (int i = 0, bandList[] = new int[1]; i < 4; i++) {
            bandList[0] = i;
            Raster imageBand = imageRtr
                    .createWritableChild(0, 0, width, height, 0, 0, bandList);
            WritableRaster band = bands
                    .createWritableChild(0, 0, width, height, 0, 0, bandList);
            findEdge(alphaRtr, imageBand, band);
        }
        BufferedImage edgeImage =
                new BufferedImage(width, height, TYPE_INT_ARGB);
        alphaRtr = edgeImage.getAlphaRaster();
        int[] pixel = new int[4];
        for (int x, y = 0, alpha[] = new int[1]; y < height; y++) {
            for (x = 0; x < width; x++) {
                bands.getPixel(x, y, pixel);
                alpha[0] = Math.max(pixel[0], pixel[1]);
                alpha[0] = Math.max(alpha[0], pixel[2]);
                alpha[0] = Math.max(alpha[0], pixel[3]);
                alphaRtr.setPixel(x, y, alpha);
            }
        }
        // create a 256-color image
        for (int x, y = 0; y < height; y++) {
            for (x = 0; x < width; x++) {
                imageRtr.getPixel(x, y, pixel);
                pixel[0] &= 0xE0;
                pixel[0] |= 0x1F;
                pixel[1] &= 0xE0;
                pixel[1] |= 0x1F;
                pixel[2] &= 0xC0;
                pixel[2] |= 0x3F;
                imageRtr.setPixel(x, y, pixel);
            }
        }
        // overlay the edge image onto the result
        g.drawImage(edgeImage, 0, 0, null);
        return image;
    }
    
}
