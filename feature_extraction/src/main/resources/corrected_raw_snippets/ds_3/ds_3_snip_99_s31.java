package snippet_splitter_out.ds_3;
public class ds_3_snip_99_s31 {
// Added to allow compilation
// Snippet s31
// SNIPPET_STARTS
public String s31() {
    if (iterateOverMe instanceof String)
        return createEnumeration(((String) iterateOverMe).toCharArray());
    if (iterateOverMe instanceof StringBuffer)
        return createEnumeration(iterateOverMe.toString().toCharArray());
    throw new IllegalArgumentException("Cannot enumerate object of type " + iterateOverMe.getClass());
}
}