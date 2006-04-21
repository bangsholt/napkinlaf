/*
 * NapkinLFCustoms.java
 *
 * Created on 17 April 2006, 00:54
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sourceforge.napkinlaf.netbeans;


import net.sourceforge.napkinlaf.NapkinTheme;
import net.sourceforge.napkinlaf.borders.*;
import static net.sourceforge.napkinlaf.util.NapkinConstants.CLEAR;
import static net.sourceforge.napkinlaf.util.NapkinConstants.HIGHLIGHT_CLEAR;

import org.netbeans.swing.plaf.LFCustoms;

import java.awt.Color;
import java.awt.Font;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Alex Lam Sze Lok
 */
public class NapkinLFCustoms extends LFCustoms {

    @Override
    public Object[] createApplicationSpecificKeysAndValues() {
        Border boxBorder = new NapkinBoxBorder();
        Border emptyBorder =
                new NapkinWrappedBorder(new EmptyBorder(0, 0, 0, 0));
        Border lineBorder = new NapkinCompoundBorder(
                new NapkinLineBorder(false), new EmptyBorder(0, 0, 3, 0));

        NapkinTheme theme = NapkinTheme.Manager.getCurrentTheme();
        Color bgColor = theme.getBackgroundColor();
        Color penColor = theme.getPenColor();
        Color selColor = theme.getSelectionColor();
        Color highlightColor = theme.getHighlightColor();
        Font textFont = theme.getTextFont();
        Font fixedFont = theme.getFixedFont();
        Font boldFont = theme.getBoldTextFont();

        final String BASE = net.sourceforge.napkinlaf.netbeans.NapkinEditorTabDisplayerUI.class.getCanonicalName();
        return new Object[] {
            "Nb.NapkinLFCustoms", this,
            
            EDITOR_TAB_DISPLAYER_UI, BASE,

            CONTROLFONT, textFont,
            LISTFONT, textFont,
            PANELFONT, textFont,
            SPINNERFONT, fixedFont,
            SUBFONT, textFont,
            SYSTEMFONT, fixedFont,
            TREEFONT, textFont,
            USERFONT, textFont,
            WINDOWTITLEFONT, boldFont,

            OUTPUT_SELECTION_BACKGROUND, highlightColor,
            PROPSHEET_SELECTED_SET_BACKGROUND, HIGHLIGHT_CLEAR,
            PROPSHEET_SELECTED_SET_FOREGROUND, penColor,
            PROPSHEET_SELECTION_BACKGROUND, HIGHLIGHT_CLEAR,
            PROPSHEET_SELECTION_FOREGROUND, penColor,
            PROPSHEET_SET_BACKGROUND, CLEAR,
            PROPSHEET_SET_FOREGROUND, penColor,
            SCROLLPANE_BORDER_COLOR, penColor,
            TAB_ACTIVE_SELECTION_BACKGROUND, HIGHLIGHT_CLEAR,
            TAB_ACTIVE_SELECTION_FOREGROUND, penColor,
            "nb.explorer.unfocusedSelBg", highlightColor,
            "nb.explorer.unfocusedSelFg", penColor,
            "Tree.selectionBackground", highlightColor,
            "Tree.selectionForeground", penColor,
            "Tree.selectionBorderColor", CLEAR,
            "TabRenderer.selectedForeground", selColor,

            DESKTOP_SPLITPANE_BORDER, emptyBorder,
            EDITOR_STATUS_INNER_BORDER, boxBorder,
            EDITOR_STATUS_LEFT_BORDER, boxBorder,
            EDITOR_STATUS_RIGHT_BORDER, boxBorder,
            EDITOR_TAB_CONTENT_BORDER, emptyBorder,
            EDITOR_TAB_OUTER_BORDER, boxBorder,
            EDITOR_TAB_TABS_BORDER, emptyBorder,
            EDITOR_TOOLBAR_BORDER, lineBorder,
            SCROLLPANE_BORDER, boxBorder,
            SLIDING_TAB_CONTENT_BORDER, boxBorder,
            SLIDING_TAB_OUTER_BORDER, boxBorder,
            SLIDING_TAB_TABS_BORDER, emptyBorder,
            VIEW_TAB_CONTENT_BORDER, boxBorder,
            VIEW_TAB_OUTER_BORDER, boxBorder,
            VIEW_TAB_TABS_BORDER, emptyBorder,

            "swing.aatext", Boolean.TRUE,
            "nb.cellrenderer.antialiasing", Boolean.TRUE,
            "nb.useSwingHtmlRendering", Boolean.FALSE,
        };
    }
}
