// $Id$

package napkin;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;

public class NapkinUtil implements NapkinConstants {
    private static final Set printed = new HashSet();

    private static final Integer BOTTOM_LAYER = new Integer(Integer.MIN_VALUE);

    private static final Logger logger = Logger.getLogger("NapkinUtil");
    public static final Random random = new Random();
    public static final Icon EMPTY_ICON = new Icon() {
        public void paintIcon(Component c, Graphics g, int x, int y) {
        }

        public int getIconWidth() {
            return 0;
        }

        public int getIconHeight() {
            return 0;
        }
    };

    private static Map strokes = new WeakHashMap();

    public static class DumpListener implements FocusListener {
        private Timer timer;

        public void focusGained(final FocusEvent ev) {
            if (timer != null)
                timer.stop();
            int delay = 1000; //milliseconds
            ActionListener taskPerformer = new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    NapkinLookAndFeel laf = (NapkinLookAndFeel) UIManager.getLookAndFeel();
                    laf.dumpFormality(
                            ((JComponent) ev.getSource()).getTopLevelAncestor(),
                            System.out);
                }
            };
            timer = new Timer(delay, taskPerformer);
            timer.start();
        }

        public void focusLost(FocusEvent e) {
            if (timer != null)
                timer.stop();
        }
    }

    static boolean isFormal(JComponent l) {
        NapkinLookAndFeel nlaf = (NapkinLookAndFeel) UIManager.getLookAndFeel();
        return nlaf.isFormal(l);
    }

    static ComponentUI uiFor(JComponent c, ComponentUI napkinUI) {
        NapkinLookAndFeel nlaf = (NapkinLookAndFeel) UIManager.getLookAndFeel();
        ComponentUI ui;
        if (nlaf.isFormal(c))
            ui = nlaf.getFormal().getDefaults().getUI(c);
        else
            ui = napkinUI;
        if (logger.isLoggable(Level.FINER) && !printed.contains(c.getClass())) {
            logger.finer(c.getUIClassID() + "\n  " + napkinUI.getClass() +
                    "\n  " + c.getClass());
            printed.add(c.getClass());
        }
        return ui;
    }

    static String prBool(boolean bool, String name) {
        if (bool)
            return name;
        else
            return '!' + name;
    }

    static String descFor(Component c) {
        if (c == null)
            return "null";
        String desc;
        if ((desc = c.getName()) != null)
            return desc;
        desc = c.getClass().getName();
        int dot = desc.lastIndexOf('.');
        if (dot > 0)
            desc = desc.substring(dot + 1);

        if (c instanceof JLabel)
            desc += ": " + ((JLabel) c).getText();
        if (c instanceof AbstractButton)
            return desc + ": " + ((AbstractButton) c).getText();
        if (c instanceof JTextField)
            return desc + ": " + ((JTextField) c).getText();
        if (c instanceof JPopupMenu)
            return desc + ": " + ((JPopupMenu) c).getLabel();
        if (c instanceof Label)
            return desc + ": " + ((Label) c).getText();
        if (c instanceof TextField)
            return desc + ": " + ((TextField) c).getText();
        if (c instanceof Button)
            return desc + ": " + ((Button) c).getLabel();
        if (c instanceof Checkbox)
            return desc + ": " + ((Checkbox) c).getLabel();
        if (c instanceof Dialog)
            return desc + ": " + ((Dialog) c).getTitle();
        if (c instanceof Frame)
            return desc + ": " + ((Frame) c).getTitle();
        if (c instanceof JInternalFrame)
            return desc + ": " + ((JInternalFrame) c).getTitle();

        if (c instanceof JComponent) {
            JComponent jc = (JComponent) c;
            Border border = jc.getBorder();
            if (border instanceof TitledBorder)
                return desc + ": " + ((TitledBorder) border).getTitle();
        }

        return desc;
    }

    public static void installUI(JComponent c) {
        c.setOpaque(false);
        c.setBackground(CLEAR);
    }

    static void setBackground(Component child, NapkinBackground bg) {
        for (Component c = child; c != null; c = c.getParent()) {
            if (c instanceof JLayeredPane) {
                JLayeredPane lp = (JLayeredPane) c;
                setBackground(lp, bg);
                return;
            }
        }
        throw new IllegalArgumentException(
                "not in JLayeredPane: " + descFor(child));
    }

    static void setBackground(JLayeredPane lp, NapkinBackground bg) {
        removeBackground(lp);
        Component cur = new NapkinBackgroundLabel(bg);
        lp.add(cur, BOTTOM_LAYER);
        lp.putClientProperty(BG_COMPONENT, cur);
    }

    static void removeBackground(Component child) {
        for (Component c = child; c != null; c = c.getParent()) {
            if (c instanceof JLayeredPane) {
                JLayeredPane lp = (JLayeredPane) c;
                removeBackground(lp);
                return;
            }
        }
        // removing is OK even if nothing is there
    }

    static void removeBackground(JLayeredPane lp) {
        Component cur = (Component) lp.getClientProperty(BG_COMPONENT);
        if (cur != null)
            lp.remove(cur);
    }

    static void uninstallLayeredPane(JLayeredPane lp) {
        removeBackground(lp);
    }

    public static double leftRight(double x, boolean left) {
        return (left ? x : ShapeGenerator.LENGTH - x);
    }

    public static AffineTransform copy(AffineTransform matrix) {
        if (matrix == null)
            return new AffineTransform();
        else
            return (AffineTransform) matrix.clone();
    }

    public static Graphics2D copy(Graphics g) {
        return (Graphics2D) g.create();
    }

    public static Graphics2D lineGraphics(Graphics orig, float w) {
        return lineGraphics((Graphics2D) orig, w);
    }

    public static Graphics2D lineGraphics(Graphics2D orig, float w) {
        Graphics2D lineG = copy(orig);

        Float key = new Float(w);
        Stroke stroke = (Stroke) strokes.get(key);
        if (stroke == null) {
            stroke = new BasicStroke(w, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND);
            strokes.put(key, stroke);
        }
        lineG.setStroke(stroke);
        lineG.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        return lineG;
    }

    public static Graphics2D defaultGraphics(Graphics g1) {
        Graphics2D g = (Graphics2D) g1;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        return g;
    }

    boolean isPressed(Component c) {
        if (c instanceof AbstractButton) {
            AbstractButton b = (AbstractButton) c;
            ButtonModel model = b.getModel();
            return model.isPressed() && model.isArmed();
        }
        return false;
    }

    boolean isDefault(Component c) {
        if (c instanceof JButton)
            return ((JButton) c).isDefaultButton();
        return false;
    }

    static AffineTransform scaleMat(double scale) {
        AffineTransform mat = new AffineTransform();
        mat.scale(scale, scale);
        return mat;
    }

    static void transform(AffineTransform matrix, double[] points) {
        if (matrix != null)
            matrix.transform(points, 0, points, 0, points.length / 2);
    }

    static JButton createArrowButton(int pointTowards) {
        JButton button = new JButton(
                NapkinIconFactory.createArrowIcon(pointTowards));
        button.setBorderPainted(false);
        return button;
    }

    static void applyPendingBackground(JComponent c) {
        Object pending = c.getClientProperty(PENDING_BG_COMPONENT);
        if (pending != null) {
            c.putClientProperty(PENDING_BG_COMPONENT, null);
            setBackground(c, (NapkinBackground) pending);
        }
    }

    static boolean isLeftToRight(Component c) {
        return c.getComponentOrientation().isLeftToRight();
    }

    public static LineHolder paintLine(Graphics g, boolean vertical,
            LineHolder holder, Rectangle bounds) {
        if (holder == null)
            holder = new LineHolder(CubicGenerator.INSTANCE, vertical);
        holder.shapeUpToDate(bounds, null);
        Graphics2D lineG = NapkinUtil.copy(g);
        lineG.setColor(Color.black);
        if (vertical)
            lineG.translate(bounds.x + bounds.width / 2, 0);
        else
            lineG.translate(0, bounds.y + bounds.height / 2);
        holder.draw(lineG);
        return holder;
    }

    static void dumpTo(String file, JComponent c) {
        try {
            PrintWriter out = new PrintWriter(
                    new BufferedWriter(new FileWriter(file)));
            Set dumped = new HashSet();
            dumpTo(out, c, c.getClass(), 0, dumped);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    private static void dumpTo(PrintWriter out, Object obj, Class cl, int level,
            Set dumped)
            throws IllegalAccessException {
        if (cl == null)
            return;
        dumpTo(out, obj, cl.getSuperclass(), level, dumped);
        Field[] fields = cl.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            Object val = field.get(obj);
            for (int l = 0; l < level; l++)
                out.print("    ");
            out.println(field.getName() + ": " + val);
            if (val != null && !dumped.contains(obj) &&
                    !field.getType().isPrimitive()) {
                dumpTo(out, val, val.getClass(), level + 1, dumped);
                dumped.add(obj);
            }
        }
    }

    public static Object[] reallocate(Object[] orig, int count) {
        if (count == orig.length)
            return orig;
        Class componentType = orig.getClass().getComponentType();
        Object[] next = (Object[]) Array.newInstance(componentType, count);
        System.arraycopy(orig, 0, next, 0, Math.min(orig.length, next.length));
        return next;
    }
}

