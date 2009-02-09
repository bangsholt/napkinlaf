package net.sourceforge.napkinlaf.dev;

import net.sourceforge.napkinlaf.shapes.AbstractDrawnGenerator;
import net.sourceforge.napkinlaf.shapes.DrawnQuadLineGenerator;
import static net.sourceforge.napkinlaf.util.NapkinConstants.BASE_LINE_LENGTH;
import net.sourceforge.napkinlaf.util.RandomValue;
import net.sourceforge.napkinlaf.util.RandomValueSource;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.*;

class QuadTest extends GeneratorTest implements GeneratorTest.Drawer {
    private final DrawnQuadLineGenerator gen;

    private Shape curve;

    private final RandomValueSpinner ctlXSpin;
    private final RandomValueSpinner ctlYSpin;
    /** @noinspection FieldNameHidesFieldInSuperclass */
    private final RandomValueSpinner widthSpin;
    private final RandomValueSource[] spinners;

    private JComponent drawing;

    private class Drawing extends JLabel {
        Drawing() {
            setBorder(new EmptyBorder(SPACE, SPACE, SPACE, SPACE));
        }

        @Override
        protected void paintComponent(Graphics g1) {
            Graphics2D g = (Graphics2D) g1;
            //noinspection IntegerDivisionInFloatingPointContext
            double mid = getHeight() / 2;
            g.translate(SPACE, mid);

            Graphics2D lineG = lineGraphics(g, (float) widthSpin.get());
            lineG.draw(curve);

            Graphics2D markG = markGraphics(g);
            mark(markG, ctlXSpin, ctlYSpin, true);
        }

        @Override
        public Dimension getPreferredSize() {
            return getMinimumSize();
        }

        @Override
        public Dimension getMinimumSize() {
            return new Dimension(BASE_LINE_LENGTH + 2 * SPACE,
                    MIN_HEIGHT * 2 + 2 * SPACE);
        }
    }

    QuadTest() {
        gen = new DrawnQuadLineGenerator();
        ctlXSpin = new RandomValueSpinner("x", gen.getCtl().getX(), 0,
                BASE_LINE_LENGTH,
                100);
        ctlYSpin = new RandomValueSpinner("y", gen.getCtl().getY(), -20, +20,
                100);
        RandomValue w = new RandomValue(1, 0);
        widthSpin = new RandomValueSpinner("w", w, 0, 3, 20);
        spinners = new RandomValueSpinner[]{ctlXSpin, ctlYSpin, widthSpin};

        rebuild();
    }

    Shape generate(AffineTransform matrix) {
        return gen.generate(matrix);
    }

    public AbstractDrawnGenerator getGenerator() {
        return gen;
    }

    public void rebuild() {
        curve = generate(null);
    }

    public RandomValueSource[] getSpinners() {
        return spinners;
    }

    public String getName() {
        return "Quad";
    }

    public JComponent getDrawing() {
        if (drawing == null)
            drawing = new Drawing();
        return drawing;
    }

    public JComponent getControls() {
        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
        controls.setAlignmentY(1.0f);

        controls.add(controlSet("Control", ctlXSpin, ctlYSpin));
        controls.add(controlSet("Line", widthSpin));

        JPanel lineControl = new JPanel();
        lineControl.setBorder(new TitledBorder("Line"));
        lineControl.setLayout(new BoxLayout(lineControl, BoxLayout.X_AXIS));

        controls.add(Box.createVerticalGlue());

        return controls;
    }
}

