
package napkin;

import java.awt.*;
import javax.swing.border.*;

public class NapkinBorderFactory {
    public static Border wrappedBorder(final Border formal) {
        return new NapkinBorder(formal) {
            protected Insets doGetBorderInsets(Component c) {
                return formal.getBorderInsets(c);
            }

            protected boolean doIsBorderOpaque() {
                return formal.isBorderOpaque();
            }

            protected void doPaintBorder(Component c, Graphics g, int x, int y,
                    int width, int height) {

                NapkinUtil.defaultGraphics(g, c);
                formal.paintBorder(c, g, x, y, width, height);
            }
        };
    }
}