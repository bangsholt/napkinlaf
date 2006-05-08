import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Toggle whether to start formality shifting
 */
public class FormalityChangeToggleAction implements Action {
    private final JComponent component;
    private final List<PropertyChangeListener> changeListeners;
    private final Map<String, Object> values;
    private boolean enabled;
    private MouseListener mouser;

    public FormalityChangeToggleAction(JComponent component) {
        this.component = component;
        changeListeners = new ArrayList<PropertyChangeListener>();
        values = new HashMap<String, Object>();
        enabled = true;
    }

    public void actionPerformed(ActionEvent e) {
        System.out.println("action: " + component);
        JRootPane root = component.getRootPane();
        Component glassPane = root.getGlassPane();
        if (glassPane.isVisible()) {
            glassPane.setVisible(false);
        } else {
            if (mouser == null) {
                mouser = new FormalityMouseListener(component);
                glassPane.addMouseListener(mouser);
            }
            glassPane.setVisible(true);
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeListeners.add(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeListeners.remove(listener);
    }

    public Object getValue(String key) {
        return values.get(key);
    }

    public void putValue(String key, Object value) {
        values.put(key, value);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}