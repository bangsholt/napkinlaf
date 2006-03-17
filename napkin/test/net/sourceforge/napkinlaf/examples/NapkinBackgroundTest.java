// $Id: NapkinBackgroundTest.java 293 2006-03-06 09:18:49 -0500 (Mon, 06 Mar 2006) kcrca $

package net.sourceforge.napkinlaf.examples;

import javax.swing.*;
import java.awt.*;

public class NapkinBackgroundTest {
    public static void main(String[] args) throws
            Exception {

        UIManager.setLookAndFeel("net.sourceforge.napkinlaf.NapkinLookAndFeel");
        JFrame frame = new JFrame("Napkin Test");

        JMenuBar mb = new JMenuBar();
        JMenu m = new JMenu("Test");
        JMenuItem mi = new JMenuItem("TestAgain");
        m.add(mi);

        mb.add(m);
        //        frame.setJMenuBar(mb);

        JPanel stuff = new JPanel();
        stuff.setLayout(new BorderLayout());
        stuff.add(BorderLayout.CENTER, new JLabel("Hello"));

        JTabbedPane tabbed = new JTabbedPane();
        tabbed.addTab("Stuff", stuff);
        for (int i = 0; i < 6; i++)
            tabbed.add("Tab " + i, new JLabel("Tab " + i));

        frame.getContentPane().add(BorderLayout.CENTER, tabbed);

        frame.setSize(200, 100);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
