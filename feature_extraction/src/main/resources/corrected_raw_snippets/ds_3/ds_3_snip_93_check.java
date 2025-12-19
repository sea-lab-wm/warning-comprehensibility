package snippet_splitter_out.ds_3;
public class ds_3_snip_93_check {
// Added to allow compilation
// Snippet s25
// SNIPPET_STARTS
public boolean check(Unit u, PathNode p) {
    if (p.getTile().getSettlement() != null && p.getTile().getSettlement().getOwner() == player && p.getTile().getSettlement() != inSettlement) {
        Settlement s = p.getTile().getSettlement();
        int turns = p.getTurns();
        destinations.add(new ChoiceItem(s.toString() + " (" + turns + ")", s));
    }
    // Added to allow compilation
    // Added to allow compilation
    return false;
}
}