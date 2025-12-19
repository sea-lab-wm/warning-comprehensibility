package manually_created_snippets.ds_3;

import javax.swing.KeyStroke;

public class ds_3_snip_35_DisbandUnitAction {
    /**
     * Creates a new <code>DisbandUnitAction</code>.
     *
     * @param freeColClient The main controller object for the client.
     */
    //SNIPPET_STARTS
    DisbandUnitAction(FreeColClient freeColClient) {
        super(freeColClient, "unit.state.8", null, KeyStroke.getKeyStroke('D', 0));
        putValue(BUTTON_IMAGE, freeColClient.getImageLibrary().getUnitButtonImageIcon(ImageLibrary.UNIT_BUTTON_DISBAND,
                0));
        putValue(BUTTON_ROLLOVER_IMAGE, freeColClient.getImageLibrary().getUnitButtonImageIcon(
                ImageLibrary.UNIT_BUTTON_DISBAND, 1));
    }
}
