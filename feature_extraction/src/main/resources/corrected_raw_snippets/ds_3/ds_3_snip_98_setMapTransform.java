package snippet_splitter_out.ds_3;
public class ds_3_snip_98_setMapTransform {
// Snippet s30
/**
 * Sets the currently chosen <code>MapTransform</code>.
 * @param mt The transform that should be applied to a
 *      <code>Tile</code> that is clicked on the map.
 */
// SNIPPET_STARTS
public void setMapTransform(MapTransform mt) {
    currentMapTransform = mt;
    MapControlsAction mca = (MapControlsAction) freeColClient.getActionManager().getFreeColAction(MapControlsAction.ID);
    if (mca.getMapControls() != null) {
        mca.getMapControls().update(mt);
    }
    // Added to allow compilation
}
}