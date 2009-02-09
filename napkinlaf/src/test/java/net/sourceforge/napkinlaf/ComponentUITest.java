/*
 * ComponentUITest.java
 * JUnit based test
 *
 * Created on 13 April 2006, 22:23
 */

package net.sourceforge.napkinlaf;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import net.sourceforge.napkinlaf.util.NapkinDebug;
import net.sourceforge.napkinlaf.util.NapkinIconFactory;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** @author Alex Lam Sze Lok */
@SuppressWarnings(
        {"CollectionDeclaredAsConcreteClass", "UseOfObsoleteCollectionType"})
public class ComponentUITest extends TestCase {
    private String lastType;
    private String blankType;

    private static final List<TestPair> pairs = new ArrayList<TestPair>();

    private static class TestPair {
        final Class<? extends ComponentUI> uiClass;
        final JComponent component;
        boolean skip;

        TestPair(Class<? extends ComponentUI> uiClass, JComponent component) {
            this.uiClass = uiClass;
            this.component = component;
        }

        TestPair skip() {
            skip = true;
            return this;
        }
    }
    private static class TestComboBoxModel implements ComboBoxModel {
        public void addListDataListener(ListDataListener l) {
        }

        public Object getElementAt(int index) {
            return null;
        }

        public Object getSelectedItem() {
            return null;
        }

        public int getSize() {
            return 0;
        }

        public void removeListDataListener(ListDataListener l) {
        }

        public void setSelectedItem(Object anItem) {
        }
    }

//    private static class TestColorSelectionModel
//            implements ColorSelectionModel {
//        private Color selColor = Color.WHITE;
//
//        public void addChangeListener(ChangeListener listener) {
//        }
//
//        public Color getSelectedColor() {
//            return selColor;
//        }
//
//        public void removeChangeListener(ChangeListener listener) {
//        }
//
//        public void setSelectedColor(Color color) {
//            selColor = color;
//        }
//    }
// --Commented out by Inspection STOP (11/20/06 6:57 PM)

    static {
        File tmp = null;
        try {
            Icon icon = NapkinIconFactory.createXIcon(20);
            String text = "testing";
            Color color = new Color(new Random().nextInt());
            Object[] items = {text, icon, color};

            pairs.add(new TestPair(NapkinDesktopPaneUI.class,
                    new JDesktopPane()).skip());

            pairs.add(new TestPair(NapkinButtonUI.class, new JButton()));
            pairs.add(new TestPair(NapkinButtonUI.class, new JButton(text)));
            pairs.add(new TestPair(NapkinButtonUI.class, new JButton(icon)));
            pairs.add(new TestPair(NapkinButtonUI.class, new JButton(text,
                    icon)));

            pairs.add(new TestPair(NapkinCheckBoxMenuItemUI.class,
                    new JCheckBoxMenuItem()));
            pairs.add(new TestPair(NapkinCheckBoxMenuItemUI.class,
                    new JCheckBoxMenuItem(icon)));
            pairs.add(new TestPair(NapkinCheckBoxMenuItemUI.class,
                    new JCheckBoxMenuItem(text)));
            pairs.add(new TestPair(NapkinCheckBoxMenuItemUI.class,
                    new JCheckBoxMenuItem(text, true)));
            pairs.add(new TestPair(NapkinCheckBoxMenuItemUI.class,
                    new JCheckBoxMenuItem(text, false)));
            pairs.add(new TestPair(NapkinCheckBoxMenuItemUI.class,
                    new JCheckBoxMenuItem(text, icon)));
            pairs.add(new TestPair(NapkinCheckBoxMenuItemUI.class,
                    new JCheckBoxMenuItem(text, icon, true)));
            pairs.add(new TestPair(NapkinCheckBoxMenuItemUI.class,
                    new JCheckBoxMenuItem(text, icon, false)));

            pairs.add(new TestPair(NapkinCheckBoxUI.class, new JCheckBox()));
            pairs.add(new TestPair(NapkinCheckBoxUI.class, new JCheckBox(
                    icon)));
            pairs.add(new TestPair(NapkinCheckBoxUI.class, new JCheckBox(icon,
                    true)));
            pairs.add(new TestPair(NapkinCheckBoxUI.class, new JCheckBox(icon,
                    false)));
            pairs.add(new TestPair(NapkinCheckBoxUI.class, new JCheckBox(
                    text)));
            pairs.add(new TestPair(NapkinCheckBoxUI.class, new JCheckBox(text,
                    true)));
            pairs.add(new TestPair(NapkinCheckBoxUI.class, new JCheckBox(text,
                    false)));
            pairs.add(new TestPair(NapkinCheckBoxUI.class, new JCheckBox(text,
                    icon)));
            pairs.add(new TestPair(NapkinCheckBoxUI.class, new JCheckBox(text,
                    icon, true)));
            pairs.add(new TestPair(NapkinCheckBoxUI.class, new JCheckBox(text,
                    icon, false)));

            pairs.add(new TestPair(NapkinComboBoxUI.class, new JComboBox()));
            pairs.add(new TestPair(NapkinComboBoxUI.class, new JComboBox(
                    new TestComboBoxModel())));
            pairs.add(new TestPair(NapkinComboBoxUI.class, new JComboBox(
                    items)));
            pairs.add(new TestPair(NapkinComboBoxUI.class, new JComboBox(
                    items)));

            pairs.add(new TestPair(NapkinDesktopIconUI.class,
                    new JInternalFrame.JDesktopIcon(
                            new JInternalFrame())).skip());

            tmp = File.createTempFile("nlaf_", ".txt");
            tmp.deleteOnExit();
            tmp.createNewFile();
            URL url = tmp.toURL();
            pairs.add(new TestPair(NapkinEditorPaneUI.class,
                    new JEditorPane()));
            pairs.add(new TestPair(NapkinEditorPaneUI.class, new JEditorPane(
                    url)));
            pairs.add(new TestPair(NapkinEditorPaneUI.class, new JEditorPane(
                    url.toString())));
            pairs.add(new TestPair(NapkinEditorPaneUI.class, new JEditorPane(
                    "text/plain", text)));

            File currentDirectory = new File(".");
            String currentDirectoryPath = currentDirectory.getPath();
            pairs.add(new TestPair(NapkinFileChooserUI.class,
                    new JFileChooser()).skip());
            pairs.add(new TestPair(NapkinFileChooserUI.class, new JFileChooser(
                    currentDirectory)).skip());
            pairs.add(new TestPair(NapkinFileChooserUI.class, new JFileChooser(
                    currentDirectoryPath)).skip());
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        } finally {
            if (tmp != null) {
                tmp.delete();
            }
        }
    }

    public ComponentUITest(String testName) {
        super(testName);
    }

    private static ComponentUI getInstance(TestPair pair) {
        try {
            Method createUI = pair.uiClass.getMethod("createUI",
                    JComponent.class);
            return (ComponentUI) createUI.invoke(null, pair.component);
        } catch (Exception ex) {
            Error err = new AssertionError(
                    "createUI() contract is broken for " +
                            pair.uiClass.getSimpleName());
            err.initCause(ex);
            ex.printStackTrace();
            throw err;
        }
    }

    public void testCreateUI() {
        for (TestPair pair : pairs) {
            Class<? extends ComponentUI> clazz = getInstance(pair).getClass();
            assertSame(pair.uiClass.getSimpleName() +
                    ".createUI() does not return the correct NapkinUI!",
                    pair.uiClass, clazz);
        }
    }

    @SuppressWarnings({"ErrorNotRethrown"})
    private void checkInstallUI(TestPair pair, ComponentUI ui,
            Color oldBackground, Border oldBorder, boolean wasOpaque) {

        pair.component.setBackground(oldBackground);
        pair.component.setBorder(oldBorder);
        pair.component.setOpaque(wasOpaque);

        ui.installUI(pair.component);
        ui.uninstallUI(pair.component);

        Color newBackground = pair.component.getBackground();
        Border newBorder = pair.component.getBorder();
        boolean isOpaque = pair.component.isOpaque();

        String uiClass = pair.uiClass.getSimpleName();
        String argDesc = argDesc(oldBackground, oldBorder, wasOpaque);
        String result = "passes";
        try {
            assertEquals(uiClass +
                    " does not restore background colour properly" + argDesc,
                    oldBackground, newBackground);
            if (newBorder != null) {
                assertEquals(
                        uiClass + " does not restore border properly" + argDesc,
                        oldBorder, newBorder);
            }
            assertEquals(
                    uiClass + " does not restore opaqueness properly" + argDesc,
                    wasOpaque, isOpaque);
        } catch (AssertionFailedError e) {
            if (!pair.skip) {
                throw e;
            }
            result = "failed (skipped)";
            System.out.println(e.getMessage());
        }
        System.out.println(showClass(uiClass) + " " + result + " for " +
                argDesc);
    }

    private String showClass(String type) {
        if (lastType == null || !type.equals(lastType)) {
            lastType = type;
            blankType = null;
            return type;
        } else {
            if (blankType == null) {
                StringBuilder sb = new StringBuilder(type.length());
                for (int i = 0; i < type.length(); i++) {
                    sb.append(' ');
                }
                blankType = sb.toString();
            }
            return blankType;
        }
    }

    private static String argDesc(Color oldBackground, Border oldBorder,
            boolean wasOpaque) {

        return "(" + NapkinDebug.toString(oldBackground) + ", " +
                NapkinDebug.toString(oldBorder) + ", " + wasOpaque + ")";
    }

    @SuppressWarnings({"JUnitTestMethodWithNoAssertions"})
    public void testInstallUI() {
        Color[] bgColors = {Color.WHITE, Color.BLUE, Color.PINK};
        Border[] borders = {new EmptyBorder(1, 1, 1, 1)};
        for (TestPair pair : pairs) {
            ComponentUI ui = getInstance(pair);
            checkInstallUI(pair, ui, pair.component.getBackground(),
                    pair.component.getBorder(), pair.component.isOpaque());
            for (Color bgColor : bgColors) {
                for (Border border : borders) {
                    checkInstallUI(pair, ui, bgColor, border, true);
                    checkInstallUI(pair, ui, bgColor, border, false);
                }
            }
        }
    }
}
