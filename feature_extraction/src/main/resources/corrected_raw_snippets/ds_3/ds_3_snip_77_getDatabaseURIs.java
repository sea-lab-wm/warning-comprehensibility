package snippet_splitter_out.ds_3;
public class ds_3_snip_77_getDatabaseURIs {
// added to allow compilation
// Snippet s9
/**
 * Returns a vector containing the URI (type + path) for all the databases.
 */
// SNIPPET_STARTS
public static Vector getDatabaseURIs() {
    Vector v = new Vector();
    Iterator it = databaseIDMap.values().iterator();
    while (it.hasNext()) {
        Database db = (Database) it.next();
    }
    // added to allow compilation
    return v;
    /*Altered return*/
    // return null; // added to allow compilation
}
}