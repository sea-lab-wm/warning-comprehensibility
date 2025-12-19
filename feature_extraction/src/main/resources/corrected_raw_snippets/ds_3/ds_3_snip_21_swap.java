package snippet_splitter_out.ds_3;
public class ds_3_snip_21_swap {
// Added to allow compilation
// Snippet s56
/**
 *     Swap in the value as the new top of the stack and return the old
 *     value.
 */
// SNIPPET_STARTS
public NameSpace swap(NameSpace newTop) {
    NameSpace oldTop = (NameSpace) (stack.elementAt(0));
    stack.setElementAt(newTop, 0);
    return oldTop;
}
}