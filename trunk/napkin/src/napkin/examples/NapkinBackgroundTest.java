// $Id$

package napkin.examples;

import java.awt.*;
import javax.swing.*;

public class NapkinBackgroundTest {
    public static void main(String[] args) throws
            Exception {

        UIManager.setLookAndFeel("napkin.NapkinLookAndFeel");
        JFrame f = new JFrame("Napkin Test");

        JMenuBar mb = new JMenuBar();
        JMenu m = new JMenu("Test");
        JMenuItem mi = new JMenuItem("TestAgain");
        m.add(mi);

        mb.add(m);
        f.setJMenuBar(mb);

        f.getContentPane().add(BorderLayout.CENTER, new
                JLabel("Hello"));

        f.setSize(200, 100);
        f.setLocationRelativeTo(null);
        f.show();
    }
}
