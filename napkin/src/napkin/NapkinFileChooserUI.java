// $Id$

package napkin;

import sun.awt.shell.ShellFolder;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.EventObject;
import java.util.Locale;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.*;
import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;
import javax.swing.plaf.basic.*;
import javax.swing.table.*;
import javax.swing.text.*;

// This is nearly entirely copied from MetalFileChooserUI.  It seems as if the
// BasicFileChooserUI is not yet well formed.
public class NapkinFileChooserUI extends MetalFileChooserUI {

    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, new NapkinFileChooserUI((JFileChooser) c));
    }

    private NapkinFileChooserUI(JFileChooser c) {
        super(c);
    }

    public void installUI(JComponent c) {
        NapkinUtil.installUI(c);
        super.installUI(c);
    }

    public void paint(Graphics g, JComponent c) {
        NapkinUtil.defaultGraphics(g);
        super.paint(g, c);
    }

    public void update(Graphics g, JComponent c) {
        NapkinUtil.background(g, c);
        super.update(g, c);
    }
}

