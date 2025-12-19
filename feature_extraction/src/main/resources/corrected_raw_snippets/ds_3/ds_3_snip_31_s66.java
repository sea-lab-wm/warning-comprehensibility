package snippet_splitter_out.ds_3;
public class ds_3_snip_31_s66 {
// Added to allow compilation
// Snippet s66
// SNIPPET_STARTS
public void s66() {
    mProgramTable.changeSelection(row, 0, false, false);
    Program p = (Program) mProgramTableModel.getValueAt(row, 1);
    JPopupMenu menu = devplugin.Plugin.getPluginManager().createPluginContextMenu(p, CapturePlugin.getInstance());
}
}