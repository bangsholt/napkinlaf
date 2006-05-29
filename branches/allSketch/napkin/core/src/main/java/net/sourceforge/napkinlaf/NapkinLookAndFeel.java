package net.sourceforge.napkinlaf;

import net.sourceforge.napkinlaf.borders.NapkinBoxBorder;
import net.sourceforge.napkinlaf.borders.NapkinLineBorder;
import net.sourceforge.napkinlaf.borders.NapkinSelectedBorder;
import net.sourceforge.napkinlaf.borders.NapkinWrappedBorder;
import net.sourceforge.napkinlaf.fonts.MergedFont;
import net.sourceforge.napkinlaf.fonts.PatchedFontUIResource;
import net.sourceforge.napkinlaf.util.AlphaColorUIResource;
import net.sourceforge.napkinlaf.util.ComponentWalker.Visitor;
import net.sourceforge.napkinlaf.util.NapkinDebug;
import net.sourceforge.napkinlaf.util.NapkinIconFactory;
import net.sourceforge.napkinlaf.util.NapkinRepaintManager;
import net.sourceforge.napkinlaf.util.NapkinUtil;
import static net.sourceforge.napkinlaf.util.NapkinConstants.*;

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
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This class defines the central behavior for the Napkin look & feel.
 *
 * @author Ken Arnold
 * @author Alex Lam
 */
public class NapkinLookAndFeel extends BasicLookAndFeel {

    /**
     * A table of Napkin ComponentUIs to be set as default when initialised.
     */
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

    /**
     * Creates a new instance of NapkinLookAndFeel
     */
    public NapkinLookAndFeel() {
        /*
         * Default values are not initialised properly before the first
         * JComponent is created in the application environment -- so if Napkin
         * is employed by UIManager before that its overrideComponentDefaults()
         * will not work as expected.
         *
         * Solution: create a JComponent to buy us security ;-)
         */
        new JLabel();
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        return "The Napkin Look and Feel";
    }

    /** {@inheritDoc} */
    @Override
    public String getID() {
        return "Napkin";
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return getID();
    }

    /**
     * {@inheritDoc}
     *
     * Napkin is a cross-platform Pluggable Look & Feel.
     */
    @Override
    public boolean isNativeLookAndFeel() {
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * Napkin is a cross-platform Pluggable Look & Feel.
     */
    @Override
    public boolean isSupportedLookAndFeel() {
        return true;
    }

    @Override
    public boolean getSupportsWindowDecorations() {
        return true;
    }

    /**
     * Initialise mapping of JComponent to their corresponding Napkin
     * ComponentUIs.
     */
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

    /**
     * Initialise Napkin default colours.
     */
    @Override
    protected void initSystemColorDefaults(UIDefaults table) {
        super.initSystemColorDefaults(table);
        // make a copy so we can modify the table as we read the key set
        NapkinTheme theme = NapkinTheme.Manager.getCurrentTheme();
        Color penColor = theme.getPenColor();
        Color bgColor = theme.getBackgroundColor();
        table.put("desktop", bgColor);
        table.put("activeCaption", bgColor);
        table.put("activeCaptionText", theme.getSelectionColor());
        table.put("activeCaptionBorder", penColor);
        table.put("inactiveCaption", bgColor);
        table.put("inactiveCaptionText", penColor);
        table.put("inactiveCaptionBorder", penColor);
        table.put("window", bgColor);
        table.put("windowBorder", penColor);
        table.put("windowText", penColor);
        table.put("text", bgColor);
        table.put("textText", penColor);
        table.put("textHighlight", theme.getHighlightColor());
        table.put("textHighlightText", penColor);
        table.put("textInactiveText", penColor);
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
        Object boxBorder = new UIDefaults.ActiveValue() {
            public Object createValue(UIDefaults table) {
                return new NapkinBoxBorder();
            }
        };
        Object emptyBorder = new UIDefaults.ActiveValue() {
            public Object createValue(UIDefaults table) {
                return new NapkinWrappedBorder(new EmptyBorder(3, 3, 3, 3));
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
                "CheckBox.icon", checkBoxButtonIcon,
                "CheckBox.textIconGap", 0,

                "CheckBoxMenuItem.checkIcon", checkedMenuItemIcon,
                "CheckBoxMenuItem.foreground", popupTheme.getPenColor(),
                "CheckBoxMenuItem.selectionForeground",
                popupTheme.getSelectionColor(),
                "CheckBoxMenuItem.textIconGap", 0,

                "DesktopIcon.border", null,

                "FileChooser.detailsViewButtonToolTipText", "Details View",
                "FileChooser.detailsViewIcon", sketchedIcon("DetailsView"),
                "FileChooser.fileNameLabelText", "File Name(s):",
                "FileChooser.filesOfTypeLabelText", "Of Type(s):",
                "FileChooser.homeFolderIcon", sketchedIcon("HomeFolder"),
                "FileChooser.homeFolderToolTipText", "Home Folder",
                "FileChooser.listViewButtonToolTipText", "List View",
                "FileChooser.listViewIcon", sketchedIcon("ListView"),
                "FileChooser.lookInLabelText", "Look in:",
                "FileChooser.newFolderIcon", sketchedIcon("NewFolder"),
                "FileChooser.newFolderToolTipText", "New Folder",
                "FileChooser.saveInLabelText", "Save in:",
                "FileChooser.upFolderIcon", sketchedIcon("UpFolder"),
                "FileChooser.upFolderToolTipText", "Up One Level",

                "FileView.computerIcon", sketchedIcon("Computer"),
                "FileView.directoryIcon", sketchedIcon("Directory"),
                "FileView.fileIcon", sketchedIcon("File"),
                "FileView.floppyDriveIcon", sketchedIcon("FloppyDrive"),
                "FileView.hardDriveIcon", sketchedIcon("HardDrive"),

                "InternalFrame.activeTitleForeground",
                popupTheme.getSelectionColor(),
                "InternalFrame.border", emptyBorder,
                "InternalFrame.closeButtonToolTip", "Close",
                "InternalFrame.closeIcon", closeIcon,
                "InternalFrame.iconButtonToolTip", "Minimise",
                "InternalFrame.iconifyIcon", iconIcon,
                "InternalFrame.inactiveTitleForeground",
                popupTheme.getPenColor(),
                "InternalFrame.maxButtonToolTip", "Maximise",
                "InternalFrame.maximizeIcon", null,
                "InternalFrame.minimizeIcon", minIcon,
                "InternalFrame.restoreButtonToolTip", "Restore",

                "List.focusCellHighlightBorder", null,

                "Menu.arrowIcon", rightArrowIcon,
                "Menu.border", null,

                "MenuBar.border", null,

                "MenuItem.disabledForeground", popupTheme.getPenColor(),
                "MenuItem.foreground", popupTheme.getPenColor(),
                "MenuItem.selectionForeground", popupTheme.getSelectionColor(),

                "OptionPane.buttonAreaBorder", null,
                "OptionPane.errorIcon", sketchedIcon("Error"),
                "OptionPane.informationIcon", sketchedIcon("Information"),
                "OptionPane.messageAreaBorder", null,
                "OptionPane.questionIcon", sketchedIcon("Question"),
                "OptionPane.warningIcon", sketchedIcon("Warning"),

                "RadioButton.icon", radioButtonIcon,
                "RadioButton.textIconGap", 0,

                "PasswordField.border", underlineBorder,

                "PopupMenu.border", null,
                "PopupMenu.foreground", popupTheme.getPenColor(),

                "RadioButtonMenuItem.checkIcon", radioButtonIcon,
                "RadioButtonMenuItem.foreground", popupTheme.getPenColor(),
                "RadioButtonMenuItem.selectionForeground",
                popupTheme.getSelectionColor(),
                "RadioButtonMenuItem.textIconGap", 0,

                "RootPane.frameBorder", boxBorder,
                "RootPane.plainDialogBorder", boxBorder,
                "RootPane.informationDialogBorder", boxBorder,
                "RootPane.errorDialogBorder", boxBorder,
                "RootPane.colorChooserDialogBorder", boxBorder,
                "RootPane.fileChooserDialogBorder", boxBorder,
                "RootPane.questionDialogBorder", boxBorder,
                "RootPane.warningDialogBorder", boxBorder,

                "SplitPane.dividerSize", NapkinSplitPaneDivider.SIZE,

                "SplitPaneDivider.border", null,

                "TabbedPane.contentBorderInsets",
                NapkinBoxBorder.LARGE_DEFAULT_INSETS,
                "TabbedPane.tabsOverlapBorder", null,

                "Table.focusCellHighlightBorder", null,
                "Table.scrollPaneBorder", null,

                "TextArea.caretForeground", theme.getPenColor(),

                "TextField.border", underlineBorder,
                "TextField.caretForeground", theme.getPenColor(),

                "ToggleButton.border", selectBorder,

                "ToolTip.border", null,
                "ToolTip.foreground", popupTheme.getPenColor(),

                "Tree.closedIcon", rightArrowIcon,
                "Tree.collapsedIcon", null,
                "Tree.expandedIcon", null,
                "Tree.hash", theme.getPenColor(),
                "Tree.leafIcon", null,
                "Tree.openIcon", downArrowIcon,
                "Tree.selectionBorderColor", null,
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

    public static void overrideComponentDefaults(UIDefaults table) {
        NapkinTheme theme = NapkinTheme.Manager.getCurrentTheme();

        Map<String, Font> fontMap = fontNameMap(theme);
        Map<MergedFont, MergedFont> fontCache =
                new HashMap<MergedFont, MergedFont>();
        Font textFont = theme.getTextFont();
        Font boldFont = theme.getBoldTextFont();

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
            if (!(entry.getKey() instanceof String)) {
                continue;
            }
            String key = (String) entry.getKey();
            Object val = entry.getValue();
            Object res;
            if ((res = propVal(key, "font", val, table)) != null) {
                if (res instanceof Font && res instanceof UIResource &&
                        !(res instanceof PatchedFontUIResource)) {

                    Font resource = (Font) res;
                    String name = resource.getFontName();
                    Font font = fontMap.get(name);
                    if (font == null) {
                        font = resource.isBold() ? boldFont : textFont;
                        System.err.println(
                                "unknown font: " + name + " for " + key);
                    }
                    if (PatchedFontUIResource.doesPatchWork()) {
                        MergedFont mFont = new MergedFont(font, resource);
                        if (fontCache.containsKey(mFont)) {
                            mFont = fontCache.get(mFont);
                        } else {
                            fontCache.put(mFont, mFont);
                        }
                        font = mFont;
                    }
                    entry.setValue(font);
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
                        ; // keep it as it is, i.e. EmptyBorder
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
                        key.endsWith(".selectionForeground") ||
                        key.endsWith(".textForeground")) {
                    entry.setValue(theme.getPenColor());
                } else if (key.endsWith(".background") ||
                        key.endsWith(".disabledBackground") ||
                        key.endsWith(".textBackground")) {
                    /**
                     * Other potential candidates includes:
                     *
                     * .buttonBackground
                     * .inactiveBackground
                     * .activeTitleBackground
                     * .inactiveTitleBackground
                     * .focusCellBackground
                     * .dockingBackground
                     * .floatingBackground
                     */
                    entry.setValue(clear);
                } else if (key.endsWith(".selectionBackground") ||
                        key.endsWith("SelectionBackground")) {
                    entry.setValue(HIGHLIGHT_CLEAR);
                } else if (key.endsWith(".activeTitleForeground") ||
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

        Object result = null;
        if (prePos > 0 && ((key.endsWith(prop) && key.charAt(prePos) == '.')
                || (key.endsWith(prop.substring(1)) && key.charAt(prePos + 1)
                == Character.toUpperCase(prop.charAt(0))))) {
            result = extractVal(val, table);
        }
        return result;
    }

    private static Object extractVal(Object val, UIDefaults table) {
        if (val instanceof LazyValue) {
            val = ((LazyValue) val).createValue(table);
        } else if (val instanceof ActiveValue) {
            val = ((ActiveValue) val).createValue(table);
        }
        return val;
    }

    private final AtomicBoolean initialised = new AtomicBoolean(false);

    private void wrapRepaintManager() {
        RepaintManager manager = RepaintManager.currentManager(null);
        RepaintManager.setCurrentManager(NapkinRepaintManager.wrap(manager));
    }

    private void unwrapRepaintManager() {
        RepaintManager manager = RepaintManager.currentManager(null);
        RepaintManager.setCurrentManager(NapkinRepaintManager.unwrap(manager));
    }

    @Override
    public void initialize() {
        if (initialised.compareAndSet(false, true)) {
            wrapRepaintManager();
            super.initialize();
        }
    }

    @Override
    public void uninitialize() {
        if (initialised.compareAndSet(true, false)) {
            unwrapRepaintManager();
            super.uninitialize();
            new Thread(
                new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        if (!(UIManager.getLookAndFeel()
                                instanceof NapkinLookAndFeel)) {
                            purgeAllInstalledComponents();
                        }
                    }
                }
            ).start();
        }
    }

    private static final WeakHashMap<JComponent, Void> installedComponents =
            new WeakHashMap<JComponent, Void>();
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();

    public static void registerComponent(JComponent component) {
        lock.writeLock().lock();
        installedComponents.put(component, null);
        lock.writeLock().unlock();
    }

    private static void purgeAllInstalledComponents() {
        lock.readLock().lock();
        for (JComponent component : installedComponents.keySet()) {
            if (NapkinUtil.isNapkinInstalled(component)) {
                component.updateUI();
            }
        }
        lock.readLock().unlock();
        lock.writeLock().lock();
        installedComponents.clear();
        lock.writeLock().unlock();
    }

    /**
     * Since we are providing erasure effects to disabled components, there is
     * no need to perform additional operations on disabled component's icon.
     */
    @Override
    public Icon getDisabledSelectedIcon(JComponent component, Icon icon) {
        return icon;
    }

    /**
     * Since we are providing erasure effects to disabled components, there is
     * no need to perform additional operations on disabled component's icon.
     */
    @Override
    public Icon getDisabledIcon(JComponent component, Icon icon) {
        return icon;
    }
}
