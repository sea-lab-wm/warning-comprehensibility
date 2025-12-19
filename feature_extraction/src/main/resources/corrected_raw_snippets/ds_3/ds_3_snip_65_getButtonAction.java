package snippet_splitter_out.ds_3;
public class ds_3_snip_65_getButtonAction {
// Added to allow compilation
// Snippet s97
// SNIPPET_STARTS
public ActionMenu getButtonAction() {
    AbstractAction action = new AbstractAction() {

        public void actionPerformed(ActionEvent evt) {
            showDialog();
        }
    };
    action.putValue(Action.NAME, mLocalizer.msg("CapturePlugin", "Capture Plugin"));
    action.putValue(Action.SMALL_ICON, createImageIcon("mimetypes", "video-x-generic", 16));
    return new ActionMenu();
    /*Altered return*/
    // return null; // Added to allow compilation
}
}