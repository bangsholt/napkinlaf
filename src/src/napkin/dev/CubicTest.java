// $Id$

package napkin.dev;

import napkin.CubicGenerator;
import napkin.ShapeGenerator;
import napkin.Value;
import napkin.ValueSource;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.border.*;

class CubicTest extends GeneratorTest implements GeneratorTest.Drawer {

    private final CubicGenerator gen;

    private final Value width;

    private Shape curve;

    private final ValueSpinner leftXSpin;
    private final ValueSpinner leftYSpin;
    private final ValueSpinner rightXSpin;
    private final ValueSpinner rightYSpin;
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

            Graphics2D lineG = lineGraphics(g, (float) width.get());
            lineG.draw(curve);

            Graphics2D markG = markGraphics(g);
            mark(markG, leftXSpin, leftYSpin, true);
            mark(markG, rightXSpin, rightYSpin, false);
        }

        public Dimension getPreferredSize() {
            return getMinimumSize();
        }

        public Dimension getMinimumSize() {
            return new Dimension(LENGTH + 2 * SPACE,
                    MIN_HEIGHT * 2 + 2 * SPACE);
        }
    };

    CubicTest() {
        gen = new CubicGenerator();

        leftXSpin = new ValueSpinner("x", gen.getLeftX(), 0, LENGTH / 2, 100);
        leftYSpin = new ValueSpinner("y", gen.getLeftY(), -20, +20, 100);
        rightXSpin =
                new ValueSpinner("x", gen.getRightX(), 0, LENGTH / 2, 100);
        rightYSpin = new ValueSpinner("y", gen.getRightY(), -20, +20, 100);
        width = new Value(1, 0);
        widthSpin = new ValueSpinner("w", width, 0, 3, 20);
        spinners = new ValueSpinner[]{
            leftXSpin, leftYSpin, rightXSpin, rightYSpin, widthSpin
        };

        rebuild();
    }

    public ShapeGenerator getGenerator() {
        return gen;
    }

    public void rebuild() {
        curve = generate(null);
    }

    public Shape generate(AffineTransform matrix) {
        return gen.generate(matrix);
    }

    public ValueSource[] getSpinners() {
        return spinners;
    }

    public String getName() {
        return "Cubic";
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

        controls.add(controlSet("Left", leftXSpin, leftYSpin));
        controls.add(controlSet("Right", rightXSpin, rightYSpin));
        controls.add(controlSet("Line", widthSpin));

        JPanel lineControl = new JPanel();
        lineControl.setBorder(new TitledBorder("Line"));
        lineControl.setLayout(new BoxLayout(lineControl, BoxLayout.X_AXIS));

        controls.add(Box.createVerticalGlue());

        return controls;
    }
}

