package hexgo;

class TilePlace {
    TilePlace(Tile tile, int x, int y) {
	this.tile = tile;
	this.x = x;
	this.y = y;
    }

    Tile tile;
    int x, y;
}
