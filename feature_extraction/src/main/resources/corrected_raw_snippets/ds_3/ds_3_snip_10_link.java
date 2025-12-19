package snippet_splitter_out.ds_3;
public class ds_3_snip_10_link {
// Snippet s44
// SNIPPET_STARTS
void link(IndexRowIterator other) {
    other.next = next;
    other.last = this;
    next.last = other;
}
}