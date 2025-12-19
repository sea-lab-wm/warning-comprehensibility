package snippet_splitter_out.ds_3;
public class ds_3_snip_53_addMessage {
// Added to allow compilation
// Snippet s85
/**
 * Adds a message to the list of messages that need to be displayed on the GUI.
 * @param message The message to add.
 */
// SNIPPET_STARTS
public synchronized void addMessage(GUIMessage message) {
    if (getMessageCount() == MESSAGE_COUNT) {
        // Renamed to allow compilation
        messages2.remove(0);
    }
    // Renamed to allow compilation
    messages2.add(message);
    freeColClient.getCanvas().repaint(0, 0, getWidth(), getHeight());
}
}