/*
 * Created by IntelliJ IDEA.
 * User: arnold
 * Date: Sep 18, 2002
 * Time: 5:15:50 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package hexgo;

import java.awt.*;

/**
 * This class represents the colors that should be used to draw the tiles
 * in this group.
 */
public class TileColors {
    /** The default colors to use. */
    public static TileColors MAIN =
	    new TileColors(new Color(117, 54, 54), Board.PLAYFIELD);
    /** The colors to use for tiles in a selected group. */
    public static TileColors SELECTED =
	    new TileColors(selected(MAIN.tile), Color.white);
    /** The default colors to use. */
    public static TileColors HAND =
	    new TileColors(SELECTED.tile, Color.black);

    /** The color to use for drawing tile backgrounds. */
    public final Color tile;
    /** The color to use for drawing tile borders. */
    public final Color border;

    /**
     * Creates a new <CODE>Colors</CODE> object with initial values.
     *
     * @param tile The color to use for tiles.
     * @param border The color to use for borders.
     */
    TileColors(Color tile, Color border) {
	this.tile = tile;
	this.border = border;
    }

    /**
     * Returns the version of the color to use in a selected group.
     *
     * @param baseColor The color from which to derive the selected color.
     *
     * @return The derived color.
     */
    private static Color selected(Color baseColor) {
	return new Color(baseColor.getColorSpace(),
		baseColor.getColorComponents(null), 0.4f);
    }
}
