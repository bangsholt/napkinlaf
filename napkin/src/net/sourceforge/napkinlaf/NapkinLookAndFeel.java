package net.sourceforge.napkinlaf;

import net.sourceforge.napkinlaf.borders.NapkinBoxBorder;
import net.sourceforge.napkinlaf.borders.NapkinLineBorder;
import net.sourceforge.napkinlaf.borders.NapkinSelectedBorder;
import net.sourceforge.napkinlaf.util.AlphaColorUIResource;
import net.sourceforge.napkinlaf.util.ComponentWalker.Visitor;
import static net.sourceforge.napkinlaf.util.NapkinConstants.*;
import net.sourceforge.napkinlaf.util.NapkinDebug;
import net.sourceforge.napkinlaf.util.NapkinFont;
import net.sourceforge.napkinlaf.util.NapkinIconFactory;
import net.sourceforge.napkinlaf.util.NapkinUtil;

import javax.swing.*;
import javax.swing.UIDefaults.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * This class defines the central behavior for the Napkin look & feel.
 *
 * @author Ken Arnold
 * @author Alex Lam
 */
public class NapkinLookAndFeel extends BasicLookAndFeel {
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

    static class DumpVisitor implements Visitor {
        private final PrintStream out;

        DumpVisitor(PrintStream out) {
            this.out = out;
        }

        public boolean visit(Component c, int depth) {
            for (int i = 0; i < depth; i++) {
                out.print(i % 2 == 0 ? '|' : '.');
                out.print(' ');
            }
            String desc = NapkinDebug.descFor(c);
            out.print(desc);
            out.print(": ");
            out.print(c.isOpaque());
            out.println();
            return true;
        }
    }

    @Override
    public String getDescription() {
        return "The Napkin Look and Feel";
    }

    @Override
    public String getID() {
        return "Napkin";
    }

    @Override
    public String getName() {
        return getID();
    }

    @Override
    public boolean isNativeLookAndFeel() {
        return false;
    }

    @Override
    public boolean isSupportedLookAndFeel() {
        return true;
    }

    @Override
    protected void initClassDefaults(UIDefaults table) {
        super.initClassDefaults(table);
        String cName = NapkinLookAndFeel.class.getName();
        String basicPackageName = cName.replace("NapkinLookAndFeel", "Napkin");
        for (String uiType : UI_TYPES) {
            String uiClass = basicPackageName + uiType;
            table.put(uiType, uiClass);
        }

        Set<Object> keys = new HashSet<Object>(table.keySet());
        keys.removeAll(Arrays.asList(UI_TYPES));
        if (keys.size() != 0) {
            System.out.println("keys we didn't overwrite: " + keys);
        }
    }

    @Override
    protected void initSystemColorDefaults(UIDefaults table) {
        super.initSystemColorDefaults(table);
        // make a copy so we can modify the table as we read the key set
        NapkinTheme theme = NapkinTheme.Manager.getCurrentTheme();
        table.put("textHighlight", theme.getHighlightColor());
    }

    @Override
    protected void initComponentDefaults(UIDefaults table) {
        super.initComponentDefaults(table);

        overrideComponentDefaults(table);

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
                return NapkinIconFactory.createArrowIcon(NORTH, 10);
            }
        };
        Object iconIcon = new UIDefaults.ActiveValue() {
            public Object createValue(UIDefaults table) {
                return NapkinIconFactory.createArrowIcon(SOUTH, 10);
            }
        };

        setupActions(table);

        NapkinTheme theme = NapkinTheme.Manager.getCurrentTheme();
        NapkinTheme popupTheme = theme.getPopupTheme();

        Object[] napkinDefaults = {
                "RadioButton.textIconGap", 0,
                "RadioButton.icon", radioButtonIcon,
                "RadioButtonMenuItem.textIconGap", 0,
                "RadioButtonMenuItem.checkIcon", radioButtonIcon,

                "CheckBox.textIconGap", 0,
                "CheckBox.icon", checkBoxButtonIcon,
                "CheckBoxMenuItem.textIconGap", 0,
                "CheckBoxMenuItem.checkIcon", checkedMenuItemIcon,

                "Menu.arrowIcon", rightArrowIcon,

                "TabbedPane.contentBorderInsets",
                NapkinBoxBorder.LARGE_DEFAULT_INSETS,
                "TabbedPane.tabsOverlapBorder", null,

                "Table.focusCellHighlightBorder", null,
                "Table.scrollPaneBorder", null,

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

                "List.focusCellHighlightBorder", null,

                "Menu.border", null,
                "MenuBar.border", null,
                "PopupMenu.border", null,
                "ToolTip.border", null,
                "DesktopIcon.border", null,
                "ToggleButton.border", selectBorder,
                "InternalFrame.border", null,

                "PopupMenu.foreground", popupTheme.getPenColor(),
                "ToolTip.foreground", popupTheme.getPenColor(),

                "CheckBoxMenuItem.foreground", popupTheme.getPenColor(),
                "CheckBoxMenuItem.selectionForeground",
                popupTheme.getSelectionColor(),

                "RadioButtonMenuItem.foreground", popupTheme.getPenColor(),
                "RadioButtonMenuItem.selectionForeground",
                popupTheme.getSelectionColor(),

                "MenuItem.foreground", popupTheme.getPenColor(),
                "MenuItem.selectionForeground", popupTheme.getSelectionColor(),

                "InternalFrame.maximizeIcon", null,
                "InternalFrame.minimizeIcon", minIcon,
                "InternalFrame.iconifyIcon", iconIcon,
                "InternalFrame.closeIcon", closeIcon,
                "InternalFrame.closeButtonToolTip", "Close",
                "InternalFrame.iconButtonToolTip", "Minimise",
                "InternalFrame.restoreButtonToolTip", "Restore",
                "InternalFrame.maxButtonToolTip", "Maximise",
                "InternalFrame.activeTitleForeground",
                popupTheme.getSelectionColor(),
                "InternalFrame.inactiveTitleForeground",
                popupTheme.getPenColor(),

                "SplitPaneDivider.border", null,
                "SplitPane.dividerSize", NapkinSplitPaneDivider.SIZE,

                "FileChooser.upFolderIcon", sketchedIcon("UpFolder"),
                "FileChooser.detailsViewIcon", sketchedIcon("DetailsView"),
                "FileChooser.listViewIcon", sketchedIcon("ListView"),
                "FileChooser.newFolderIcon", sketchedIcon("NewFolder"),
                "FileChooser.homeFolderIcon", sketchedIcon("HomeFolder"),
                "FileChooser.lookInLabelText", "Look in:",
                "FileChooser.saveInLabelText", "Save in:",
                "FileChooser.fileNameLabelText", "File Name(s):",
                "FileChooser.filesOfTypeLabelText", "Of Type(s):",
                "FileChooser.upFolderToolTipText", "Up One Level",
                "FileChooser.homeFolderToolTipText", "Home Folder",
                "FileChooser.newFolderToolTipText", "New Folder",
                "FileChooser.listViewButtonToolTipText", "List View",
                "FileChooser.detailsViewButtonToolTipText", "Details View",

                "FileView.directoryIcon", sketchedIcon("Directory"),
                "FileView.fileIcon", sketchedIcon("File"),
                "FileView.computerIcon", sketchedIcon("Computer"),
                "FileView.hardDriveIcon", sketchedIcon("HardDrive"),
                "FileView.floppyDriveIcon", sketchedIcon("FloppyDrive"),

                "OptionPane.buttonAreaBorder", null,
                "OptionPane.messageAreaBorder", null,
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
        //!! Should get actions from the native L&F for all map defaults
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
                "ctrl shift KP_LEFT",
                DefaultEditorKit.selectionPreviousWordAction,
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
                "ctrl BACK_SLASH", "unselect"
                /*DefaultEditorKit.unselectAction*/,
                "control shift O", "toggle-componentOrientation"
                /*DefaultEditorKit.toggleComponentOrientation*/
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
                "ctrl shift KP_LEFT",
                DefaultEditorKit.selectionPreviousWordAction,
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
                "ctrl BACK_SLASH", "unselect"
                /*DefaultEditorKit.unselectAction*/,
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
                //!! Should get input maps from the native L&F for all map defaults
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
                BasicBorders.MarginBorder inside =
                        new BasicBorders.MarginBorder();
                return new CompoundBorder(outside, inside);
            }
        };

        Color clear = new AlphaColorUIResource(CLEAR);

        for (Map.Entry<Object, Object> entry : table.entrySet()) {
            String key = (String) entry.getKey();
            Object val = entry.getValue();
            Object res;
            if ((res = propVal(key, "font", val, table)) != null) {
                if (res instanceof Font && res instanceof UIResource) {
                    Font resource = (Font) res;
                    String name = resource.getFontName();
                    Font font = fontMap.get(name);
                    if (font != null)
                        entry.setValue(new NapkinFont(font, resource));
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
                    if (res instanceof CompoundBorder) {
                        entry.setValue(compoundBorder);
                    } else if (res instanceof EmptyBorder) {
                        // keep it as it is, i.e. EmptyBorder
                    } else {
                        entry.setValue(drawnBorder);
                    }
                }
            } else {
                // We set things up right for these ones manually
                if (key.contains("Text") || key.startsWith("Password")
                        || key.startsWith("Editor")) {
                    continue;
                }

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
                        key.endsWith(".textBackground")) {
                    /**
                     * Other potential candidates includes:
                     *
                     * .buttonBackground
                     * .disabledBackground
                     * .inactiveBackground
                     * .activeTitleBackground
                     * .inactiveTitleBackground
                     * .focusCellBackground
                     * .dockingBackground
                     * .floatingBackground
                     */
                    entry.setValue(clear);
                } else if (key.endsWith(".selectionBackground")) {
                    entry.setValue(HIGHLIGHT_CLEAR);
                } else if (key.endsWith(".selectionForeground") ||
                        key.endsWith(".activeTitleForeground") ||
                        key.endsWith("SelectionForeground")) {
                    entry.setValue(theme.getSelectionColor());
                }
            }
        }
    }

    @SuppressWarnings({"HardcodedFileSeparator"})
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
                    //noinspection SuspiciousMethodCalls
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

        if (prePos > 0 && ((key.endsWith(prop) && key.charAt(prePos) == '.')
                || (key.endsWith(prop.substring(1)) && key.charAt(prePos + 1)
                == Character.toUpperCase(prop.charAt(0))))) {
            return extractVal(val, table);
        } else {
            return null;
        }
    }

    private static Object extractVal(Object val, UIDefaults table) {
        if (val instanceof LazyValue) {
            val = ((LazyValue) val).createValue(table);
        } else if (val instanceof ActiveValue) {
            val = ((ActiveValue) val).createValue(table);
        }
        return val;
    }
}
