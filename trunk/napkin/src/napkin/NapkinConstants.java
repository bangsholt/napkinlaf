package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;

public interface NapkinConstants extends SwingConstants {
    int LENGTH = 100;

    String IS_PAPER = "napkin.isPaper";
    String PAPER = "napkin.paper";
    String PAPER_HOLDER = "napkin.paperHolder";
    String BACKGROUND = "napkin.background";
    String DISABLED_MARK = "napkin.disabledMark";

    String[] CLIENT_PROPERTIES = {
        IS_PAPER, PAPER, PAPER_HOLDER, BACKGROUND, DISABLED_MARK
    };

    // ColorUIResource can't seem to deal with the alpha channel, so we
    // have to use a Color.  I've filed a bug, but for now we do this.
    Color CLEAR = new Color(0, 0, 0, 0);
    ColorUIResource BLACK = new ColorUIResource(Color.black);

    int NO_SIDE = -1;
}