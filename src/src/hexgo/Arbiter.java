/*
 * Created by IntelliJ IDEA.
 * User: arnold
 * Date: Oct 28, 2002
 * Time: 4:31:38 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package hexgo;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.util.Collection;

public class Arbiter {
    private Deck deck;			// the deck the game will use
    private Player[] players;
    private int turn;
    private Sequence rows;		// the rows of tiles/

    public Arbiter(String[] names) {
	deck = new Deck();
	deck.shuffle();

	players = new Player[names.length];
	for (int i = 0; i < names.length; i++)
	    players[i] = new Player(names[i]);

	turn = -1;

	rows = new Sequence();
    }

    public Player[] getPlayers() {
	return players;
    }

    public Player currentPlayer() {
	return players[turn % players.length];
    }

    public int getTurn() {
	return turn;
    }

    public void nextTurn() {
	turn++;
    }

    public List dealtTiles() {
	return deck.dealtTiles();
    }

    public Tile nextTile() {
	return deck.nextTile();
    }

    public void takeBack(Tile tile) {
	deck.takeBack(tile);
    }

    public void dump() {
	for (int r = rows.min(); r < rows.max(); r++) {
	    System.out.println(r + ":");
	    Sequence row = (Sequence) rows.get(r);
	    if (row == null)
		continue;
	    for (int c = row.min(); c < row.max(); c++) {
		List contents = (List) row.get(c);
		if (contents == null || contents.size() == 0)
		    continue;
		for (int i = 0; i < contents.size(); i++) {
		    Tile tile = (Tile) contents.get(i);
		    System.out.println("  " + c + ": " + tile);
		}
	    }
	}
    }

    /**
     * Gets the contents of the cell at the given row and column indices.
     *
     * @param row The row of the cell.
     * @param col The column of the cell.
     *
     * @return A list of tiles at the given grid location.
     */
    public List getTiles(int row, int col) {
	Sequence rowContents = (Sequence) rows.get(row);
	if (rowContents == null) {
	    rowContents = new Sequence();
	    rows.set(row, rowContents);
	}
	List contents = (List) rowContents.get(col);
	if (contents == null) {
	    contents = new ArrayList();
	    rowContents.set(col, contents);
	}
	return contents;
    }

    /**
     * Returns an iterator that walks through all the tiles that are adjacent
     * on the grid to the given tile.
     *
     * @param tile The tile to use as the center.
     *
     * @return An iterator to use.
     */
    public Iterator tilesAround(Tile tile) {
	return tilesAround(tile.getRow(), tile.getCol());
    }

    /**
     * Returns an iterator that walks through all the tiles that are adjacent
     * on the grid to the given row and column.
     *
     * @param row The row to use as the center.
     * @param col The column to use as the center.
     *
     * @return An iterator to use.
     */
    private Iterator tilesAround(int row, int col) {
	Set tiles = new HashSet();
	mergeAll(tiles, getTiles(row, col - 1));
	mergeAll(tiles, getTiles(row, col + 1));
	if (row % 2 == 0) {
	    mergeAll(tiles, getTiles(row - 1, col - 1));
	    mergeAll(tiles, getTiles(row - 1, col));
	    mergeAll(tiles, getTiles(row + 1, col - 1));
	    mergeAll(tiles, getTiles(row + 1, col));
	} else {
	    mergeAll(tiles, getTiles(row - 1, col));
	    mergeAll(tiles, getTiles(row - 1, col + 1));
	    mergeAll(tiles, getTiles(row + 1, col));
	    mergeAll(tiles, getTiles(row + 1, col + 1));
	}
	return tiles.iterator();
    }


    /**
     * Merges the contents of the second collection into the first.  If the
     * source collection is <CODE>null</CODE>, this does nothing.
     *
     * @param target The target collection into which to merge the source.
     * @param source The source collection to merge into the target;
     * 		<CODE>null</CODE> will be ignored with no effect.
     */
    private static void mergeAll(Collection target, Collection source) {
	if (source != null)
	    target.addAll(source);
    }


    /**
     * Removes a tile from the board.
     *
     * @param tile The tile to remove.
     */
    private void remove(Tile tile) {
	List contents = getTiles(tile.getRow(), tile.getCol());
	contents.remove(tile);	// no error if not there
    }

    public void placeTile(Tile tile, int nRow, int nCol) {
	remove(tile);
	List contents;

	contents = getTiles(nRow, nCol);
	contents.add(0, tile);
	tile.setPlace(nRow, nCol);
    }

    public void discarded(Group discarded) {
	for (Iterator it = discarded.tiles().iterator(); it.hasNext();) {
	    Tile tile = (Tile) it.next();
	    remove(tile);
	    takeBack(tile);
	}
    }
}
