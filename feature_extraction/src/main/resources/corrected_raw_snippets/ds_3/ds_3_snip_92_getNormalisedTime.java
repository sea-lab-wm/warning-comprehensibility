package snippet_splitter_out.ds_3;
public class ds_3_snip_92_getNormalisedTime {
// Added to allow compilation
// Snippet s24
// SNIPPET_STARTS
public static long getNormalisedTime(long t) {
    synchronized (tempCalDefault) {
        setTimeInMillis(tempCalDefault, t);
        resetToTime(tempCalDefault);
        return getTimeInMillis(tempCalDefault);
    }
    // Added to allow compilation
}
}