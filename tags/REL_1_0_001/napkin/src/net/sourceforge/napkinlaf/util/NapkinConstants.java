// $Id$

package net.sourceforge.napkinlaf.util;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface NapkinConstants extends SwingConstants {
    int LENGTH = 100;

    String THEME_KEY = "net.sourceforge.napkinlaf.theme";
    String BACKGROUND_KEY = "net.sourceforge.napkinlaf.background";
    String DISABLED_MARK_KEY = "net.sourceforge.napkinlaf.disabledMark";
    String BORDER_KEY = "net.sourceforge.napkinlaf.border";
    String OPAQUE_KEY = "net.sourceforge.napkinlaf.wasOpaque";
    String NEEDS_REVALIDATION = "net.sourceforge.napkinlaf.revalidated";

    @SuppressWarnings({"HardcodedFileSeparator"})
    String RESOURCE_PATH = "/net/sourceforge/napkinlaf/resources/";

    List<String> CLIENT_PROPERTIES = Collections.unmodifiableList(
            Arrays.asList(
                    THEME_KEY, BACKGROUND_KEY, DISABLED_MARK_KEY,
                    BORDER_KEY, OPAQUE_KEY
            )
    );

    /**
     * Since Swing cannot handle alpha correctly/consistently, when things fail
     * we ended up with pitch dark background which is a horrid. However the
     * erasure actually depends on such failure in order to paint, so if set to
     * (0xFF, 0xFF, 0xFF, 0x00) then the erasure mark effectively disappears.
     * <p/>
     * !! So a mid-value is chosen for a quick-fix.
     */
    Color CLEAR = new AlphaColorUIResource(0x80, 0x80, 0x80, 0x00);

    int NO_SIDE = -1;

    float CHECK_WIDTH = 2.5f;
}
