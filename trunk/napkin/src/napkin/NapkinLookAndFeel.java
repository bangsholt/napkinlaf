// $Id$

package napkin;

import java.awt.*;
import java.awt.event.*;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import javax.swing.*;
import javax.swing.UIDefaults.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.text.*;

import napkin.ComponentWalker.Visitor;

public class NapkinLookAndFeel extends BasicLookAndFeel
        implements NapkinConstants {

    private LookAndFeel formalLAF;
    private final Map flags = new WeakHashMap();

    private final Visitor clearKidsVisitor = new Visitor() {
        public boolean visit(Component c, int depth) {
            FormalityFlags ff = flags(c);
            System.out.println(
                    "clearKidsVisitor " + NapkinUtil.descFor(c) + ": " + ff);

            if (depth == 0) {
                System.out.println("    depth == 0, return true");
                return true;
            }

            ff.known = !ff.inherited;
            System.out.println("    return " + ff.inherited);
            return ff.inherited;
        }
    };

    private final Visitor updateUIVisitor = new Visitor() {
        public boolean visit(Component c, int depth) {
            System.out.println("updateUIVisitor " + NapkinUtil.descFor(c));
            FormalityFlags ff = flags(c, false);
            if (depth > 0 && !ff.inherited)
                return false;
            if (c instanceof JComponent) {
                System.out.println("    updateUI");
                ((JComponent) c).updateUI();
            }
            return true;
        }
    };

    private final Visitor addListenerVisitor = new Visitor() {
        public boolean visit(Component c, int depth) {
            System.out.println("addListenerVisitor " + NapkinUtil.descFor(c));
            if (!(c instanceof Container) || flags.containsKey(c))
                return false;

            FormalityFlags ff = flags(c, false);
            ((Container) c).addContainerListener(ff);
            System.out.println("adding listener for " + NapkinUtil.descFor(c) +
                    ", " +
                    System.identityHashCode(c));
            return true;
        }
    };

    private static boolean JUST_NAPKIN = true;

    private static final String[] UI_TYPES = {
        "ButtonUI",
        "CheckBoxMenuItemUI",
        "CheckBoxUI",
        "ColorChooserUI",
        "ComboBoxUI",
        "DesktopIconUI",
        "DesktopPaneUI",
        "EditorPaneUI",
        "FileChooserUI",
        "FormattedTextFieldUI",
        "InternalFrameUI",
        "LabelUI",
        "ListUI",
        "MenuBarUI",
        "MenuItemUI",
        "MenuUI",
        "OptionPaneUI",
        "PanelUI",
        "PasswordFieldUI",
        "PopupMenuSeparatorUI",
        "PopupMenuUI",
        "ProgressBarUI",
        "RadioButtonMenuItemUI",
        "RadioButtonUI",
        "RootPaneUI",
        "ScrollBarUI",
        "ScrollPaneUI",
        "SeparatorUI",
        "SliderUI",
        "SpinnerUI",
        "SplitPaneUI",
        "TabbedPaneUI",
        "TableHeaderUI",
        "TableUI",
        "TextAreaUI",
        "TextFieldUI",
        "TextPaneUI",
        "ToggleButtonUI",
        "ToolBarSeparatorUI",
        "ToolBarUI",
        "ToolTipUI",
        "TreeUI",
        "ViewportUI",
    };

    class DumpVisitor implements Visitor {
        private final PrintStream out;

        DumpVisitor(PrintStream out) {
            this.out = out;
        }

        public boolean visit(Component c, int depth) {
            FormalityFlags ff = flags(c);
            for (int i = 0; i < depth; i++) {
                out.print(i % 2 == 0 ? '|' : '.');
                out.print(' ');
            }
            String desc = NapkinUtil.descFor(c);
            out.print(desc);
            out.print(": ");
            out.print(ff);
            out.print(", " + c.isOpaque());
            out.println();
            return true;
        }
    };

    private class FormalityFlags implements ContainerListener {
        boolean known;
        boolean formal;
        boolean inherited = true;

        public void componentAdded(ContainerEvent e) {
            Component child = e.getChild();
            System.out.println("Added: " + NapkinUtil.descFor(child) + " to " +
                    e.getComponent());
            clear(child);
        }

        public void componentRemoved(ContainerEvent e) {
            Component child = e.getChild();
            System.out.println("Removed: " + NapkinUtil.descFor(child) +
                    " to " +
                    e.getComponent());
            clear(child);
        }

        public String toString() {
            if (!known)
                return "???";
            String desc = (formal ? "formal" : "napkin");
            if (!inherited)
                desc = desc.toUpperCase();
            return desc;
        }

        private void setFrom(FormalityFlags parentFlags) {
            if (parentFlags != null && parentFlags.known && inherited) {
                known = true;
                formal = parentFlags.formal;
            }
        }
    }

    private NapkinLookAndFeel(LookAndFeel formal) {
        setFormalLAF(formal);
    }

    public NapkinLookAndFeel() {
        this(UIManager.getLookAndFeel());
    }

    public String getDescription() {
        String desc = "The Napkin Look and Feel";
        if (formalLAF != null)
            desc += " [backed by " + formalLAF.getDescription() + "]";
        return desc;
    }

    public String getID() {
        String desc = "Napkin";
        if (formalLAF != null)
            desc += "[" + formalLAF.getID() + "]";
        return desc;
    }

    public String getName() {
        return getID();
    }

    public boolean isNativeLookAndFeel() {
        return false;
    }

    public boolean isSupportedLookAndFeel() {
        return true;
    }

    public LookAndFeel getFormalLAF() {
        return formalLAF;
    }

    private void setFormalLAF(LookAndFeel formalLAF) {
        if (!JUST_NAPKIN)
            this.formalLAF = formalLAF;
    }

    public void provideErrorFeedback(Component component) {
        // nothing special needed here -- not a formal/informal thing
        if (formalLAF != null)
            formalLAF.provideErrorFeedback(component);
        else
            super.provideErrorFeedback(component);
    }

    public void initialize() {
        if (formalLAF != null)
            formalLAF.initialize();
    }

    public void uninitialize() {
        if (formalLAF != null)
            formalLAF.uninitialize();
    }

    public boolean isFormal(Component c) {
        if (JUST_NAPKIN)
            return false;

        FormalityFlags ff = flags(c);
        System.out.println("isFormal(" + NapkinUtil.descFor(c) + "): " + ff);
        if (ff.known)
            return ff.formal;

        FormalityFlags pff = inheritedFormal(c.getParent());
        ff.setFrom(pff);
        return ff.formal;
    }

    private FormalityFlags inheritedFormal(Container container) {
        System.out.println(
                "inheritedFormal(" + NapkinUtil.descFor(container) + ")");
        if (container == null)
            return null;
        FormalityFlags ff = flags(container);
        System.out.println("    flags = " + ff);
        if (ff.known)
            return ff;
        else {
            FormalityFlags pff = inheritedFormal(container.getParent());
            ff.setFrom(pff);
            return pff;
        }
    }

    protected void initClassDefaults(UIDefaults table) {
        super.initClassDefaults(table);
        final String basicPackageName =
                NapkinLookAndFeel.class.getPackage().getName() + ".Napkin";
        for (int i = 0; i < UI_TYPES.length; i++) {
            String uiType = UI_TYPES[i];
            String uiClass = basicPackageName + uiType;
            table.put(uiType, uiClass);
        }
        Set keys = new HashSet(table.keySet());
        keys.removeAll(Arrays.asList(UI_TYPES));
        if (keys.size() != 0)
            System.out.println("keys we didn't overwrite: " + keys);
    }

    protected void initSystemColorDefaults(UIDefaults table) {
        super.initSystemColorDefaults(table);
        // make a copy so we can modify the table as we read the key set
        NapkinTheme theme = NapkinTheme.Manager.getCurrentTheme();
        table.put("textHighlight", theme.getHighlightColor());
    }

    protected void initComponentDefaults(UIDefaults table) {
        super.initComponentDefaults(table);

        overrideComponentDefaults(table);

        Integer zero = new Integer(0);
        Object checkBoxButtonIcon = new UIDefaults.ActiveValue() {
            public Object createValue(UIDefaults table) {
                return NapkinIconFactory.createCheckBoxIcon();
            }
        };
        Object radioButtonIcon = new UIDefaults.ActiveValue() {
            public Object createValue(UIDefaults table) {
                return NapkinIconFactory.createRadioButtonIcon();
            }
        };

        Object underlineBorder = new UIDefaults.ActiveValue() {
            public Object createValue(UIDefaults table) {
                return new NapkinLineBorder(false);
            }
        };
        Object selectBorder = new UIDefaults.ActiveValue() {
            public Object createValue(UIDefaults table) {
                return new NapkinSelectedBorder();
            }
        };

        Object treeOpenIcon = new UIDefaults.ActiveValue() {
            public Object createValue(UIDefaults table) {
                return NapkinIconFactory.createArrowIcon(SOUTH, 8);
            }
        };
        Object treeClosedIcon = new UIDefaults.ActiveValue() {
            public Object createValue(UIDefaults table) {
                return NapkinIconFactory.createArrowIcon(EAST, 8);
            }
        };

        Object closeIcon = new UIDefaults.ActiveValue() {
            public Object createValue(UIDefaults table) {
                return NapkinIconFactory.createXIcon(10);
            }
        };
        Object minIcon = new UIDefaults.ActiveValue() {
            public Object createValue(UIDefaults table) {
                return NapkinIconFactory.createArrowIcon(SOUTH, 10);
            }
        };

        setupActions(table);

        Object[] napkinDefaults = {
            "RadioButton.textIconGap", zero,
            "RadioButton.icon", radioButtonIcon,
            "RadioButtonMenuItem.textIconGap", zero,
            "RadioButtonMenuItem.checkIcon", radioButtonIcon,

            "CheckBox.textIconGap", zero,
            "CheckBox.icon", checkBoxButtonIcon,
            "CheckBoxMenuItem.textIconGap", zero,
            "CheckBoxMenuItem.checkIcon", checkBoxButtonIcon,

            "OptionPane.messageAreaBorder", null,

            "TabbedPane.contentBorderInsets", NapkinBoxBorder.DEFAULT_INSETS,

            "Tree.openIcon", treeOpenIcon,
            "Tree.closedIcon", treeClosedIcon,
            "Tree.leafIcon", null,
            "Tree.selectionBorderColor", null,

            "TextField.border", underlineBorder,
            "PasswordField.border", underlineBorder,

            "Menu.border", null,
            "PopupMenu.border", null,
            "ToolTip.border", null,
            "DesktopIcon.border", null,
            "ToggleButton.border", selectBorder,
            "InternalFrame.border", new BorderUIResource(
                    new EmptyBorder(3, 3, 3, 3)),

            "InternalFrame.maximizeIcon", null,
            "InternalFrame.minimizeIcon", null,
            "InternalFrame.iconifyIcon", minIcon,
            "InternalFrame.closeIcon", closeIcon,

            "SplitPaneDivider.border", null,
            "SplitPane.dividerSize", new Integer(NapkinSplitPaneDivider.SIZE),
        };

        table.putDefaults(napkinDefaults);
    }

    private static void setupActions(UIDefaults table) {
        //!! These are copied from Metal LookAndFeel, but we should get them
        //!! From the formal L&F, as well as getting *all* behavior.  -arnold
        Object fieldInputMap = new UIDefaults.LazyInputMap(new Object[]{
            "ctrl C", DefaultEditorKit.copyAction,
            "ctrl V", DefaultEditorKit.pasteAction,
            "ctrl X", DefaultEditorKit.cutAction,
            "COPY", DefaultEditorKit.copyAction,
            "PASTE", DefaultEditorKit.pasteAction,
            "CUT", DefaultEditorKit.cutAction,
            "shift LEFT", DefaultEditorKit.selectionBackwardAction,
            "shift KP_LEFT", DefaultEditorKit.selectionBackwardAction,
            "shift RIGHT", DefaultEditorKit.selectionForwardAction,
            "shift KP_RIGHT", DefaultEditorKit.selectionForwardAction,
            "ctrl LEFT", DefaultEditorKit.previousWordAction,
            "ctrl KP_LEFT", DefaultEditorKit.previousWordAction,
            "ctrl RIGHT", DefaultEditorKit.nextWordAction,
            "ctrl KP_RIGHT", DefaultEditorKit.nextWordAction,
            "ctrl shift LEFT", DefaultEditorKit.selectionPreviousWordAction,
            "ctrl shift KP_LEFT", DefaultEditorKit.selectionPreviousWordAction,
            "ctrl shift RIGHT", DefaultEditorKit.selectionNextWordAction,
            "ctrl shift KP_RIGHT", DefaultEditorKit.selectionNextWordAction,
            "ctrl A", DefaultEditorKit.selectAllAction,
            "HOME", DefaultEditorKit.beginLineAction,
            "END", DefaultEditorKit.endLineAction,
            "shift HOME", DefaultEditorKit.selectionBeginLineAction,
            "shift END", DefaultEditorKit.selectionEndLineAction,
            "typed \010", DefaultEditorKit.deletePrevCharAction,
            "DELETE", DefaultEditorKit.deleteNextCharAction,
            "RIGHT", DefaultEditorKit.forwardAction,
            "LEFT", DefaultEditorKit.backwardAction,
            "KP_RIGHT", DefaultEditorKit.forwardAction,
            "KP_LEFT", DefaultEditorKit.backwardAction,
            "ENTER", JTextField.notifyAction,
            "ctrl BACK_SLASH", "unselect"/*DefaultEditorKit.unselectAction*/,
            "control shift O", "toggle-componentOrientation"/*DefaultEditorKit.toggleComponentOrientation*/
        });

        Object multilineInputMap = new UIDefaults.LazyInputMap(new Object[]{
            "ctrl C", DefaultEditorKit.copyAction,
            "ctrl V", DefaultEditorKit.pasteAction,
            "ctrl X", DefaultEditorKit.cutAction,
            "COPY", DefaultEditorKit.copyAction,
            "PASTE", DefaultEditorKit.pasteAction,
            "CUT", DefaultEditorKit.cutAction,
            "shift LEFT", DefaultEditorKit.selectionBackwardAction,
            "shift KP_LEFT", DefaultEditorKit.selectionBackwardAction,
            "shift RIGHT", DefaultEditorKit.selectionForwardAction,
            "shift KP_RIGHT", DefaultEditorKit.selectionForwardAction,
            "ctrl LEFT", DefaultEditorKit.previousWordAction,
            "ctrl KP_LEFT", DefaultEditorKit.previousWordAction,
            "ctrl RIGHT", DefaultEditorKit.nextWordAction,
            "ctrl KP_RIGHT", DefaultEditorKit.nextWordAction,
            "ctrl shift LEFT", DefaultEditorKit.selectionPreviousWordAction,
            "ctrl shift KP_LEFT", DefaultEditorKit.selectionPreviousWordAction,
            "ctrl shift RIGHT", DefaultEditorKit.selectionNextWordAction,
            "ctrl shift KP_RIGHT", DefaultEditorKit.selectionNextWordAction,
            "ctrl A", DefaultEditorKit.selectAllAction,
            "HOME", DefaultEditorKit.beginLineAction,
            "END", DefaultEditorKit.endLineAction,
            "shift HOME", DefaultEditorKit.selectionBeginLineAction,
            "shift END", DefaultEditorKit.selectionEndLineAction,

            "UP", DefaultEditorKit.upAction,
            "KP_UP", DefaultEditorKit.upAction,
            "DOWN", DefaultEditorKit.downAction,
            "KP_DOWN", DefaultEditorKit.downAction,
            "PAGE_UP", DefaultEditorKit.pageUpAction,
            "PAGE_DOWN", DefaultEditorKit.pageDownAction,
            "shift PAGE_UP", "selection-page-up",
            "shift PAGE_DOWN", "selection-page-down",
            "ctrl shift PAGE_UP", "selection-page-left",
            "ctrl shift PAGE_DOWN", "selection-page-right",
            "shift UP", DefaultEditorKit.selectionUpAction,
            "shift KP_UP", DefaultEditorKit.selectionUpAction,
            "shift DOWN", DefaultEditorKit.selectionDownAction,
            "shift KP_DOWN", DefaultEditorKit.selectionDownAction,
            "ENTER", DefaultEditorKit.insertBreakAction,
            "typed \010", DefaultEditorKit.deletePrevCharAction,
            "DELETE", DefaultEditorKit.deleteNextCharAction,
            "RIGHT", DefaultEditorKit.forwardAction,
            "LEFT", DefaultEditorKit.backwardAction,
            "KP_RIGHT", DefaultEditorKit.forwardAction,
            "KP_LEFT", DefaultEditorKit.backwardAction,
            "TAB", DefaultEditorKit.insertTabAction,
            "ctrl BACK_SLASH", "unselect"/*DefaultEditorKit.unselectAction*/,
            "ctrl HOME", DefaultEditorKit.beginAction,
            "ctrl END", DefaultEditorKit.endAction,
            "ctrl shift HOME", DefaultEditorKit.selectionBeginAction,
            "ctrl shift END", DefaultEditorKit.selectionEndAction,
            "ctrl T", "next-link-action",
            "ctrl shift T", "previous-link-action",
            "ctrl SPACE", "activate-link-action",
            "control shift O", "toggle-componentOrientation"/*DefaultEditorKit.toggleComponentOrientation*/
        });

        Object[] actionDefaults = {
            // these are just copied from Metal L&F -- no values in Basic L&F
            //!! Should get input maps from the formal L&F for all map defaults
            "TextField.focusInputMap", fieldInputMap,
            "PasswordField.focusInputMap", fieldInputMap,
            "TextArea.focusInputMap", multilineInputMap,
            "TextPane.focusInputMap", multilineInputMap,
            "EditorPane.focusInputMap", multilineInputMap,
        };

        table.putDefaults(actionDefaults);
    }

    private static void overrideComponentDefaults(UIDefaults table) {
        NapkinTheme theme = NapkinTheme.Manager.getCurrentTheme();

        Font dialogPlain = theme.getTextFont();
        Font dialogBold = theme.getBoldTextFont();
        Font serifPlain = theme.getTextFont();
        Font sansSerifPlain = theme.getTextFont();
        Font monospacedPlain = theme.getFixedFont();

        Object drawnBorder = new UIDefaults.ActiveValue() {
            public Object createValue(UIDefaults table) {
                return new NapkinBoxBorder();
            }
        };
        Object compoundBorder = new UIDefaults.ActiveValue() {
            public Object createValue(UIDefaults table) {
                NapkinBoxBorder outside = new NapkinBoxBorder();
                BasicBorders.MarginBorder inside = new BasicBorders.MarginBorder();
                CompoundBorder compound = new CompoundBorder(outside, inside);
                return compound;
            }
        };

        Color clear = new AlphaColorUIResource(CLEAR);

        for (Iterator it = table.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            Object val = entry.getValue();
            Object res;
            if ((res = propVal(key, "font", val, table)) != null) {
                if (res instanceof FontUIResource) {
                    FontUIResource resource = (FontUIResource) res;
                    String name = resource.getFontName();
                    if (name.equals("Dialog.plain")) {
                        entry.setValue(dialogPlain);
                    } else if (name.equals("Dialog.bold")) {
                        entry.setValue(dialogBold);
                    } else if (name.equals("Serif.plain")) {
                        entry.setValue(serifPlain);
                    } else if (name.equals("SansSerif.plain")) {
                        entry.setValue(sansSerifPlain);
                    } else if (name.equals("MonoSpaced.plain")) {
                        entry.setValue(monospacedPlain);
                    } else {
                        System.err.println(
                                "unknown font; " + name + " for " + key);
                    }
                }
            } else if ((res = propVal(key, "border", val, table)) != null) {
                if (res instanceof UIResource || (val instanceof UIResource && (
                        res instanceof BevelBorder ||
                        res instanceof EtchedBorder ||
                        res instanceof LineBorder ||
                        res instanceof CompoundBorder))
                ) {
                    // we override manually later
                    if (!(res instanceof CompoundBorder))
                        entry.setValue(drawnBorder);
                    else
                        entry.setValue(compoundBorder);
                }
            } else {
                // We set things up right for these ones manually
                if (key.indexOf("Text") >= 0 || key.startsWith("Password") ||
                        key.startsWith("Editor"))
                    continue;

                if (key.endsWith(".foreground") ||
                        key.endsWith("BorderColor") ||
                        key.endsWith(".textForeground")) {
                    entry.setValue(theme.getPenColor());
                } else if (key.endsWith(".background") ||
                        key.endsWith(".selectionBackground") ||
                        key.endsWith(".textBackground")) {
                    entry.setValue(clear);
                } else if (key.endsWith(".selectionForeground")) {
                    entry.setValue(theme.getSelectionColor());
                }
            }
        }
    }

    private static Object
            propVal(String key, String prop, Object val, UIDefaults table) {

        int keyLen = key.length();
        int propLen = prop.length();
        int prePos = keyLen - propLen - 1;
        if (prePos <= 0)
            return null;

        boolean match = false;
        if (key.endsWith(prop) && key.charAt(prePos) == '.')
            match = true;
        else if (key.endsWith(prop.substring(1)) &&
                key.charAt(prePos + 1) ==
                Character.toUpperCase(prop.charAt(0)))
            match = true;

        if (!match)
            return null;

        if (val instanceof ProxyLazyValue) {
            val = ((ProxyLazyValue) val).createValue(table);
        } else if (val instanceof ActiveValue) {
            val = ((ActiveValue) val).createValue(table);
        }
        return val;
    }

    public void setIsFormal(Component c, boolean isFormal) {
        if (JUST_NAPKIN)
            return;
        FormalityFlags ff = flags(c);
        ff.known = true;
        ff.formal = isFormal;
        ff.inherited = false;
        clearKids(c);
    }

    private void clear(Component c) {
        System.out.println("clear(" + NapkinUtil.descFor(c) + ")");
        clearKids(c);
        FormalityFlags ff = flags(c);
        ff.known = !ff.inherited;
    }

    private void clearKids(Component c) {
        new ComponentWalker(c, clearKidsVisitor);
        new ComponentWalker(c, updateUIVisitor);
    }

    private FormalityFlags flags(Component c) {
        return flags(c, true);
    }

    private FormalityFlags flags(Component c, boolean recurse) {
        if (JUST_NAPKIN)
            return null;
        FormalityFlags ff = (FormalityFlags) flags.get(c);
        if (ff == null) {
            System.out.println("adding flags: " + NapkinUtil.descFor(c));
            if (recurse && c instanceof Container) {
                new ComponentWalker(c, addListenerVisitor);
                ff = (FormalityFlags) flags.get(c);
            } else {
                ff = new FormalityFlags();
                flags.put(c, ff);
            }
        }
        return ff;
    }

    public void dumpFormality(Component top, PrintStream out) {
        new ComponentWalker(top, new DumpVisitor(out));
    }
}

