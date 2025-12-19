package snippet_splitter_out.ds_3;
public class ds_3_snip_96_filter {
// Added to allow compilation
// Snippet s28
// SNIPPET_STARTS
public void filter(Filter filter) throws NoTestsRemainException {
    for (Iterator<Runner> iter = fRunners.iterator(); iter.hasNext(); ) {
        Runner runner = iter.next();
        if (filter.shouldRun(runner.getDescription()))
            filter.apply(runner);
        else
            iter.remove();
    }
    // Added to allow compilation
}
}