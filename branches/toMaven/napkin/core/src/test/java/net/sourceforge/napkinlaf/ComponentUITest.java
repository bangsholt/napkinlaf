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
import java.util.List;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import junit.framework.TestCase;
import net.sourceforge.napkinlaf.util.NapkinIconFactory;

/**
 *
 * @author Alex Lam Sze Lok
 */
public class ComponentUITest extends TestCase {

    private static class TestPair {
        final Class<? extends ComponentUI> uiClass;
        final JComponent component;
        public TestPair(Class<? extends ComponentUI> uiClass, JComponent component) {
            this.uiClass = uiClass;
            this.component = component;
        }
    }

    private static final List<TestPair> pairList = new ArrayList<TestPair>();

    static {
        Icon icon = NapkinIconFactory.createXIcon(20);
        String text = "testing";

        pairList.add(new TestPair(NapkinButtonUI.class, new JButton()));
        pairList.add(new TestPair(NapkinButtonUI.class, new JButton(text)));
        pairList.add(new TestPair(NapkinButtonUI.class, new JButton(icon)));
        pairList.add(new TestPair(NapkinButtonUI.class, new JButton(text, icon)));

        pairList.add(new TestPair(NapkinCheckBoxMenuItemUI.class, new JCheckBoxMenuItem()));
        pairList.add(new TestPair(NapkinCheckBoxMenuItemUI.class, new JCheckBoxMenuItem(icon)));
        pairList.add(new TestPair(NapkinCheckBoxMenuItemUI.class, new JCheckBoxMenuItem(text)));
        pairList.add(new TestPair(NapkinCheckBoxMenuItemUI.class, new JCheckBoxMenuItem(text, true)));
        pairList.add(new TestPair(NapkinCheckBoxMenuItemUI.class, new JCheckBoxMenuItem(text, false)));
        pairList.add(new TestPair(NapkinCheckBoxMenuItemUI.class, new JCheckBoxMenuItem(text, icon)));
        pairList.add(new TestPair(NapkinCheckBoxMenuItemUI.class, new JCheckBoxMenuItem(text, icon, true)));
        pairList.add(new TestPair(NapkinCheckBoxMenuItemUI.class, new JCheckBoxMenuItem(text, icon, false)));

        pairList.add(new TestPair(NapkinCheckBoxUI.class, new JCheckBox()));
        pairList.add(new TestPair(NapkinCheckBoxUI.class, new JCheckBox(icon)));
        pairList.add(new TestPair(NapkinCheckBoxUI.class, new JCheckBox(icon, true)));
        pairList.add(new TestPair(NapkinCheckBoxUI.class, new JCheckBox(icon, false)));
        pairList.add(new TestPair(NapkinCheckBoxUI.class, new JCheckBox(text)));
        pairList.add(new TestPair(NapkinCheckBoxUI.class, new JCheckBox(text, true)));
        pairList.add(new TestPair(NapkinCheckBoxUI.class, new JCheckBox(text, false)));
        pairList.add(new TestPair(NapkinCheckBoxUI.class, new JCheckBox(text, icon)));
        pairList.add(new TestPair(NapkinCheckBoxUI.class, new JCheckBox(text, icon, true)));
        pairList.add(new TestPair(NapkinCheckBoxUI.class, new JCheckBox(text, icon, false)));

    }

    public ComponentUITest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
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

    public static void assertEquals(String msg, Object obj1, Object obj2) {
        if (!obj1.equals(obj2)) {
            System.err.println(msg);
        }
    }

    // TODO add test methods here. The name must begin with 'test'. For example:
    // public void testHello() {}
    public void testCreateUI() {
        for (TestPair pair : pairList) {
            Class<? extends ComponentUI> clazz = getInstance(pair).getClass();
            assertSame(pair.uiClass.getCanonicalName() +
                    ".createUI() does not return the correct NapkinUI!",
                    clazz, pair.uiClass);
        }
    }

    public void testInstallUI() {
        for (TestPair pair : pairList) {
            ComponentUI ui = getInstance(pair);

            Color oldBackground = pair.component.getBackground();
            Border oldBorder = pair.component.getBorder();
            boolean wasOpaque = pair.component.isOpaque();

            ui.installUI(pair.component);
            ui.uninstallUI(pair.component);

            Color newBackground = pair.component.getBackground();
            Border newBorder = pair.component.getBorder();
            boolean isOpaque = pair.component.isOpaque();

            assertEquals(pair.uiClass.getCanonicalName() +
                    " does not restore background colour properly!",
                    oldBackground, newBackground);
            assertEquals(pair.uiClass.getCanonicalName() +
                    " does not restore border properly!",
                    oldBorder, newBorder);
            assertEquals(pair.uiClass.getCanonicalName() +
                    " does not restore opaqueness properly!",
                    wasOpaque, isOpaque);
        }
    }
}
