/*
 * NapkinSettings.java
 *
 * Created on 17 April 2006, 12:44
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sourceforge.napkinlaf.netbeans;

import java.beans.PropertyVetoException;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import org.openide.options.SystemOption;
import org.openide.util.SharedClassObject;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 *
 * @author Alex Lam Sze Lok
 */
public class NapkinSettings extends SystemOption {

    static final long serialVersionUID = 378221546854587L;
    private static final String NAPKIN_ENABLED = "napkinlaf.isEnabled";
    private static final String OLD_LOOK_AND_FEEL = "napkinlaf.oldLookAndFeel";

    private static NapkinSettings getInstance() {
        return (NapkinSettings)
                SharedClassObject.findObject(NapkinSettings.class, true);
    }

    public static boolean isNapkinEnabled() {
        return getInstance().isEnabled();
    }
    
    public static String getDefaultLookAndFeel() {
        return getInstance().getOldLookAndFeel();
    }
    
    public String displayName() {
        return "Napkin Look & Feel";
    }
    
    public boolean isEnabled() {
        return !Boolean.FALSE.equals(getProperty(NAPKIN_ENABLED));
    }

    public void setEnabled(boolean value) {
        putProperty(NAPKIN_ENABLED, value, true);
    }
    
    public String getOldLookAndFeel() {
        String result = (String) getProperty(OLD_LOOK_AND_FEEL);
        if (result == null) {
            result = UIManager.getSystemLookAndFeelClassName();
        }
        return result;
    }
    
    public void setOldLookAndFeel(String value) {
        try {
            if (LookAndFeel.class.isAssignableFrom(Class.forName(value))) {
                putProperty(OLD_LOOK_AND_FEEL, value);
            }
        } catch (ClassNotFoundException ex) {
            ; // do nothing
        }
    }
}
