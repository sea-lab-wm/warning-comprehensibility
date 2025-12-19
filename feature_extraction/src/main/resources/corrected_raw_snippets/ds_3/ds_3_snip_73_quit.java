package snippet_splitter_out.ds_3;
public class ds_3_snip_73_quit {
// Snippet s5
/**
 * Quits the application without any questions.
 */
// SNIPPET_STARTS
public void quit() {
    getConnectController().quitGame(true);
    if (!windowed) {
        gd.setFullScreenWindow(null);
    }
    System.exit(0);
}
}