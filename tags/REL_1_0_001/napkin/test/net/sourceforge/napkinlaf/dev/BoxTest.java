// $Id: BoxTest.java 355 2006-03-15 04:15:55 -0500 (Wed, 15 Mar 2006) kcrca $

package net.sourceforge.napkinlaf.dev;

import net.sourceforge.napkinlaf.shapes.DrawnBoxGenerator;
import net.sourceforge.napkinlaf.shapes.DrawnCubicLineGenerator;
import net.sourceforge.napkinlaf.shapes.DrawnQuadLineGenerator;
import static net.sourceforge.napkinlaf.util.NapkinConstants.LENGTH;
import net.sourceforge.napkinlaf.util.RandomValueSource;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

class BoxTest extends GeneratorTest implements GeneratorTest.Drawer {
    private final DrawnBoxGenerator gen;

    private final RandomValueSpinner begXSpin;
    private final RandomValueSpinner endYSpin;
    private final RandomValueSpinner startAdjustSpin;
    private final RandomValueSpinner sizeXSpin;
    private final RandomValueSpinner sizeYSpin;
    private final RandomValueSource[] spinners;

    private final Side[] sides;

    private JComponent drawing;

    private static final String[] TYPE_NAMES = {"cubic", "quad", "straight"};
    private static final Class[] TYPES = {
            DrawnCubicLineGenerator.class, DrawnQuadLineGenerator.class, null
    };

    private class Side {
        private final JComboBox selector;
        private final JCheckBox show;

        Side(String name, final int which) {
            selector = new JComboBox(TYPE_NAMES);
            selector.addActionListener(REPAINT_ACTION);
            selector.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    gen.setGenerator(which, TYPES[selector.getSelectedIndex()]);
                }
            });
            show = new JCheckBox(name, true);
            show.addActionListener(REPAINT_ACTION);
        }
    }

    private class Drawing extends JLabel {
        Drawing() {
            int space = SPACE;
            setBorder(new EmptyBorder(space, space, space, space));
        }

        protected void paintComponent(Graphics g1) {
            Graphics2D g = (Graphics2D) g1;
            double mid = getHeight() / 2.0 - sizeYSpin.get() / 2.0;
            g.translate(SPACE, mid);

            Graphics2D lineG = lineGraphics(g, (float) widthSpin.get());
            Graphics2D markG = markGraphics(g);

            mark(markG, ZERO, ZERO, sizeXSpin, sizeYSpin);

            for (int i = 0; i < 4; i++)
                showSide(lineG, sides[i], gen.getSide(i));

            mark(markG, begXSpin, ZERO, true);
            mark(markG, ZERO, endYSpin, true);
        }

        private void showSide(Graphics2D lineG, Side side, Shape shape) {
            if (side.show.isSelected())
                lineG.draw(shape);
        }

        public Dimension getPreferredSize() {
            return getMinimumSize();
        }

        public Dimension getMinimumSize() {
            return new Dimension(LENGTH + 2 * SPACE,
                    MIN_HEIGHT * 2 + 2 * SPACE);
        }
    }

    private static final String[] SIDE_NAMES = {
            null, "top", "left", "bottom", "right"
    };

    BoxTest(DrawnCubicLineGenerator cubic, DrawnQuadLineGenerator quad) {
        gen = new DrawnBoxGenerator(cubic, quad);

        begXSpin = new RandomValueSpinner("x", gen.getCorner().getX(), -5, +5,
                100);
        endYSpin = new RandomValueSpinner("y", gen.getCorner().getY(), -5, +5,
                100);
        startAdjustSpin = new RandomValueSpinner("adj", gen.getStartAdjust(), 0,
                10, 100, false);
        sizeXSpin = new RandomValueSpinner("width", gen.getSize().getX(), 0,
                LENGTH, 100, false);
        sizeYSpin = new RandomValueSpinner("height", gen.getSize().getY(), 0,
                LENGTH, 100, false);
        spinners = new RandomValueSource[]{begXSpin, endYSpin, widthSpin};

        sides = new Side[4];
        for (int i = 0; i < sides.length; i++) {
            sides[i] = new Side(SIDE_NAMES[i], i);
            gen.setGenerator(i, DrawnCubicLineGenerator.class);
        }

        rebuild();
    }

    public void generate(AffineTransform matrix) {
        gen.generate(matrix);
    }

    public void rebuild() {
        generate(null);
    }

    public RandomValueSource[] getSpinners() {
        return spinners;
    }

    public String getName() {
        return "Box";
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

        controls.add(controlSet("Corner", begXSpin, endYSpin, startAdjustSpin));
        controls.add(controlSet("Size", sizeXSpin, sizeYSpin));
        controls.add(controlSet("Line", widthSpin));

        JPanel typeControl = new JPanel();
        typeControl.setBorder(new TitledBorder("Side types"));
        typeControl.setLayout(new SpringLayout());
        for (Side side : sides) {
            typeControl.add(side.show);
            typeControl.add(side.selector);
        }
        SpringUtilities.makeCompactGrid(typeControl, 2, 4, 0, 0, 0, 0);
        controls.add(typeControl);

        controls.add(Box.createVerticalGlue());

        return controls;
    }
}

