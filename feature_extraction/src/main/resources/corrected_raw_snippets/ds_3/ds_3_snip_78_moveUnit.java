package snippet_splitter_out.ds_3;
public class ds_3_snip_78_moveUnit {
// added to allow compilation
// Snippet s10
// SNIPPET_STARTS
private void moveUnit(KeyEvent e) {
    if (!parent.isMapboardActionsEnabled()) {
        return;
    }
    switch(e.getKeyCode()) {
        case KeyEvent.VK_ESCAPE:
            // main menu
            break;
        case KeyEvent.VK_NUMPAD1:
        case KeyEvent.VK_END:
            inGameController.moveActiveUnit(Map.SW);
    }
}
}