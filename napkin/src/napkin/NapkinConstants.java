package napkin;

import java.awt.*;
import javax.swing.*;

public interface NapkinConstants extends SwingConstants {
    int LENGTH = 100;

    String IS_THEME_TOP_KEY = "napkin.isThemeTop";
    String THEME_KEY = "napkin.theme";
    String THEME_TOP_KEY = "napkin.themeTop";
    String BACKGROUND_KEY = "napkin.background";
    String DISABLED_MARK_KEY = "napkin.disabledMark";
    String BORDER_KEY = "napkin.border";

    String[] CLIENT_PROPERTIES = {
        IS_THEME_TOP_KEY, THEME_KEY, THEME_TOP_KEY, BACKGROUND_KEY,
        DISABLED_MARK_KEY, BORDER_KEY
    };

    // ColorUIResource can't seem to deal with the alpha channel, so we
    // have to use a Color.  I've filed a bug, but for now we do this.
    Color CLEAR = new Color(0, 0, 0, 0);

    int NO_SIDE = -1;
}