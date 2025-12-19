package featureextraction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import java.io.File;
import java.io.FileNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FeatureExtractorTestMultiple {

  private FeatureVisitor featureVisitor1;
  private FeatureVisitor featureVisitor2;

  final int NUM_OF_LOOP_STATEMENTS_1 = 9;
  final int NUM_OF_IF_STATEMENTS_1 = 6;
  final int NUM_OF_PARAMETERS_1 = 2;
  final int NUM_OF_COMMENTS_1 = 9;
  final int NUM_OF_LINES_OF_CODE_1 = 50;
  final int NUM_OF_COMPARISONS_1 = 9;
  final int NUM_OF_ARITHMETIC_OPERATORS_1 = 3;
  final int NUM_OF_CONDITIONALS_1 = 7;

  final double loc_1 = 59;
  final int NUM_OF_PARANTHESIS_1 = 72;
  final double AVG_NUM_OF_PARENTHESIS_1 = 1.2203389830508475;
  final int NUM_OF_COMMAS_1 = 2;
  final double AVG_NUM_OF_COMMAS_1 = 0.03389830508474576;
  final int NUM_OF_PERIODS_1 = 32;
  final double AVG_NUM_OF_PERIODS_1 = 0.5423728813559322;
  final int NUM_OF_SPACES_1 = 846;
  final double AVG_NUM_OF_SPACES_1 = 14.338983050847459;
  //indentation length avg and max
  final int MAX_INDENTATION_LENGTH_1 = 20;
  final double AVG_INDENTATION_LENGTH_1 = 12.203389830508474;
  //line length avg and max
  final int MAX_LINE_LENGTH_1 = 65;
  final double AVG_LINE_LENGTH_1 = 30.864406779661017;
  //blank lines avg
  final double AVG_BLANK_LINES_1 = 0.03389830508474576;
  final int NUM_OF_ASSIGNMENT_EXPRESSIONS_1 = 11;
  final int NUM_OF_NUMBERS_1 = 24;
  final int MAX_NUMBERS_1 = 2;
  final int NUM_OF_STATEMENTS_1 = 42;


  final int NUM_OF_LOOP_STATEMENTS_2 = 6;
  final int NUM_OF_IF_STATEMENTS_2 = 4;
  final int NUM_OF_PARAMETERS_2 = 3;
  final int NUM_OF_COMMENTS_2 = 7;
  final int NUM_OF_LINES_OF_CODE_2 = 36;
  final int NUM_OF_COMPARISONS_2 = 6;
  final int NUM_OF_ARITHMETIC_OPERATORS_2 = 5;
  final int NUM_OF_CONDITIONALS_2 = 5;
  final int NUM_OF_ASSIGNMENT_EXPRESSIONS_2 = 10;
  final int NUM_OF_NUMBERS_2 = 16;
  final int MAX_NUMBERS_2 = 2;
  final int NUM_OF_STATEMENTS_2 = 26;

  final double loc_2 = 48;
  final int NUM_OF_PARANTHESIS_2 = 40;
  final double AVG_NUM_OF_PARENTHESIS_2 = 0.8333333333333334;
  final int NUM_OF_COMMAS_2 = 3;
  final double AVG_NUM_OF_COMMAS_2 = 0.0625;
  final int NUM_OF_PERIODS_2 = 16;
  final double AVG_NUM_OF_PERIODS_2 = 0.3333333333333333;
  final int NUM_OF_SPACES_2 = 568;
  final double AVG_NUM_OF_SPACES_2 = 11.833333333333334;
  //indentation length avg and max
  final int MAX_INDENTATION_LENGTH_2 = 16;
  final double AVG_INDENTATION_LENGTH_2 = 9.833333333333334;
  //line length avg and max
  final int MAX_LINE_LENGTH_2 = 51;
  final double AVG_LINE_LENGTH_2 = 23.854166666666668;
  //blank lines avg
  final double AVG_BLANK_LINES_2 = 0.041666666666666664;



  static FeatureVisitor featureVisitor = null;

  static Features features1 = null; 
  static Features features2 = null; 

  @BeforeEach
  public void setup() {
    System.out.println("Working Directory = " + System.getProperty("user.dir"));

    String dirPath = "src/test/resources/data";
    File projectDir = new File(dirPath);
 
    new DirExplorer(
            (level, path, file) -> path.endsWith(".java"),
            (level, path, file) -> {
              // File file = new File(getClass().getResource("/data/TestSnippet_1.java").getFile());
              // File file = new File("src/test/resources/data/TestSnippet_1.java");
              System.out.println(file.getName());

              featureVisitor = new FeatureVisitor();
              CompilationUnit cu = null;
              CompilationUnit cuNoComm = null;
              try {
                cu = StaticJavaParser.parse(file);
                JavaParser parser = new JavaParser(new ParserConfiguration().setAttributeComments(false));
                cuNoComm = parser.parse(file).getResult().get();
              } catch (FileNotFoundException e) {
                e.printStackTrace();
              }

              if (file.getName().equals("TestSnippet_1.java")) {
                featureVisitor1 = new FeatureVisitor();
                featureVisitor1.visit(cu, null);
                SyntacticFeatureExtractor sfe1 = new SyntacticFeatureExtractor(featureVisitor1.getFeatures());
                features1 = sfe1.extract(cuNoComm.toString());
              } else if (file.getName().equals("TestSnippet_2.java")) {
                featureVisitor2 = new FeatureVisitor();
                featureVisitor2.visit(cu, null);
                SyntacticFeatureExtractor sfe2 = new SyntacticFeatureExtractor(featureVisitor2.getFeatures());
                features2 = sfe2.extract(cuNoComm.toString());
              }

              featureVisitor.visit(cu, null);

              // Extract syntactic features (non JavaParser extraction)
              SyntacticFeatureExtractor sfe =
                new SyntacticFeatureExtractor(featureVisitor.getFeatures());
              FeatureExtractorTest.features = sfe.extract(cuNoComm.toString());
            })
        .explore(projectDir);
  }

  @Test
  public void testLoops1() {
    assertEquals(NUM_OF_LOOP_STATEMENTS_1, featureVisitor1.getFeatures().getNumOfLoops());
  }

  // @Test
  // public void testIfStatements1() {
  //   assertEquals(NUM_OF_IF_STATEMENTS_1, featureVisitor1.getFeatures().getNumOfIfStatements());
  // }

  @Test
  public void testMethodParameters1() {
    assertEquals(NUM_OF_PARAMETERS_1, featureVisitor1.getFeatures().getNumOfParameters());
  }

  @Test
  public void testComments1() {
    assertEquals(NUM_OF_COMMENTS_1, featureVisitor1.getFeatures().getNumOfComments());
  }

  @Test
  public void testAvgComments1() {
    assertEquals(1.0 * NUM_OF_COMMENTS_1 / NUM_OF_LINES_OF_CODE_1, 1.0 * featureVisitor1.getFeatures().getNumOfComments() / NUM_OF_LINES_OF_CODE_1);
  }

  @Test
  public void testComparisons1() {
    assertEquals(NUM_OF_COMPARISONS_1, featureVisitor1.getFeatures().getComparisons());
  }

  @Test
  public void testArithmeticOperators1() {
    assertEquals(NUM_OF_ARITHMETIC_OPERATORS_1, featureVisitor1.getFeatures().getArithmeticOperators());
  }

  @Test
  public void testConditionals1() {
    assertEquals(NUM_OF_CONDITIONALS_1, featureVisitor1.getFeatures().getConditionals());
  }
  
  @Test
  public void testAvgLoops1() {
    assertEquals(1.0 * NUM_OF_LOOP_STATEMENTS_1 / NUM_OF_LINES_OF_CODE_1, 1.0 * featureVisitor1.getFeatures().getNumOfLoops() / NUM_OF_LINES_OF_CODE_1);
  }

  @Test
  public void testAvgAssignExprs1() {
    assertEquals(1.0 * NUM_OF_ASSIGNMENT_EXPRESSIONS_1 / NUM_OF_LINES_OF_CODE_1, 1.0 * featureVisitor1.getFeatures().getAssignExprs() / NUM_OF_LINES_OF_CODE_1);
  }

  @Test
  public void testAvgNumbers1() {
    assertEquals(1.0 * NUM_OF_NUMBERS_1 / NUM_OF_LINES_OF_CODE_1, 1.0 * featureVisitor1.getFeatures().getNumbers() / NUM_OF_LINES_OF_CODE_1);
  }

  @Test
  public void testMaxNumbers1() {
    assertEquals(MAX_NUMBERS_1, featureVisitor1.getFeatures().findMaxNumbers());
  }

  @Test
  public void testAvgConditionals1() {
    assertEquals(1.0 * NUM_OF_CONDITIONALS_1 / NUM_OF_LINES_OF_CODE_1, 1.0 * featureVisitor1.getFeatures().getConditionals() / NUM_OF_LINES_OF_CODE_1);
  }

  // @Test
  // public void testStatements1() {
  //   assertEquals(NUM_OF_STATEMENTS_1, featureVisitor1.getFeatures().getStatements());
  // }

  @Test  
  public void testParenthesis1() {
    assertEquals(NUM_OF_PARANTHESIS_1, features1.getParenthesis());
  }

  @Test
  public void testAvgParenthesis1() {
    assertEquals(AVG_NUM_OF_PARENTHESIS_1, (double)features1.getParenthesis() / loc_1);
  }

  @Test
  public void testCommas1() {
    assertEquals(NUM_OF_COMMAS_1, features1.getCommas());
  }

  @Test
  public void testAvgCommas1() {
    assertEquals(AVG_NUM_OF_COMMAS_1, (double)features1.getCommas() / loc_1);
  }

  @Test
  public void testPeriods1() {
    assertEquals(NUM_OF_PERIODS_1, features1.getPeriods());
  }

  @Test
  public void testAvgPeriods1() {
    assertEquals(AVG_NUM_OF_PERIODS_1, (double)features1.getPeriods() / loc_1);
  }

  @Test
  public void testSpaces1() {
    assertEquals(NUM_OF_SPACES_1, features1.getSpaces());
  }

  @Test
  public void testAvgSpaces1() {
    assertEquals(AVG_NUM_OF_SPACES_1, (double)features1.getSpaces() / loc_1);
  }

  @Test
  public void testMaxIndentationLength1() {
    assertEquals(MAX_INDENTATION_LENGTH_1, features1.getMaxIndentation());
  }

  @Test
  public void testAvgIndentationLength1() {
    assertEquals(AVG_INDENTATION_LENGTH_1, (double)features1.getTotalIndentation() / loc_1);
  }

  @Test
  public void testMaxLineLength1() {
    assertEquals(MAX_LINE_LENGTH_1, features1.getMaxLineLength());
  }

  @Test
  public void testAvgLineLengt1h() {
    assertEquals(AVG_LINE_LENGTH_1, (double)features1.getTotalLineLength() / loc_1);
  }

  @Test
  public void testAvgBlankLength1() {
    assertEquals(AVG_BLANK_LINES_1, (double)features1.getTotalBlankLines() / loc_1);
  }

  @Test
  public void testLoops2() {
    assertEquals(NUM_OF_LOOP_STATEMENTS_2, featureVisitor2.getFeatures().getNumOfLoops());
  }

  @Test
  public void testIfStatements2() {
    assertEquals(NUM_OF_IF_STATEMENTS_2, featureVisitor2.getFeatures().getNumOfIfStatements());
  }

  @Test
  public void testMethodParameters2() {
    assertEquals(NUM_OF_PARAMETERS_2, featureVisitor2.getFeatures().getNumOfParameters());
  }

  @Test
  public void testComments2() {
    assertEquals(NUM_OF_COMMENTS_2, featureVisitor2.getFeatures().getNumOfComments());
  }

  @Test
  public void testAvgComments2() {
    assertEquals(1.0 * NUM_OF_COMMENTS_2 / NUM_OF_LINES_OF_CODE_2, 1.0 * featureVisitor2.getFeatures().getNumOfComments() / NUM_OF_LINES_OF_CODE_2);
  }

    @Test
  public void testComparisons2() {
    assertEquals(NUM_OF_COMPARISONS_2, featureVisitor2.getFeatures().getComparisons());
  }

  @Test
  public void testArithmeticOperators2() {
    assertEquals(NUM_OF_ARITHMETIC_OPERATORS_2, featureVisitor2.getFeatures().getArithmeticOperators());
  }

  @Test
  public void testConditionals2() {
    assertEquals(NUM_OF_CONDITIONALS_2, featureVisitor2.getFeatures().getConditionals());
  }

  @Test
  public void testParenthesis2() {
    assertEquals(NUM_OF_PARANTHESIS_2, features2.getParenthesis());
  }

  @Test
  public void testAvgParenthesis2() {
    assertEquals(AVG_NUM_OF_PARENTHESIS_2, (double)features2.getParenthesis() / loc_2);
  }

  @Test
  public void testCommas2() {
    assertEquals(NUM_OF_COMMAS_2, features2.getCommas());
  }

  @Test
  public void testAvgCommas2() {
    assertEquals(AVG_NUM_OF_COMMAS_2, (double)features2.getCommas() / loc_2);
  }

  @Test
  public void testPeriods2() {
    assertEquals(NUM_OF_PERIODS_2, features2.getPeriods());
  }

  @Test
  public void testAvgPeriods2() {
    assertEquals(AVG_NUM_OF_PERIODS_2, (double)features2.getPeriods() / loc_2);
  }

  @Test
  public void testSpaces2() {
    assertEquals(NUM_OF_SPACES_2, features2.getSpaces());
  }

  @Test
  public void testAvgSpaces2() {
    assertEquals(AVG_NUM_OF_SPACES_2, (double)features2.getSpaces() / loc_2);
  }

  @Test
  public void testMaxIndentationLength2() {
    assertEquals(MAX_INDENTATION_LENGTH_2, features2.getMaxIndentation());
  }

  @Test
  public void testAvgIndentationLength2() {
    assertEquals(AVG_INDENTATION_LENGTH_2, (double)features2.getTotalIndentation() / loc_2);
  }

  @Test
  public void testMaxLineLength2() {
    assertEquals(MAX_LINE_LENGTH_2, features2.getMaxLineLength());
  }

  @Test
  public void testAvgLineLength2() {
    assertEquals(AVG_LINE_LENGTH_2, (double)features2.getTotalLineLength() / loc_2);
  }

  @Test
  public void testAvgBlankLength2() {
    assertEquals(AVG_BLANK_LINES_2, (double)features2.getTotalBlankLines() / loc_2);
  }
    
  @Test
  public void testAvgLoops2() {
    assertEquals(1.0 * NUM_OF_LOOP_STATEMENTS_2 / NUM_OF_LINES_OF_CODE_2, 1.0 * featureVisitor2.getFeatures().getNumOfLoops() / NUM_OF_LINES_OF_CODE_2);
  }

  @Test
  public void testAvgAssignExprs2() {
    assertEquals(1.0 * NUM_OF_ASSIGNMENT_EXPRESSIONS_2 / NUM_OF_LINES_OF_CODE_2, 1.0 * featureVisitor2.getFeatures().getAssignExprs() / NUM_OF_LINES_OF_CODE_2);
  }

  @Test
  public void testAvgNumbers2() {
    assertEquals(1.0 * NUM_OF_NUMBERS_2 / NUM_OF_LINES_OF_CODE_2, 1.0 * featureVisitor2.getFeatures().getNumbers() / NUM_OF_LINES_OF_CODE_2);
  }
  
  @Test
  public void testAverageConditionals2() {
    assertEquals(1.0 * NUM_OF_CONDITIONALS_2 / NUM_OF_LINES_OF_CODE_2, 1.0 * featureVisitor2.getFeatures().getConditionals() / NUM_OF_LINES_OF_CODE_2);
  }

  @Test
  public void testMaxNumbers2() {
    assertEquals(MAX_NUMBERS_2, featureVisitor2.getFeatures().findMaxNumbers());
  }

  // @Test
  // public void testStatements2() {
  //   assertEquals(NUM_OF_STATEMENTS_2, featureVisitor2.getFeatures().getStatements());
  // }
}

