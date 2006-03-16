// $Id$

package napkin.shapes;

import napkin.borders.NapkinBoxBorder;
import static napkin.util.NapkinConstants.HORIZONTAL;

import java.awt.*;
import java.awt.geom.*;

public class DrawnScribbleHolder extends DrawnShapeHolder {
    private Rectangle size;
    private Insets insets;
    private int orientation;
    private int shown;
    private boolean backwards;

    private static final float LINE_WIDTH = 3;

    public DrawnScribbleHolder() {
        super(new DrawnScribbleGenerator(LINE_WIDTH), LINE_WIDTH);
    }

    public boolean shapeUpToDate(Component c, Rectangle sz, int orient, int shn,
            boolean bwrds) {
        Insets in = (c instanceof Container ?
                ((Container) c).getInsets() : NapkinBoxBorder.getDefaultInsets(
                c.getBounds()));

        if (size != null && bwrds == backwards && insets.equals(in) &&
                orientation == orient && shown == shn &&
                size.width == sz.width && size.height == sz.height) {

            return false;
        }

        size = (Rectangle) sz.clone();
        insets = (Insets) in.clone();
        orientation = orient;
        shown = shn;
        backwards = bwrds;

        int cornerX = in.top / 2 + 1;
        int cornerY = in.left / 2 + 1;

        double innerWidth = sz.getWidth() - (in.left + in.right);
        double innerHeight = sz.getHeight() - (in.top + in.bottom);

        DrawnScribbleGenerator dsg = (DrawnScribbleGenerator) gen;
        dsg.setShown(shown);
        dsg.setOrientation(orientation);
        dsg.setRange(orientation == HORIZONTAL ? innerHeight : innerWidth);
        dsg.setMax(orientation == HORIZONTAL ? innerWidth : innerHeight);

        AffineTransform matrix = new AffineTransform();
        matrix.translate(cornerY, cornerX);
        if (backwards) {
            matrix.translate(innerWidth, innerHeight);
            matrix.scale(-1, -1);
        }

        shape = dsg.generate(matrix);
        return true;
    }
}
