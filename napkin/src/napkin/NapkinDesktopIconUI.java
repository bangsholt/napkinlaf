// $Id$

package napkin;

import java.awt.BorderLayout;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicDesktopIconUI;

import napkin.util.NapkinPainter;
import napkin.util.NapkinUtil;

public class NapkinDesktopIconUI extends BasicDesktopIconUI
        implements NapkinPainter {

    /** @noinspection MethodOverridesStaticMethodOfSuperclass */
    public static ComponentUI createUI(JComponent c) {
        return new NapkinDesktopIconUI();
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        NapkinUtil.installUI(c);
        NapkinUtil.setupPaper(c, NapkinTheme.POPUP_THEME);
    }

    protected void installComponents() {                            // PASTED
        iconPane = new NapkinInternalFrameTitlePane(frame);         // MODIFIED
        desktopIcon.setLayout(new BorderLayout());                  // PASTED
        desktopIcon.add(iconPane, BorderLayout.CENTER);             // PASTED
    }

    public void uninstallUI(JComponent c) {
        NapkinUtil.uninstallUI(c);
        super.uninstallUI(c);
    }

    public void update(Graphics g, JComponent c) {
        NapkinUtil.update(g, c, this);
    }

    public void superPaint(Graphics g, JComponent c, NapkinTheme theme) {
        super.update(g, c);
    }

}