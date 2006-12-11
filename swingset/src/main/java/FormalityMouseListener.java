import javax.swing.*;
import javax.swing.plaf.metal.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * When we're in "set formality" mode, this is the listener that figures out
 * which components to toggle.
 */
public class FormalityMouseListener extends MouseAdapter {
    private final JRootPane root;
    private final Container top;
    private final Map<Container, ChangeDesc> changes =
            new WeakHashMap<Container, ChangeDesc>();
    private int nextGen = 0;

    private static MetalLookAndFeel metalLAF = new MetalLookAndFeel();

    private static class ChangeDesc {
        int gen;
        boolean formal;

        ChangeDesc(boolean formal, int gen) {
            this.formal = formal;
            this.gen = gen;
        }

        @Override
        public String toString() {
            return formal + " (" + gen + ")";
        }
    }

    public FormalityMouseListener(JComponent component) {
        root = component.getRootPane();
        top = root.getContentPane();
    }

    @Override
    public void mouseClicked(MouseEvent ev) {
        MouseEvent cvt = SwingUtilities.convertMouseEvent(
                root.getGlassPane(), ev, top);
        Component c = SwingUtilities.getDeepestComponentAt(
                top, cvt.getX(), cvt.getY());

        if (c == null)
            return;

        JComponent over;
        while (!(c instanceof JComponent)) {
            c = c.getParent();
            if (c == null)
                return;
        }
        over = (JComponent) c;
        boolean formal = isFormal(over);
        if (formal) {
            SwingUtilities.updateComponentTreeUI(over);
        } else {
            try {
                LookAndFeel laf = UIManager.getLookAndFeel();
                UIManager.setLookAndFeel(metalLAF);
                SwingUtilities.updateComponentTreeUI(over);
                UIManager.setLookAndFeel(laf);
            } catch (UnsupportedLookAndFeelException e) {
                throw new IllegalStateException("no metal?", e);
            }
        }
        changes.put(over, new ChangeDesc(!formal, nextGen++));
        ev.consume();
    }

    private boolean isFormal(JComponent over) {
        ChangeDesc ownerChange = null;
        Container owner = null;
        for (Container c = over; c != null; c = c.getParent()) {
            ChangeDesc change = changes.get(c);
            if (change != null) {
                if (ownerChange == null) {
                    owner = c;
                    ownerChange = change;
                } else {
                    if (ownerChange.gen < change.gen) {
                        changes.remove(owner);
                        owner = c;
                        ownerChange = change;
                    }
                }
            }
        }
        if (ownerChange == null)
            return false;
        else
            return ownerChange.formal;
    }
}