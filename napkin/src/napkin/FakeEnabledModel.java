// $Id$

package napkin;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * @see FakeEnabledButton
 */
class FakeEnabledModel implements ButtonModel, FakeEnabled {
    private final ButtonModel origModel;

    FakeEnabledModel(ButtonModel model) {
        if (model == null)
            throw new NullPointerException("model");
        origModel = model;
    }

    public boolean isEnabled() {
        return true;
    }

    public void addActionListener(ActionListener l) {
        origModel.addActionListener(l);
    }

    public void addChangeListener(ChangeListener l) {
        origModel.addChangeListener(l);
    }

    public void addItemListener(ItemListener l) {
        origModel.addItemListener(l);
    }

    public String getActionCommand() {
        return origModel.getActionCommand();
    }

    public int getMnemonic() {
        return origModel.getMnemonic();
    }

    public Object[] getSelectedObjects() {
        return origModel.getSelectedObjects();
    }

    public boolean isArmed() {
        return origModel.isArmed();
    }

    public boolean isPressed() {
        return origModel.isPressed();
    }

    public boolean isRollover() {
        return origModel.isRollover();
    }

    public boolean isSelected() {
        return origModel.isSelected();
    }

    public void removeActionListener(ActionListener l) {
        origModel.removeActionListener(l);
    }

    public void removeChangeListener(ChangeListener l) {
        origModel.removeChangeListener(l);
    }

    public void removeItemListener(ItemListener l) {
        origModel.removeItemListener(l);
    }

    public void setActionCommand(String s) {
        origModel.setActionCommand(s);
    }

    public void setArmed(boolean b) {
        origModel.setArmed(b);
    }

    public void setEnabled(boolean b) {
        origModel.setEnabled(b);
    }

    public void setGroup(ButtonGroup group) {
        origModel.setGroup(group);
    }

    public void setMnemonic(int key) {
        origModel.setMnemonic(key);
    }

    public void setPressed(boolean b) {
        origModel.setPressed(b);
    }

    public void setRollover(boolean b) {
        origModel.setRollover(b);
    }

    public void setSelected(boolean b) {
        origModel.setSelected(b);
    }
}
