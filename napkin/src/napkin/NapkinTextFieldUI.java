// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class NapkinTextFieldUI extends BasicTextFieldUI {

    private static final Rectangle EMPTY = new Rectangle(1, 1);

    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, new NapkinTextFieldUI());
    }

    public void installUI(JComponent c) {
        NapkinUtil.installUI(c);
        super.installUI(c);
        c.setMaximumSize(new Dimension(1, 1));
    }

    //!! Fix this
    /**
     * This works around a problem: Without this, I get a NullPointerException
     * When it tries to paint a zero-dimension (either width or height) text
     * field in SwingSet2.  I don't know why it tries to do this:  It doesn't
     * seem to happen for any other L&F.  I can be sure because the BasicTextUI
     * object has a bug in this method for zero-dimensions fields by returning
     * null, which is then used blindly in the paint method.  This is a
     * workaround, but I really ought to find out why it's hapenning at all.
     */
    protected Rectangle getVisibleEditorRect() {
        Rectangle ret = super.getVisibleEditorRect();
        if (ret == null)
            ret = EMPTY;
        return ret;
    }

    protected void paintSafely(Graphics g) {
        NapkinUtil.defaultGraphics(g);
        super.paintSafely(g);
    }

    public void update(Graphics g, JComponent c) {
        NapkinUtil.background(g, c);
        super.update(g, c);
    }
}

