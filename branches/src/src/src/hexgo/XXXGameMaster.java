package hexgo;

import net.jini.core.event.*;
import net.jini.core.lease.*;

public interface XXXGameMaster {
    public XXXMoveMaker watch(XXXHexgoListener listener);
    public XXXMoveMaker join(XXXPlayerListener listener);
}

interface XXXHexgoListener {
    public void play(Player player, Tile[] tiles, Path[] paths, int score);
    public void discard(Player player, int numTiles);
    public void dropOut(Player player);
}

interface XXXPlayerListener extends XXXHexgoListener {
    public void deal(Tile[] tiles);
}

interface XXXMoveMaker {
    public void play(int[] tiles, int score, Path[] paths);
    public void discard(int[] tiles);
    public Lease getLease();
}
