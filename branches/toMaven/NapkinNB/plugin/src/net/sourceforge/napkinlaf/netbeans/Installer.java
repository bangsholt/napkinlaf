package net.sourceforge.napkinlaf.netbeans;

import net.sourceforge.napkinlaf.NapkinLookAndFeel;

import org.netbeans.swing.plaf.LFCustoms;
import org.netbeans.swing.plaf.Startup;
import org.openide.ErrorManager;
import org.openide.modules.ModuleInstall;

import java.util.Map;
import javax.swing.*;

/**
 * Manages a module's lifecycle. Remember that an installer is optional and
 * often not needed at all.
 */
public class Installer extends ModuleInstall {

    static LookAndFeel origLAF = null;
    static String origLAFClass = null;

    private static void updateLookAndFeel() {
        boolean isNapkin = NapkinSettings.isNapkinEnabled();
        if (isNapkin &&
                (UIManager.getLookAndFeel() instanceof NapkinLookAndFeel)) {

            return;
        }
        try {
            if (isNapkin) {
                UIManager.setLookAndFeel(new NapkinLookAndFeel());
            } else if (origLAF == null) {
                UIManager.setLookAndFeel(origLAFClass);
            } else {
                UIManager.setLookAndFeel(origLAF);
            }
            LookAndFeel laf = UIManager.getLookAndFeel();
            if (laf.getSupportsWindowDecorations()) {
                ErrorManager.getDefault().log(ErrorManager.WARNING,
                        "Look and Feel supports window decorations!");
                JFrame.setDefaultLookAndFeelDecorated(true);
                JDialog.setDefaultLookAndFeelDecorated(true);
            } else {
                JFrame.setDefaultLookAndFeelDecorated(false);
                JDialog.setDefaultLookAndFeelDecorated(false);
            }
            LFCustoms lfCustoms = null;
            if (isNapkin) {
                NapkinLookAndFeel.overrideComponentDefaults(UIManager.getDefaults());
                lfCustoms = new NapkinLFCustoms();
            } else {
                lfCustoms = (LFCustoms)
                        UIManager.get("Nb." + laf.getID() + "LFCustoms");
            }
            if (lfCustoms != null) {
                UIManager.getDefaults().putDefaults(
                        lfCustoms.createApplicationSpecificKeysAndValues());
                UIManager.getDefaults().putDefaults(
                        lfCustoms.createGuaranteedKeysAndValues());
                UIManager.getDefaults().putDefaults(lfCustoms
                        .createLookAndFeelCustomizationKeysAndValues());
            }
            Startup.run(laf.getClass(), 0, null);
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }

    private static void dumpUIDefaults() {
        UIDefaults uiDefs = UIManager.getDefaults();
        ErrorManager manager = ErrorManager.getDefault();
        manager.log(ErrorManager.WARNING, "UIDefaults");
        for (Map.Entry<Object, Object> entry : uiDefs.entrySet()) {
            manager.log(ErrorManager.WARNING,
                    entry.getKey() + " ==> " + entry.getValue());
        }
    }

    @Override
    public void restored() {
        origLAF = UIManager.getLookAndFeel();
        origLAFClass = UIManager.getSystemLookAndFeelClassName();
        ErrorManager.getDefault().log(ErrorManager.WARNING,
                "Look and Feel should be " +
                (NapkinSettings.isNapkinEnabled() ? "Napkin" : origLAFClass));
        updateLookAndFeel();
    }
}
