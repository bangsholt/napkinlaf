package net.sourceforge.napkinlaf.examples;

import net.sourceforge.napkinlaf.sketch.SketchifiedIcon;
import net.sourceforge.napkinlaf.sketch.SketchifiedImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * This program shows how {@link SketchifiedImage} (and by implication {@link
 * SketchifiedIcon}) work.  A window is brought up that allows you to select an
 * image, and then choose between showing the original and the sketched
 * version.
 */
public class Sketchification {
    private JFrame top;
    private JLabel display;
    private JFileChooser chooser;
    private ImageIcon orig;
    private ImageIcon sketch;

    /**
     * Run this class as a program
     *
     * @param args The command line arguments.
     *
     * @throws Exception Exception we don't recover from.
     */
    public static void main(String[] args) throws Exception {
        new Sketchification().run();
    }

    public void run() {
        top = new JFrame("Sketchification");
        top.setLayout(new BorderLayout());
        createControls();
        display = new JLabel();
        top.add(new JScrollPane(display), BorderLayout.CENTER);
        top.pack();
        top.setVisible(true);
    }

    private void createControls() {
        JPanel controls = new JPanel();
        controls.setLayout(new FlowLayout());
        JButton open = new JButton("Open...");
        open.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                changeFile();
            }
        });
        controls.add(open);

        ButtonGroup group = new ButtonGroup();
        addChoice(controls, "Sketched", group, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                display.setIcon(sketch);
            }
        }).setSelected(true);
        addChoice(controls, "Orig", group, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                display.setIcon(orig);
            }
        });

        top.add(controls, BorderLayout.NORTH);
    }

    private JRadioButton addChoice(JComponent controls, String title,
            ButtonGroup group, Action action) {

        JRadioButton button = new JRadioButton(title);
        button.setAction(action);
        button.setText(title);
        group.add(button);
        controls.add(button);
        return button;
    }

    private void changeFile() {
        if (chooser == null) {
            chooser = new JFileChooser();
        }
        int returnVal = chooser.showOpenDialog(top);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            top.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Image image = ImageIO.read(chooser.getSelectedFile());
            orig = new ImageIcon(image);
            sketch = new SketchifiedIcon(display, orig);
            display.setIcon(sketch);
            top.pack();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(top, e.getMessage(), "Image error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            top.setCursor(Cursor.getDefaultCursor());
        }
    }
}