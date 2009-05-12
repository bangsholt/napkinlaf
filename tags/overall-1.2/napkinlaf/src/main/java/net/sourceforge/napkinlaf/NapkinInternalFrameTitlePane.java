package net.sourceforge.napkinlaf;

import net.sourceforge.napkinlaf.shapes.DrawnCubicLineGenerator;
import net.sourceforge.napkinlaf.shapes.DrawnLineHolder;
import net.sourceforge.napkinlaf.util.NapkinPainter;
import net.sourceforge.napkinlaf.util.NapkinUtil;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;
import java.awt.*;

public class NapkinInternalFrameTitlePane extends BasicInternalFrameTitlePane
        implements NapkinPainter {

    private transient DrawnLineHolder line;
    private Rectangle bounds;

    private static final Dimension NO_SIZE = new Dimension(0, 0);

    public class NapkinTitlePaneLayout extends TitlePaneLayout {
        @Override
        public Dimension preferredLayoutSize(Container c) {
            return calcSize(c, true, 15);
        }

        @Override
        public Dimension minimumLayoutSize(Container c) {
            return calcSize(c, false, 3);
        }

        private Dimension calcSize(Component c, boolean pref, int min) {
            Dimension closeSize = sizeFor(frame.isClosable(), pref, closeButton)
                    ;
            Dimension maxSize = sizeFor(frame.isMaximizable(), pref, maxButton);
            Dimension iconSize = sizeFor(frame.isIconifiable(), pref,
                    iconButton);

            // Calculate width.

            int width = closeSize.width;
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

    @Override
    protected LayoutManager createLayout() {
        return new NapkinTitlePaneLayout();
    }

    private static Dimension sizeFor(boolean is, boolean pref,
            Component button) {
        return is ?
                (pref ? button.getPreferredSize() : button.getMinimumSize()) :
                NO_SIZE;
    }

    @Override
    protected void createButtons() {
        super.createButtons();
        setupButton(iconButton);
        setupButton(maxButton);
        setupButton(closeButton);
    }

    @Override
    protected void setButtonIcons() {
        super.setButtonIcons();
        maxButton.setIcon(null);
    }

    private void setupButton(JComponent button) {
        if (!button.equals(maxButton)) {
            button.setBorder(new EmptyBorder(1, 1, 1, 1));
        }
        /**!!
         * this is to fix the issue with invisible Close button
         * Steps to reproduce:
         * 1) goto swingset2 internal frame demo
         * 2) click on any button to create a new internal frame
         */
        button.setOpaque(false);
    }

    @Override
    public void paint(Graphics g) {
        NapkinUtil.update(g, this, this);
    }

    public void superPaint(Graphics g, JComponent c) {
        super.paint(g);
    }

    @Override
    protected void paintTitleBackground(Graphics g) {
        if (line == null) {
            line = new DrawnLineHolder(new DrawnCubicLineGenerator());
        }
        Graphics2D ulG = NapkinUtil.copy(g);
        bounds = getBounds(bounds);
        bounds.x = bounds.y = 0;
        line.shapeUpToDate(bounds, null);
        ulG.translate(0, bounds.height - 2);
        line.draw(ulG);
    }
}
