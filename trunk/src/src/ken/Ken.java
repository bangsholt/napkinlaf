package ken;

import java.awt.*;
import javax.swing.*;

public class Ken {
    /**
     * Run this class as a program
     *
     * @param args The command line arguments.
     * @throws Exception Exception we don't recover from.
     */
    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame("Ken");

        frame.getContentPane().add(new JLabel("Ken"));
        frame.pack();
        frame.setVisible(true);

        JDialog dialog = new JDialog(frame, "Ken Dialog", false);
        Color c = Color.red;
        dialog.getContentPane().setBackground(c);
        dialog.getContentPane().add(new JLabel("Dialog"));
        dialog.setVisible(true);

        for (int alpha = 255; alpha > 0; alpha = (int) (alpha * 0.9)) {
            Thread.sleep(1000);
            c = new Color(255, 0, 0, alpha);
            System.out.println(alpha + ": " + c);
            dialog.getContentPane().setBackground(c);
            dialog.getContentPane().repaint();
        }

        Thread.sleep(5000000);

    }
}