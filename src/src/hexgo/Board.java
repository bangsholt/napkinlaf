// $Header$

package hexgo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class represents the playing board itself.  It handles almost all of the
 * UI events.
 */
public class Board extends JPanel {
    private Arbiter arbiter;		// the game's arbiter
    private Player player;		// the current player
    private List groups;		// the currently displayed groups
    private JLabel turnLabel;
    private Point lastMouse;		// the previous mouse position
    private Group selected;		// the currently selected group, or null
    private boolean dragged;		// was the mouse dragged?
    private Set paths;			// the current completed paths (if any)

    /** The color of the playfield (sort of a felt green). */
    public static final Color PLAYFIELD = new Color(27, 116, 33).darker();

    private static int SCALE = 30;	// the scaling to use

    /**
     * This class handles mouse buttons on the playing field.
     */
    private class BoardMouse extends MouseAdapter {

        public void mousePressed(MouseEvent ev) {
            requestFocus();
            lastMouse = ev.getPoint();	// remember where we started
            dragged = false;		// haven't dragged *yet*
        }

        public void mouseReleased(MouseEvent ev) {
            if (dragged) {		// if we dragged a group along
                displace(ev);		// move to final position
                if (selected != null)	// snap group to grid
                    selected.snap();
                repaint();
                calculateSizes();
            }
        }

        /**
         * Handle a simple "click".  There are essentially five possibilities:
         * <DL> <DT>The click is over no tile. <DD>Any selected group is
         * unselected. <DT>The click is on an unselected tile. <DD>The tile's
         * group is selected, and the tile becomes the selected tile in the
         * group.  Any previous selection is dropped. <DT>The click is on a
         * selected tile. <DD>The group becomes unselected. <DT>The click is on
         * an unselected tile and the shift key is pressed. <DD>The tile's group
         * is merged into a group containing all other selected groups, and the
         * tile is made the current tile. <DT>The click is on a selected tile
         * and the shift key is pressed. <DD>The most recent group of which that
         * tile was a part is recreated, breaking up the existing group into its
         * most recent component groups.  The group is made the selected group,
         * and the tile is made the selected tile. </DL>
         *
         * @param ev
         */
        public void mouseClicked(MouseEvent ev) {
            Tile clickedTile = tileAt(ev.getPoint());

            if (clickedTile == null) {
                // The click is over no tile.
                if (selected != null) {
                    deselect();
                    repaint();
                }
                return;
            }

            Group clickedGroup = clickedTile.getGroup();

            if (!ev.isShiftDown()) {
                if (!clickedTile.isSelected()) {
                    // The click is on an unselected tile
                    if (clickedGroup != selected && selected != null)
                        selected.setSelected(null);
                    clickedGroup.setSelected(clickedTile);
                    selected = clickedGroup;
                } else {
                    // The click is on a selected tile
                    clickedGroup.setSelected(null);
                    selected = null;
                }
            } else {
                if (!clickedGroup.isSelected()) {
                    // The click is on an unselected tile, shifted
                    if (selected == null) {
                        selected = clickedGroup;
                    } else {
                        if (clickedGroup.canMerge(selected)) {
                            selected.adoptAll(clickedGroup);
                            groups.remove(clickedGroup);
                        }
                    }
                    selected.setSelected(clickedTile);
                } else {
                    // The click is on a selected tile, shifted
                    Collection broken = selected.breakUp();
                    groups.remove(selected);
                    groups.addAll(broken);

                    Group newGroup = clickedTile.getGroup();
                    if (newGroup != clickedGroup)
                        clickedGroup.setSelected(null);
                    newGroup.setSelected(clickedTile);
                    selected = newGroup;
                }
            }
            if (selected != null)
                makeTop(selected);
            repaint();
        }

    }

    /**
     * This class handles mouse motion on the playing field.
     */
    private class BoardMouseMotion extends MouseMotionAdapter {
        public void mouseDragged(MouseEvent ev) {
            if (!dragged) {
                if (selected != null)
                    selected.setSelected(null);
                selected = selectAt(lastMouse);
                dragged = true;
            }
            displace(ev);
            repaint();
        }
    }

    /**
     * This class handles keystrokes on the playing field.
     */
    private class BoardKey extends KeyAdapter {
        public void keyTyped(KeyEvent ev) {
            System.out.println();
            switch (ev.getKeyChar()) {
            case 'p':
                playTiles(true);
                break;
            case 'u':
                breakUp();
                break;
            case '>':
                rotateGroup(+1);
                calculateSizes();
                break;
            case '<':
                rotateGroup(-1);
                calculateSizes();
                break;
            case 'r':
                resetView();
                break;
            case 'd':
                discardTiles();
                break;

            case 'T':
                dealTile();
                break;
            case 'P':
                playTiles(false);
                break;
            case 'N':	// used for debug
                System.out.println("-----------------------------------------");
                break;
            case 'B':	// used for debug
                Hexgo.doBreak = !Hexgo.doBreak;
                break;
            case 'Q':
                System.exit(0);
                break;	// not needed, but it looks nicer with it here
            case 'C':
                dumpColor("Playfield: ", Board.PLAYFIELD);
                dumpColor("Main: ", TileColors.MAIN.tile);
                dumpColor("Hand: ", TileColors.HAND.tile);
                dumpColor("Selected: ", TileColors.SELECTED.tile);
                break;
            case 'S':
                if (selected == null)
                    System.out.println("selected == null");
                else if (selected.getSelected() == null)
                    System.out.println("selected.getSelected() == null");
                else
                    selected.getSelected().printSurround();
                arbiter.dump();
                break;
            }
        }
    }

    /**
     * Creates a new <CODE>Board</CODE> objec.
     *
     * @param deck The deck to draw from.
     */
    public Board(Arbiter arbiter, JLabel turnLabel) {
        this.arbiter = arbiter;
        this.turnLabel = turnLabel;
        Tile.setScale(SCALE);
        groups = new ArrayList();
        paths = new HashSet();
        dealTile();
        ((Group) groups.get(0)).makeMain();

        // set some graphics basics
        setBackground(PLAYFIELD);
        calculateSizes();
        addMouseListener(new BoardMouse());
        addMouseMotionListener(new BoardMouseMotion());
        addKeyListener(new BoardKey());
        setOpaque(true);
        resetView();

        nextTurn(null);
    }

    /**
     * Calculate the sizes of the tiles on the board so we know how big the
     * space to display is.
     */
    private void calculateSizes() {
        Dimension origMin = getMinimumSize();
        Dimension origPref = getPreferredSize();

        double minX, maxX, minY, maxY;
        minX = minY = Double.MAX_VALUE;
        maxX = maxY = Double.MIN_VALUE;

        List dealt = arbiter.dealtTiles();
        for (int i = 0; i < dealt.size(); i++) {
            Tile tile = (Tile) dealt.get(i);
            minX = Math.min(minX, tile.getX());
            minY = Math.min(minY, tile.getY());
            maxX = Math.max(maxX, tile.getX());
            maxY = Math.max(maxY, tile.getY());
        }

        // make it symetric about (0,0)
//	if (minX < -maxX)
//	    maxX = -minX;
//	else
//	    minX = -maxX;
//	if (minY < -maxY)
//	    maxY = -minY;
//	else
//	    minY = -maxY;

        minX -= Tile.getWidth();
        minY -= Tile.getHeight();
        maxX += Tile.getWidth();
        maxY += Tile.getHeight();

        int minWide = intOf(maxX - minX);
        int minHigh = intOf(maxY - minY);
        Dimension minSize = new Dimension(minHigh, minWide);

        int prefWide = Math.max(500, minWide);
        int prefHigh = Math.max(500, minHigh);
        Dimension prefSize = new Dimension(prefWide, prefHigh);

        if (!origMin.equals(minSize))
            setMinimumSize(minSize);
        if (!origPref.equals(prefSize)) {
            setPreferredSize(prefSize);
            setSize(prefSize);
        }
    }

    /**
     * Convenience method to return the rounded <CODE>int</CODE> of a
     * <CODE>double</CODE> value.
     *
     * @param v The value.
     *
     * @return The rounded <CODE>int</CODE>.
     */
    public static int intOf(double v) {
        return (int) Math.round(v);
    }

    /**
     * Play the currently selected tiles onto the main group.  If the move is
     * not legal, this does nothing.
     */
    public void playTiles(boolean doChecks) {
        if (selected == null)
            return;

        Group main = mainGroup();

        if (doChecks) {
            if (!main.canMerge(selected))
                return;

            // these are used to pass info between recursive calls to pathsFrom
            paths.clear();
            Map usedDirs = new HashMap();
            selected.pathsFrom(paths, usedDirs);
            if (!onlyValidPaths(paths)) {
                paths.clear();
                return;
            }
            printPaths(paths);
        }

        int score = totalScore(paths);
        player.add(score);
        nextTurn(selected);

        main.adoptAll(selected);

        groups.remove(selected);
        selected.setSelected(null);
        makeBottom(main);
        repaint();
    }

    /**
     * Move to the next turn, removing the given played tiles.  If there is a
     * current player, their hand will be filled.  Then the next player becomes
     * the current player.
     *
     * @param played The tiles that were played; can be <CODE>null</CODE>.
     */
    private void nextTurn(Group played) {
        if (player != null) {
            player.setCurrent(false);
            player.fillHand(played, this);
        }

        arbiter.nextTurn();

        turnLabel.setText("Turn: " + arbiter.getTurn());

        player = arbiter.currentPlayer();
        player.setCurrent(true);
        player.fillHand(null, this);
//	resetView();
    }

    /**
     * Returns the total score from all the paths in the set.
     *
     * @param paths The paths to get scores from.
     *
     * @return The total score.
     */
    private int totalScore(Set paths) {
        int score = 0;
        boolean usesAll = false;
        for (Iterator it = paths.iterator(); it.hasNext();) {
            Path path = (Path) it.next();
            score += path.getScore();
            usesAll |= path.usesAll();
        }
        if (usesAll)
            score += 25;
        return score;
    }

    /**
     * Dumps out a color for debugging.
     *
     * @param name  The name of the color (it's purpose).
     * @param color The color.
     */
    private static void dumpColor(String name, Color color) {
        System.out.println(name + ": " + color + ", trans = " +
                color.getAlpha());
    }

    /**
     * Returns the main group of the board.
     *
     * @return The main group of the board.
     */
    private Group mainGroup() {
        for (Iterator it = groups.iterator(); it.hasNext();) {
            Group group = (Group) it.next();
            if (group.isMain())
                return group;
        }
        return null;
    }

    /**
     * Returns true if at least one of the paths is a valid play.  Valid play
     * paths are ones that at least once exit the main group and re-enter it.
     * Other paths can be created as long as all their tiles are part of this
     * primary path.  All paths that do not contribute to scoring are removed,
     * primarily those "paths" that only have one element.
     *
     * @param paths The set of paths.
     *
     * @return <CODE>true</CODE> if at least one path is valid.
     */
    private boolean onlyValidPaths(Set paths) {
        System.out.println("Board.onlyValidPaths");

        /*
         * First, find the longest potential primary path.  There can be more
         * than one longest path, as long as they all use the same tiles.
         */
        boolean onlyValid = false;
        Path primary = null;
        for (Iterator it = paths.iterator(); it.hasNext();) {
            Path path = (Path) it.next();
            System.out.println("  checking " + path);
            if (!path.isPrimary()) {	// not really a path
                System.out.println("  not primary; removing");
                it.remove();
            } else if (primary == null) {	// found a primary path yet?
                System.out.println("  setting primary");
                primary = path;
            } else if (primary.tileCount() < path.tileCount()) {
                System.out.println("  found longer primary");
                primary = path;
            }
        }

        if (primary == null) {		// no main path found
            System.out.println("  no main path");
            return onlyValid;
        }

        // make sure all paths are built from only the tiles in the primary path
        for (Iterator it = paths.iterator(); it.hasNext();) {
            Path path = (Path) it.next();
            System.out.println("  checking build of " + path);
            if (path != primary && !primary.containsAllTiles(path)) {
                System.out.println("    path != primary && !primary.containsAllTiles(path)");
                return onlyValid;
            }
        }

        System.out.println("  return true");
        onlyValid = true;
        return onlyValid;
    }

    /**
     * Prints out the paths in the set.
     *
     * @param paths The paths to print.
     */
    private void printPaths(Set paths) {
        System.out.println(paths.size() + " path(s)");
        for (Iterator it = paths.iterator(); it.hasNext();) {
            Path path = (Path) it.next();
            System.out.println("  " + path);
        }
    }

    /**
     * Breaks up the selected group into a set of groups, one per tile.
     */
    private void breakUp() {
        if (selected == null)
            return;

        breakUp(selected);

        selected = null;
        for (int i = 0; i < groups.size(); i++) {
            Group group = (Group) groups.get(i);
            if (group.isSelected()) {
                selected = group;
                makeTop(selected);
                break;
            }
        }

        repaint();
    }

    /**
     * Breaks up the group into individual tiles.
     *
     * @param toBreak The group to break up.
     */
    private void breakUp(Group toBreak) {
        groups.remove(toBreak);
        groups.addAll(toBreak.breakUp());
    }

    /**
     * Makes this group be the bottom-most group.
     *
     * @param group Group to push to the bottom.
     */
    private void makeBottom(Group group) {
        if (groups.size() < 1 && groups.get(0) != group) {
            groups.remove(group);
            groups.add(0, group);
        }
    }

    /**
     * Makes this group be the top-most group.
     *
     * @param group Group to push to the top.
     */
    private void makeTop(Group group) {
        if (groups.size() > 1 && groups.get(groups.size() - 1) != group) {
            groups.remove(group);
            groups.add(group);
        }
    }

    /**
     * Displaces the currently-slected group by the amount indicated by the
     * given event's displacement from the last event.
     *
     * @param ev The most recent mouse event.
     */
    private void displace(MouseEvent ev) {
        if (selected != null) {
            Point cur = ev.getPoint();
            selected.displace(cur.x - lastMouse.x, cur.y - lastMouse.y);
            lastMouse = cur;
        }
    }

    /**
     * Creates a new one-tile group.
     */
    public Tile dealTile() {
        Tile tile = arbiter.nextTile();
        if (tile != null) {
            groups.add(new Group(this, tile));
            place(tile.getX(), tile.getY(), tile);
            repaint();
        }
        return tile;
    }

    /**
     * Place the given tile at the specified position.  Sets the tile's row and
     * column as appropriate.  The tile is removed from its current position.
     *
     * @param nx   The new <i>x</i> position.
     * @param ny   The new <i>y</i> position.
     * @param tile The tile to place.
     */
    public void place(double nx, double ny, Tile tile) {
        int nRow = Tile.rowOf(ny);
        int nCol = Tile.colOf(nRow, nx);
        arbiter.placeTile(tile, nRow, nCol);

    }

    /**
     * Gets the contents of the cell at the given x and y screen coordinates.
     *
     * @param x The <I>x</I> coordinate of the cell.
     * @param y The <I>y</I> coordinate of the cell.
     *
     * @return A list of tiles at the given screen coordinates.
     */
    public List getTilesAt(double x, double y) {
        int row = Tile.rowOf(y);
        int col = Tile.colOf(row, x);
        return arbiter.getTiles(row, col);
    }

    /**
     * Selects the tile and group at the given screen coordinates.  If there is
     * a tile at the given position, the group is made selected and the tile is
     * made the selected one in the group.
     *
     * @param pos The screen position.
     *
     * @return The group at the tile; <CODE>null</CODE> if there is none.
     */
    private Group selectAt(Point pos) {
        Tile tile = tileAt(pos);
        if (tile == null)
            return null;

        Group group = tile.getGroup();
        makeTop(group);
        group.setSelected(tile);
        return group;
    }

    /**
     * Returns the tile at the given screen coordinates.
     *
     * @param pos The screen coordinates
     *
     * @return The tile at the given screen coordinates; <CODE>null</CODE> if
     *         there is none.
     */
    private Tile tileAt(Point pos) {
        Point mid = getMidpoint();
        int x = pos.x - mid.x;
        int y = pos.y - mid.y;
        for (int i = groups.size() - 1; i >= 0; i--) {
            Group group = (Group) groups.get(i);
            if (group.isMain())			// main getGroup is not selectable
                continue;
            Tile tile = group.findTileAt(x, y);
            if (tile != null && isVisible(tile))
                return tile;
        }
        return null;
    }

    /**
     * Returns the middle point of the board in screen coordinates.
     *
     * @return The middle point of the board in screen coordinates.
     */
    private Point getMidpoint() {
        Rectangle bounds = getBounds();
        Point mid = new Point(bounds.width / 2, bounds.height / 2);
        return mid;
    }

    /**
     * Deselects the current selected group and tile.
     */
    private void deselect() {
        if (selected != null) {
            selected.setSelected(null);
            selected = null;
        }
    }

    /**
     * Rotates the currently selected group.  Does nothing if no group is
     * selected.
     */
    public void rotateGroup(int count) {
        if (selected != null) {
            selected.rotate(count);
            repaint();
        }
    }

    /**
     * Paints the board, including all the tiles on it.
     *
     * @param g The graphics object to use.
     */
    public void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        super.paintComponent(g.create());
        Rectangle bounds = getBounds();
        g.translate(bounds.width / 2, bounds.height / 2);

        for (int i = 0; i < groups.size(); i++) {
            Group group = (Group) groups.get(i);
            group.paintComponent(g.create());
        }
    }

    /**
     * Returns <CODE>true</CODE> if the line on the tile is part of any current
     * path.
     *
     * @param tile The tile on which the line appears.
     * @param line The line in question.
     *
     * @return <CODE>true</CODE> if the line on the tile is in a path.
     */
    public boolean inPath(Tile tile, Line line) {
        for (Iterator it = paths.iterator(); it.hasNext();) {
            Path path = (Path) it.next();
            if (path.inPath(tile, line))
                return true;
        }
        return false;
    }

    /**
     * Reset the view so that all the player's tiles are visible and the center
     * of the world is in the middle of the shown area.
     */
    private void resetView() {
        JViewport viewport = (JViewport) getParent();
        if (viewport == null)
            return;

        List origGroups = new ArrayList(groups);
        for (Iterator it = origGroups.iterator(); it.hasNext();) {
            Group group = (Group) it.next();
            if (isVisible(group) && !group.isMain())
                breakUp(group);
        }

        Rectangle boardBounds = getBounds();
        Point mid = getMidpoint();
        double x = -mid.x - boardBounds.getX() + Tile.getWidth() / 2;
        double y = -mid.y - boardBounds.getY() + Tile.getHeight() / 2;
        for (int i = 0; i < groups.size(); i++) {
            Group group = (Group) groups.get(i);
            if (isVisible(group) && !group.isMain()) {
                group.setPosition(intOf(x), intOf(y));
                group.snap();
                group.setSelected(null);
                x += Tile.getWidth();
            }
        }

        deselect();
        repaint();
    }

    /**
     * Sets the scale to use for displaying the board.
     *
     * @param scale The scale to use for displaying the board.
     */
    public void newScale(double scale) {
        Tile.setScale(scale);
        for (Iterator it = groups.iterator(); it.hasNext();) {
            Group group = (Group) it.next();
            group.newScale();
        }
        repaint();
    }

    /**
     * Returns <CODE>true</CODE> if the given tile is visible to the current
     * player.
     *
     * @param tile The tile to inspect.
     *
     * @return <CODE>true</CODE> if the tile should be shown.
     */
    public boolean isVisible(Tile tile) {
        Player owner = tile.getOwner();
        return (owner == null || owner == player);
    }

    /**
     * Returns <CODE>true</CODE> if the given group is visible to the current
     * player.
     *
     * @param group The group to inspect.
     *
     * @return <CODE>true</CODE> if the group should be shown.
     */
    public boolean isVisible(Group group) {
        return isVisible((Tile) group.tiles().iterator().next());
    }

    /**
     * Discards the tiles in the currently selected group.
     */
    public void discardTiles() {
        if (selected == null)
            return;

        Group discarded = selected;
        groups.remove(selected);
        deselect();

        player.discarded();
        arbiter.discarded(selected);

        calculateSizes();
        nextTurn(discarded);

        repaint();
    }

    /**
     * Returns the current scaling in use.
     *
     * @return The current scaling in use.
     */
    public double getScale() {
        return Tile.getScale();
    }

    public Arbiter getArbiter() {
        return arbiter;
    }
}
