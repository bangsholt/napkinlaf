// $Id$

package napkin.examples;

import java.awt.*;
import javax.swing.*;

public class NapkinBackgroundTest {
    public static void main(String[] args) throws
            Exception {

        UIManager.setLookAndFeel("napkin.NapkinLookAndFeel");
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
