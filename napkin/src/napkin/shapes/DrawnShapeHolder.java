// $Id$

package napkin.shapes;

import napkin.util.NapkinUtil;

import java.awt.*;

class DrawnShapeHolder {
    protected AbstractDrawnGenerator gen;
    protected Shape shape;
    protected float width;

    public DrawnShapeHolder(AbstractDrawnGenerator gen) {
        this(gen, 1);
    }

    public DrawnShapeHolder(AbstractDrawnGenerator gen, float width) {
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
