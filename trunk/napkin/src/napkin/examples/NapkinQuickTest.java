// $Id$

package napkin.examples;

import java.awt.*;
import java.awt.event.*;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import napkin.NapkinLookAndFeel;
import napkin.NapkinUtil;

public class NapkinQuickTest implements SwingConstants {

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

        final Set toDisable = new HashSet();

        final JFrame top = new JFrame();
        top.setBackground(Color.cyan);
        JTabbedPane tabbed = new JTabbedPane();
        JPanel mainPanel = new JPanel();
        tabbed.addTab("Main Stuff", mainPanel);
        top.getContentPane().add(tabbed);

        JLabel label = new JLabel("-- Label --");

        laf.setIsFormal(top, true, true);
        mainPanel.setLayout(new GridLayout(4, 2));
        laf.setIsFormal(label, true, false);
        System.out.println("\nAdding label to " +
                System.identityHashCode(mainPanel));
        mainPanel.add(label);

        JButton button = new JButton("Button!");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println();
                laf.dumpFormality(top, System.out);
            }
        });
        laf.setIsFormal(button, true, false);
        System.out.println("\nAdding button");
        mainPanel.add(button);
        label.setText(laf.isFormal(label) ? "formal" : "napkin");
        toDisable.add(button);

        mainPanel.add(new JCheckBox("Check?"));
        final JCheckBox disableButton = new JCheckBox("Disable");
        final JFrame disableControls = createDisableControls(toDisable);
        disableButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean enable = !disableButton.isSelected();
                for (Iterator it = toDisable.iterator(); it.hasNext();) {
                    JComponent component = (JComponent) it.next();
                    component.setEnabled(enable);
                }
                disableControls.setVisible(!enable);
            }
        });
        mainPanel.add(disableButton);

        ButtonGroup bgrp = new ButtonGroup();
        JRadioButton r1 = new JRadioButton("Radio?");
        JRadioButton r2 = new JRadioButton("Radio!");
        bgrp.add(r1);
        bgrp.add(r2);
        mainPanel.add(r1);
        mainPanel.add(r2);
        toDisable.add(r1);
        toDisable.add(r2);

        String[] words = new String[]{"combo", "box", "ui", "test"};
        JComboBox comboBox = new JComboBox(words);
        mainPanel.add(comboBox);
        toDisable.add(comboBox);

        JSlider slider = new JSlider(JSlider.HORIZONTAL, -100, 100, 0);
        int majorSpacing = 50;
        slider.setMajorTickSpacing(majorSpacing);
        slider.setMinorTickSpacing(10);
        slider.setPaintTicks(true);
        Dictionary labels = slider.createStandardLabels(majorSpacing);
        slider.setLabelTable(labels);
        slider.setPaintLabels(true);
        mainPanel.add(slider);
        toDisable.add(slider);

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
        tabbed.addTab("Text", scrollPane);

        JPanel fields = new JPanel();
        fields.setLayout(new BorderLayout());
        JTextField field = new JTextField("of dreams", 20);
        fields.add(new JLabel("Field:"), BorderLayout.WEST);
        fields.add(field, BorderLayout.CENTER);
        tabbed.addTab("Fields", fields);

        for (int i = 0; i < 4; i++)
            tabbed.addTab("Tab " + i, new JLabel("Just a Label #" + i, CENTER));

        JPanel tabCtrls = new JPanel();
        ButtonGroup ctlGrp = new ButtonGroup();
        tabCtrls.setLayout(new GridLayout(2, 2));
        addCtrl(tabbed, tabCtrls, ctlGrp, "top", TOP, true);
        addCtrl(tabbed, tabCtrls, ctlGrp, "right", RIGHT, false);
        addCtrl(tabbed, tabCtrls, ctlGrp, "left", LEFT, false);
        addCtrl(tabbed, tabCtrls, ctlGrp, "bottom", BOTTOM, false);
        tabbed.addTab("Controls", tabCtrls);

        top.pack();
        top.show();
    }

    private static JFrame createDisableControls(final Set toRepaint) {
        JFrame top = new JFrame("Disable Controls");
        Container content = top.getContentPane();

        if (true)
            return top;

        final SpinnerModel alphaModel = new SpinnerNumberModel(NapkinUtil.alpha, 0.0, 1.0, 0.05);
        JSpinner alpha = new JSpinner(alphaModel);
        JPanel alphaPanel = new JPanel(new FlowLayout());
        alphaPanel.add(new JLabel("alpha"));
        alphaPanel.add(alpha);

        final SpinnerNumberModel modeModel = new SpinnerNumberModel(NapkinUtil.compositeType, 1, 12, 1);
        JSpinner mode = new JSpinner(modeModel);
        JPanel modePanel = new JPanel(new FlowLayout());
        modePanel.add(new JLabel("mode"));
        modePanel.add(mode);

        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
        controls.add(alphaPanel);
        controls.add(modePanel);

        content.setLayout(new BorderLayout());
        content.add(controls, BorderLayout.CENTER);
//        JLabel texture = new JLabel(new ImageIcon(NapkinUtil.textureImage));
        JPanel texture = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);    //To change body of overridden methods use File | Settings | File Templates.
                g.drawImage(NapkinUtil.textureImage, 0, 0, this);
            }

            public Dimension getPreferredSize() {
                return new Dimension(NapkinUtil.textureImage.getWidth(this),
                        NapkinUtil.textureImage.getHeight(this));
            }

            public Dimension getMinimumSize() {
                return getPreferredSize();
            }
        };
        texture.setBorder(new BevelBorder(BevelBorder.RAISED));
        content.add(texture, BorderLayout.WEST);
        top.pack();

        ChangeListener repaintIt = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                NapkinUtil.compositeType = ((Number) modeModel.getValue()).intValue();
                NapkinUtil.alpha = ((Number) alphaModel.getValue()).floatValue();
                for (Iterator it = toRepaint.iterator(); it.hasNext();) {
                    JComponent component = (JComponent) it.next();
                    component.repaint();
                }
            }
        };
        modeModel.addChangeListener(repaintIt);
        alphaModel.addChangeListener(repaintIt);

        return top;
    }

    private static void addCtrl(final JTabbedPane tabs, Container ctrls,
            ButtonGroup grp, String lab, final int side, boolean on) {
        JRadioButton button = new JRadioButton(lab, on);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tabs.setTabPlacement(side);
            }
        });
        grp.add(button);
        ctrls.add(button);
    }
}