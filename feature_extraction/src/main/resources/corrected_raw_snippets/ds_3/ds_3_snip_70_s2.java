package snippet_splitter_out.ds_3;
public class ds_3_snip_70_s2 {
// Snippet s2
// SNIPPET_STARTS
public static Object s2() {
    if (actionList.size() == 1) {
        ActionMenu menu = actionList.get(0);
        if (menu.getSubItems().length == 0) {
            return null;
        }
        if (menu.getSubItems().length == 1) {
            Action action = menu.getSubItems()[0].getAction();
        }
        // had to be added to allow compilation
    }
    // had to be added to allow compilation
    return new Object();
    /*Altered return*/
    // return null; // had to be added to allow compilation
}
}