// $Id$

package napkin;

import javax.swing.*;
import javax.swing.plaf.basic.*;

public class NapkinComboPopup extends BasicComboPopup
        implements NapkinConstants {

    public NapkinComboPopup(JComboBox combo) {
        super(combo);
    }

    protected void configureList() {
        super.configureList();

        // now override those things that we set from the theme
        NapkinTheme theme = NapkinUtil.themeFor(this);
        list.setFont(NapkinUtil.ifReplace(list.getFont(), theme.getTextFont()));
        list.setForeground(NapkinUtil.ifReplace(list.getForeground(),
                theme.getPenColor()));
        list.setBackground(NapkinUtil.ifReplace(list.getBackground(), CLEAR));
        list.setSelectionForeground(NapkinUtil.ifReplace(
                list.getSelectionForeground(), theme.getSelectionColor()));
        list.setSelectionBackground(
                NapkinUtil.ifReplace(list.getSelectionBackground(), CLEAR));
    }
}
