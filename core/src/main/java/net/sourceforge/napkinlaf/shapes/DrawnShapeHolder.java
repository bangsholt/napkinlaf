package net.sourceforge.napkinlaf.shapes;

import net.sourceforge.napkinlaf.util.NapkinUtil;

import java.awt.*;

@SuppressWarnings({"WeakerAccess"})
class DrawnShapeHolder {
    AbstractDrawnGenerator gen;
    Shape shape;
    float width;
    int cap;
    int join;

    DrawnShapeHolder(AbstractDrawnGenerator gen) {
        this(gen, 1);
    }

    DrawnShapeHolder(AbstractDrawnGenerator gen, float width) {
        this.gen = gen;
        this.width = width;
        cap = BasicStroke.CAP_ROUND;
        join = BasicStroke.JOIN_ROUND;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public int getJoin() {
        return join;
    }

    public void setJoin(int join) {
        this.join = join;
    }

    public int getCap() {
        return cap;
    }

    @SuppressWarnings({"SameParameterValue"})
    public void setCap(int cap) {
        this.cap = cap;
    }

    public void draw(Graphics g) {
        Graphics2D lineG = NapkinUtil.lineGraphics(g, width, cap, join);
        lineG.draw(shape);
    }

    void fill(Graphics g) {
        Graphics2D fillG = NapkinUtil.lineGraphics(g, 1);
        fillG.fill(shape);
    }
}
