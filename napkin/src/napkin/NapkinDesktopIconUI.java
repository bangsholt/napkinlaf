// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class NapkinDesktopIconUI extends BasicDesktopIconUI {
    // I cannot override the desktop icon, which is package
    // protected.  This means that I cannot change the BasicDesktopIconUI to
    // use a NapkinInternalFrameTitlePane, which is how I handle this stuff in
    // NapkinInternalFrameUI.  I have filed a bug, but I have to work around
    // it.  Which means that much of the code here is pasted in.  Yuck!
    //!! Periodically check to see if this has been fixed.

    protected JComponent iconPane;                                  // PASTED

    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, new NapkinDesktopIconUI());
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        NapkinUtil.installUI(c);
    }

    protected void installComponents() {                            // PASTED
        super.installComponents();  // must do this to set iconPane in parent
        desktopIcon.removeAll();    // now get it out of the way
        iconPane = new BasicInternalFrameTitlePane(frame);          // PASTED
        desktopIcon.setLayout(new BorderLayout());                  // PASTED
        desktopIcon.add(iconPane, BorderLayout.CENTER);             // PASTED
    }

    public void uninstallUI(JComponent c) {
        desktopIcon.remove(iconPane);                               // PASTED
        NapkinUtil.uninstallUI(c);
        super.uninstallUI(c);
    }

    public void update(Graphics g, JComponent c) {
        g = NapkinUtil.defaultGraphics(g, c);
        NapkinUtil.background(g, c);
        super.update(g, c);
        NapkinUtil.finishGraphics(g, c);
    }

    public Dimension getMinimumSize(JComponent c) {                 // PASTED
        Dimension dim = new Dimension(iconPane.getMinimumSize());   // PASTED
        Border border = frame.getBorder();                          // PASTED
        // PASTED
        if (border != null) {                                       // PASTED
            dim.height += border.getBorderInsets(frame).bottom + // PASTED
                    border.getBorderInsets(frame).top;              // PASTED
        }                                                           // PASTED
        return dim;                                                 // PASTED
    }                                                               // PASTED

    public Dimension getMaximumSize(JComponent c) {                 // PASTED
        return iconPane.getMaximumSize();                           // PASTED
    }                                                               // PASTED
}

