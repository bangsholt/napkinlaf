// $Header$

package hexgo;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class represents a group of tiles.  All groups have at least one tile.
 */
public class Group implements HexgoConstants {
    private Arbiter arbiter;		// the game's arbiter
    private List tiles;			// the tiles in this group
    private Point pos;			// the offset position (used to drag)
    private Tile selectedTile;		// the currently selected tile, or null
    private Board board;		// the board we're on
    private boolean isMain;		// is this the main group?
    private final int num;		// an identifying number

    private static int nextNum = 0;	// the next identifying number to use

    /**
     * Creates a new <CODE>Group</CODE> object.
     *
     * @param board   The board the group lives on.
     * @param initial The initial tile in the group.
     */
    public Group(Board board, Tile initial) {
        this.board = board;
        arbiter = board.getArbiter();
        tiles = new ArrayList();
        adopt(initial);
        pos = new Point(0, 0);
        num = nextNum++;
    }

    /**
     * Makes this group the main group.
     */
    public void makeMain() {
        isMain = true;
    }

    /**
     * Adopts the given tile into this group.  If the group isn't the main
     * group, this group is pushed on the tile's stack of recent groups.
     *
     * @param tile The tile to adopt.
     */
    private void adopt(Tile tile) {
        tiles.add(tile);
        tile.setGroup(this);

        if (isMain)
            tile.setOwner(null);
    }

    /**
     * Displace the group by the given screen distance.
     *
     * @param x The <I>x</I> distance.
     * @param y The <I>y</I> distance.
     */
    public void displace(int x, int y) {
        pos = new Point(pos.x + x, pos.y + y);
    }

    /**
     * Sets the group to live at the given position.
     *
     * @param x
     * @param y
     */
    public void setPosition(int x, int y) {
        if (tiles.size() != 1)
            throw new IllegalStateException("cannot set pos for multis (yet)");
        Tile tile = ((Tile) tiles.get(0));
        pos = new Point(intOf(x - tile.getX()), intOf(y - tile.getY()));
    }

    /**
     * Conveneince method to return the rounded <CODE>int</CODE> value from a
     * <CODE>double</CODE>.
     *
     * @param v The value to round.
     *
     * @return The rounded value.
     */
    private int intOf(double v) {
        return Board.intOf(v);
    }

    /**
     * Snap this group to the nearest grid position.
     */
    public void snap() {
        Point.Double delta = Tile.snapDelta(pos);
        for (int i = 0; i < tiles.size(); i++) {
            Tile tile = (Tile) tiles.get(i);
            tile.move(delta);
        }
        pos.x = 0;
        pos.y = 0;
    }

    /**
     * Returns the tile at the given screen coordinates, adjusting for any
     * offset of the group itself.
     *
     * @param x The <I>x</I> screen coordinate.
     * @param y The <I>y</I> screen coordinate.
     *
     * @return The tile at the coordinates, or <CODE>null</CODE> if there is
     *         none.
     */
    public Tile findTileAt(double x, double y) {
        return findTileAtLocalCoords(x - pos.x, y - pos.y);
    }

    /**
     * Returns the tile at the given screen coordinates.  The coordinates are
     * assumed to be adjusted for our current offset.
     *
     * @param x The <I>x</I> screen coordinate.
     * @param y The <I>y</I> screen coordinate.
     *
     * @return The tile at the coordinates, or <CODE>null</CODE> if there is
     *         none.
     */
    private Tile findTileAtLocalCoords(double x, double y) {
        List contents = board.getTilesAt(x, y);
        for (int i = 0; i < contents.size(); i++) {
            Tile tile = (Tile) contents.get(i);
            if (tile.getGroup() == this)
                return tile;
        }
        return null;
    }

    /**
     * Sets the selected tile to be the specified one.  If an existing tile of
     * the group is already selected, it is deselected in favor of this new one.
     * If the tile is <CODE>null</CODE>, no tile will end up selected.
     *
     * @param selected The tile to select.
     */
    public void setSelected(Tile selected) {
        if (selectedTile == selected)
            return;

        if (selectedTile != null)
            selectedTile.setSelected(false);
        this.selectedTile = selected;
        if (selectedTile != null)
            selectedTile.setSelected(true);
    }

    /**
     * Returns <CODE>true</CODE> if this group is currently selected.
     *
     * @return <CODE>true</CODE> if this group is currently selected.
     */
    public boolean isSelected() {
        return selectedTile != null;
    }

    /**
     * Returns the colors to use when drawing this group.
     *
     * @return The colors to use when drawing this group.
     */
    public TileColors colors() {
        if (isSelected())
            return TileColors.SELECTED;
        else if (isMain())
            return TileColors.MAIN;
        else
            return TileColors.HAND;
    }

    /**
     * Rotate this group around its selected tile.  The coordinates of all the
     * component tiles are updated.  If there is no selected tile, this has no
     * effect.
     */
    public void rotate(int count) {
        if (selectedTile == null)
            return;

        // translate to the tile's center, rotate, and then back
        AffineTransform matrix = AffineTransform.getTranslateInstance(selectedTile.getX(), selectedTile.getY());
        matrix.rotate(count * ROTATE_ONCE);
        matrix.translate(-selectedTile.getX(), -selectedTile.getY());

        for (int i = 0; i < tiles.size(); i++)
            ((Tile) tiles.get(i)).rotate(count, matrix);
    }

    /**
     * Paints this group, including all its component tiles.
     *
     * @param g The graphics object to use.
     */
    public void paintComponent(Graphics g) {
        g.translate(pos.x, pos.y);
        for (int i = 0; i < tiles.size(); i++) {
            Tile tile = ((Tile) tiles.get(i));
//	    System.out.println("paint tile" + tile);
            if (tile != selectedTile)
                tile.paintComponent(g.create());
        }
        // paint this last so any decoration is on top
        if (selectedTile != null)
            selectedTile.paintComponent(g.create());

        if (Hexgo.paintDebug()) {
            Graphics orig = g;
            orig.setColor(Color.black);
            for (int i = 0; i < tiles.size(); i++)
                ((Tile) tiles.get(i)).paintConnections(orig.create());
        }
    }

    /**
     * Returns <CODE>true</CODE> if that other group can be merged with this
     * one.  This asks if each tile of one of the groups can be merged with the
     * other group.
     *
     * @param that The other group.
     *
     * @return <CODE>true</CODE> if the groups can be merged.
     *
     * @see #isMergeable
     */
    public boolean canMerge(Group that) {
        if (that == this)
            return false;

        System.out.println(this + " canMerge(" + that + ")");
        // non-main mereley has to be non-conflicting; main must be right
        boolean doMerge = !(isMain() || that.isMain());
        for (Iterator it = that.tiles.iterator(); !doMerge && it.hasNext();) {
            Tile tile = (Tile) it.next();
            int mergeable = isMergeable(tile);
            if (mergeable == NO)
                return false;
            else if (mergeable == YES)
                doMerge = true;
        }
        System.out.println(this + " canMerge: " + doMerge);
        return doMerge;
    }

    /**
     * Adopts all the tile from that group into this one.
     *
     * @param that The other group.
     */
    public void adoptAll(Group that) {
        for (Iterator it = that.tiles.iterator(); it.hasNext();) {
            Tile tile = (Tile) it.next();
            adopt(tile);
        }

        isMain |= that.isMain;		// adding to the main getGroup?
        if (isMain)
            setSelected(null);		// nothing in main is ever selected

        newScale();
    }

    /**
     * Marks the border for this group as stale
     */
    public void newScale() {
        // the border for this group is composed of the borders of the tiles
        for (Iterator it = tiles.iterator(); it.hasNext();) {
            Tile tile = (Tile) it.next();
            tile.newScale();
        }
    }

    /**
     * Returns a value defining the mergeability of the given tile from another
     * group into this group.  The return values are: <CODE>YES</CODE>,
     * <CODE>NO</CODE>, and <CODE>MAYBE</CODE>.  A tile is mergeable if it is
     * adjacent to one tile in this group, and the colors on all edges are
     * compatible with their facing tile's edges in this group.  It must also
     * not overlap with any other tiles.  If these tests fail, a <CODE>NO</CODE>
     * is returned.  If no tile in this group is near enough to perform such a
     * test, a <CODE>MABYE</CODE> is returned (the tile does not affect the
     * mergeability of the tile's group).  Otherwise a <CODE>YES</CODE> is
     * returned.
     *
     * @param nTile The tile to check for mergeability.
     *
     * @return <CODE>YES</CODE>, <CODE>NO</CODE>, or <CODE>MAYBE</CODE>.
     */
    private int isMergeable(Tile nTile) {
        System.out.println(this + " isMergeable(" + nTile + ")");

        List samePlace = arbiter.getTiles(nTile.getRow(), nTile.getCol());
        Tile vis = null;
        try {
            vis = nTile.visibleAt(samePlace);
        } catch (IllegalArgumentException e) {
            ;	// means more than one tile there, so null is OK value
        }
        if (vis != nTile) {
            System.out.println("  multiple tiles @ place");
            return NO;
        }

        boolean foundNeighbor = false;
        Iterator it = arbiter.tilesAround(nTile);
        while (it.hasNext()) {
            Tile t = (Tile) it.next();
            if (t.getGroup() != this)		// ignore irrelevant tiles
                continue;
            if (!t.colorCompatible(nTile))	// mismatch, so not mergeable
                return NO;
            else				// found a matching neighbor
                foundNeighbor = true;
        }

        // if we found a neighbor, then we are definitely happy
        int answer = (foundNeighbor ? YES : MAYBE);
        System.out.println("  " + this + " isMergeable: " + answer);
        return answer;
    }

    /**
     * Returns <CODE>true</CODE> if this is the main group.
     *
     * @return <CODE>true</CODE> if this is the main group.
     */
    public boolean isMain() {
        return isMain;
    }

    /**
     * Returns the currently selected tile or <CODE>null</CODE>.
     *
     * @return The currently selected tile or <CODE>null</CODE>.
     */
    public Tile getSelected() {
        return selectedTile;
    }

    /**
     * Breaks up this group's tiles into a set of groups with one tile in each
     * group.
     *
     * @return The set of groups created by the breakup.
     */
    public Collection breakUp() {
        if (tiles.size() == 1)	// nothing to break up
            return Collections.singleton(this);

        List groups = new ArrayList(tiles.size());
        for (int i = 0; i < tiles.size(); i++) {
            Tile tile = (Tile) tiles.get(i);
            tile.makeAlone();
            Group ng = new Group(board, tile);
            if (tile == selectedTile)
                ng.setSelected(tile);
            groups.add(ng);
        }
        return groups;
    }

    /**
     * Returns the board this group is on.
     *
     * @return The board this group is on.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Calculates the paths that emerge from this group.  Paths are added to the
     * passed-in set.  The two input parameters are used to maintain values
     * between calls to <CODE>pathsFrom</CODE>, both here and in
     * <CODE>Tile.pathsFrom</CODE>.  The idea is to build a complete list of
     * paths that would result from a given play, so we want to traverse each
     * path exactly once, even if it runs through multiple groups and/or tiles.
     * <P> Because this is used to check the playability of tiles, only tiles in
     * the main group and any selected groups are allowed in paths.
     *
     * @param paths    The set of paths this should add paths to.
     * @param usedDirs Maps each tile to a boolean array that says whether any
     *                 given direction has been used while plotting the paths in
     *                 the set.
     */
    public void pathsFrom(Set paths, Map usedDirs) {
        for (Iterator it = tiles.iterator(); it.hasNext();) {
            Tile tile = (Tile) it.next();
            tile.pathsFrom(paths, usedDirs);
        }
    }

    /**
     * Returns the tiles in this group.
     *
     * @return The tiles in this group.
     */
    public Collection tiles() {
        return tiles;
    }

//    public String toString() {
//	return "Group #" + num + "/" + super.toString();
//    }
}
