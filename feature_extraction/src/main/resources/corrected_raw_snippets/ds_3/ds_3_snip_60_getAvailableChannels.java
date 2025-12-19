package snippet_splitter_out.ds_3;
public class ds_3_snip_60_getAvailableChannels {
// Added to allow compilation
// Snippet s92
/**
 * Get the List of all available Channels
 *
 * @return All available Channels
 */
// SNIPPET_STARTS
public ElgatoChannel[] getAvailableChannels() {
    ArrayList<ElgatoChannel> list = new ArrayList<ElgatoChannel>();
    String res = null;
    try {
        res = mAppleScript.executeScript(CHANNELLIST);
    } finally {
        // Added to allow compilation
    }
    return new ElgatoChannel[0];
    /*Altered return*/
    // return null; // Added to allow compilation
}
}