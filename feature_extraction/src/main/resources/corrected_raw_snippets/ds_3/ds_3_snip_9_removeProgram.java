package snippet_splitter_out.ds_3;
public class ds_3_snip_9_removeProgram {
// Added to allow compilation
// Snippet s42
// SNIPPET_STARTS
public synchronized void removeProgram(Program program) {
    PluginTreeNode node = findProgramTreeNode(program, false);
    if (node != null) {
        mChildNodes.remove(node);
        if (mMarker != null) {
            program.unmark(mMarker);
        }
        // Added to allow compilation
    }
    // Added to allow compilation
}
}