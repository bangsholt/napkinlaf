// $Id$

package napkin.dev;

import napkin.BoxGenerator;
import napkin.CheckGenerator;
import napkin.ShapeGenerator;
import napkin.ValueSource;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

class CheckBoxTest extends GeneratorTest
        implements GeneratorTest.Drawer {

    private CheckGenerator checkGen;
    private BoxGenerator boxGen;

    private ValueSpinner midXScaleSpin;
    private ValueSpinner midYScaleSpin;
    private ValueSpinner leftXScaleSpin;
    private ValueSpinner leftYScaleSpin;
    private ValueSpinner rightXScaleSpin;
    private ValueSpinner rightYScaleSpin;
    private JCheckBox isSelected;
    private JCheckBox useBox;
    private final ValueSource[] spinners;

    private JComponent drawing;

    private Shape box;
    private Shape check;

    private static final BoxGenerator DEFAULT_BOX = new BoxGenerator();

    static {
        DEFAULT_BOX.getSizeX().setMid(10);
        DEFAULT_BOX.getSizeY().setMid(10);
    }

    private class Drawing extends JLabel {
        Drawing() {
            final int space = SPACE;
            setBorder(new EmptyBorder(space, space, space, space));
        }

        protected void paintComponent(Graphics g1) {
            Graphics2D g = (Graphics2D) g1;
            double mid = getHeight() / 2 - getWidth() / 2;
            g.translate(SPACE, mid);

            Graphics2D boxG = lineGraphics(g, 1);
            if (useBox.isSelected())
                boxG.draw(box);
            else
                boxG.draw(boxGen.getSide(BoxGenerator.BOTTOM));

            if (isSelected.isSelected()) {
                Graphics2D checkG = lineGraphics(g, (float) widthSpin.get());
                checkG.setColor(Color.green.darker());
                checkG.draw(check);
            }

            Graphics2D markG = markGraphics(g);
            mark(markG, leftXScaleSpin, leftYScaleSpin, true);
            mark(markG, midXScaleSpin, midYScaleSpin, true);
            mark(markG, rightXScaleSpin, rightYScaleSpin, true);
        }

        public Dimension getPreferredSize() {
            return getMinimumSize();
        }

        public Dimension getMinimumSize() {
            return new Dimension(LENGTH + 2 * SPACE,
                    MIN_HEIGHT * 2 + 2 * SPACE);
        }
    };

    public CheckBoxTest() {
        this(DEFAULT_BOX);
    }

    public CheckBoxTest(BoxGenerator boxGen) {
        this.boxGen = boxGen;
        checkGen = new CheckGenerator();

        isSelected = new JCheckBox("Selected", true);
        isSelected.addChangeListener(REPAINT);
        useBox = new JCheckBox("Use Box", true);
        useBox.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {

            }
        });
        useBox.addChangeListener(REPAINT);

        midXScaleSpin =
                new ValueSpinner("mid x", checkGen.getMidXScale(), 0, 2, 100);
        midYScaleSpin =
                new ValueSpinner("mid y", checkGen.getMidYScale(), 0, 2, 100);
        leftXScaleSpin =
                new ValueSpinner("left x", checkGen.getLeftXScale(), 0, 2, 100);
        leftYScaleSpin =
                new ValueSpinner("left y", checkGen.getLeftYScale(), 0, 2, 100);
        rightXScaleSpin =
                new ValueSpinner("right x", checkGen.getRightXScale(), 0, 2,
                        100);
        rightYScaleSpin =
                new ValueSpinner("right y", checkGen.getRightYScale(), 0, 2,
                        100);
        spinners = new ValueSource[]{
            midXScaleSpin, midYScaleSpin,
            leftXScaleSpin, leftYScaleSpin,
            rightXScaleSpin, rightYScaleSpin,
            widthSpin
        };

        rebuild();
    }

    public ShapeGenerator getGenerator() {
        return null;
    }

    public Shape generate(AffineTransform matrix) {
        Shape box = boxGen.generate(matrix);
        if (!isSelected.isSelected())
            return box;

        GeneralPath path = new GeneralPath();
        path.append(box, false);
        path.append(checkGen.generate(matrix), false);
        return path;
    }

    public ValueSource[] getSpinners() {
        return spinners;
    }

    public String getName() {
        return "CheckBox";
    }

    public JComponent getDrawing() {
        if (drawing == null)
            drawing = new Drawing();
        return drawing;
    }

    public void rebuild() {
        box = boxGen.generate(null);
        check = checkGen.generate(null);
    }

    public JComponent getControls() {
        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
        controls.setAlignmentY(1.0f);

        controls.add(isSelected);
        controls.add(useBox);
        controls.add(controlSet("left", leftXScaleSpin, leftYScaleSpin));
        controls.add(controlSet("mid", midXScaleSpin, midYScaleSpin));
        controls.add(controlSet("right", rightXScaleSpin, rightYScaleSpin));
        controls.add(controlSet("Line", widthSpin));

        JPanel lineControl = new JPanel();
        lineControl.setBorder(new TitledBorder("Line"));
        lineControl.setLayout(new BoxLayout(lineControl, BoxLayout.X_AXIS));

        controls.add(Box.createVerticalGlue());

        return controls;
    }
}
