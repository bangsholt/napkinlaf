// $Header$

package hexgo;

/**
 * A line within a tile, from one edge to another.
 */
public class Line {
    private int edge1, edge2;	// the two edges the line connects
    private int type;		// the type of line

    /** A short curve (connects two adjacent edges). */
    public static final int SHORT_CURVE = 0;
    /** A long curve (connects one edge to another with one edge between). */
    public static final int LONG_CURVE = 1;
    /** A straight line (connects opposite edges). */
    public static final int STRAIGHT_LINE = 2;

    /** The name of each line type as a string. */
    public static final String[] TYPE_NAMES = {
        "SHORT_CURVE", "LONG_CURVE", "STRAIGHT_LINE"
    };

    /**
     * Creates a new line.
     *
     * @param edge1 One of the edges.
     * @param edge2 The other edge.
     * @param type  The type of line.
     */
    public Line(int edge1, int edge2, int type) {
        this.edge1 = edge1;
        this.edge2 = edge2;
        this.type = type;
    }

    /**
     * Returns one of this line's edges.
     */
    public int edge1() {
        return edge1;
    }

    /**
     * Returns the other of this line's edges.
     */
    public int edge2() {
        return edge2;
    }

    /**
     * Returns this line's type.
     */
    public int type() {
        return type;
    }
}

;
