package net.sourceforge.napkinlaf.dev;

import net.sourceforge.napkinlaf.shapes.AbstractDrawnGenerator;
import net.sourceforge.napkinlaf.shapes.DrawnCubicLineGenerator;
import static net.sourceforge.napkinlaf.util.NapkinConstants.BASE_LINE_LENGTH;
import net.sourceforge.napkinlaf.util.RandomValue;
import net.sourceforge.napkinlaf.util.RandomValueSource;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.*;

class CubicTest extends GeneratorTest implements GeneratorTest.Drawer {
    private final DrawnCubicLineGenerator gen;

    /** @noinspection FieldNameHidesFieldInSuperclass */
    private final RandomValue width;

    private Shape curve;

    private final RandomValueSpinner leftXSpin;
    private final RandomValueSpinner leftYSpin;
    private final RandomValueSpinner rightXSpin;
    private final RandomValueSpinner rightYSpin;
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

            Graphics2D lineG = lineGraphics(g, (float) width.get());
            lineG.draw(curve);

            Graphics2D markG = markGraphics(g);
            mark(markG, leftXSpin, leftYSpin, true);
            mark(markG, rightXSpin, rightYSpin, false);
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

    CubicTest() {
        gen = new DrawnCubicLineGenerator();

        leftXSpin =
                new RandomValueSpinner("x", gen.getLeft().getX(), 0,
                        BASE_LINE_LENGTH / 2.0, 100);
        leftYSpin =
                new RandomValueSpinner("y", gen.getLeft().getY(), -20, +20,
                        100);
        rightXSpin =
                new RandomValueSpinner("x", gen.getRight().getX(), 0,
                        BASE_LINE_LENGTH / 2.0, 100);
        rightYSpin =
                new RandomValueSpinner("y", gen.getRight().getY(), -20, +20,
                        100);
        width = new RandomValue(5, 1);
        widthSpin = new RandomValueSpinner("w", width, 0, 100, 100);
        spinners = new RandomValueSpinner[]{
                leftXSpin, leftYSpin, rightXSpin, rightYSpin, widthSpin
        };

        rebuild();
    }

    public AbstractDrawnGenerator getGenerator() {
        return gen;
    }

    public void rebuild() {
        curve = generate(null);
    }

    Shape generate(AffineTransform matrix) {
        return gen.generate(matrix);
    }

    public RandomValueSource[] getSpinners() {
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

