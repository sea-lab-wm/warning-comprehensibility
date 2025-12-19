package snippet_splitter_out.ds_3;
public class ds_3_snip_14_close {
// Snippet s49
// SNIPPET_STARTS
public void close() {
    if (isClosed) {
        return;
    }
    isClosed = true;
    try {
        resultOut.setResultType(ResultConstants.SQLDISCONNECT);
    } finally {
        // Added to allow compilation
    }
}
}