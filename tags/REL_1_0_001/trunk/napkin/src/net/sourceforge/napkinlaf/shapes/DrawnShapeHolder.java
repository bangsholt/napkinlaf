// $Id$

package net.sourceforge.napkinlaf.shapes;

import net.sourceforge.napkinlaf.util.NapkinUtil;

import java.awt.*;

class DrawnShapeHolder {
    AbstractDrawnGenerator gen;
    Shape shape;
    float width;

    DrawnShapeHolder(AbstractDrawnGenerator gen) {
        this(gen, 1);
    }

    DrawnShapeHolder(AbstractDrawnGenerator gen, float width) {
        this.gen = gen;
        this.width = width;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void draw(Graphics g) {
        Graphics2D lineG = NapkinUtil.lineGraphics(g, width);
        lineG.draw(shape);
    }

    void fill(Graphics g) {
        Graphics2D fillG = NapkinUtil.lineGraphics(g, 1);
        fillG.fill(shape);
    }
}
