// $Id$

package napkin;

import javax.swing.plaf.*;

public class DrawnBorder extends NapkinBoxBorder implements UIResource {
    public DrawnBorder() {
        super(NapkinTheme.Manager.getCurrentTheme().drawColor());
    }
}
