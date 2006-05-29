package net.sourceforge.napkinlaf.util;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface NapkinConstants extends SwingConstants {
    int LENGTH = 100;

    String INSTALL_KEY = "net.sourceforge.napkinlaf.installed";
    String THEME_KEY = "net.sourceforge.napkinlaf.theme";
    String BACKGROUND_KEY = "net.sourceforge.napkinlaf.background";
    String DISABLED_BACKGROUND_KEY = "net.sourceforge.napkinlaf.bgWhenEnabled";
    String DISABLED_MARK_KEY = "net.sourceforge.napkinlaf.disabledMark";
    String BORDER_KEY = "net.sourceforge.napkinlaf.border";
    String BOX_BORDER_KEY = "net.sourceforge.napkinlaf.boxBorder";
    String OPAQUE_KEY = "net.sourceforge.napkinlaf.wasOpaque";
    // the following key persists between LAF switches
    String HIGHLIGHT_KEY = "net.sourceforge.napkinlaf.highlighted";
    String REVALIDATE_KEY = "net.sourceforge.napkinlaf.revalidated";
    // the following key persists between LAF switches
    String ROLLOVER_KEY = "net.sourceforge.napkinlaf.rollovered";
    String ROLLOVER_ENABLED = "net.sourceforge.napkinlaf.wasRolloverEnabled";
    // the following key persists between LAF switches
    String NO_ROLLOVER_KEY = "net.sourceforge.napkinlaf.noRollover";
    String BUTTON_ICON_KEY = "net.sourceforge.napkinlaf.buttonIcon";
    String PRESSED_ICON_KEY = "net.sourceforge.napkinlaf.pressedIcon";
    String SELECTED_ICON_KEY = "net.sourceforge.napkinlaf.selectedIcon";
    String ROLLOVER_SELECTED_ICON_KEY = "net.sourceforge.napkinlaf.rolloverSelectedIcon";
    String ROLLOVER_ICON_KEY = "net.sourceforge.napkinlaf.rolloverIcon";
    String DISABLED_SELECTED_ICON_KEY = "net.sourceforge.napkinlaf.disabledSelectedIcon";
    String DISABLED_ICON_KEY = "net.sourceforge.napkinlaf.disabledIcon";

    List<String> CLIENT_PROPERTIES = Collections.unmodifiableList(
            Arrays.asList(
                    INSTALL_KEY, THEME_KEY, BACKGROUND_KEY,
                    DISABLED_BACKGROUND_KEY, DISABLED_MARK_KEY, BORDER_KEY,
                    BOX_BORDER_KEY, OPAQUE_KEY, REVALIDATE_KEY,
                    ROLLOVER_ENABLED, BUTTON_ICON_KEY, PRESSED_ICON_KEY,
                    SELECTED_ICON_KEY, ROLLOVER_SELECTED_ICON_KEY,
                    ROLLOVER_ICON_KEY, DISABLED_SELECTED_ICON_KEY,
                    DISABLED_ICON_KEY
            )
    );

    /**
     * Since Swing cannot handle alpha correctly/consistently, when things fail
     * we ended up with pitch dark background which is a horrid.
     */
    Color CLEAR = new AlphaColorUIResource(0xD0, 0xD0, 0xD0, 0x00);
    /**
     * Identification token for highlight-style selections, in cases where we
     * can't reach though the proper ComponentUI mechanism
     */
    Color HIGHLIGHT_CLEAR = new AlphaColorUIResource(0x12, 0x34, 0x56, 0x00);

    int NO_SIDE = -1;

    float CHECK_WIDTH = 2.5f;
}
