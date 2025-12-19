package snippet_splitter_out.ds_3;
public class ds_3_snip_72_Grantee {
// Snippet s4
/**
 * Constructor, with a argument reference to the PUBLIC User Object which
 * is null if this is the SYS or PUBLIC user.
 *
 * The dependency upon a GranteeManager is undesirable.  Hopefully we
 * can get rid of this dependency with an IOC or Listener re-design.
 */
// SNIPPET_STARTS
public // public void added to allow compilation
void // public void added to allow compilation
Grantee(// public void added to allow compilation
String name, // public void added to allow compilation
Grantee inGrantee, GranteeManager man) throws HsqlException {
    rightsMap = new IntValueHashMap();
    granteeName = name;
    granteeManager = man;
}
}