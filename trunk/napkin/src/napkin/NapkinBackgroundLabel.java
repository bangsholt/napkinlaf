
package napkin;

import java.awt.*;
import javax.swing.*;

public class NapkinBackgroundLabel extends JLabel {
    private final NapkinBackground bg;

    public NapkinBackgroundLabel(NapkinBackground bg) {
        this.bg = bg;
        setBounds(0, 0, 50000, 50000);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        bg.paint(this, g, getParent().getWidth(), getParent().getHeight());
    }

    NapkinBackground getNapkinBackground() {
        return bg;
    }

    public Dimension getPreferredSize() {
        return new Dimension(super.getSize());
    }

    public Dimension getMinimumSize() {
        return getPreferredSize();
    }
}