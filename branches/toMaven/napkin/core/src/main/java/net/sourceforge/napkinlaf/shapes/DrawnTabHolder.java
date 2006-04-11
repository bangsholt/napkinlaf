package net.sourceforge.napkinlaf.shapes;

import static net.sourceforge.napkinlaf.util.NapkinConstants.*;

import java.awt.geom.*;

public class DrawnTabHolder extends DrawnShapeHolder {
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

    public void shapeUpToDate(int tabPlacement, int x, int y, int w, int h) {
        if (tabPlacement != this.tabPlacement || w != this.w || h != this.h ||
                this.x != x || this.y != y) {

            if (tabPlacement != this.tabPlacement) {
                gen = DrawnTabGenerator.generatorFor(tabPlacement);
            }

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
    }

    private void setBreak(int tabPlacement, int x, int w, int y, int h) {
        DrawnTabGenerator tab = ((DrawnTabGenerator) gen);
        switch (tabPlacement) {
        case TOP:
            breakBeg.setLocation(x + tab.getLL().getX().get() * w,
                    y + tab.getLL().getY().get() * h);
            breakEnd.setLocation(x + tab.getLR().getX().get() * w,
                    y + tab.getLR().getY().get() * h);
            break;
        case RIGHT:
            breakBeg.setLocation(x + tab.getUL().getX().get() * w,
                    y + tab.getUL().getY().get() * h);
            breakEnd.setLocation(x + tab.getLL().getX().get() * w,
                    y + tab.getLL().getY().get() * h);
            break;
        case BOTTOM:
            breakBeg.setLocation(x + tab.getUR().getX().get() * w,
                    y + tab.getUR().getY().get() * h);
            breakEnd.setLocation(x + tab.getUL().getX().get() * w,
                    y + tab.getUL().getY().get() * h);
            break;
        case LEFT:
            breakBeg.setLocation(x + tab.getLR().getX().get() * w,
                    y + tab.getLR().getY().get() * h);
            breakEnd.setLocation(x + tab.getUR().getX().get() * w,
                    y + tab.getUR().getY().get() * h);
            break;
        default:
            throw new IllegalStateException("unknown side: " + tabPlacement);
        }
    }

    public Point2D getBreakBeg() {
        return breakBeg;
    }

    public Point2D getBreakEnd() {
        return breakEnd;
    }
}
