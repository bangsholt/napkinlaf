// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;

public class NapkinBackground {
    public final Icon icon;
    public final Insets insets;

    public static final NapkinBackground NAPKIN_BG =
            new NapkinBackground("resources/napkin.jpg");
    public static final NapkinBackground[] POSTITS = {
        new NapkinBackground("resources/postit01.jpg", 15, 15, 38, 32),
        new NapkinBackground("resources/postit00.jpg", 38, 20, 100, 83),
    };
    public static final NapkinBackground POSTIT_BG = POSTITS[0];

    public NapkinBackground(String name) {
        this(name, null);
    }

    public NapkinBackground(String name, int top, int left, int bottom,
            int right) {
        this(name, new Insets(top, left, bottom, right));
    }

    public NapkinBackground(String name, Insets insets) {
        icon = new ImageIcon(getClass().getResource(name));
        this.insets = insets;
    }
}
