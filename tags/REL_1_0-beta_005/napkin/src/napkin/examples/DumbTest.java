// $Id$

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
    public static void main(final String[] args) throws Exception {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    realMain(args);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    static void realMain(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("setting 'control'");
            UIManager.getDefaults().put("control", new Color(0, 0, 0, 0));
        }
        JFrame f = new JFrame("DumbTest") {
            public void reshape(int x, int y, int width, int height) {
                System.out.println("--- reshape");
                Thread.dumpStack();
                super.reshape(x, y, width, height);
            }
        };
        JLabel lab = new JLabel("foo");
        lab.setPreferredSize(new Dimension(100, 25));
        f.getContentPane().add(lab);
        f.pack();
        f.show();
        NapkinUtil.dumpObject(f, "1");
    }
}
