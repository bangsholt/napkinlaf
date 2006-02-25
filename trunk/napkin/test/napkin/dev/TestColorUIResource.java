// $Id$

package napkin.dev;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.metal.*;

public class TestColorUIResource {
    /**
     * Run this class as a program
     *
     * @param args The command line arguments.
     *
     * @throws Exception Exception we don't recover from.
     */
    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(new MetalLookAndFeel());
        JFrame frame = new JFrame("TestColorUIResource");
        JPanel panel = new JPanel();
        frame.getContentPane().add(panel);
        JPanel green = new JPanel();
        JPanel blue = new JPanel();
        green.setPreferredSize(new Dimension(30, 10));
        blue.setPreferredSize(new Dimension(30, 10));
        panel.add(green);
        panel.add(blue);
        frame.getContentPane().setBackground(Color.yellow);
        panel.setBackground(Color.red);
        green.setBackground(new Color(0, 255, 0, 120));
        blue.setBackground(Color.blue);

        frame.pack();
        frame.setVisible(true);
    }
}
