package snippet_splitter_out.ds_3;
public class ds_3_snip_75_Date {
// Snippet s7
/**
 * Attention: DO NOT USE THIS!
 * Under Os/2 it has some problems with calculating the real Date!
 *
 * @deprecated
 */
// SNIPPET_STARTS
public void Date(int daysSince1970) {
    // return type void added to allow compilation
    long l = (long) daysSince1970 * 24 * 60 * 60 * 1000;
    java.util.Date d = new java.util.Date(l);
    Calendar cal = Calendar.getInstance();
}
}