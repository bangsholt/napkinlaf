import javax.swing.*;

public class Test {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("napkin.NapkinLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        JFrame lFrame = new JFrame();
        lFrame.getContentPane().add(new JButton("blabla"));
        lFrame.pack();
        lFrame.setVisible(true);
    }
}