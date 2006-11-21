package net.sourceforge.napkinlaf.borders;

import javax.swing.border.*;

/**
 * This class holds a border that consists of two borders, an out and an inner
 * one.
 *
 * @author Alex Lam Sze Lok
 */
public class NapkinCompoundBorder extends CompoundBorder
        implements NapkinBorder {

    /**
     * Creates a new instance of <tt>NapkinCompoundBorder</tt>
     *
     * @param outside The outside border.
     * @param inside  The inside border.
     */
    public NapkinCompoundBorder(Border outside, Border inside) {
        super(outside, inside);
    }
}
