// $Header$

package hexgo;

import javax.swing.*;
import java.awt.*;

/**
 * This class repesents a single player's hand of tiles in the game.
 */
public class Hand extends JComponent {
    private Tile tiles[] = new Tile[7];		// the tiles in the hand

    /**
     * Creates a new <CODE>Hand</CODE>.
     */
    public Hand() {
        setBackground(Color.white.darker());
        setPreferredSize(new Dimension(200, 80));
    }

    public void paintComponent(Graphics g1) {
        Graphics2D g = (Graphics2D) g1;
        Dimension dim = getSize();
        g.setColor(getBackground());
        g.fillRect(0, 0, dim.width, dim.height);
        g.translate(40, 40);
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i] != null)
                tiles[i].paintComponent(g.create());
            g.translate(80, 0);
        }
    }

    /**
     * Fill up the hand with tiles.
     *
     * @param deck The deck from which to get tiles.
     */
    public void fill(Deck deck) {
        for (int i = 0; i < tiles.length; i++)
            if (tiles[i] == null)
                tiles[i] = deck.nextTile();
    }
}
