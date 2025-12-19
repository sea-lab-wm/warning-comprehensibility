public class TestSnippet_3 {
    /* This
     * is a comment
     */
    public void TestLoops(int x, int y, int z) {
        for (int i = 0; i < 10; i++) {
            System.out.println("single_for");
        }
        int i = 0;
        while (true && i < 10) {
            System.out.println("single_while");
            i = i + 1;
        }
    }
}
