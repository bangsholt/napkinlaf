// $Header$

package hexgo;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * This class manages the deck of tiles.
 */
class Deck implements HexgoConstants {
    private Tile[] tiles;		// the tiles of the deck
    private List toDeal;		// the tiles not yet dealt
    private List dealt;		// the tiles already dealt

    private final static int NUM_TILES = 83;	// the number of tiles in a deck
    private final static Pattern[] PATTERNS = {// the pattern of each tile
        new Pattern(0, 2, 4, 0, 0, 0),
        new Pattern(0, 2, 4, 0, 0, 1),
        new Pattern(0, 2, 4, 0, 0, 2),
        new Pattern(0, 2, 4, 0, 1, 1),
        new Pattern(0, 2, 4, 0, 1, 2),
        new Pattern(0, 2, 4, 0, 2, 1),
        new Pattern(0, 2, 4, 0, 2, 2),
        new Pattern(0, 2, 4, 1, 1, 1),
        new Pattern(0, 2, 4, 1, 1, 2),
        new Pattern(0, 2, 4, 1, 2, 2),
        new Pattern(0, 2, 4, 2, 2, 2),
        new Pattern(1, 12, 4, 0, 0, 0),
        new Pattern(1, 12, 4, 0, 0, 1),
        new Pattern(1, 12, 4, 0, 0, 2),
        new Pattern(1, 12, 4, 0, 1, 1),
        new Pattern(1, 12, 4, 0, 1, 2),
        new Pattern(1, 12, 4, 0, 2, 0),
        new Pattern(1, 12, 4, 0, 2, 1),
        new Pattern(1, 12, 4, 0, 2, 2),
        new Pattern(1, 12, 4, 1, 0, 1),
        new Pattern(1, 12, 4, 1, 0, 2),
        new Pattern(1, 12, 4, 1, 1, 1),
        new Pattern(1, 12, 4, 1, 1, 2),
        new Pattern(1, 12, 4, 1, 2, 1),
        new Pattern(1, 12, 4, 1, 2, 2),
        new Pattern(1, 12, 4, 2, 0, 2),
        new Pattern(1, 12, 4, 2, 1, 2),
        new Pattern(1, 12, 4, 2, 2, 2),
        new Pattern(1, 10, 9, 0, 0, 0),
        new Pattern(1, 10, 9, 0, 0, 1),
        new Pattern(1, 10, 9, 0, 0, 2),
        new Pattern(1, 10, 9, 0, 1, 0),
        new Pattern(1, 10, 9, 0, 1, 1),
        new Pattern(1, 10, 9, 0, 1, 2),
        new Pattern(1, 10, 9, 0, 2, 0),
        new Pattern(1, 10, 9, 0, 2, 1),
        new Pattern(1, 10, 9, 0, 2, 2),
        new Pattern(1, 10, 9, 1, 0, 0),
        new Pattern(1, 10, 9, 1, 0, 1),
        new Pattern(1, 10, 9, 1, 0, 2),
        new Pattern(1, 10, 9, 1, 1, 0),
        new Pattern(1, 10, 9, 1, 1, 1),
        new Pattern(1, 10, 9, 1, 1, 2),
        new Pattern(1, 10, 9, 1, 2, 0),
        new Pattern(1, 10, 9, 1, 2, 1),
        new Pattern(1, 10, 9, 1, 2, 2),
        new Pattern(1, 10, 9, 2, 0, 0),
        new Pattern(1, 10, 9, 2, 0, 1),
        new Pattern(1, 10, 9, 2, 0, 2),
        new Pattern(1, 10, 9, 2, 1, 0),
        new Pattern(1, 10, 9, 2, 1, 1),
        new Pattern(1, 10, 9, 2, 1, 2),
        new Pattern(1, 10, 9, 2, 2, 0),
        new Pattern(1, 10, 9, 2, 2, 1),
        new Pattern(1, 10, 9, 2, 2, 2),
        new Pattern(6, 13, 9, 0, 0, 0),
        new Pattern(6, 13, 9, 0, 0, 1),
        new Pattern(6, 13, 9, 0, 0, 2),
        new Pattern(6, 13, 9, 0, 1, 1),
        new Pattern(6, 13, 9, 0, 1, 2),
        new Pattern(6, 13, 9, 0, 2, 0),
        new Pattern(6, 13, 9, 0, 2, 1),
        new Pattern(6, 13, 9, 0, 2, 2),
        new Pattern(6, 13, 9, 1, 0, 1),
        new Pattern(6, 13, 9, 1, 0, 2),
        new Pattern(6, 13, 9, 1, 1, 1),
        new Pattern(6, 13, 9, 1, 1, 2),
        new Pattern(6, 13, 9, 1, 2, 1),
        new Pattern(6, 13, 9, 1, 2, 2),
        new Pattern(6, 13, 9, 2, 0, 2),
        new Pattern(6, 13, 9, 2, 1, 2),
        new Pattern(6, 13, 9, 2, 2, 2),
        new Pattern(12, 13, 14, 0, 0, 0),
        new Pattern(12, 13, 14, 0, 0, 1),
        new Pattern(12, 13, 14, 0, 0, 2),
        new Pattern(12, 13, 14, 0, 1, 1),
        new Pattern(12, 13, 14, 0, 1, 2),
        new Pattern(12, 13, 14, 0, 2, 1),
        new Pattern(12, 13, 14, 0, 2, 2),
        new Pattern(12, 13, 14, 1, 1, 1),
        new Pattern(12, 13, 14, 1, 1, 2),
        new Pattern(12, 13, 14, 1, 2, 2),
        new Pattern(12, 13, 14, 2, 2, 2),
    };

    /**
     * Creates a new <CODE>Deck</CODE> object.  The tiles will be initially
     * unshuffled.
     */
    Deck() {
        tiles = new Tile[NUM_TILES];

        // a sanity check
        if (PATTERNS.length != tiles.length) {
            throw new IllegalStateException("PATTERNS.length ["
                    + PATTERNS.length + "] != tiles.length [" + tiles.length + "]");
        }

        // give an initial pattern for each tile
        for (int i = 0; i < tiles.length; i++) {
            tiles[i] = new Tile();
            tiles[i].setPattern(PATTERNS[i]);
        }
    }

    /**
     * Shuffles the tiles into a random order.
     */
    public void shuffle() {
        for (int i = 0; i < tiles.length; i++) {
            Tile tile = tiles[i];
            tile.reset();
        }
        toDeal = new LinkedList();
        toDeal.addAll(Arrays.asList(tiles));
        dealt = new LinkedList();
    }

    /**
     * Returns the next tile from the deck.  If there are no more to return,
     * returns <CODE>null</CODE>.
     *
     * @return The next tile from the deck.
     */
    public Tile nextTile() {
        if (toDeal.size() == 0)
            return null;
        int index = random.nextInt(toDeal.size());
        Tile tile = (Tile) toDeal.remove(index);
        dealt.add(tile);
        return tile;
    }

    /**
     * Returns the tiles that have been dealt from the deck.
     *
     * @return The tiles that have been dealt from the deck.
     */
    public List dealtTiles() {
        return dealt;
    }

    /**
     * Puts back a tile into the deck.
     *
     * @param tile The tile to put back.
     */
    public void takeBack(Tile tile) {
        dealt.remove(tile);
        tile.reset();
        toDeal.add(tile);
    }
}
