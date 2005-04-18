
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ShowKen {

    static JFrame f;

    static int lafDex;
    static JTextField tf;

    public static void main(String[] args) {
        f = new JFrame("Humm...");

        String laf = "napkin.NapkinLookAndFeel";
        UIManager.installLookAndFeel("Napkin", laf);

        JComboBox feature = new JComboBox(new String[]{
            "DSS", "SPM", "DMR",
            "DRS", "DRA", "DMT"
        });

        f.getContentPane().add(tf = new JTextField("text field..", 30),
                BorderLayout.NORTH);

        f.getContentPane().add(feature, BorderLayout.CENTER);

        JButton gen = new JButton("Generate");
        gen.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                UIManager.LookAndFeelInfo[] installed = UIManager
                        .getInstalledLookAndFeels();
                try {
                    tf.setText(installed[lafDex].getName());
                    UIManager.setLookAndFeel(installed[lafDex].getClassName());
                    SwingUtilities.updateComponentTreeUI(f);
                } catch (Exception ee) {
                    System.out.println(ee.getMessage());
                }
                if (++lafDex == installed.length) lafDex = 0;

            }
        });

        f.getContentPane().add(gen, BorderLayout.SOUTH);

        // Center and Display
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = 100;
        int height = 100;
        f.setLocation((screen.width - width) / 2, (screen.height - height) / 2);
        f.setSize(width, height);
        f.setVisible(true);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}