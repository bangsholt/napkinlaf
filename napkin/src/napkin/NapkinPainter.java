package napkin;

import java.awt.*;
import javax.swing.*;

public interface NapkinPainter {
    void superPaintText(Graphics g, JComponent c, Rectangle textRect, String text);
}