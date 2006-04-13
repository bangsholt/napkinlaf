/*
 * ComponentUITest.java
 * JUnit based test
 *
 * Created on 13 April 2006, 22:23
 */

package net.sourceforge.napkinlaf;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
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
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    // public void testHello() {}
    public void testCreateUI() {
        for (TestPair pair : pairList) {
            try {
                Method createUI = pair.uiClass
                        .getMethod("createUI", JComponent.class);
                Class clazz = createUI.invoke(null, pair.component).getClass();
                assertSame(pair.uiClass.getCanonicalName() +
                        ".createUI() does not return the correct NapkinUI!",
                        clazz, pair.uiClass);
            } catch (Exception ex) {
                Error err =
                        new AssertionError("createUI() contract is broken for "
                        + pair.uiClass.getCanonicalName());
                err.initCause(ex);
                ex.printStackTrace();
                throw err;
            }
        }
    }
}
