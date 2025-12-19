package snippet_splitter_out.ds_3;
public class ds_3_snip_58_displayTileCursor {
// Added to allow compilation
// Snippet s90
// SNIPPET_STARTS
public boolean displayTileCursor(Tile tile, int canvasX, int canvasY) {
    if (currentMode == ViewMode.VIEW_TERRAIN_MODE) {
        Position selectedTilePos = gui.getSelectedTile();
        if (selectedTilePos == null)
            return false;
        if (selectedTilePos.getX() == tile.getX() && selectedTilePos.getY() == tile.getY()) {
            TerrainCursor cursor = gui.getCursor();
        }
        // Added to allow compilation
    }
    // Added to allow compilation
    // Added to allow compilation
    return false;
}
}