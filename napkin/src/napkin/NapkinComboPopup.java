// $Id$

package napkin;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import javax.swing.plaf.basic.*;

public class NapkinComboPopup extends BasicComboPopup {
    public NapkinComboPopup(JComboBox combo) {
        super(combo);
    }

    protected void configureList() {
        super.configureList();
        list.setCellRenderer(new NapkinComboBoxUI.RenderResource());
    }

    protected PropertyChangeListener createPropertyChangeListener() {
        return new PropertyChangeHandler() {
            public void propertyChange(PropertyChangeEvent e) {
                if (!e.getPropertyName().equals("renderer"))
                    super.propertyChange(e);
                else {
                    // done for renderer in super.propertChange, don't know why
                    if (isVisible())
                        hide();
                }
            }
        };
    }
}
