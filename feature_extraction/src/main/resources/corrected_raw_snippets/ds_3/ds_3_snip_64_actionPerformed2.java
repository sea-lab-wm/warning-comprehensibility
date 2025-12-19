package snippet_splitter_out.ds_3;
public class ds_3_snip_64_actionPerformed2 {
// Added to allow compilation
// Snippet s96
/**
 * Applies this action.
 *
 * @param e The <code>ActionEvent</code>.
 */
// SNIPPET_STARTS
public void actionPerformed2(ActionEvent e) {
    // Renamed to allow compilation
    final Game game = freeColClient.getGame();
    final Map map = game.getMap();
    Parameters p = showParametersDialog();
}
}