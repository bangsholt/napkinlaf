// $Id$

package napkin;

import java.awt.*;
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
        NapkinTheme theme = NapkinUtil.themeFor(comboBox);
        list.setFont((Font) NapkinUtil.ifReplace(list.getFont(),
                theme.getTextFont()));
        list.setForeground((Color) NapkinUtil.ifReplace(list.getForeground(),
                theme.getPenColor()));
        list.setBackground((Color) NapkinUtil.ifReplace(list.getBackground(),
                CLEAR));
        list.setSelectionForeground((Color)
                NapkinUtil.ifReplace(list.getSelectionForeground(),
                        theme.getPenColor()));
        list.setSelectionBackground((Color)
                NapkinUtil.ifReplace(list.getSelectionBackground(), CLEAR));
        list.setCellRenderer(new NapkinComboBoxUI.RenderResource());
    }
}
