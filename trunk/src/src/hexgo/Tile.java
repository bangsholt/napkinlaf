// $Header$

package hexgo;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class represents a single tile in the game.  A tile may be in the deck
 * or in a group.  A tile also can contain a stack of its recent groups, which
 * can be used to merge groups together and then unmerge them back to the
 * original contents.
 */
public class Tile implements HexgoConstants {
    private Pattern pattern;		// the tile's pattern
    private int row, col;		// the grid coordinates
    private double x, y;		// the screen coordinates
    private Group group;		// the current group
    private Arbiter arbiter;		// the game's arbiter
    private Player owner;		// the current owner of the tile, if any
    private int rotation;		// the rotation (0-5)
    private Color[] colors;		// the color of each edge (rotated)
    private GeneralPath border;		// the current border
    private BufferedImage image;	// current image of the tile
    private Scale imageScale;		// scale used to draw image
    private TileColors imageColors;	// colors used to draw image
    private boolean selected;		// is this tile selected in its group?
    private Board board;		// the board the tile is on
    private int num = nextNum++;	// the tile's ID number

    private static int nextNum = 1;	// value for the next tile's ID number

    private final static int NUM_LINES = 3;		// # of lines on a tile

    private static Scale scale = new Scale(1.0);	// starting scale

    private static final Color NUMBER_COLOR = new Color(0, 214, 0);
    private static final Color CLEAR = new Color(0, 0, 0, 0);

    /**
     * Creates a new <CODE>Tile</CODE> object.  This is not ready to use until
     * <CODE>setPattern</CODE> is invoked.
     *
     * @see #setPattern
     */
    public Tile() {
        colors = new Color[NUM_DIRS];
        image = null;
        makeAlone();
    }

    /**
     * Sets the pattern for this tile.  Once set, this is never changed.
     *
     * @param pattern This tile's pattern.
     */
    public void setPattern(Pattern pattern) {
        //System.out.println(pattern);
        this.pattern = pattern;
        for (int i = 0; i < NUM_LINES; i++) {
            Line line = Pattern.line[pattern.line(i)];
            int edge1 = line.edge1();
            int edge2 = line.edge2();
            colors[edge1] = colors[edge2] = pattern.color(i);
        }
        image = null;
    }

    /**
     * Returns the <I>x</I> screen coordinate.
     *
     * @return The <I>x</I> screen coordinate.
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the <I>y</I> screen coordinate.
     *
     * @return The <I>y</I> screen coordinate.
     */
    public double getY() {
        return y;
    }

    /**
     * Returns the group.
     *
     * @return The group.
     */
    public Group getGroup() {
        return group;
    }

    /**
     * Sets the group.  The stack of group history is cleared.
     *
     * @param group The tile's group.
     */
    public void setGroup(Group group) {
        this.group = group;
        image = null;
        board = group.getBoard();
        arbiter = board.getArbiter();
    }

    /**
     * Returns the direction from this tile to that one.  That tile is presumed
     * to be a neighbor; if not, weird things will happen.
     *
     * @param neighbor A neighboring tile.
     *
     * @return The direction from this tile to that one.
     */
    private int dirIn(Tile neighbor) {
        double xd = neighbor.x - x;
        double yd = neighbor.y - y;
        if (Math.abs(yd) < scale.epsilon)
            return (xd < 0 ? LEFT : RIGHT);
        else if (yd < 0)
            return (xd < 0 ? UPPER_LEFT : UPPER_RIGHT);
        else
            return (xd < 0 ? LOWER_LEFT : LOWER_RIGHT);
    }

    /**
     * Returns the value to add to a row to move to the neighboring tile in the
     * given direction from this tile.
     *
     * @param dir The direction to the other tile.
     *
     * @return The delta from the row of this tile in the given direction.
     */
    private int rowDelta(int dir) {
        switch (dir) {
        case LEFT:
        case RIGHT:
            return 0;
        case UPPER_LEFT:
        case UPPER_RIGHT:
            return -1;
        case LOWER_LEFT:
        case LOWER_RIGHT:
            return 1;
        }
        throw new IllegalArgumentException("bad dir: " + dir);
    }

    /**
     * Returns the value to add to a column to move to the neighboring tile in
     * the given direction from this tile.  The row is presumed to be that
     * returned by <CODE>rowDelta</CODE>.
     *
     * @param row The row this tile is in.
     * @param dir The direction to the other tile.
     *
     * @return The delta from the column of this tile in the given direction.
     *
     * @see #rowDelta
     */
    private int colDelta(int row, int dir) {
        int colOff = (row % 2 == 0 ? 1 : 0);
        switch (dir) {
        case LEFT:
            return -1;
        case RIGHT:
            return +1;
        case UPPER_LEFT:
        case LOWER_LEFT:
            return colOff - 1;
        case UPPER_RIGHT:
        case LOWER_RIGHT:
            return colOff;
        }
        throw new IllegalArgumentException("bad dir: " + dir);
    }

    /**
     * Returns the delta need to snap a tile to the nearest grid element to the
     * given point.
     *
     * @param at The location from which to find the nearest grid element.
     *
     * @return A <CODE>Point</CODE> whose values are the deltas.
     */
    public static Point.Double snapDelta(Point at) {
        /*
         * We do this by finding the nearest grid element in an even row and
         * an odd row, and then using whichever is nearest to our original
         * point.  There may be a single calcualtion for this, but I haven't
         * figured it out yet, and this is probably quite fast enough.  It
         * might even be easier to understand.
         */
        Point.Double snapEven = snapTo(at, scale.evenOrigin);
        Point.Double snapOdd = snapTo(at, scale.oddOrigin);

        double distEven = snapEven.distanceSq(at);
        double distOdd = snapOdd.distanceSq(at);

        Point.Double snap = (distEven < distOdd ? snapEven : snapOdd);
        return new Point.Double(snap.getX(), snap.getY());
    }

    /**
     * Returns the delta need to snap a tile to the nearest grid element to the
     * given point, given that the grid has the given origin.
     *
     * @param at     The location from which to find the nearest grid element.
     * @param origin The origin of the grid we're snapping to.
     *
     * @return A <CODE>Point</CODE> whose values are the deltas.
     */
    private static Point.Double snapTo(Point at, Point.Double origin) {
        double offX = nearest(at.getX() - origin.x, scale.gridX);
        double offY = nearest(at.getY() - origin.y, scale.gridY);
        return new Point.Double(offX + origin.x, offY + origin.y);
    }

    /**
     * Returns the nearest value to the <CODE>coord</CODE> in the given
     * <CODE>grid</CODE> (interval).
     *
     * @param coord The coordinate
     * @param grid  The grid (internval) to snap to.
     *
     * @return The nereast value.
     */
    private static double nearest(double coord, double grid) {
        double mod = Math.IEEEremainder(coord, grid);
        double smaller = coord - mod;
        return (Math.abs(mod) < grid / 2 ? smaller : smaller + grid);
    }

    /**
     * Sets whether this tile is selected.
     *
     * @param selected <CODE>true</CODE> if this tile is selected.
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
        image = null;
    }

    /**
     * Recalculates the border for this tile.  The border is drawn where there
     * is no adjacent tile in the same group.
     */
    private void reborder() {
        // set up the partially drawn border -- no lines between adjacent tiles
        border = new GeneralPath();

        Tile neighbor[] = new Tile[NUM_DIRS];

        Iterator it = arbiter.tilesAround(this);
        while (it.hasNext()) {
            Tile adjacent = (Tile) it.next();
            if (adjacent.group == group)
                neighbor[dirIn(adjacent)] = adjacent;
        }

        for (int i = 0; i < scale.points.length; i++) {
            float xp = (float) scale.points[i].getX();
            float yp = (float) scale.points[i].getY();
            if (i == 0 || (!selected && neighbor[i % NUM_DIRS] != null))
                border.moveTo(xp, yp);
            else
                border.lineTo(xp, yp);
        }
    }

    /**
     * Makes this tile alone, forgetting about groups and neighbors.
     *
     * @see #reset
     */
    public void makeAlone() {
        group = null;
        image = null;
    }

    /**
     * Resets all of the tile's value to their initial state.
     *
     * @see #makeAlone
     */
    public void reset() {
        makeAlone();
        x = y = 0;
        row = col = 0;
        image = null;
        owner = null;
    }

    /**
     * Rotates this tile once.  The matrix will relocate the tile to another
     * grid position of the tile is a member of a group, but not the tile around
     * which the rotation is happening.
     *
     * @param matrix The transformation matrix for positioning within the
     *               group.
     */
    public void rotate(int count, AffineTransform matrix) {
        rotation = (rotation + count) % NUM_DIRS;
        if (rotation < 0)
            rotation += NUM_DIRS;

        Point.Double pos = new Point.Double(x, y);
        matrix.transform(pos, pos);
        x = pos.getX();
        y = pos.getY();
        board.place(x, y, this);

        rotateArray(colors, count);
        image = null;
    }

    /**
     * Rotate an array once along with the tile.
     *
     * @param array The array to rotate.
     * @param count The number of positions to rotate (can be negative).
     */
    private static void rotateArray(Object[] array, int count) {
        if (count < 0)
            count += NUM_DIRS;

        int remain = array.length - count;
        Object[] tmp = new Object[count];
        System.arraycopy(array, remain, tmp, 0, count);
        System.arraycopy(array, 0, array, count, remain);
        System.arraycopy(tmp, 0, array, 0, count);
    }

    // inherit doc comment
    public void paintComponent(Graphics g1) {
        if (!board.isVisible(this))
            return;

        Graphics2D g = (Graphics2D) g1;

        //!! Using the buffer is slower if rescaling is done, so maybe we want
        //!! a direct draw (without buffer) in that case (not critical)
        if (image == null || imageScale != scale ||
                imageColors != group.colors()) {
            updateBuffer();
        }

        g.translate(x, y);

        Graphics2D gImage = (Graphics2D) g.create();
        gImage.translate(-scale.halfGridX, -scale.halfGridY);
        gImage.drawImage(image, null, 0, 0);

        paintDebug(g);

    }

    /**
     * Update the buffered image.
     */
    private void updateBuffer() {
        int width = (int) Math.ceil(scale.gridX + 2 * scale.borderStrokeWidth);
        int height = (int) Math.ceil(scale.gridY + 2 * scale.borderStrokeWidth);
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setColor(CLEAR);
        g.fillRect(0, 0, width, height);
        g.translate(scale.borderStrokeWidth, scale.borderStrokeWidth);

        g.translate(scale.halfGridX, scale.halfGridY);

        Graphics2D gBorder = (Graphics2D) g.create(); // border already rotated

        if (rotation != 0)
            g.rotate(rotation * ROTATE_ONCE);
        if (!Hexgo.paintRaw())
            g.setClip(scale.tile);
        else
            setupRaw(g);

        paintBackground(g);
        paintLines(g);
        paintBorder(gBorder);

        imageScale = scale;
        imageColors = group.colors();
    }

    /**
     * Sets up any debug stuff for the graphics.
     *
     * @param g The graphics to use.
     */
    private void setupRaw(Graphics2D g) {
        // debug code that shows the tile more clearly, but uglier
        g.setColor(Color.black);
        g.fill(new Rectangle2D.Double(-2 * scale.scale, -2 * scale.scale,
                4 * scale.scale, 4 * scale.scale));
        g.setColor(Color.blue.darker());
        g.setStroke(scale.borderStroke);
        for (int x = -2; x <= 2; x++)
            g.draw(new Line2D.Double(x * scale.scale, -2 * scale.scale,
                    x * scale.scale, 2 * scale.scale));
        for (int y = -2; y <= 2; y++)
            g.draw(new Line2D.Double(-2 * scale.scale, y * scale.scale,
                    2 * scale.scale, y * scale.scale));
    }

    /**
     * Paints the tile background.
     *
     * @param g The graphics to use.
     */
    private void paintBackground(Graphics2D g) {
        // Draw the tile background
        g.setColor(group.colors().tile);
        g.fill(scale.tile);
        g.draw(scale.tile);
    }

    /**
     * Paint the lines on the tile.
     *
     * @param g The graphics to use.
     */
    private void paintLines(Graphics2D g) {
        g.setStroke(scale.pathStroke);

        // The graphics for drawing the highlight for current path arcs
        Graphics2D gHighlight = (Graphics2D) g.create();
        gHighlight.setStroke(scale.midPathStroke);

        // Draw each line
        //System.out.println();
        for (int i = 0; i < NUM_LINES; i++) {
            Line line = Pattern.line[pattern.line(i)];
            g.setColor(pattern.color(i));
            int edge1 = line.edge1();
            int edge2 = line.edge2();

            // we use (e1 - 1) in the loop below so we need to adjust
            if (edge1 == LEFT)
                edge1 += NUM_DIRS;
            if (edge2 == LEFT)
                edge2 += NUM_DIRS;

            int startAngle;
            Shape arc;
            switch (line.type()) {
            case Line.SHORT_CURVE:
                startAngle = (270 - edge1 * 60 + 360) % 360;
                if (startAngle < 0)
                    startAngle += 360;
                Arc2D.Double shortArc = new Arc2D.Double(scale.points[edge1].x - scale.halfScale,
                        scale.points[edge1].y - scale.halfScale,
                        scale.scale, scale.scale,
                        startAngle, 120,
                        Arc2D.OPEN);
                arc = shortArc;
                //System.out.println("short curve " + shortArc.getStartPoint() + " -- " + shortArc.getEndPoint());
                break;

            case Line.LONG_CURVE:
                edge1 = inRange(edge1 + 1);
                Point.Double lc = delta(edge1);
                startAngle = (-30 - edge1 * 60) % 360;
                if (startAngle < 0)
                    startAngle += 360;
                Arc2D.Double longArc = new Arc2D.Double(lc.x - scale.adjAngleY, lc.y - scale.adjAngleY,
                        2 * scale.adjAngleY, 2 * scale.adjAngleY,
                        startAngle, 60,
                        Arc2D.OPEN);
                arc = longArc;
                //System.out.println("long curve  " + longArc.getStartPoint() + " -- " + longArc.getEndPoint());
                break;

            case Line.STRAIGHT_LINE:
                Point.Double lp1 = scale.points[edge1 - 1];
                Point.Double p1 = scale.points[edge1];
                Point.Double lp2 = scale.points[edge2 - 1];
                Point.Double p2 = scale.points[edge2];
                Line2D.Double straight =
                        new Line2D.Double((lp1.x + (p1.x - lp1.x) / 2),
                                (lp1.y + (p1.y - lp1.y) / 2),
                                (lp2.x + (p2.x - lp2.x) / 2),
                                (lp2.y + (p2.y - lp2.y) / 2));
                //System.out.println("long curve  " + straight.P1() + " -- " + straight.P2());
                arc = straight;
                break;

            default:
                throw new IllegalStateException("Bad line type " + line.type());
            }

            g.draw(arc);
            if (board.inPath(this, line)) {
                gHighlight.setColor(group.colors().tile);
//		gHighlight.setColor(pattern.darker(i));
                gHighlight.draw(arc);
            }
        }
    }

    /**
     * Paints the border, regenerating it if needed.
     *
     * @param g The graphics to use.
     */
    private void paintBorder(Graphics2D g) {
        reborder();
        g.setStroke(scale.borderStroke);
        g.setColor(group.colors().border);
        g.draw(border);
    }

    /**
     * Paint any debug info onto the tile.
     *
     * @param g The graphics to use.
     */
    private void paintDebug(Graphics2D g) {
        if (!Hexgo.paintDebug())
            return;
        g.setColor(NUMBER_COLOR);
        String label = "#" + num;
        Font font = g.getFont();
        FontRenderContext frc = g.getFontRenderContext();
        GlyphVector gv = font.createGlyphVector(frc, label);
        Rectangle2D bounds = gv.getVisualBounds();
        g.drawGlyphVector(gv, (float) bounds.getWidth() / -2f,
                (float) bounds.getHeight() / +2f);
    }

    /**
     * Paint the connections between members of the same group.  This is used
     * for debugging purposes.
     */
    public void paintConnections(Graphics g1) {
        Graphics2D g = (Graphics2D) g1;
        Iterator it = arbiter.tilesAround(this);
        while (it.hasNext()) {
            Tile adjacent = (Tile) it.next();
            if (adjacent.group == group) {
                g.draw(new Line2D.Double(x, y, adjacent.x, adjacent.y));
//		g.drawString(++pnum + "", (float) neighbor.x, (float) neighbor.y);
                g.fill(new Ellipse2D.Double(adjacent.x - 2, adjacent.y - 2, 5, 5));
            }
        }
    }

    /**
     * Return the screen-coordinate delta between a tile and the neighbor one in
     * the given direction.
     *
     * @param dir The direction
     *
     * @return An <I>x,y</I> pair which is the distance to move.
     */
    private static Point.Double delta(int dir) {
        double x, y;

        // these are the values most common to start from
        x = scale.adjAngleX;
        y = scale.adjAngleY;
        switch (dir) {
        case LEFT:
        case RIGHT:
            x = 2 * scale.straightX;
            if (dir == LEFT)
                x = -x;
            y = 0;
            break;
        case UPPER_LEFT:
            x = -x;
            y = -y;
            break;
        case UPPER_RIGHT:
            y = -y;
            break;
        case LOWER_RIGHT:
            break;
        case LOWER_LEFT:
            x = -x;
            break;
        default:
            throw new IllegalArgumentException("bad dir in delta: " + dir);
        }
        return new Point.Double(x, y);
    }

    /**
     * Returns this value coerced to be in the range of direction values.  This
     * handles negative values.
     *
     * @param dir A direction
     *
     * @return The modulus of that direction.
     */
    private static int inRange(int dir) {
        if (dir < 0)
            dir += NUM_DIRS;
        else if (dir >= NUM_DIRS)
            dir -= NUM_DIRS;
        return dir;
    }

    /**
     * Returns the direction that is opposite the given one.
     *
     * @param dir A direction
     *
     * @return The direction's opposite.
     */
    public static int oppositeDir(int dir) {
        return (dir + NUM_DIRS / 2) % NUM_DIRS;
    };

    /**
     * Sets the tile scale to the given one.  This sets the <CODE>scale</CODE>
     * field to be an appropriate scaling object.
     *
     * @param scale The size to scale to.
     */
    public static void setScale(double scale) {
        Tile.scale = new Scale(scale);
    }

    /**
     * Moves this tile the given distance.
     *
     * @param delta The distance to move.
     */
    public void move(Point.Double delta) {
        place(x + delta.getX(), y + delta.getY());
    }

    /**
     * Places the tile at the given coordinates on the board.  This updates the
     * (row,col) for this tile as well.
     *
     * @param x The <I>x</I> position on the board.
     * @param y The <I>y</I> position on the board.
     *
     * @see Board#place
     */
    public void place(double x, double y) {
//	System.out.println("place @ " + x + "," + y + ": " + this);
        this.x = x;
        this.y = y;
        board.place(x, y, this);
//	System.out.println("      @ " + x + "," + y + ": " + this);
    }

    /**
     * Returns the row in the grid associated with the given screen coordinate.
     *
     * @param y The y screen coordinate.
     *
     * @return The associated row in the grid.
     */
    public static int rowOf(double y) {
        return (int) Math.round(y / scale.halfGridY);
    }

    /**
     * Returns the column in the grid associated with the given screen
     * coordinate and row.
     *
     * @param row The row in which the x value lies.
     * @param x   The y screen coordinate.
     *
     * @return The associated row in the grid.
     */
    public static int colOf(int row, double x) {
        double colOff = (row % 2 == 0 ? 0.0 : scale.halfGridX);
        return (int) Math.round((x - colOff) / scale.gridX);
    }

    /**
     * Calculates the (x,y) values based on the current (row,col).
     */
    private void calcXY() {
        y = row * scale.halfGridY;
        double colOff = (row % 2 == 0 ? 0.0 : scale.halfGridX);
        x = col * scale.gridX + colOff;
    }

    public String toString() {
        return "Tile #" + num + " @ [" + row + "," + col + "], " + group;
    }

    /**
     * Returns <CODE>true</CODE> if the common edge between this tile and that
     * one have the same color.  The results are untrustworthy if the tiles are
     * not neighbors.
     *
     * @param that A neighboring tile.
     *
     * @return <CODE>true</CODE> if the edges match.
     */
    public boolean colorCompatible(Tile that) {
        int toThat = dirIn(that);
        int toThis = oppositeDir(toThat);
        System.out.println("colorCompatible: " + DIRECTION_NAMES[toThat] + "<->"
                + DIRECTION_NAMES[toThis]);
        System.out.println("this: " + this + ", (rot " + this.rotation + "): " + this.pattern);
        System.out.println("that: " + that + ", (rot " + that.rotation + "): " + that.pattern);
        boolean compatible = (colors[toThat].equals(that.colors[toThis]));
        System.out.println("compatible = " + compatible);
        return compatible;
    }

    /**
     * Sets the place in the grid that this tile belongs.
     *
     * @param row The row for this tile.
     * @param col The column for this tile.
     */
    public void setPlace(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Returns the row in the grid for this tile.
     *
     * @return The row in the grid for this tile.
     */
    public int getRow() {
        return row;
    }

    /**
     * Returns the column in the grid for this tile.
     *
     * @return The column in the grid for this tile.
     */
    public int getCol() {
        return col;
    }

    /**
     * Marks the image as needing to be redrawn because of some state change.
     */
    public void imageStale() {
        image = null;
    }

    /**
     * Prints the tiles that surround this one.
     */
    public void printSurround() {
        Iterator it = arbiter.tilesAround(this);
        System.out.println("this = " + this);
        while (it.hasNext()) {
            Tile adjacent = (Tile) it.next();
            System.out.println("  " + DIRECTION_NAMES[dirIn(adjacent)] + ": " + adjacent);
        }
    }

    /**
     * Calculates all the paths that this tile is part of.  This is a piece of
     * an overall investigation, which is represented by the parameters.  Any
     * direction that this tile has already participated in will be ignored.
     *
     * @param paths    The set of all paths being calculated.  This method will
     *                 add any newly-discovered paths to this set.
     * @param usedDirs A map for each tile to a boolean array that marks if a
     *                 given direction has already been traversed from this
     *                 tile.
     */
    public void pathsFrom(Set paths, Map usedDirs) {
        boolean[] used = booleanDirs(usedDirs);
        for (int dir = 0; dir < NUM_DIRS; dir++) {
            if (!used[oppositeDir(dir)])
                paths.add(new Path(this, dir, usedDirs));
        }
    }

    /**
     * Returns a boolean array from the map for this tile that represents all
     * directions.  If no array exists in the map, this will allocate one with
     * all values <CODE>false</CODE> and place it into the map before returning
     * it.
     *
     * @param arraysFor The map of tiles to existing direction arrays
     *
     * @return The array for this tile.
     */
    public boolean[] booleanDirs(Map arraysFor) {
        boolean[] used = (boolean[]) arraysFor.get(this);
        if (used == null) {
            used = new boolean[NUM_DIRS];
            arraysFor.put(this, used);
        }
        return used;
    }

    /**
     * Given an entry into this tile on the given edge, return the direction one
     * would exit from it along the line attached to that edge.
     *
     * @param dirIn The inbound direction.
     *
     * @return The corresponding outbound direction.
     */
    public int exitDir(int dirIn) {
//	System.out.println("exitDir(" + DIRECTION_NAMES[dirIn] + "), rot = " + rotation);
        int unrotDirIn = unrotated(dirIn);
        int exit = -1;
        for (int i = 0; exit < 0 && i < NUM_LINES; i++) {
            Line line = Pattern.line[pattern.line(i)];
            int edge1 = line.edge1();
            int edge2 = line.edge2();
//	    System.out.println(i + ": " + DIRECTION_NAMES[edge1] + " -> " + DIRECTION_NAMES[edge2]);
            if (edge1 == unrotDirIn)
                exit = edge2;
            else if (edge2 == unrotDirIn)
                exit = edge1;
        }
        if (exit >= 0)
            return (exit + rotation) % NUM_DIRS;
        throw new IllegalStateException("no exit for entry?");
    }

    /**
     * Returns the direction that corresponds to the input dir, unrotated by
     * this tile's current rotation.
     *
     * @param rotated The rotated direction.
     *
     * @return The unrotated direction.
     */
    public int unrotated(int rotated) {
        return (rotated - rotation + NUM_DIRS) % NUM_DIRS;
    }

    /**
     * Returns the direction that corresponds to the input dir, rotated by this
     * tile's current rotation.
     *
     * @param unrotated The unrotated direction.
     *
     * @return The rotated direction.
     */
    public int rotated(int unrotated) {
        return (unrotated + rotation) % NUM_DIRS;
    }

    /**
     * Returns the neighboring tile in the given direction.  If there is none,
     * returns <CODE>null</CODE>.  If there is more than one tile in that
     * direction, throws <CODE>IllegalArgumentException</CODE>.
     *
     * @return The neighboring tile in the given direction.
     *
     * @throws IllegalArgumentException Multiple tiles lie in that direction.
     */
    public Tile neighbor(int dir) {
        int nRow = row + rowDelta(dir);
        int nCol = col + colDelta(nRow, dir);
        List contents = arbiter.getTiles(nRow, nCol);
//	System.out.println("neighbor(" + DIRECTION_NAMES[dir] + "), " + nRow + ", " + nCol);
        return visibleAt(contents);
    }

    /**
     * Returns the tile that is visible from the contents of a grid position. If
     * there is none, returns <CODE>null</CODE>.
     *
     * @param contents The contents of the grid position.
     *
     * @return The visible tile or <CODE>null</CODE>.
     *
     * @throws IllegalArgumentException There is more than one visible tile.
     */
    public Tile visibleAt(List contents) {
        Tile vis = null;
        for (Iterator it = contents.iterator(); it.hasNext();) {
            Tile tile = (Tile) it.next();
            if (board.isVisible(tile)) {
                if (vis == null)
                    vis = tile;
                else
                    throw new IllegalArgumentException("multiple visible");
            }
        }
        return vis;
    }

    /**
     * Returns <CODE>true</CODE> if this tile is currently selected.
     *
     * @return <CODE>true</CODE> if this tile is currently selected.
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Returns the width of a tile in the current scaling.
     *
     * @return The width of a tile in the current scaling.
     */
    public static double getWidth() {
        return scale.gridX;
    }

    /**
     * Returns the height of a tile in the current scaling.
     *
     * @return The height of a tile in the current scaling.
     */
    public static double getHeight() {
        return scale.gridY;
    }

    /**
     * There is a new scale; react to it.
     */
    public void newScale() {
        imageStale();
        calcXY();
    }

    /**
     * Returns the tile's owner.
     *
     * @return The tile's owner.
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * Sets the tile's owner.
     *
     * @param owner The tile's owner.
     */
    public void setOwner(Player owner) {
        this.owner = owner;
    }

    /**
     * Returns the current scaling in use.
     *
     * @return The current scaling in use.
     */
    public static double getScale() {
        return scale.scale;
    }
}
