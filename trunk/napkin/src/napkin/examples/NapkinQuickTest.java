// $Id$

package napkin.examples;

import napkin.NapkinLookAndFeel;

import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import java.util.Hashtable;
import java.util.Dictionary;
import javax.swing.*;

public class NapkinQuickTest {

    /**
     * Run this class as a program
     *
     * @param args The command line arguments.
     *
     * @throws Exception Exception we don't recover from.
     */
    public static void main(String[] args) throws Exception {
        final NapkinLookAndFeel laf = new NapkinLookAndFeel();
        UIManager.setLookAndFeel(laf);

        final JFrame top = new JFrame();
        top.setBackground(Color.cyan);
        JLabel label = new JLabel("-- Label --");

        laf.setIsFormal(top, true, true);
        Container content = top.getContentPane();
        content.setLayout(new GridLayout(4, 2));
        laf.setIsFormal(label, true, false);
        System.out.println("\nAdding label to " +
                System.identityHashCode(content));
        content.add(label);

        JButton button = new JButton("Button!");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println();
                laf.dumpFormality(top, System.out);
            }
        });
        laf.setIsFormal(button, true, false);
        System.out.println("\nAdding button");
        content.add(button);
        label.setText(laf.isFormal(label) ? "formal" : "napkin");

        content.add(new JCheckBox("Check?"));
        content.add(new JCheckBox("Check!"));

        ButtonGroup bgrp = new ButtonGroup();
        JRadioButton r1 = new JRadioButton("Radio?");
        JRadioButton r2 = new JRadioButton("Radio!");
        bgrp.add(r1);
        bgrp.add(r2);
        content.add(r1);
        content.add(r2);

        String[] words = new String[]{"combo", "box", "ui", "test"};
        JComboBox comboBox = new JComboBox(words);
        content.add(comboBox);

        JTextArea textArea = new JTextArea();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 400; i++) {
            if (sb.length() > 0)
                sb.append(' ');
            sb.append(i);
            if (i > 0 && i % 20 == 0) {
                textArea.append(sb.toString());
                textArea.append("\n");
                sb.delete(0, 1000);
            }
        }
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(200, 100));
        content.add(scrollPane);

        JSlider slider = new JSlider(JSlider.HORIZONTAL, -100, 100, 0);
        int majorSpacing = 50;
        slider.setMajorTickSpacing(majorSpacing);
        slider.setMinorTickSpacing(10);
        slider.setPaintTicks(true);
        Dictionary labels = slider.createStandardLabels(majorSpacing);
        slider.setLabelTable(labels);
        slider.setPaintLabels(true);
        content.add(slider);

        top.pack();
        top.show();
    }
}