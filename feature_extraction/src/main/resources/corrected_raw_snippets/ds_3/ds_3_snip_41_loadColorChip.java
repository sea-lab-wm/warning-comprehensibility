package snippet_splitter_out.ds_3;
public class ds_3_snip_41_loadColorChip {
// Added to allow compilation
// Snippet s75
/**
 * Generates a color chip image and stores it in memory.
 *
 * @param gc The GraphicsConfiguration is needed to create images that are
 *            compatible with the local environment.
 * @param c The color of the color chip to create.
 */
// SNIPPET_STARTS
private void loadColorChip(GraphicsConfiguration gc, Color c) {
    BufferedImage tempImage = gc.createCompatibleImage(11, 17);
    Graphics g = tempImage.getGraphics();
    if (c.equals(Color.BLACK)) {
        g.setColor(Color.WHITE);
    }
}
}