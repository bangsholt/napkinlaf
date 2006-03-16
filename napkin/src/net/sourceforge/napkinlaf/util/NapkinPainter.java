// $Id$

package napkin.util;

import napkin.NapkinTheme;

import javax.swing.*;
import java.awt.*;

public interface NapkinPainter {
    void superPaint(Graphics g, JComponent c, NapkinTheme theme);
}