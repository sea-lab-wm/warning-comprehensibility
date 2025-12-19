package snippet_splitter_out.ds_3;
public class ds_3_snip_88_s20 {
// Added to allow compilation
// Snippet s20
// SNIPPET_STARTS
public void s20() {
    int eventId = active ? ON : OFF;
    ActionEvent blinkEvent = new ActionEvent(this, eventId, "blink");
    fireActionEvent(blinkEvent);
}
}