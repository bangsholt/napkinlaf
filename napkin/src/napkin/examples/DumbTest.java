package napkin.examples;

import java.awt.*;
import javax.swing.*;

import napkin.NapkinUtil;

public class DumbTest {
    /**
     * Run this class as a program
     *
     * @param args The command line arguments.
     *
     * @throws Exception Exception we don't recover from.
     */
    public static void main(String[] args) throws Exception {
        JFrame f = new JFrame("DummyTest");
        UIManager.setLookAndFeel("napkin.NapkinLookAndFeel");
        SwingUtilities.updateComponentTreeUI(f);
        JLabel lab = new JLabel("foo");
        lab.setPreferredSize(new Dimension(100, 25));
        f.getContentPane().add(lab);
        f.pack();
        f.show();
        NapkinUtil.dumpObject(f, "1");
    }
}
