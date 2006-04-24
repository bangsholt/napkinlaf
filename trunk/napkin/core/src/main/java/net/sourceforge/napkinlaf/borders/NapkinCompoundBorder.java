package net.sourceforge.napkinlaf.borders;

import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

/** @author Alex Lam Sze Lok */
public class NapkinCompoundBorder extends CompoundBorder
        implements NapkinBorder {

    /** Creates a new instance of NapkinCompoundBorder */
    public NapkinCompoundBorder(Border outside, Border inside) {
        super(outside, inside);
    }
}
