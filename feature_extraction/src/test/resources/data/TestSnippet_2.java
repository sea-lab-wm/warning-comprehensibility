package data;

public class TestSnippet_2 {
  /**
   * javadoc comment assigned to TestLoops
   * @param x
   * @param y
   * @param z
   * #1
   */
  public void TestLoops(int x, int y, int z) {
    for (int i = 0; i < 10; i++) {
      System.out.println("single_for");
    }

    /*
     * block comment inside the method
     * if (true)
     * for ()
     * TestLoops(int x, int y, intz)
     * lines above shouldn't modify test results
     * #2
     */
    int i = 0;
    while (i < 10) {
      System.out.println("single_while");
      i = i + 1;
    }

    // each #3
    // comment #4
    // here #5
    // is #6
    // counted #7
    for (int j = 0; j < 10; j++) {
      for (int k = 0; k < 10; k++) {
        System.out.println("nested_for");
      }
    }

    int k = 0, j = 0;
    while (k < 10) {
      while (j < 10) {
        System.out.println("nested_while");
      }
    }

    if (true) {
      System.out.println("single_if");
    }

    if (true) {
      if (true) {
        System.out.println("nested_if");
      } else if (true) {
        System.out.println("nested_if");
      } else {
        System.out.println("else_if");
      }
    }

    switch (k) {
      case 0: k = k + k;
      case 1: k = k * k;
      case 2: k = k / k;
      default: k = k % k;
    }
  }
}
