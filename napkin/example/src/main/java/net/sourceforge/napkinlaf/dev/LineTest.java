package net.sourceforge.napkinlaf.dev;

import net.sourceforge.napkinlaf.shapes.DrawnLineHolder;
import net.sourceforge.napkinlaf.util.NapkinConstants;
import net.sourceforge.napkinlaf.util.RandomValue;
import net.sourceforge.napkinlaf.util.RandomValueSource;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

class LineTest extends GeneratorTest implements GeneratorTest.Drawer {
    private DrawnLineHolder gen;

    /** @noinspection FieldNameHidesFieldInSuperclass */
    private final RandomValue width;

    /** @noinspection FieldNameHidesFieldInSuperclass */
    private final RandomValueSpinner widthSpin;
    private final RandomValueSource[] spinners;

    private JComponent drawing;

    private class Drawing extends JLabel {
        Drawing() {
            setBorder(new EmptyBorder(SPACE, SPACE, SPACE, SPACE));
        }

        protected void paintComponent(Graphics g1) {
            Graphics2D lineG = lineGraphics(g1, (float) width.get());
            double mid = (double) (getHeight() / 2);
            lineG.translate(SPACE, mid);
            gen.shapeUpToDate(new Rectangle(0, 0, 100, 0), null);
            gen.draw(lineG);
        }

        public Dimension getPreferredSize() {
            return getMinimumSize();
        }

        public Dimension getMinimumSize() {
            return new Dimension(NapkinConstants.LENGTH + 2 * SPACE,
                    MIN_HEIGHT * 2 + 2 * SPACE);
        }
    }

    LineTest() {
        gen = new DrawnLineHolder(null);

        width = new RandomValue(5, 1);
        widthSpin = new RandomValueSpinner("w", width, 0, 100, 100);
        spinners = new RandomValueSpinner[]{
                widthSpin
        };

        rebuild();
    }

    public void rebuild() {
        gen = new DrawnLineHolder(null);
    }

    public RandomValueSource[] getSpinners() {
        return spinners;
    }

    public String getName() {
        return "Line";
    }

    public JComponent getDrawing() {
        if (drawing == null) {
            drawing = new Drawing();
            drawing.setDebugGraphicsOptions(DebugGraphics.BUFFERED_OPTION|DebugGraphics.LOG_OPTION);
        }
        return drawing;
    }

    public JComponent getControls() {
        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
        controls.setAlignmentY(1.0f);

        controls.add(controlSet("Line", widthSpin));

        JPanel lineControl = new JPanel();
        lineControl.setBorder(new TitledBorder("Line"));
        lineControl.setLayout(new BoxLayout(lineControl, BoxLayout.X_AXIS));

        controls.add(Box.createVerticalGlue());

        return controls;
    }
}