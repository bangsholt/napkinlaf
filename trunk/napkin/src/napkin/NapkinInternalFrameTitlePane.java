// $Id$

package napkin;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.IconifyAction;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.TitlePaneLayout;

import napkin.shapes.DrawnCubicLineGenerator;
import napkin.shapes.DrawnLineHolder;
import napkin.util.NapkinConstants;
import napkin.util.NapkinIconFactory;
import napkin.util.NapkinPainter;
import napkin.util.NapkinUtil;

public class NapkinInternalFrameTitlePane extends BasicInternalFrameTitlePane
        implements NapkinConstants, NapkinPainter {

    private DrawnLineHolder line;
    private Rectangle bounds;

    private static final Dimension NO_SIZE = new Dimension(0, 0);

    public class NapkinTitlePaneLayout extends TitlePaneLayout {
        public Dimension preferredLayoutSize(Container c) {
            return calcSize(c, true, 15);
        }

        public Dimension minimumLayoutSize(Container c) {
            return calcSize(c, false, 3);
        }

        private Dimension calcSize(Container c, boolean pref, int min) {
            Dimension closeSize =
                    sizeFor(frame.isClosable(), pref, closeButton);
            Dimension maxSize =
                    sizeFor(frame.isMaximizable(), pref, maxButton);
            Dimension iconSize =
                    sizeFor(frame.isIconifiable(), pref, iconButton);

            // Calculate width.
            int width = 0;

            width += closeSize.width;
            width += maxSize.width;
            width += iconSize.width;

            FontMetrics fm = getFontMetrics(getFont());
            String frameTitle = frame.getTitle();
            if (frameTitle != null) {
                int titleW = fm.stringWidth(frameTitle);
                int titleLength = frameTitle.length();

                // Leave room for three characters in the title.
                if (titleLength <= min) {
                    width += titleW;
                } else {
                    String replStr = frameTitle.substring(0, min) + "...";
                    int subtitleW = fm.stringWidth(replStr);
                    width += (titleW < subtitleW) ? titleW : subtitleW;
                }
            }

            // Calculate height.
            Icon icon = frame.getFrameIcon();
            int fontHeight = fm.getHeight();

            int iconHeight = 0;
            if (icon != null) {
                // SystemMenuBar forces the icon to be 16x16 or less.
                iconHeight = Math.min(icon.getIconHeight(), 16);
            }

            int height = Math.max(fontHeight, iconHeight);
            height += 2;

            Dimension dim = new Dimension(width, height);

            // Take into account the border insets if any.
            if (getBorder() != null) {
                Insets insets = getBorder().getBorderInsets(c);
                dim.height += insets.top + insets.bottom;
                dim.width += insets.left + insets.right;
            }
            return dim;
        }
    }

    public NapkinInternalFrameTitlePane(JInternalFrame f) {
        super(f);
    }

    protected LayoutManager createLayout() {
        return new NapkinTitlePaneLayout();
    }

    private Dimension sizeFor(boolean is, boolean pref, JButton button) {
        if (!is)
            return NO_SIZE;
        return (pref ? button.getPreferredSize() : button.getMinimumSize());
    }

    protected void createButtons() {
        super.createButtons();
        setupButton(iconButton);
        setupButton(maxButton);
        setupButton(closeButton);
    }

    protected void setButtonIcons() {
        super.setButtonIcons();
        maxButton.setIcon(null);
    }

    private void setupButton(JButton button) {
        if (button != maxButton)
            button.setBorder(new EmptyBorder(1, 1, 1, 1));
        button.setOpaque(false);
    }

    public void paint(Graphics g) {
        NapkinUtil.update(g, this, this);
    }

    public void superPaint(Graphics g, JComponent c, NapkinTheme theme) {
        super.paint(g);
    }

    protected void paintTitleBackground(Graphics g) {
        if (line == null)
            line = new DrawnLineHolder(new DrawnCubicLineGenerator());
        Graphics2D ulG = NapkinUtil.copy(g);
        bounds = getBounds(bounds);
        bounds.x = bounds.y = 0;
        line.shapeUpToDate(bounds, null);
        ulG.translate(0, bounds.height - 2);
        line.draw(ulG);
    }
}
