package snippet_splitter_out.ds_3;
public class ds_3_snip_68_purgeOldMessagesFromMessagesToIgnore {
// Added to allow compilation
// Snippet s100                                                                     /*ORGINALLY COMMENTED OUT*/
// SNIPPET_STARTS
private synchronized void purgeOldMessagesFromMessagesToIgnore(int thisTurn) {
    List<String> keysToRemove = new ArrayList<String>();
    for (Entry<String, Integer> entry : messagesToIgnore.entrySet()) {
        if (entry.getValue().intValue() < thisTurn - 1) {
            if (logger.isLoggable(Level.FINER)) {
                logger.finer("Removing old model message with key " + entry.getKey() + " from ignored messages.");
            }
            keysToRemove.add(entry.getKey());
        }
    }
}
}