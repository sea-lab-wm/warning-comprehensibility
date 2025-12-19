package snippet_splitter_out.ds_3;
public class ds_3_snip_59_jjMoveStringLiteralDfa18_0 {
// Added to allow compilation
// Snippet s91
// SNIPPET_STARTS
private final int jjMoveStringLiteralDfa18_0(long old1, long active1, long old2, long active2) {
    if (((active1 &= old1) | (active2 &= old2)) == 0L)
        return jjStartNfa_0(16, 0L, old1, old2);
    try {
        curChar = input_stream.readChar();
    } catch (java.io.IOException e) {
        jjStopStringLiteralDfa_0(17, 0L, active1, active2);
    }
    // Added to allow compilation
    return 0;
}
}