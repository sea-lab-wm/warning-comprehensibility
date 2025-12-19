package snippet_splitter_out.ds_3;
public class ds_3_snip_7_deliverGift {
// Added to allow compilation
// Snippet s40
/**
 * Handles an "deliverGift"-request.
 *
 * @param element The element (root element in a DOM-parsed XML tree) that
 *            holds all the information.
 */
// SNIPPET_STARTS
private Element deliverGift(Element element) {
    Element unitElement = Message.getChildElement(element, Unit.getXMLElementTagName());
    Unit unit = (Unit) getGame().getFreeColGameObject(unitElement.getAttribute("ID"));
    unit.readFromXMLElement(unitElement);
    return unitElement;
    /*Altered return*/
    // return null; // Added to allow compilation
}
}