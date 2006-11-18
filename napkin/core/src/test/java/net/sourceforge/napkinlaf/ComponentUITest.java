/*
 * ComponentUITest.java
 * JUnit based test
 *
 * Created on 13 April 2006, 22:23
 */

package net.sourceforge.napkinlaf;

import java.awt.Color;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import javax.swing.ComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListDataListener;
import javax.swing.plaf.ComponentUI;

import junit.framework.TestCase;
import net.sourceforge.napkinlaf.util.NapkinIconFactory;

/** @author Alex Lam Sze Lok */
public class ComponentUITest extends TestCase {

    private static class TestPair {
        final Class<? extends ComponentUI> uiClass;
        final JComponent component;

        public TestPair
                (Class<? extends ComponentUI> uiClass, JComponent component) {
            this.uiClass = uiClass;
            this.component = component;
        }
    }

    private static final List<TestPair> pairList = new ArrayList<TestPair>();

    static {
        Icon icon = NapkinIconFactory.createXIcon(20);
        String text = "testing";
        Color color = new Color(new Random().nextInt());
        Object[] items = {text, icon, color};
        Vector<Object> vector = new Vector<Object>(Arrays.asList(items));

//        pairList.add(new TestPair(NapkinDesktopPaneUI.class,
//                new JDesktopPane()));

        pairList.add(new TestPair(NapkinButtonUI.class, new JButton()));
        pairList.add(new TestPair(NapkinButtonUI.class, new JButton(text)));
        pairList.add(new TestPair(NapkinButtonUI.class, new JButton(icon)));
        pairList.add(new TestPair(NapkinButtonUI.class, new JButton(text,
                icon)));

        pairList.add(new TestPair(NapkinCheckBoxMenuItemUI.class,
                new JCheckBoxMenuItem()));
        pairList.add(new TestPair(NapkinCheckBoxMenuItemUI.class,
                new JCheckBoxMenuItem(icon)));
        pairList.add(new TestPair(NapkinCheckBoxMenuItemUI.class,
                new JCheckBoxMenuItem(text)));
        pairList.add(new TestPair(NapkinCheckBoxMenuItemUI.class,
                new JCheckBoxMenuItem(text, true)));
        pairList.add(new TestPair(NapkinCheckBoxMenuItemUI.class,
                new JCheckBoxMenuItem(text, false)));
        pairList.add(new TestPair(NapkinCheckBoxMenuItemUI.class,
                new JCheckBoxMenuItem(text, icon)));
        pairList.add(new TestPair(NapkinCheckBoxMenuItemUI.class,
                new JCheckBoxMenuItem(text, icon, true)));
        pairList.add(new TestPair(NapkinCheckBoxMenuItemUI.class,
                new JCheckBoxMenuItem(text, icon, false)));

        pairList.add(new TestPair(NapkinCheckBoxUI.class, new JCheckBox()));
        pairList.add(new TestPair(NapkinCheckBoxUI.class, new JCheckBox(icon)));
        pairList.add(new TestPair(NapkinCheckBoxUI.class, new JCheckBox(icon,
                true)));
        pairList.add(new TestPair(NapkinCheckBoxUI.class, new JCheckBox(icon,
                false)));
        pairList.add(new TestPair(NapkinCheckBoxUI.class, new JCheckBox(text)));
        pairList.add(new TestPair(NapkinCheckBoxUI.class, new JCheckBox(text,
                true)));
        pairList.add(new TestPair(NapkinCheckBoxUI.class, new JCheckBox(text,
                false)));
        pairList.add(new TestPair(NapkinCheckBoxUI.class, new JCheckBox(text,
                icon)));
        pairList.add(new TestPair(NapkinCheckBoxUI.class, new JCheckBox(text,
                icon, true)));
        pairList.add(new TestPair(NapkinCheckBoxUI.class, new JCheckBox(text,
                icon, false)));

//        pairList.add(new TestPair(NapkinColorChooserUI.class, new JColorChooser()));
//        pairList.add(new TestPair(NapkinColorChooserUI.class, new JColorChooser(color)));
//        pairList.add(new TestPair(NapkinColorChooserUI.class, new JColorChooser(new ColorSelectionModel() {
//            private Color selColor = Color.WHITE;
//            public void addChangeListener(ChangeListener listener) {
//            }
//            public Color getSelectedColor() {
//                return selColor;
//            }
//            public void removeChangeListener(ChangeListener listener) {
//            }
//            public void setSelectedColor(Color color) {
//                selColor = color;
//            }
//        })));

        pairList.add(new TestPair(NapkinComboBoxUI.class, new JComboBox()));
        pairList.add(new TestPair(NapkinComboBoxUI.class, new JComboBox(
                new ComboBoxModel() {
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
                })));
        pairList.add(new TestPair(NapkinComboBoxUI.class, new JComboBox(
                items)));
        pairList.add(new TestPair(NapkinComboBoxUI.class, new JComboBox(
                vector)));

//        pairList.add(new TestPair(NapkinDesktopIconUI.class, new JInternalFrame.JDesktopIcon(internalFrame)));

        pairList.add(new TestPair(NapkinEditorPaneUI.class, new JEditorPane()));
//        pairList.add(new TestPair(NapkinEditorPaneUI.class, new JEditorPane(url)));
//        pairList.add(new TestPair(NapkinEditorPaneUI.class, new JEditorPane(initialPage)));
//        pairList.add(new TestPair(NapkinEditorPaneUI.class, new JEditorPane("text/plain", text)));

//        pairList.add(new TestPair(NapkinFileChooserUI.class, new JFileChooser()));
//        pairList.add(new TestPair(NapkinFileChooserUI.class, new JFileChooser(currentDirectory)));
//        pairList.add(new TestPair(NapkinFileChooserUI.class, new JFileChooser(currentDirectory, fsv)));
//        pairList.add(new TestPair(NapkinFileChooserUI.class, new JFileChooser(fsv)));
//        pairList.add(new TestPair(NapkinFileChooserUI.class, new JFileChooser(currentDirectoryPath)));
//        pairList.add(new TestPair(NapkinFileChooserUI.class, new JFileChooser(currentDirectoryPath, fsv)));
    }

    public ComponentUITest(String testName) {
        super(testName);
    }

    private ComponentUI getInstance(TestPair pair) {
        try {
            Method createUI = pair.uiClass
                    .getMethod("createUI", JComponent.class);
            return (ComponentUI) createUI.invoke(null, pair.component);
        } catch (Exception ex) {
            Error err = new AssertionError("createUI() contract is broken for "
                    + pair.uiClass.getCanonicalName());
            err.initCause(ex);
            ex.printStackTrace();
            throw err;
        }
    }

    public void testCreateUI() {
        for (TestPair pair : pairList) {
            Class<? extends ComponentUI> clazz = getInstance(pair).getClass();
            assertSame(pair.uiClass.getCanonicalName() +
                    ".createUI() does not return the correct NapkinUI!",
                    clazz, pair.uiClass);
        }
    }

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

        String uiClass = pair.uiClass.getCanonicalName();
        String argDesc = argDesc(oldBackground, oldBorder, wasOpaque);
        assertEquals(uiClass + " does not restore background colour properly" +
                argDesc, oldBackground, newBackground);
        if (newBorder != null) {
            assertEquals(uiClass +
                    " does not restore border properly" + argDesc, oldBorder,
                    newBorder);
        }
        assertEquals(uiClass + " does not restore opaqueness properly" +
                argDesc, wasOpaque, isOpaque);
        System.out.println(uiClass + " passes for " + argDesc);
    }

    private String argDesc(Color oldBackground, Border oldBorder,
            boolean wasOpaque) {

        return "(" + oldBackground + ", " + oldBorder + ", " + wasOpaque + ")";
    }

    public void testInstallUI() {
        Color[] bgColors = new Color[]{Color.WHITE, Color.BLUE, Color.PINK};
        Border[] borders = new Border[]{new EmptyBorder(1, 1, 1, 1)};
        for (TestPair pair : pairList) {
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
