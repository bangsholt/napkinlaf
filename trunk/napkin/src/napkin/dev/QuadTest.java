// $Id$

package napkin.dev;

import napkin.QuadGenerator;
import napkin.ShapeGenerator;
import napkin.Value;
import napkin.ValueSource;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.border.*;

class QuadTest extends GeneratorTest implements GeneratorTest.Drawer {

    private final QuadGenerator gen;

    private final Value width;

    private Shape curve;

    private final ValueSpinner ctlXSpin;
    private final ValueSpinner ctlYSpin;
    private final ValueSpinner widthSpin;
    private final ValueSource[] spinners;

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
        gen = new QuadGenerator();
        ctlXSpin = new ValueSpinner("x", gen.getCtlX(), 0, LENGTH, 100);
        ctlYSpin = new ValueSpinner("y", gen.getCtlY(), -20, +20, 100);
        width = new Value(1, 0);
        widthSpin = new ValueSpinner("w", width, 0, 3, 20);
        spinners = new ValueSpinner[]{ctlXSpin, ctlYSpin, widthSpin};

        rebuild();
    }

    public Shape generate(AffineTransform matrix) {
        return gen.generate(matrix);
    }

    public ShapeGenerator getGenerator() {
        return gen;
    }

    public void rebuild() {
        curve = generate(null);
    }

    public ValueSource[] getSpinners() {
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

