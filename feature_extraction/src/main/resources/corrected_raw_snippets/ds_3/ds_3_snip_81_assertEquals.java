package snippet_splitter_out.ds_3;
public class ds_3_snip_81_assertEquals {
// Snippet s13
/*
     * @param expected expected value
	 * @param actual actual value
	 */
// SNIPPET_STARTS
static public void assertEquals(String message, Object expected, Object actual) {
    if (expected == null && actual == null)
        return;
    if (expected != null && isEquals(expected, actual))
        return;
    else if (expected instanceof String && actual instanceof String) {
        String cleanMessage = message == null ? "" : message;
    }
    // added to allow compilation
}
}