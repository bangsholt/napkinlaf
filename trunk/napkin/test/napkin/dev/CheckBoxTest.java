// $Id$

package napkin.dev;

import napkin.shapes.DrawnBoxGenerator;
import napkin.shapes.DrawnCheckGenerator;
import static napkin.util.NapkinConstants.LENGTH;
import napkin.util.RandomValueSource;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;

class CheckBoxTest extends GeneratorTest
        implements GeneratorTest.Drawer {

    private final DrawnCheckGenerator checkGen;
    private final DrawnBoxGenerator boxGen;

    private final RandomValueSpinner midXScaleSpin;
    private final RandomValueSpinner midYScaleSpin;
    private final RandomValueSpinner leftXScaleSpin;
    private final RandomValueSpinner leftYScaleSpin;
    private final RandomValueSpinner rightXScaleSpin;
    private final RandomValueSpinner rightYScaleSpin;
    private final JCheckBox isSelected;
    private final JCheckBox useBox;
    private final RandomValueSource[] spinners;

    private JComponent drawing;

    private Shape box;
    private Shape check;

    private static final DrawnBoxGenerator DEFAULT_BOX =
            new DrawnBoxGenerator();

    static {
        DEFAULT_BOX.getSize().setMid(10, 10);
    }

    private class Drawing extends JLabel {
        Drawing() {
            int space = SPACE;
            setBorder(new EmptyBorder(space, space, space, space));
        }

        protected void paintComponent(Graphics g1) {
            Graphics2D g = (Graphics2D) g1;
            double mid = getHeight() / 2.0 - getWidth() / 2.0;
            g.translate(SPACE, mid);

            Graphics2D boxG = lineGraphics(g, 1);
            if (useBox.isSelected())
                boxG.draw(box);
            else
                boxG.draw(boxGen.getSide(BOTTOM));

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
    }

    CheckBoxTest() {
        this(DEFAULT_BOX);
    }

    CheckBoxTest(DrawnBoxGenerator boxGen) {
        this.boxGen = boxGen;
        checkGen = new DrawnCheckGenerator();

        isSelected = new JCheckBox("Selected", true);
        isSelected.addChangeListener(REPAINT);
        useBox = new JCheckBox("Use Box", true);
        useBox.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {

            }
        });
        useBox.addChangeListener(REPAINT);

        midXScaleSpin = new RandomValueSpinner("mid x",
                checkGen.getMidScale().getX(), 0, 2, 100);
        midYScaleSpin = new RandomValueSpinner("mid y",
                checkGen.getMidScale().getY(), 0, 2, 100);
        leftXScaleSpin = new RandomValueSpinner("left x",
                checkGen.getLeftScale().getX(), 0, 2, 100);
        leftYScaleSpin = new RandomValueSpinner("left y",
                checkGen.getLeftScale().getY(), 0, 2, 100);
        rightXScaleSpin = new RandomValueSpinner("right x",
                checkGen.getRightScale().getX(), 0, 2, 100);
        rightYScaleSpin = new RandomValueSpinner("right y",
                checkGen.getRightScale().getY(), 0, 2, 100);
        spinners = new RandomValueSource[]{
                midXScaleSpin, midYScaleSpin,
                leftXScaleSpin, leftYScaleSpin,
                rightXScaleSpin, rightYScaleSpin,
                widthSpin
        };

        rebuild();
    }

    public RandomValueSource[] getSpinners() {
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
