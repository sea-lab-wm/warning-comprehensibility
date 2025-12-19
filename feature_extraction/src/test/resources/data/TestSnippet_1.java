package data;

public class TestSnippet_1 {

    /**
      * this is a javadoc comment! #1
      * use this test snippet to create test cases
      */
    public void TestLoops(int x, int y) { //I think it's funny that x and y aren't actually used in the snippet. #2
    for (int i = 0; i < 10; i++) {
      System.out.println("single_for");
    }

    int i = 0;
    while (i < 10) {
      System.out.println("single_while");
      i = i + 1;
    }
    /*
    This is a block comment!
     * if (true)
     * for ()
     * TestLoops(int x, int y, intz)
     * lines above shouldn't modify test results
    #3
    */
    for (int j = 0; j < 10; j++) {
      for (int k = 0; k < 10; k++) {
        System.out.println("nested_for");
      }
    }

    //These first two comments are orphan comments, as they aren't assigned to any code. #4
    //However, they will still count towards the total! #5
    //#6    
    int k = 0, j = 0;
    while (k < 10) {
      while (j < 10) {
        System.out.println("nested_while");
        j = j + 1;
      }
      k = k + 1;
    }

    //I'm adding parethesis here () #7
    int i1 = 0;
    while (i1 < 10) {
      for (int i2 = 0; i2 < 10; i2++) {
        System.out.println("nested_while_for");
        for (i1 = 1; i1 < 5; i1++) {
          System.out.println("nested_while_for_depth");
        }
      }
    }

    if (true) {
      System.out.println("single_if"); //And here () #8
    }

    if (true) {
      if (true) {
        System.out.println("nested_if");
      }
    }

    if (true) {
      if (true) {
        System.out.println("nested_if");
      } else if (true) {
        System.out.println("nested_if"); //This is dead code and will never execute. #9
      } else {
        System.out.println("else_if");
      }
    }

    try {
      System.out.println("try");
    } catch (Exception e) {
      System.out.println("catch");
    } finally {
      System.out.println("finally");
    }

    synchronized (this) {
      System.out.println("synchronized");
    }

    switch (k) {
      case 0:
      case 1:
      case 2:
    }

    do {
      System.out.println("do_while");
    } while (true);

    
  }
}
