// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;

// It seems as if the BasicFileChooserUI is not yet well formed, so we're just
// borrowing the metal chooser for now.

public class NapkinFileChooserUI extends MetalFileChooserUI
        implements NapkinPainter {
    private static final ComponentWalker.Visitor NO_BORDER_VISITOR =
            new ComponentWalker.Visitor() {
                public boolean visit(Component c, int depth) {
                    if (c instanceof JButton) {
                        JButton button = (JButton) c;
                        button.setBorderPainted(false);
                    }
                    return true;
                }
            };

    /** @noinspection MethodOverridesStaticMethodOfSuperclass */
    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, new NapkinFileChooserUI((JFileChooser) c));
    }

    private NapkinFileChooserUI(JFileChooser c) {
        super(c);
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        NapkinUtil.installUI(c);
    }

    public void uninstallUI(JComponent c) {
        NapkinUtil.uninstallUI(c);
        super.uninstallUI(c);
    }

    public void installComponents(JFileChooser fc) {
        super.installComponents(fc);
        Component[] comps = fc.getComponents();
        JPanel topPanel = (JPanel) comps[0];
        new ComponentWalker(NO_BORDER_VISITOR).walk(topPanel);
    }

    public void update(Graphics g, JComponent c) {
        NapkinUtil.update(g, c, this);
    }

    public void superPaint(Graphics g, JComponent c, NapkinTheme theme) {
        super.update(g, c);
    }
}

