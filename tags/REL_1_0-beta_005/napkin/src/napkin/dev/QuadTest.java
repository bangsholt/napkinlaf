// $Id$

package napkin.dev;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.border.*;

import napkin.DrawnQuadLineGenerator;
import napkin.DrawnShapeGenerator;
import napkin.RandomValue;
import napkin.RandomValueSource;

class QuadTest extends GeneratorTest implements GeneratorTest.Drawer {

    private final DrawnQuadLineGenerator gen;

    private final RandomValue width;

    private Shape curve;

    private final RandomValueSpinner ctlXSpin;
    private final RandomValueSpinner ctlYSpin;
    private final RandomValueSpinner widthSpin;
    private final RandomValueSource[] spinners;

    private JComponent drawing;

    private class Drawing extends JLabel {
        Drawing() {
            final int space = SPACE;
            setBorder(new EmptyBorder(space, space, space, space));
        }

        protected void paintComponent(Graphics g1) {
            Graphics2D g = (Graphics2D) g1;
            double mid = getHeight() / 2;
            g.translate(SPACE, mid);

            Graphics2D lineG = lineGraphics(g, (float) widthSpin.get());
            lineG.draw(curve);

            Graphics2D markG = markGraphics(g);
            mark(markG, ctlXSpin, ctlYSpin, true);

        }

        public Dimension getPreferredSize() {
            return getMinimumSize();
        }

        public Dimension getMinimumSize() {
            return new Dimension(LENGTH + 2 * SPACE,
                    MIN_HEIGHT * 2 + 2 * SPACE);
        }
    };

    QuadTest() {
        gen = new DrawnQuadLineGenerator();
        ctlXSpin = new RandomValueSpinner("x", gen.getCtlX(), 0, LENGTH, 100);
        ctlYSpin = new RandomValueSpinner("y", gen.getCtlY(), -20, +20, 100);
        width = new RandomValue(1, 0);
        widthSpin = new RandomValueSpinner("w", width, 0, 3, 20);
        spinners = new RandomValueSpinner[]{ctlXSpin, ctlYSpin, widthSpin};

        rebuild();
    }

    public Shape generate(AffineTransform matrix) {
        return gen.generate(matrix);
    }

    public DrawnShapeGenerator getGenerator() {
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

