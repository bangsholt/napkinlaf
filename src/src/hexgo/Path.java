/*
 * Created by IntelliJ IDEA.
 * User: arnold
 * Date: Sep 12, 2002
 * Time: 7:51:44 AM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package hexgo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class represents a single path through the tiles on the board.
 */
public class Path implements HexgoConstants {
    private Map tiles;		// the tiles this path runs through (unordered)
    private int score;		// the score for this path
    private int newCount;	// number of new tiles in the path
    private boolean loop;	// does this path loop?
    private boolean fromMain;
    private boolean isPrimary;
    private boolean everIntoMain;

    private static final boolean DEBUG = false;

    /**
     * Creates a new <CODE>Path</CODE> object.  This calculates all the path
     * values.
     *
     * @param tile One tile in the path.
     * @param dirIn The direction to start from, entering the tile.
     * @param usedDirs A map for each tile to a boolean array saying which
     * 		directions have already been traversed.
     */
    public Path(Tile tile, int dirIn, Map usedDirs) {
	if (tile == null)
	    throw new NullPointerException();

	// First we start with the specified input direction.
	tiles = new HashMap();
	out(0, "\nFirst way");
	walkPath(null, tile, dirIn, usedDirs, false, 0);

	// Then, if the path doesn't loop, we follow it out the other way
	if (!loop) {
	    out(0, "Other way");
	    int dirFrom = Tile.oppositeDir(dirIn);
	    //int dirOut = tile.exitDir(dirFrom);
	    out(0, "dirIn\t= " + DIRECTION_NAMES[dirIn]);
	    out(0, "dirFrom\t= " + DIRECTION_NAMES[dirFrom]);
	    Tile neighbor = tile.neighbor(dirFrom);
	    out(0, "neighbor = " + neighbor);
	    fromMain = everIntoMain;
	    out(0, "fromMain = " + fromMain);
	    walkPath(tile, neighbor, dirFrom, usedDirs, true, 1);
	}
    }

    /**
     * Returns <CODE>true</CODE> if this path goes out of the main group and
     * later re-enters it.
     *
     * @return <CODE>true</CODE> if the path goes out and then in.
     */
    public boolean isPrimary() {
	return isPrimary;
    }

    /**
     * Walk the given path.  This method works with the given tile, as entered
     * from the specified direction.  After accounting for this step in the
     * path, it recurses (if necessary) to the next step.  Only tiles in the
     * main group and selected groups are followed.
     *
     * @param fromTile The tile we're coming to this one from (if any).
     * @param tile The tile to walk.
     * @param dirIn The direction traversed in to this tile.
     * @param swap Swap the tiles (we're moving in reverse).
     * @param usedDirs The map of used directions for all tiles.
     * @param level The level of recursion (for debugging).
     */
    private void walkPath(Tile fromTile, Tile tile, int dirIn, Map usedDirs,
			  boolean swap, int level) {
	out(level, "walkPath(" + tile + ")");
	if (loop)
	    return;		// don't walk farther along a loop
	if (tile == null)
	    return;

	Group group = tile.getGroup();
	if (group == null)
	    Hexgo.doBreak();
	out(level, "group " + group + ", isSelected = " + group.isSelected());
	if (!group.isMain() && !group.isSelected())
	    return;

	// This traversal is OK, now see how it affects the in/out check
	checkPrimary(fromTile, tile, level);

	/*
	 * A note on terminology: "dirIn" is the direction that is travelled
	 * going in to this tile; "dirFrom" is the direction that this tile
	 * is enetered from; and "dirOut" is the direction we will exit this
	 * tile (and which therefore is the next tile's "dirIn").  For example,
	 * given a tile that that has a short path connecting the RIGHT edge
	 * to the UPPER_RIGHT edge, and which is approached from the tile on its
	 * RIGHT, "dirIn" will be LEFT (we're leaving the on the right and
	 * travelling to that tile's LEFT); "dirFrom" will be RIGHT (it is
	 * coming at this tile from this tile's RIGHT), and "dirOut" will be
	 * UPPER_RIGHT.  And therefore when we move to the next tile (the one
	 * to this tile's upper-right), that tile's "dirIn" will be UPPER_RIGHT.
	 *
	 *	______
	 *     /      \
	 *   /          \
	 *  |            |
	 *  | dirFrom--> |  <--dirIn
	 *  |            |
	 *   \          /
	 *     \______/
	 *
	 */
	int dirFrom = Tile.oppositeDir(dirIn);
	int dirOut = tile.exitDir(dirFrom);
	boolean[] used = tile.booleanDirs(usedDirs);
	out(level, "dirIn\t= " + DIRECTION_NAMES[dirIn]);
	out(level, "dirFrom\t= " + DIRECTION_NAMES[dirFrom] + (used[dirFrom] ? "" : " not") + " used");
	out(level, "dirOut\t= " + DIRECTION_NAMES[dirOut] + (used[dirOut] ? "" : " not") + " used");
	if (used[dirOut]) {
	    score *= 2;			// loops score double
	    loop = true;		// come around in a loop
	    isPrimary = everIntoMain;	// if we went in once we must have left
	    out(level, "loop = " + loop);
	    out(level, "score = " + score);
	} else {
	    score++;		// each tile is one point
	    if (tiles.containsKey(tile))
		score += 2;	// crossover is two more
	    if (!tile.getGroup().isMain())
		newCount++;
	    boolean[] inPath = tile.booleanDirs(tiles);
	    out(level, "score = " + score);
	    inPath[dirFrom] = used[dirFrom] = true;
	    inPath[dirOut] = used[dirOut] = true;
	    Tile neighbor = tile.neighbor(dirOut);
	    out(level, "neighbor = " + neighbor);
	    walkPath(tile, neighbor, dirOut, usedDirs, swap, level + 1);
	}
    }

    public boolean usesAll() {
/*###155 [cc] operator == cannot be applied to boolean,int%%%*/
	return (newCount == 7);
    }

    /**
     * Check if a transition from one tile to another crosses either in or
     * out of the main group.
     *
     * @param from The tile we're moving from.
     * @param to The tile we're moving to.
     * @param level The recursion level (for debugging).
     */
    private void checkPrimary(Tile from, Tile to, int level) {
	if (isPrimary)	// we already know the answer
	    return;

	out(level, "checkPrimary: " + from + ", " + to);
	if (from == null || to == null)
	    return;

	boolean fromTileMain = from.getGroup().isMain();
	boolean toTileMain = to.getGroup().isMain();
	out(level, "fromTileMain = " + fromTileMain + ", toTileMain = " +
		toTileMain);
	if (fromTileMain && !toTileMain)
	    fromMain = true;
	else if (!fromTileMain && toTileMain) {
	    if (fromMain)
		isPrimary = true;
	    fromMain = false;
	    everIntoMain = true;
	}
	out(level, "fromMain = " + fromMain + ", isPrimary = " + isPrimary +
		", everIntoMain = " + everIntoMain);
    }

    /**
     * Prints out a debugging message.
     *
     * @param level The recursion level.
     * @param message The message to print.
     */
    private static void out(int level, String message) {
	if (!DEBUG)
	    return;
	for (int i = 0; i < level; i++)
	    System.out.print('.');
	System.out.println(message);
    }

    /**
     * Returns the score for this path.
     * @return The score for this path.
     */
    public int getScore() {
	return score;
    }

    // inherit doc comment
    public String toString() {
	StringBuffer buf = new StringBuffer();
	buf.append(score).append(": ");
	if (loop)
	    buf.append("(loop) ");
	if (isPrimary())
	    buf.append("(primary) ");
	boolean first = true;
	for (Iterator it = tiles.keySet().iterator(); it.hasNext();) {
	    Tile tile = (Tile) it.next();
	    if (!first)
		buf.append(", ");
	    buf.append(tile);
	    first = false;
	}
	return buf.toString();
    }

    /**
     * Returns the number of tiles contained in this path.
     * @return The number of tiles contained in this path.
     */
    public int tileCount() {
	return tiles.size();
    }

    /**
     * Returns <CODE>true</CODE> if that path contains exactly the same tiles
     * as this path, or are already in the main group.
     *
     * @param that The path to compare to.
     * @return <CODE>true</CODE> if they use the same tiles.
     */
    public boolean sameTiles(Path that) {
	return containsAllTiles(that) && that.containsAllTiles(this);
    }

    /**
     * Returns <CODE>true</CODE> if that path's tiles are all contained in
     * this path, or are already in the main group.
     *
     * @param that The path whose tiles should all be in this path.
     * @return <CODE>true</CODE> if the tiles are all in this path.
     */
    public boolean containsAllTiles(Path that) {
	for (Iterator it = that.tiles.keySet().iterator(); it.hasNext();) {
	    Tile tile = (Tile) it.next();
	    if (!tile.getGroup().isMain() && !tiles.containsKey(tile))
		return false;
	}
	return true;
    }

    public boolean inPath(Tile tile, Line line) {
	boolean[] inPath = (boolean[]) tiles.get(tile);
	if (inPath == null)
	    return false;
	return inPath[tile.rotated(line.edge1())];
    }
}
