/*
 * Created by IntelliJ IDEA.
 * User: arnold
 * Date: Sep 28, 2002
 * Time: 4:36:30 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package hexgo;

import java.util.HashSet;
import java.util.Set;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

public class Player implements SwingConstants {
    private int score;
    private int lastScore;
    private String name;
    private JLabel nameLabel;
    private JLabel scoreLabel;
    private JLabel lastScoreLabel;
    private Icon markerIcon;
    private boolean isCurrent;
    private Set hand;

    private static final int MARKER_X = 8;
    private static final int MARKER_Y = 8;
    private static Shape MARKER;

    public static final int NONE = -1;
    public static final int DISCARDED = -2;

    static {
	GeneralPath triangle = new GeneralPath();
	triangle.moveTo(0, 0);
	triangle.lineTo(MARKER_X, MARKER_Y / 2);
	triangle.lineTo(0, MARKER_X);
	triangle.closePath();
	MARKER = triangle;
    }

    public Player(String name) {
	this.name = name;
	lastScore = NONE;
	nameLabel = new JLabel(name, LEFT);
	scoreLabel = new JLabel("   0", RIGHT);
	lastScoreLabel = new JLabel("", RIGHT);
	updateLastScore();
	hand = new HashSet();
	markerIcon = new Icon() {
	    public void paintIcon(Component c, Graphics g1, int x, int y) {
		if (isCurrent) {
		    Graphics2D g = (Graphics2D) g1.create();
		    g.translate(x, y);
		    g.fill(MARKER);
		}
	    }

	    public int getIconWidth() {
		return MARKER_X;
	    }

	    public int getIconHeight() {
		return MARKER_Y;
	    }
	};
    }

    public void add(int moreScore) {
	lastScore = moreScore;
	score += moreScore;
	scoreLabel.setText(Integer.toString(score));
	updateLastScore();
    }

    private void updateLastScore() {
	if (lastScore == NONE)
	    lastScoreLabel.setText("    ");
	else if (lastScore == DISCARDED)
	    lastScoreLabel.setText("discard");
	else
	    lastScoreLabel.setText(Integer.toString(lastScore));
    }

    public int getScore() {
	return score;
    }

    public String getName() {
	return name;
    }

    public JLabel getNameLabel() {
	return nameLabel;
    }

    public JLabel getScoreLabel() {
	return scoreLabel;
    }

    public JLabel getLastScoreLabel() {
	return lastScoreLabel;
    }

    public Icon getMarkerIcon() {
	return markerIcon;
    }

    public void setCurrent(boolean isCurrent) {
	if (isCurrent != this.isCurrent) {
	    this.isCurrent = isCurrent;
	    nameLabel.repaint();
	}
    }

    public void fillHand(Group played, Board board) {
	if (played != null)
	    hand.removeAll(played.tiles());

	while (hand.size() < 7) {
	    Tile tile = board.dealTile();
	    if (tile == null)
		return;
	    tile.setOwner(this);
	    hand.add(tile);
	}
    }

    public void discarded() {
	lastScore = DISCARDED;
	updateLastScore();
    }
}
