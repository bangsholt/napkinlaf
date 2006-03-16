// $Id: NapkinPainter.java 293 2006-03-06 14:18:49Z kcrca $

package net.sourceforge.napkinlaf.util;

import net.sourceforge.napkinlaf.NapkinTheme;

import javax.swing.*;
import java.awt.*;

public interface NapkinPainter {
    void superPaint(Graphics g, JComponent c, NapkinTheme theme);
}