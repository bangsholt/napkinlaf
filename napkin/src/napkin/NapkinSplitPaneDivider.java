// $Id$

package napkin;

import napkin.util.NapkinConstants;
import napkin.util.NapkinIconFactory;
import napkin.util.NapkinPainter;
import napkin.util.NapkinUtil;

import javax.swing.*;
import javax.swing.plaf.basic.*;
import java.awt.*;

public class NapkinSplitPaneDivider extends BasicSplitPaneDivider
        implements NapkinConstants, NapkinPainter {

    private JButton left;
    private JButton right;

    public static final int ARROW_SIZE = 5;
    public static final int SIZE = ARROW_SIZE + 4;

    private static final Cursor DEFAULT_CURSOR =
            Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

    public NapkinSplitPaneDivider(NapkinSplitPaneUI ui) {
        super(ui);
    }

    public void update(Graphics g) {
        NapkinUtil.update(g, splitPane, this);
    }

    public void superPaint(Graphics g, JComponent c, NapkinTheme theme) {
        super.update(g);
    }

    // largely copied from superclass
    protected JButton createLeftOneTouchButton() {
        int dir = (orientation == JSplitPane.HORIZONTAL_SPLIT ? WEST : NORTH);
        return (left = createOneTouchButton(dir));
    }

    protected JButton createRightOneTouchButton() {
        int dir = (orientation == JSplitPane.HORIZONTAL_SPLIT ? EAST : SOUTH);
        return (right = createOneTouchButton(dir));
    }

    protected JButton createOneTouchButton(int dir) {
        JButton b = NapkinUtil.createArrowButton(dir, ARROW_SIZE);
        b.setFocusable(false);
        b.setMinimumSize(new Dimension(SIZE, SIZE));
        b.setCursor(DEFAULT_CURSOR);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setRequestFocusEnabled(false);
        return b;
    }

    protected void setOrientation(int orientation) {
        if (left == null)   // haven't yet created the buttons
            return;

        if (orientation == JSplitPane.HORIZONTAL_SPLIT) {
            left.setIcon(NapkinIconFactory.createArrowIcon(WEST, ARROW_SIZE));
            right.setIcon(NapkinIconFactory.createArrowIcon(EAST, ARROW_SIZE));
        } else {
            left.setIcon(NapkinIconFactory.createArrowIcon(NORTH, ARROW_SIZE));
            right.setIcon(NapkinIconFactory.createArrowIcon(SOUTH, ARROW_SIZE));
        }
    }
}
