package snippet_splitter_out.ds_3;
public class ds_3_snip_54_jjCanMove_1 {
// Added to allow compilation
// Snippet s86
// SNIPPET_STARTS
private static final boolean jjCanMove_1(int hiByte, int i1, int i2, long l1, long l2) {
    switch(hiByte) {
        case 0:
            return ((jjbitVec0[i2] & l2) != 0L);
        default:
            if ((jjbitVec1[i1] & l1) != 0L)
                return true;
            return false;
    }
    // Added to allow compilation
}
}