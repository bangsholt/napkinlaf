package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;

public interface NapkinConstants extends SwingConstants {
    int LENGTH = 100;

    String IS_PAPER_KEY = "napkin.isPaper";
    String PAPER_KEY = "napkin.paper";
    String PAPER_HOLDER_KEY = "napkin.paperHolder";
    String BACKGROUND_KEY = "napkin.background";
    String DISABLED_MARK_KEY = "napkin.disabledMark";
    String BORDER_KEY = "napkin.border";

    String[] CLIENT_PROPERTIES = {
        IS_PAPER_KEY, PAPER_KEY, PAPER_HOLDER_KEY, BACKGROUND_KEY,
        DISABLED_MARK_KEY, BORDER_KEY
    };

    // ColorUIResource can't seem to deal with the alpha channel, so we
    // have to use a Color.  I've filed a bug, but for now we do this.
    Color CLEAR = new Color(0, 0, 0, 0);
    ColorUIResource BLACK = new ColorUIResource(Color.black);

    int NO_SIDE = -1;
}