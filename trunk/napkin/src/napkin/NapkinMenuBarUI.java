// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class NapkinMenuBarUI extends BasicMenuBarUI {

    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, new NapkinMenuBarUI());
    }

//    private static int num = 0;

    public void installUI(JComponent c) {
//        System.out.println(
//                num + ": " + System.identityHashCode(this) + ": before: " + c);
        super.installUI(c);
        NapkinUtil.installUI(c);
//        NapkinUtil.dumpTo("/tmp/c." + num, c);
//        System.out.println(num++ + ": " + System.identityHashCode(this) +
//                ": after:  " + c);
    }

    public void paint(Graphics g, JComponent c) {
        NapkinUtil.defaultGraphics(g);
        super.paint(g, c);
    }
}

