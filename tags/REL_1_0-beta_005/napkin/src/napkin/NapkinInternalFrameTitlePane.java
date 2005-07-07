// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;

public class NapkinInternalFrameTitlePane extends BasicInternalFrameTitlePane
        implements NapkinConstants {

    private DrawnLineHolder line;
    private Rectangle bounds;

    private static Dimension NO_SIZE = new Dimension(0, 0);

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
                int title_w = fm.stringWidth(frameTitle);
                int title_length = frameTitle.length();

                // Leave room for three characters in the title.
                if (title_length <= min) {
                    width += title_w;
                } else {
                    String replStr = frameTitle.substring(0, min) + "...";
                    int subtitle_w = fm.stringWidth(replStr);
                    width += (title_w < subtitle_w) ? title_w : subtitle_w;
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

    private void setupButton(JButton button) {
        if (button != maxButton)
            button.setBorder(new EmptyBorder(1, 1, 1, 1));
        button.setOpaque(false);
    }

    public void paint(Graphics g) {
        g = NapkinUtil.defaultGraphics(g, this);
        selectedTextColor = notSelectedTextColor = g.getColor();
        NapkinUtil.background(g, this);
        super.paint(g);
        NapkinUtil.finishGraphics(g, this);
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