// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class NapkinEditorPaneUI extends BasicEditorPaneUI {

    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, new NapkinEditorPaneUI());
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        NapkinUtil.installUI(c);
    }

    protected void paintSafely(Graphics g) {
        NapkinUtil.defaultGraphics(g);
        super.paintSafely(g);
    }
}

