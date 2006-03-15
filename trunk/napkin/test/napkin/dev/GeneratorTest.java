// $Id$

package napkin.dev;

import napkin.shapes.DrawnCubicLineGenerator;
import napkin.shapes.DrawnQuadLineGenerator;
import static napkin.util.NapkinConstants.LENGTH;
import napkin.util.NapkinUtil;
import napkin.util.RandomValue;
import napkin.util.RandomValueSource;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.text.DecimalFormat;

public class GeneratorTest extends NapkinUtil {
    private static final double MARK_SIZE = 3;

    // Subclass of this that implement Generator will have a symbol conflict
    // if we just call this "length"
    private static final int STD_LENGTH = LENGTH;
    static final int SPACE = STD_LENGTH / 2;
    static final int MIN_HEIGHT = STD_LENGTH * 2;

    private static final Rectangle2D mark = new Rectangle2D.Double();

    static final DecimalFormat DECIMAL = new DecimalFormat("#0.09");

    private static Drawer currentDrawer;
    private static JCheckBox showControlPoints;

    private static final Drawer[] drawers;

    static final RandomValueSource ZERO = new RandomValue(0);

    static final ChangeListener REPAINT = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
            if (currentDrawer != null) {
                currentDrawer.getDrawing().repaint();
            }
        }
    };
    static final ChangeListener NEWPOINTS = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
            if (currentDrawer != null) {
                currentDrawer.rebuild();
                REPAINT.stateChanged(null);
            }
        }
    };
    static final ActionListener REPAINT_ACTION = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            REPAINT.stateChanged(null);
        }
    };
    protected final RandomValue width;
    protected final RandomValueSpinner widthSpin;

    static {
        CubicTest cubic = new CubicTest();
        QuadTest quad = new QuadTest();
        BoxTest box = new BoxTest(
                (DrawnCubicLineGenerator) cubic.getGenerator(),
                (DrawnQuadLineGenerator) quad.getGenerator());
        CheckBoxTest checkBox = new CheckBoxTest();
        drawers = new Drawer[]{cubic, quad, box, checkBox};
    }

    interface Drawer {
        JComponent getDrawing();

        JComponent getControls();

        RandomValueSource[] getSpinners();

        String getName();

        void rebuild();
    }

    public GeneratorTest() {
        width = new RandomValue(1);
        widthSpin = new RandomValueSpinner("w", width, 0, 3, 20);
    }

    /**
     * Run this class as a program
     *
     * @param args The command line arguments.
     *
     * @throws Exception Exception we don't recover from.
     */
    public static void main(String[] args) throws Exception {
        final JTabbedPane tabs = new JTabbedPane();
        for (Drawer drawer : drawers) {
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            panel.add(drawer.getControls(), BorderLayout.CENTER);
            panel.add(drawer.getDrawing(), BorderLayout.WEST);
            tabs.addTab(drawer.getName(), panel);
        }
        currentDrawer = drawers[0];

        tabs.addChangeListener(new ChangeListener() {
            /** @noinspection AssignmentToStaticFieldFromInstanceMethod */
            public void stateChanged(ChangeEvent e) {
                currentDrawer = drawers[tabs.getSelectedIndex()];
            }
        });
        tabs.setSelectedIndex(0);

        JFrame top = new JFrame("Drawing Test");
        top.getContentPane().add(tabs, BorderLayout.CENTER);
        top.getContentPane().add(displayControls(), BorderLayout.SOUTH);

        top.pack();
        top.setVisible(true);
    }

    private static JPanel displayControls() {
        JButton randomize;
        showControlPoints = new JCheckBox("Show control points", false);
        randomize = new JButton("Randomize");
        randomize.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (currentDrawer != null) {
                    RandomValueSource[] sources = currentDrawer.getSpinners();
                    for (RandomValueSource source : sources) {
                        source.randomize();
                    }
                    currentDrawer.rebuild();
                }
            }
        });
        randomize.addChangeListener(REPAINT);

        return controlSet("Display", showControlPoints, randomize);
    }

    static JPanel controlSet(String label, JComponent v1) {
        return controlSet(label, new JComponent[]{v1});
    }

    static JPanel controlSet(String label, JComponent v1, JComponent v2) {
        return controlSet(label, new JComponent[]{v1, v2});
    }

    static JPanel controlSet(String label, JComponent v1, JComponent v2,
            JComponent v3) {
        return controlSet(label, new JComponent[]{v1, v2, v3});
    }

    private static JPanel controlSet(String label, JComponent[] cs) {
        JPanel controls = new JPanel();
        controls.setBorder(new TitledBorder(label));
        controls.setLayout(new BoxLayout(controls, BoxLayout.X_AXIS));
        for (JComponent c : cs) {
            if (c == null)
                continue;
            controls.add(c);
            if (c instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) c;
                button.addChangeListener(REPAINT);
            }
        }
        return controls;
    }

    /** @noinspection MethodOverridesStaticMethodOfSuperclass */
    public static Graphics2D lineGraphics(Graphics2D orig, float w) {
        return NapkinUtil.lineGraphics(orig, w);
    }

    static Graphics2D markGraphics(Graphics2D orig) {
        if (!showControlPoints.isSelected())
            return null;
        Graphics2D markG = (Graphics2D) orig.create();
        markG.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        markG.setColor(Color.blue);
        markG.setStroke(new BasicStroke(0.25f));
        return markG;
    }

    static void
            mark(Graphics2D g, RandomValueSource vx, RandomValueSource vy,
            boolean left) {

        if (g == null)
            return;

        double d = MARK_SIZE / 2;
        double x = NapkinUtil.leftRight(vx.get(), left);
        double y = vy.get();
        mark.setRect(x - d, y - d, MARK_SIZE, MARK_SIZE);
        g.setColor(Color.blue);
        g.fill(mark);

        double xMid = NapkinUtil.leftRight(vx.getMid(), left);
        double xRange = vx.getRange();
        double yMid = vy.getMid();
        double yRange = vy.getRange();
        mark.setRect(xMid - xRange, yMid - yRange, xRange * 2, yRange * 2);
        g.setColor(Color.red);
        g.draw(mark);
    }

    static void mark(Graphics2D g, RandomValueSource vx, RandomValueSource vy,
            RandomValueSource vw, RandomValueSource vh) {
        if (g == null)
            return;

        g.draw(new Rectangle2D.Double(vx.getMid() - vx.getRange(),
                vy.getMid() - vy.getRange(), vw.getMid() + vw.getRange(),
                vh.getMid() + vh.getRange()));
        g.draw(new Rectangle2D.Double(vx.getMid() + vx.getRange(),
                vy.getMid() + vy.getRange(), vw.getMid() - vw.getRange(),
                vh.getMid() - vh.getRange()));
    }
}

