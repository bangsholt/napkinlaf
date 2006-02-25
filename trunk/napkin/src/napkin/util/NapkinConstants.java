// $Id$

package napkin.util;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.SwingConstants;

public interface NapkinConstants extends SwingConstants {
    int LENGTH = 100;

    String THEME_KEY = "napkin.theme";
    String BACKGROUND_KEY = "napkin.background";
    String DISABLED_MARK_KEY = "napkin.disabledMark";
    String BORDER_KEY = "napkin.border";
    String OPAQUE_KEY = "napkin.wasOpaque";
    
    String RESOURCE_PATH = "/napkin/resources/";

    List<String> CLIENT_PROPERTIES = Collections.unmodifiableList(
            Arrays.asList(
                THEME_KEY, BACKGROUND_KEY, DISABLED_MARK_KEY,
                BORDER_KEY, OPAQUE_KEY
            )
        );

    Color CLEAR = new AlphaColorUIResource(new Color(0, 0, 0, 0));

    int NO_SIDE = -1;

    float CHECK_WIDTH = 2.5f;
}
