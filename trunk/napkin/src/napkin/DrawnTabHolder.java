// $Id$

package napkin;

import java.awt.geom.*;

public class DrawnTabHolder extends DrawnShapeHolder
        implements NapkinConstants {

    private int tabPlacement;
    private int x, y;
    private int w, h;
    private final Point2D breakBeg;
    private final Point2D breakEnd;

    public DrawnTabHolder(int tabPlacement) {
        super(DrawnTabGenerator.generatorFor(tabPlacement));
        breakBeg = new Point2D.Double();
        breakEnd = new Point2D.Double();
    }

    /**
     * @noinspection ParameterHidingMemberVariable
     */
    public void shapeUpToDate(int tabPlacement, int x, int y, int w, int h) {
        if (tabPlacement == this.tabPlacement && w == this.w && h == this.h &&
                this.x == x && this.y == y) {
            return;
        }

        if (tabPlacement != this.tabPlacement)
            gen = DrawnTabGenerator.generatorFor(tabPlacement);

        AffineTransform matrix = new AffineTransform();
        matrix.translate(x, y);
        matrix.scale(w, h);
        shape = gen.generate(matrix);

        this.tabPlacement = tabPlacement;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;

        setBreak(tabPlacement, x, w, y, h);
    }

    private void setBreak(int tabPlacement, int x, int w, int y, int h) {
        DrawnTabGenerator tab = ((DrawnTabGenerator) gen);
        switch (tabPlacement) {
        case TOP:
            breakBeg.setLocation(x + tab.getLLX().get() * w,
                    y + tab.getLLY().get() * h);
            breakEnd.setLocation(x + tab.getLRX().get() * w,
                    y + tab.getLRY().get() * h);
            break;
        case RIGHT:
            breakBeg.setLocation(x + tab.getULX().get() * w,
                    y + tab.getULY().get() * h);
            breakEnd.setLocation(x + tab.getLLX().get() * w,
                    y + tab.getLLY().get() * h);
            break;
        case BOTTOM:
            breakBeg.setLocation(x + tab.getURX().get() * w,
                    y + tab.getURY().get() * h);
            breakEnd.setLocation(x + tab.getULX().get() * w,
                    y + tab.getULY().get() * h);
            break;
        case LEFT:
            breakBeg.setLocation(x + tab.getLRX().get() * w,
                    y + tab.getLRY().get() * h);
            breakEnd.setLocation(x + tab.getURX().get() * w,
                    y + tab.getURY().get() * h);
            break;
        }
    }

    public Point2D getBreakBeg() {
        return breakBeg;
    }

    public Point2D getBreakEnd() {
        return breakEnd;
    }

}
