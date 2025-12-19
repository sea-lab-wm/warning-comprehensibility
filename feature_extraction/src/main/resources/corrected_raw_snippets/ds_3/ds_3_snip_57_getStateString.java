package snippet_splitter_out.ds_3;
public class ds_3_snip_57_getStateString {
// Added to allow compilation
// Snippet s89
// SNIPPET_STARTS
String getStateString() {
    int state = getState();
    switch(state) {
        case DATABASE_CLOSING:
            return "DATABASE_CLOSING";
        case DATABASE_ONLINE:
            return "DATABASE_ONLINE";
    }
    // Added to allow compilation
    return new String();
    /*Altered return*/
    // return null; // Added to allow compilation
}
}