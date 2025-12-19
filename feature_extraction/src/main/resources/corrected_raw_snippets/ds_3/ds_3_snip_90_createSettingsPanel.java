package snippet_splitter_out.ds_3;
public class ds_3_snip_90_createSettingsPanel {
// Snippet s22
/**
 * Returns the PluginPanel
 * @return Panel
 */
// SNIPPET_STARTS
public JPanel createSettingsPanel() {
    mPanel = new CapturePluginPanel(mOwner, mCloneData);
    mPanel.setBorder(Borders.createEmptyBorder(Sizes.DLUY5, Sizes.DLUX5, Sizes.DLUY5, Sizes.DLUX5));
    mPanel.setSelectedTab(mCurrentPanel);
    return new JPanel();
    /*Altered return*/
    // return null; // Added to allow compilation
}
}