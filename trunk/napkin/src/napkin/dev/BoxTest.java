// $Id$

package napkin.dev;

import napkin.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.border.*;

class BoxTest extends GeneratorTest implements GeneratorTest.Drawer {

    private final BoxGenerator gen;

    private final ValueSpinner begXSpin;
    private final ValueSpinner endYSpin;
    private final ValueSpinner startAdjustSpin;
    private final ValueSpinner sizeXSpin;
    private final ValueSpinner sizeYSpin;
    private final ValueSource[] spinners;

    private final Side[] sides;

    private JComponent drawing;

    private static final String[] TYPE_NAMES = {"cubic", "quad", "straight"};
    private static final Class[] TYPES = {
        CubicGenerator.class, QuadGenerator.class, null
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
            final int space = SPACE;
            setBorder(new EmptyBorder(space, space, space, space));
        }

        protected void paintComponent(Graphics g1) {
            Graphics2D g = (Graphics2D) g1;
            double mid = getHeight() / 2 - sizeYSpin.get() / 2;
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
    };

    BoxTest(CubicGenerator cubic, QuadGenerator quad) {
        gen = new BoxGenerator(cubic, quad);

        begXSpin = new ValueSpinner("x", gen.getBegX(), -5, +5, 100);
        endYSpin = new ValueSpinner("y", gen.getEndY(), -5, +5, 100);
        startAdjustSpin =
                new ValueSpinner("adj", gen.getStartAdjust(), 0, 10, 100,
                        false);
        sizeXSpin =
                new ValueSpinner("width", gen.getSizeX(), 0, LENGTH, 100,
                        false);
        sizeYSpin =
                new ValueSpinner("height", gen.getSizeY(), 0, LENGTH, 100,
                        false);
        spinners = new ValueSource[]{begXSpin, endYSpin, widthSpin};

        sides = new Side[4];
        for (int i = 0; i < sides.length; i++) {
            sides[i] = new Side(BoxGenerator.SIDE_NAMES[i], i);
            gen.setGenerator(i, CubicGenerator.class);
        }

        rebuild();
    }

    public Shape generate(AffineTransform matrix) {
        return gen.generate(matrix);
    }

    public ShapeGenerator getGenerator() {
        return gen;
    }

    public void rebuild() {
        generate(null);
    }

    public ValueSource[] getSpinners() {
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
        for (int i = 0; i < sides.length; i++) {
            Side side = sides[i];
            typeControl.add(side.show);
            typeControl.add(side.selector);
        }
        SpringUtilities.makeCompactGrid(typeControl, 2, 4, 0, 0, 0, 0);
        controls.add(typeControl);

        controls.add(Box.createVerticalGlue());

        return controls;
    }
}

