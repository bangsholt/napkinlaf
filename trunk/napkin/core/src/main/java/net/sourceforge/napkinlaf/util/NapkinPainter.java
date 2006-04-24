package net.sourceforge.napkinlaf.util;

import net.sourceforge.napkinlaf.NapkinTheme;

import javax.swing.*;
import java.awt.*;

public interface NapkinPainter {
    void superPaint(Graphics g, JComponent c, NapkinTheme theme);
}
