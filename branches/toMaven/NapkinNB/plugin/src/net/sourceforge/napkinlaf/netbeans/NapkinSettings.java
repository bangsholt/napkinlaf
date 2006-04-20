/*
 * NapkinSettings.java
 *
 * Created on 17 April 2006, 12:44
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sourceforge.napkinlaf.netbeans;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.openide.options.SystemOption;
import org.openide.util.SharedClassObject;

/**
 *
 * @author Alex Lam Sze Lok
 */
public class NapkinSettings extends SystemOption {

    static final long serialVersionUID = 8946546455638648L;
    private static final String NAPKIN_ENABLED = "napkinlaf.isEnabled";

    public static NapkinSettings getInstance() {
        return (NapkinSettings)
                SharedClassObject.findObject(NapkinSettings.class, true);
    }

    public static boolean isNapkinEnabled() {
        return getInstance().isEnabled();
    }
    
    public static void setNapkinEnabled(boolean value) {
        getInstance().setEnabled(value);
    }
    
    public String displayName() {
        return "Napkin Look & Feel";
    }
    
    // this method serializes the options
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(getProperty(NAPKIN_ENABLED));
    }
    
    // this method deserializes the options
    @Override
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        super.readExternal(in);
        putProperty(NAPKIN_ENABLED, in.readObject(), true);
    }

    public boolean isEnabled() {
        return !Boolean.FALSE.equals(getProperty(NAPKIN_ENABLED));
    }

    public void setEnabled(boolean value) {
        putProperty(NAPKIN_ENABLED, value, true);
    }
}
