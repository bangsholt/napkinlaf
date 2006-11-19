package net.sourceforge.napkinlaf.sketch;

import java.awt.*;
import static java.awt.RenderingHints.*;
import java.awt.image.*;
import static java.awt.image.BufferedImage.*;
import static java.awt.image.DataBuffer.*;
import java.util.LinkedList;
import java.util.List;

/** @author Alex Lam Sze Lok */
@SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
public class SketchifiedImage extends Image implements ImageObserver {

    private BufferedImage sketch = null;
    private List<ImageObserver> observers = new LinkedList<ImageObserver>();

    private static final RenderingHints hints = new RenderingHints(null);

    static {
        hints.put(KEY_ALPHA_INTERPOLATION, VALUE_ALPHA_INTERPOLATION_QUALITY);
        hints.put(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        hints.put(KEY_INTERPOLATION, VALUE_INTERPOLATION_BICUBIC);
    }

    private static final BandCombineOp invertOp = new BandCombineOp(
            new float[][]{
                    {-1.0f, 255.0f},
            }, hints
    );

    private static final ConvolveOp blurOp = new ConvolveOp(
            new Kernel(3, 3,
                    new float[]{
                            0.0625f, 0.1250f, 0.0625f,
                            0.1250f, 0.2500f, 0.1250f,
                            0.0625f, 0.1250f, 0.0625f,
                    }
            ),
            ConvolveOp.EDGE_NO_OP, hints
    );

    /**
     * Creates a new instance of SketchifiedImage
     *
     * @param image Image to sketchify.
     */
    public SketchifiedImage(Image image) {
        sketch = sketchify(image);
        if (sketch == null) {
            getGraphics().drawImage(image, 0, 0, this);
        }
    }

    @SuppressWarnings({"UnusedAssignment"})
    private static Raster
                   findEdge(Raster alphaRtr, Raster srcRtr,
            WritableRaster dstRtr) {

        WritableRaster invRtr = invertOp.filter(srcRtr, null);
        invRtr = blurOp.filter(invRtr, null);
        int width = invRtr.getWidth();
        int height = invRtr.getHeight();
        if (dstRtr == null) {
            dstRtr = Raster.createBandedRaster(
                    TYPE_BYTE, width, height, 1, null);
        }
        int[] srcVal = new int[1];
        int[] invVal = new int[1];
        int[] alpha = new int[1];
        int[] retVal = new int[1];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                srcRtr.getPixel(x, y, srcVal);
                invRtr.getPixel(x, y, invVal);
                alphaRtr.getPixel(x, y, alpha);
                float c = (srcVal[0] + invVal[0]) / 255.0f;
                c = Math.max(0.0f, Math.min(1.0f, (c - 0.9f) / 0.1f));
                c *= c;
                retVal[0] = (int) (alpha[0] * (1.0f - c));
                dstRtr.setPixel(x, y, retVal);
            }
        }
        return dstRtr;
    }

    static BufferedImage sketchify(Image origImage) {
        int width = origImage.getWidth(null);
        int height = origImage.getHeight(null);
        if (width < 0 || height < 0) {
            return null;
        }
        BufferedImage image =
                new BufferedImage(width, height, TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        ((Graphics2D) g).setRenderingHints(hints);
        g.drawImage(origImage, 0, 0, null);
        // renders the edge image
        WritableRaster imageRtr = image.getRaster();
        WritableRaster alphaRtr = image.getAlphaRaster();
        WritableRaster bands = Raster.createBandedRaster(
                DataBuffer.TYPE_BYTE, width, height, 4, null);
        int[] bandList = new int[1];
        for (int i = 0; i < 4; i++) {
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
        int[] alpha = new int[1];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                bands.getPixel(x, y, pixel);
                alpha[0] = Math.max(pixel[0], pixel[1]);
                alpha[0] = Math.max(alpha[0], pixel[2]);
                alpha[0] = Math.max(alpha[0], pixel[3]);
                alphaRtr.setPixel(x, y, alpha);
            }
        }
        // create a 256-color image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
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

    @Override
    public int getWidth(ImageObserver observer) {
        if (sketch == null) {
            if (observer != null && !observers.contains(observer)) {
                observers.add(observer);
            }
            return -1;
        }
        return sketch.getWidth(observer);
    }

    @Override
    public int getHeight(ImageObserver observer) {
        if (sketch == null) {
            if (observer != null && !observers.contains(observer)) {
                observers.add(observer);
            }
            return -1;
        }
        return sketch.getHeight(observer);
    }

    @Override
    public ImageProducer getSource() {
        return sketch == null ? null : sketch.getSource();
    }

    @Override
    public Graphics getGraphics() {
        return sketch == null ?
                new BufferedImage(1, 1, TYPE_INT_ARGB).getGraphics() :
                sketch.getGraphics();
    }

    @Override
    public Object getProperty(String name, ImageObserver observer) {
        return sketch.getProperty(name, observer);
    }

    public boolean imageUpdate(
            Image img, int infoflags, int x, int y, int width, int height) {

        sketch = sketchify(img);
        sketch.setAccelerationPriority(getAccelerationPriority());
        // always return true to stay on for further events
        return true;
    }

    @Override
    public ImageCapabilities getCapabilities(GraphicsConfiguration gc) {
        return sketch == null ?
                super.getCapabilities(gc) : sketch.getCapabilities(gc);
    }

    @SuppressWarnings({"ParameterHidesMemberVariable"})
    @Override
    public Image getScaledInstance(int width, int height, int hints) {
        return sketch == null ?
                null : sketch.getScaledInstance(width, height, hints);
    }

    @Override
    public float getAccelerationPriority() {
        return sketch == null ?
                super.getAccelerationPriority() :
                sketch.getAccelerationPriority();
    }

    @Override
    public void flush() {
        if (sketch != null) {
            sketch.flush();
        }
        //!! only works with Mustang (or later)
        //super.flush();
    }
}
