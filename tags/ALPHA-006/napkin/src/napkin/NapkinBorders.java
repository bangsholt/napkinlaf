// $Id$

package napkin;

import javax.swing.border.*;

public class NapkinBorders {
    public static Border getDrawnBorder() {
        return new DrawnBorder();
    }

    public static Border getUnderlineBorder() {
        return new NapkinLineBorder(null, false);
    }
}

