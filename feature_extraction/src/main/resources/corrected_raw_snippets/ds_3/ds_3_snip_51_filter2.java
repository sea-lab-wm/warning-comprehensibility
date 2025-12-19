package snippet_splitter_out.ds_3;
public class ds_3_snip_51_filter2 {
// Snippet s83
// SNIPPET_STARTS
public void filter2(Filter filter) throws NoTestsRemainException {
    // Renamed to allow compilation
    for (Iterator<Method> iter = fTestMethods.iterator(); iter.hasNext(); ) {
        Method method = iter.next();
        if (!filter.shouldRun(methodDescription(method)))
            iter.remove();
    }
    if (fTestMethods.isEmpty())
        throw new NoTestsRemainException();
}
}