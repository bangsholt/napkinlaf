
package napkin;

import java.awt.*;
import javax.swing.*;

public interface NapkinPainter {
    void superPaint(Graphics g, JComponent c, NapkinTheme theme);
}