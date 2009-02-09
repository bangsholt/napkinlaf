package net.sourceforge.napkinlaf.dev;

import net.sourceforge.napkinlaf.NapkinLookAndFeel;
import net.sourceforge.napkinlaf.sketch.AbstractSketcher;
import net.sourceforge.napkinlaf.sketch.DrawnIcon;
import net.sourceforge.napkinlaf.sketch.Template;
import net.sourceforge.napkinlaf.sketch.sketchers.DraftSketcher;
import net.sourceforge.napkinlaf.sketch.sketchers.IdealSketcher;
import net.sourceforge.napkinlaf.sketch.sketchers.JotSketcher;
import net.sourceforge.napkinlaf.sketch.sketchers.LineSketcher;

import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.plaf.metal.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

/**
 * A test application for loading and displaying XML template files. This
 * program allows the sketching style and the look and feel to be changed during
 * runtime.
 *
 * @author Justin Crafford
 * @author Peter Goodspeed
 */
@SuppressWarnings({"ObjectEquality", "ObjectEquality", "ObjectEquality"})
public class SketchTest implements ActionListener {
    // Constants used for setting the current sketching style
    private static final int IDEAL = 0;
    private static final int JOT = 1;
    private static final int LINE = 2;
    private static final int DRAFTSMAN = 3;
    private static final int DEFAULT = IDEAL;

    // Drawn icons generated from an XML template
    private DrawnIcon templateIcon;

    // Various GUI widgets for controlling the application's settings
    private static JFrame templateTestFrame;
    private static JMenuBar menuBar;
    private final JPanel mainPanel;
    private final JPanel selectPanel;
    private final JPanel displayPanel;
    private JMenuItem openMenuItem,
            exitMenuItem;
    private final JFileChooser fileChooser;
    private JComboBox sketchChoices;
    private JButton sketchButton;
    private JButton reloadButton;
    private JLabel templateImageLabel;

    private boolean isNapkinLAF;
    private File file;

    /** Constructs the main GUI objects that the application uses */
    private SketchTest() {
        // Create the file chooser
        fileChooser = new JFileChooser(currentDir());
        XMLFilter fileFilter = new XMLFilter();
        fileChooser.setFileFilter(fileFilter);
        DrawnIconFileView diFileView = new DrawnIconFileView();
        fileChooser.setFileView(diFileView);

        // Create the sketch style selection and display panels
        selectPanel = new JPanel();
        displayPanel = new JPanel();

        // Add various widgets to the sub panels
        addWidgets();

        // Create the main panel to contain the two sub panels
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 50, 20));

        // Add the select and display panels to the main panel
        mainPanel.add(selectPanel);
        mainPanel.add(displayPanel);
    }

    private static File currentDir() {
        File curDir = new File(".");
        try {
            return curDir.getCanonicalFile();
        } catch (IOException e) {
            throw new IllegalArgumentException(curDir.getAbsolutePath(), e);
        }
    }

    /**
     * This class represents a file filter for use by the file chooser. It
     * limits the files displayed only to those with an "xml" file extension.
     */
    static class XMLFilter extends FileFilter {
        /** {@inheritDoc} */
        @Override
        public boolean accept(File pathname) {
            if (pathname.isDirectory()) {
                return true;
            }

            String extension = getExtension(pathname);
            return (extension != null && extension.equals("xml"));
        }

        /** {@inheritDoc} */
        @Override
        public String getDescription() {
            return "XML Templates";
        }

        /**
         * @param f
         *
         * @return the extension of the specified File
         */
        public String getExtension(File f) {
            String ext = null;
            String s = f.getName();
            int i = s.lastIndexOf('.');

            if (i > 0 && i < s.length() - 1) {
                ext = s.substring(i + 1).toLowerCase();
            }
            return ext;
        }
    }

    private class DrawnIconFileView extends FileView {
        /** {@inheritDoc} */
        @Override
        public Icon getIcon(File f) {
            Icon icon = null;

            if (f.isDirectory() && isNapkinLAF) {
                icon = templateIcon;
            }
            return icon;
        }
    }

    /** Creates the GUI objects for the File and Look and Feel menus */
    private void addMenuWidgets() {
        // Create the menu bar
        menuBar = new JMenuBar();

        // Create the File menu and set accessibility features
        JMenu menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menu.getAccessibleContext().setAccessibleDescription("The File Menu");

        // Create the Open Template File menu item and set accessibility
        // features
        openMenuItem = new JMenuItem("Open Template", KeyEvent.VK_O);
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                ActionEvent.ALT_MASK));
        openMenuItem.getAccessibleContext().setAccessibleDescription(
                "Opens an XML template file");
        openMenuItem.addActionListener(this);
        menu.add(openMenuItem); // Add the menu item to the File menu

        // Create the Exit File menu item and set accessibility features
        exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
        openMenuItem.getAccessibleContext().setAccessibleDescription(
                "Exits the program");
        exitMenuItem.addActionListener(this);
        menu.add(exitMenuItem); // Add the menu item to the File menu
        menuBar.add(menu); // Add the menu to the menu bar

        // Create the Look & Feel menu and set accessibility features
        menu = new JMenu("Look & Feel");
        menu.setMnemonic(KeyEvent.VK_L);
        menu.getAccessibleContext().setAccessibleDescription(
                "The Look & Feel Menu");

        // Create the radio button group to contain the radio buttom menu items
        ButtonGroup group = new ButtonGroup();

        // Create the radio button menu item for selecting the Java Look and
        // Feel
        JRadioButtonMenuItem rbMenuItem = new JRadioButtonMenuItem(
                "Java Look & Feel");
        rbMenuItem.setActionCommand("Java Look & Feel");
        rbMenuItem.setMnemonic(KeyEvent.VK_J);
        rbMenuItem.getAccessibleContext().setAccessibleDescription(
                "Changes the GUI to the Java Look & Feel");
        rbMenuItem.addActionListener(this);
        rbMenuItem.setSelected(true);
        group.add(rbMenuItem); // Add the radio button menu item to the group
        menu.add(rbMenuItem); // Add the radio buttom menu item to the menu

        // Create the radio button menu item for selecting the Napkin Look and
        // Feel
        rbMenuItem = new JRadioButtonMenuItem("Napkin Look & Feel");
        rbMenuItem.setActionCommand("Napkin Look & Feel");
        rbMenuItem.setMnemonic(KeyEvent.VK_N);
        rbMenuItem.getAccessibleContext().setAccessibleDescription(
                "Changes the GUI to the Napkin Look & Feel");
        rbMenuItem.addActionListener(this);
        group.add(rbMenuItem); // Add the radio button menu item to the group
        menu.add(rbMenuItem); // Add the radio button menu itme to the menu

        // Add the Look & Feel menu to the menu bar
        menuBar.add(menu);
    }

    /**
     * Creates the GUI objects for displaying the template file and adds the
     * various GUI widgets to the application panels
     */
    private void addWidgets() {
        // Creates and adds the menu widgets
        addMenuWidgets();

        // Create a label for displaying the sketched template image
        templateImageLabel = new JLabel();

        // Create a combo box with sketch style choices
        String[] sketchStyles = {"Ideal", "Jot", "StraightLine", "Draftsman"};
        sketchChoices = new JComboBox(sketchStyles);
        sketchChoices.setSelectedIndex(DEFAULT);
        sketchChoices.setEnabled(false); // Disable until a template is loaded

        // Create a button for re-sketching the icon
        sketchButton = new JButton("Re-sketch");
        sketchButton.setEnabled(false); // Disable until a template is loaded

        // Create a button for reloading the current file
        reloadButton = new JButton("Reload");
        reloadButton.setEnabled(false);
        reloadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadFile();
            }
        });

        // Add a border around the select panel
        selectPanel.setBorder(BorderFactory
                .createTitledBorder("Select Sketch Style"));

        // Add a border around the display panel
        displayPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
                .createTitledBorder("Generated Icon"), BorderFactory
                .createEmptyBorder(10, 100, 10, 100)));

        // Add combo box and button to select panel and image label
        displayPanel.add(templateImageLabel);
        selectPanel.add(sketchChoices);
        selectPanel.add(sketchButton);
        selectPanel.add(reloadButton);

        // Listen to events from the combo box and sketch button
        sketchChoices.addActionListener(this);
        sketchButton.addActionListener(this);
    }

    /**
     * Determines the appropriate action to take when a user-initiated event
     * occurs
     *
     * @param event
     */
    public void actionPerformed(ActionEvent event) {
        // Get the current sketch style
        int sketchStyle = sketchChoices.getSelectedIndex();

        // Combo box events
        if ("comboBoxChanged".equals(event.getActionCommand())) {
            //Update the icon to display the new image
            templateIcon.setSketcher(getSketchStyle(sketchStyle));
            templateImageLabel.setIcon(templateIcon);
            templateImageLabel.repaint();
        }

        if (event.getSource() == sketchButton) {
            templateIcon.setSketched(false);
            templateImageLabel.repaint();
        }

        // File menu item events
        // Open a XML template file to display
        if (event.getSource() == openMenuItem) {
            int returnVal = fileChooser.showOpenDialog(mainPanel);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
                loadFile();

                // Enable the combo box for choosing the sketching style
                sketchChoices.setEnabled(true);
                // Enable the sketch button for re-sketching the icon's image
                sketchButton.setEnabled(true);
                // Now we have something to reload
                reloadButton.setEnabled(true);
            }
        }
        // Exit the java application
        if (event.getSource() == exitMenuItem) {
            System.exit(1);
        }

        // Radio button menu item events
        // Change the look and feel to the default, Java Look and Feel
        if ("Java Look & Feel".equals(event.getActionCommand())) {
            LookAndFeel laf = new MetalLookAndFeel();
            try {
                UIManager.setLookAndFeel(laf);
            } catch (UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
            SwingUtilities.updateComponentTreeUI(templateTestFrame);
            SwingUtilities.updateComponentTreeUI(fileChooser);

            isNapkinLAF = false;
        }

        // Change the look and feel to the Napkin Look and Feel
        if ("Napkin Look & Feel".equals(event.getActionCommand())) {
            LookAndFeel laf = new NapkinLookAndFeel();
            try {
                UIManager.setLookAndFeel(laf);
            } catch (UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
            SwingUtilities.updateComponentTreeUI(templateTestFrame);
            SwingUtilities.updateComponentTreeUI(fileChooser);

            isNapkinLAF = true;
        }
    }

    private void loadFile() {
        try {
            templateIcon = createDrawnIcon(file.getPath(), DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Update the icon to display the new image
        templateImageLabel.setToolTipText(templateIcon
                .getTemplateDescription());
        templateImageLabel.setIcon(templateIcon);
        templateImageLabel.repaint();
    }

    /**
     * Creates a sketched image of an XML template in the given sketch style
     *
     * @param templatePath The path to the XML template document
     * @param sketchStyle  The sketch style in which to draw the XML template
     *
     * @return An icon image of the sketched template
     */
    private static DrawnIcon
    createDrawnIcon(String templatePath, int sketchStyle) {

        AbstractSketcher sketcher = getSketchStyle(sketchStyle);
        DrawnIcon ret  ;

        try {
            Template template = Template.createFromXML(templatePath);
            ret = new DrawnIcon(template, sketcher);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }

        return ret;
    }

    private static AbstractSketcher getSketchStyle(int sketchStyle) {
        AbstractSketcher sketcher;

        // Selects the sketching style to use
        switch (sketchStyle) {
        case IDEAL:
            sketcher = new IdealSketcher();
            break;
        case JOT:
            sketcher = new JotSketcher();
            break;
        case LINE:
            sketcher = new LineSketcher();
            break;
        case DRAFTSMAN:
            sketcher = new DraftSketcher();
            break;
        default:
            sketcher = new IdealSketcher();
            break;
        }

        return sketcher;
    }

    /**
     * Create the GUI and show it. For thread safety, this method should be
     * invoked from the event-dispatching thread
     */
    private static void createAndShowGUI() {
        // Create a new instance of TemplateTest
        SketchTest sketches = new SketchTest();

        // Create and set up the window
        templateTestFrame = new JFrame("Sketch Test");
        templateTestFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        templateTestFrame.setContentPane(sketches.mainPanel);
        templateTestFrame.setJMenuBar(menuBar);

        // Display the window.
        templateTestFrame.pack();
        templateTestFrame.setVisible(true);
    }

    /**
     * The main function to run when the program is first started Starts a new
     * thread for the application's GUI
     *
     * @param args Arguments passed to the program from the command line
     */
    public static void main(String[] args) {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}

