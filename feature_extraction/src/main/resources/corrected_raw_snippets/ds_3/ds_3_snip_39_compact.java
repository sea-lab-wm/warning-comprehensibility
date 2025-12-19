package snippet_splitter_out.ds_3;
public class ds_3_snip_39_compact {
// Snippet s73
// SNIPPET_STARTS
public String compact(String message) {
    if (fExpected == null || fActual == null || areStringsEqual())
        return Assert.format(message, fExpected, fActual);
    findCommonPrefix();
    findCommonSuffix();
    return message;
    /*Altered return*/
    // return null; // Added to allow compilation
}
}