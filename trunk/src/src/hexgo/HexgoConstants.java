// $Header$

package hexgo;

import java.util.Random;

/**
 * Constants that are used in several places in this game.
 */
interface HexgoConstants {
    /** The distance to rotate for each unit of tile rotation. */
    public final double ROTATE_ONCE = Math.PI / 3;

    /** A shared random number generator. */
    public final Random random = new Random();

    /** The direction "left". */
    public final int LEFT = 0;
    /** The direction "upper left". */
    public final int UPPER_LEFT = 1;
    /** The direction "upper right". */
    public final int UPPER_RIGHT = 2;
    /** The direction "right". */
    public final int RIGHT = 3;
    /** The direction "lower right". */
    public final int LOWER_RIGHT = 4;
    /** The direction "lower left". */
    public final int LOWER_LEFT = 5;
    /** The number of directions we care about. */
    public final int NUM_DIRS = 6;

    /** The name of each direction as a string. */
    public final String DIRECTION_NAMES[] = {
        "LEFT", "UPPER_LEFT", "UPPER_RIGHT",
        "RIGHT", "LOWER_RIGHT", "LOWER_LEFT",
    };

    /** A "no" trit (three-state bit) value. */
    public final int NO = 0;
    /** A "yes" trit (three-state bit) value. */
    public final int YES = 1;
    /** A "maybe" trit (three-state bit) value. */
    public final int MAYBE = -1;
}
