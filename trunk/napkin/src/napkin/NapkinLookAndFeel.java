// $Id$

package napkin;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.WeakHashMap;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIDefaults.ActiveValue;
import javax.swing.UIDefaults.LazyValue;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.basic.BasicLookAndFeel;
import javax.swing.text.DefaultEditorKit;
import napkin.borders.NapkinBoxBorder;
import napkin.borders.NapkinLineBorder;
import napkin.borders.NapkinSelectedBorder;
import napkin.util.AlphaColorUIResource;
import napkin.util.ComponentWalker;
import napkin.util.ComponentWalker.Visitor;
import napkin.util.NapkinConstants;
import napkin.util.NapkinDebug;
import napkin.util.NapkinIconFactory;
import napkin.util.NapkinUtil;

public class NapkinLookAndFeel extends BasicLookAndFeel
        implements NapkinConstants {
    private LookAndFeel formalLAF;
    private final Map<Component, FormalityFlags> flags =
            new WeakHashMap<Component, FormalityFlags>();

    private final Visitor clearKidsVisitor = new Visitor() {
        public boolean visit(Component c, int depth) {
            FormalityFlags ff = flags(c);
            System.out.println(
                    "clearKidsVisitor " + NapkinDebug.descFor(c) + ": " + ff);

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
            System.out.println("updateUIVisitor " + NapkinDebug.descFor(c));
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
            System.out.println("addListenerVisitor " + NapkinDebug.descFor(c));
            if (!(c instanceof Container) || flags.containsKey(c))
                return false;

            FormalityFlags ff = flags(c, false);
            ((Container) c).addContainerListener(ff);
            System.out.println("adding listener for " + NapkinDebug.descFor(c) +
                    ", " +
                    System.identityHashCode(c));
            return true;
        }
    };

    private static boolean justNapkin = true;

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
            String desc = NapkinDebug.descFor(c);
            out.print(desc);
            out.print(": ");
            out.print(ff);
            out.print(", " + c.isOpaque());
            out.println();
            return true;
        }
    }

    private class FormalityFlags implements ContainerListener {
        boolean known;
        boolean formal;
        boolean inherited = true;

        public void componentAdded(ContainerEvent e) {
            Component child = e.getChild();
            System.out.println("Added: " + NapkinDebug.descFor(child) + " to " +
                    e.getComponent());
            clear(child);
        }

        public void componentRemoved(ContainerEvent e) {
            Component child = e.getChild();
            System.out.println("Removed: " + NapkinDebug.descFor(child) +
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
        //noinspection NonConstantStringShouldBeStringBuffer
        String desc = "The Napkin Look and Feel";
        if (formalLAF != null)
            desc += " [backed by " + formalLAF.getDescription() + "]";
        return desc;
    }

    public String getID() {
        //noinspection NonConstantStringShouldBeStringBuffer
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
        if (!justNapkin)
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
        if (justNapkin)
            return false;

        FormalityFlags ff = flags(c);
        System.out.println("isFormal(" + NapkinDebug.descFor(c) + "): " + ff);
        if (ff.known)
            return ff.formal;

        FormalityFlags pff = inheritedFormal(c.getParent());
        ff.setFrom(pff);
        return ff.formal;
    }

    private FormalityFlags inheritedFormal(Container container) {
        System.out.println(
                "inheritedFormal(" + NapkinDebug.descFor(container) + ")");
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
        String basicPackageName =
                NapkinLookAndFeel.class.getPackage().getName() + ".Napkin";
        for (String uiType : UI_TYPES) {
            String uiClass = basicPackageName + uiType;
            table.put(uiType, uiClass);
        }
        
        // The following line is a hot-fix for non-working FileChooser on Windows
        table.put("FileChooserUI", "javax.swing.plaf.metal.MetalFileChooserUI");
        // The following line is a hot-fix for JScrollPanel's paint issue on Windows
        table.put("ScrollPaneUI", "javax.swing.plaf.basic.BasicScrollPaneUI");
        
        Set<Object> keys = new HashSet<Object>(table.keySet());
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

        Integer zero = 0;
        Object checkBoxButtonIcon = new UIDefaults.ActiveValue() {
            public Object createValue(UIDefaults table) {
                return NapkinIconFactory.createCheckBoxIcon();
            }
        };
        Object checkedMenuItemIcon = new UIDefaults.ActiveValue() {
            public Object createValue(UIDefaults table) {
                return NapkinIconFactory.createCheckedMenuItemIcon();
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

        Object downArrowIcon = new UIDefaults.ActiveValue() {
            public Object createValue(UIDefaults table) {
                return NapkinIconFactory.createArrowIcon(SOUTH, 8);
            }
        };
        Object rightArrowIcon = new UIDefaults.ActiveValue() {
            public Object createValue(UIDefaults table) {
                return NapkinIconFactory.createArrowIcon(EAST, 8);
            }
        };

        Object closeIcon = new UIDefaults.ActiveValue() {
            public Object createValue(UIDefaults table) {
                return NapkinIconFactory.createXIcon(15);
            }
        };
        Object minIcon = new UIDefaults.ActiveValue() {
            public Object createValue(UIDefaults table) {
                return NapkinIconFactory.createArrowIcon(SOUTH, 10);
            }
        };

        setupActions(table);
        
        NapkinTheme theme = NapkinTheme.Manager.getCurrentTheme();

        Object[] napkinDefaults = {
            "RadioButton.textIconGap", zero,
            "RadioButton.icon", radioButtonIcon,
            "RadioButtonMenuItem.textIconGap", zero,
            "RadioButtonMenuItem.checkIcon", radioButtonIcon,

            "CheckBox.textIconGap", zero,
            "CheckBox.icon", checkBoxButtonIcon,
            "CheckBoxMenuItem.textIconGap", zero,
            "CheckBoxMenuItem.checkIcon", checkedMenuItemIcon,

            "Menu.arrowIcon", rightArrowIcon,

            "OptionPane.messageAreaBorder", null,

            "TabbedPane.contentBorderInsets", NapkinBoxBorder.LARGE_DEFAULT_INSETS,
            
            "Tree.openIcon", downArrowIcon,
            "Tree.closedIcon", rightArrowIcon,
            "Tree.hash", theme.getPenColor(),
            "Tree.collapsedIcon", null,
            "Tree.expandedIcon", null,
            "Tree.leafIcon", null,
            "Tree.selectionBorderColor", null,

            "TextField.caretForeground", theme.getPenColor(),
            "TextArea.caretForeground", theme.getPenColor(),
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
            "SplitPane.dividerSize", NapkinSplitPaneDivider.SIZE,

            "FileChooser.upFolderIcon", sketchedIcon("UpFolder"),
            "FileChooser.detailsViewIcon", sketchedIcon("DetailsView"),
            "FileChooser.listViewIcon", sketchedIcon("ListView"),
            "FileChooser.newFolderIcon", sketchedIcon("NewFolder"),
            "FileChooser.homeFolderIcon", sketchedIcon("HomeFolder"),

            "FileView.directoryIcon", sketchedIcon("Directory"),
            "FileView.fileIcon", sketchedIcon("File"),
            "FileView.computerIcon", sketchedIcon("Computer"),
            "FileView.hardDriveIcon", sketchedIcon("HardDrive"),
            "FileView.floppyDriveIcon", sketchedIcon("FloppyDrive"),

            "OptionPane.errorIcon", sketchedIcon("Error"),
            "OptionPane.informationIcon", sketchedIcon("Information"),
            "OptionPane.warningIcon", sketchedIcon("Warning"),
            "OptionPane.questionIcon", sketchedIcon("Question"),
        };

        table.putDefaults(napkinDefaults);
    }

    private static Object sketchedIcon(final String templateName) {
        return new UIDefaults.ActiveValue() {
            public Object createValue(UIDefaults table) {
                return NapkinIconFactory.createSketchedIcon(templateName);
            }
        };
    }

    private static void setupActions(UIDefaults table) {
        //!! These are copied from Metal LookAndFeel, but we should get them
        //!! From the formal L&F, as well as getting *all* behavior.  -arnold
        Object fieldInputMap = new UIDefaults.LazyInputMap(new Object[] {
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
            "control shift O", "toggle-componentOrientation"
            /*DefaultEditorKit.toggleComponentOrientation*/
        });

        Object multilineInputMap = new UIDefaults.LazyInputMap(new Object[] {
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
            "control shift O", "toggle-componentOrientation"
            /*DefaultEditorKit.toggleComponentOrientation*/
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

        Map<String, Font> fontMap = fontNameMap(theme);

        Object drawnBorder = new UIDefaults.ActiveValue() {
            public Object createValue(UIDefaults table) {
                return new NapkinBoxBorder();
            }
        };
        Object compoundBorder = new UIDefaults.ActiveValue() {
            public Object createValue(UIDefaults table) {
                NapkinBoxBorder outside = new NapkinBoxBorder();
                BasicBorders.MarginBorder inside = new BasicBorders.MarginBorder();
                return new CompoundBorder(outside, inside);
            }
        };

        Color clear = new AlphaColorUIResource(CLEAR);

        for (Map.Entry<Object, Object> entry : table.entrySet()) {
            String key = (String) entry.getKey();
            Object val = entry.getValue();
            Object res;
            if ((res = propVal(key, "font", val, table)) != null) {
                if (res instanceof FontUIResource) {
                    FontUIResource resource = (FontUIResource) res;
                    String name = resource.getFontName();
                    Font font = fontMap.get(name);
                    if (font != null)
                        entry.setValue(font);
                    else {
                        System.err.println(
                                "unknown font: " + name + " for " + key);
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
                        key.endsWith(".caretForeground") ||
                        key.endsWith(".acceleratorForeground") ||
                        key.endsWith(".disabledForeground") ||
                        key.endsWith(".inactiveForeground") ||
                        key.endsWith(".inactiveTitleForeground") ||
                        key.endsWith(".textForeground")) {
                    entry.setValue(theme.getPenColor());
                } else if (key.endsWith(".background") ||
                        key.endsWith(".selectionBackground") ||
//                        key.endsWith(".buttonBackground") ||
//                        key.endsWith(".disabledBackground") ||
//                        key.endsWith(".inactiveBackground") ||
//                        key.endsWith(".activeTitleBackground") ||
//                        key.endsWith(".inactiveTitleBackground") ||
//                        key.endsWith(".focusCellBackground") ||
//                        key.endsWith(".dockingBackground") ||
//                        key.endsWith(".floatingBackground") ||
                        key.endsWith(".textBackground")) {
                    entry.setValue(clear);
                } else if (key.endsWith(".selectionForeground") ||
                        key.endsWith(".activeTitleForeground") ||
                        key.endsWith("SelectionForeground")) {
                    entry.setValue(theme.getSelectionColor());
                }
            }
        }
    }

    private static Map<String, Font> fontNameMap(NapkinTheme theme) {
        Font dialogPlain = theme.getTextFont();
        Font dialogBold = theme.getBoldTextFont();
        Font serifPlain = theme.getTextFont();
        Font sansSerifPlain = theme.getTextFont();
        Font monospacedPlain = theme.getFixedFont();

        Map<String, Font> fromName = new HashMap<String, Font>();
        fromName.put("dialogBold", dialogBold);
        fromName.put("dialogPlain", dialogPlain);
        fromName.put("monospacedPlain", monospacedPlain);
        fromName.put("sansSerifPlain", sansSerifPlain);
        fromName.put("serifPlain", serifPlain);

        // These are defaults, also in the font file but here for backup
        fromName.put("Dialog.plain", dialogPlain);
        fromName.put("Dialog.bold", dialogBold);
        fromName.put("Serif.plain", serifPlain);
        fromName.put("SansSerif.plain", sansSerifPlain);
        fromName.put("MonoSpaced.plain", monospacedPlain);
        // the problematic ones (Spinner.font, Spinner.font, PasswordField.font)
        // have the following case instead
        fromName.put("Monospaced.plain", monospacedPlain);

        // read in from the property file
        InputStream fonts =
                NapkinLookAndFeel.class
                        .getResourceAsStream("resources/fonts.properties");
        if (fonts != null) {
            try {
                Properties props = new Properties();
                props.load(fonts);
                fonts.close();

                for (Map.Entry<Object, Object> entry : props.entrySet()) {
                    String fontName = (String) entry.getKey();
                    Font font = fromName.get(entry.getValue());
                    if (font == null)
                        System.err.println("unknown font: " + fontName);
                    else
                        fromName.put(fontName, font);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                NapkinUtil.tryClose(fonts);
            }
        }

        return fromName;
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

        return extractVal(val, table);
    }

    private static Object extractVal(Object val, UIDefaults table) {
        if (val instanceof LazyValue) {
            val = ((LazyValue) val).createValue(table);
        } else if (val instanceof ActiveValue) {
            val = ((ActiveValue) val).createValue(table);
        }
        return val;
    }

    public void setIsFormal(Component c, boolean isFormal) {
        if (justNapkin)
            return;
        FormalityFlags ff = flags(c);
        ff.known = true;
        ff.formal = isFormal;
        ff.inherited = false;
        clearKids(c);
    }

    private void clear(Component c) {
        System.out.println("clear(" + NapkinDebug.descFor(c) + ")");
        clearKids(c);
        FormalityFlags ff = flags(c);
        ff.known = !ff.inherited;
    }

    private void clearKids(Component c) {
        new ComponentWalker(clearKidsVisitor).walk(c);
        new ComponentWalker(updateUIVisitor).walk(c);
    }

    private FormalityFlags flags(Component c) {
        return flags(c, true);
    }

    private FormalityFlags flags(Component c, boolean recurse) {
        if (justNapkin)
            return null;
        FormalityFlags ff = flags.get(c);
        if (ff == null) {
            System.out.println("adding flags: " + NapkinDebug.descFor(c));
            if (recurse && c instanceof Container) {
                new ComponentWalker(addListenerVisitor).walk(c);
                ff = flags.get(c);
            } else {
                ff = new FormalityFlags();
                flags.put(c, ff);
            }
        }
        return ff;
    }

    public void dumpFormality(Component top, PrintStream out) {
        new ComponentWalker(new DumpVisitor(out)).walk(top);
    }
}

