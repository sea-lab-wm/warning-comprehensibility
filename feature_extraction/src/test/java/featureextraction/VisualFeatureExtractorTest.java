package featureextraction;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class VisualFeatureExtractorTest {
  private VisualFeatures visualFeatures;
  private VisualFeatureVisitor visualFeatureVisitor = new VisualFeatureVisitor();

  private double VISUAL_X_SCORE_KEYWORDS = 2.466108354;
  private double VISUAL_X_SCORE_IDENTIFIERS = 3.113012525;
  private double VISUAL_X_SCORE_OPERATORS = 0.5269230769;
  private double VISUAL_X_SCORE_NUMBERS = 0.8141025641;
  private double VISUAL_X_SCORE_STRINGS = 0.8131868132;
  private double VISUAL_X_SCORE_LITERALS = 1.893956044;
  private double VISUAL_X_SCORE_COMMENTS = 3.0;


  private double VISUAL_Y_SCORE_KEYWORDS = 9.714285714;
  private double VISUAL_Y_SCORE_IDENTIFIERS = 14.62738095;
  private double VISUAL_Y_SCORE_OPERATORS = 1.033333333;
  private double VISUAL_Y_SCORE_NUMBERS = 1.441666667;
  private double VISUAL_Y_SCORE_STRINGS = 9.166666667;
  private double VISUAL_Y_SCORE_LITERALS = 11.24285714;
  private double VISUAL_Y_SCORE_COMMENTS = 7.38214286;

  private double VISUAL_AREA_KEYWORDS = 0.1297935103;
  private double VISUAL_AREA_IDENTIFIERS = 0.1887905605;
  private double VISUAL_AREA_OPERATORS = 0.01474926254;
  private double VISUAL_AREA_NUMBERS = 0.02064896755;
  private double VISUAL_AREA_STRINGS = 0.06489675516;
  private double VISUAL_AREA_LITERALS = 0.09734513274;
  private double VISUAL_AREA_COMMENTS = 0.09734513274;

  private static Integer[][] VISUAL_FEATURES_MATRIX_2 = new Integer[][] {
    {1,1,1,1,1,1,0,1,1,1,1,1,0,2,2,2,2,2,2,2,2,2,2,2,2,2,0,0},
    {0,0,0,0,7,7,7,7,7,7,7},
    {7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7},
    {7,7,7,7,7,7,7},
    {0,0,0,0,1,1,1,1,1,1,0,1,1,1,1,0,2,2,2,2,2,2,2,2,2,0,1,1,1,0,2,0,0,1,1,1,0,2,0,0,1,1,1,0,2,0,0,0},
    {0,0,0,0,0,0,0,0,1,1,1,0,0,1,1,1,0,2,0,0,0,4,0,0,2,0,3,0,4,4,0,0,2,0,0,0,0,0},
    {0,0,0,0,0,0,0,0,0,0,0,0,2,2,2,2,2,2,0,2,2,2,0,2,2,2,2,2,2,2,0,0,5,5,5,5,5,5,5,5,5,5,0,0,0},
    {0,0,0,0,0,0,0,0,0},
    {0,0,0,0,0,0,0,0,1,1,1,0,2,0,0,0,4,0},
    {0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,6,6,6,6,0,3,3,0,2,0,3,0,4,4,0,0,0},
    {0,0,0,0,0,0,0,0,0,0,0,0,2,2,2,2,2,2,0,2,2,2,0,2,2,2,2,2,2,2,0,0,5,5,5,5,5,5,5,5,5,5,5,5,0,0,0},
    {0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,2,0,3,0,4,0},
    {0,0,0,0,0,0,0,0,0},
    {0,0,0,0,0},
    {0}
  };

  @BeforeEach
  public void setup() {

    String filePath = "src/test/resources/data/TestSnippet_3.java";
    File file = new File(filePath);

    CompilationUnit cu = null;
    String[] split = null;
    try {
      cu = StaticJavaParser.parse(file);
      // split = Files.readString(file.toPath()).split("\r?\n");
      split = new String(Files.readAllBytes(file.toPath())).split("\r?\n" );
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    visualFeatureVisitor.getVisualFeatures().makeVisualFeaturesMatrix(split);

    visualFeatureVisitor.visit(cu, null);
    visualFeatures = visualFeatureVisitor.getVisualFeatures();
    visualFeatures.calculateVisualFeatures();
  }

  //Test if the generated matrix matches the correct version
  @Test
  public void testMatrix() {
    ArrayList<ArrayList<Integer>> matrix = visualFeatures.getVisualFeaturesMatrix();
    for (int row = 0; row < matrix.size(); row++) {
      ArrayList<Integer> line = matrix.get(row);
      for (int col = 0; col < line.size(); col++) {
        if (line.get(col) != VISUAL_FEATURES_MATRIX_2[row][col]) {
          fail("Matrix is incorrect");
        }
      }
    }
  }

  // Visual X
  @Test
  public void testVisualXKeywords() {
    assertTrue(VISUAL_X_SCORE_KEYWORDS - visualFeatures.getKeywordsX() < 0.0001);
  }

  @Test
  public void testVisualXIdentifiers() {
    assertTrue(VISUAL_X_SCORE_IDENTIFIERS - visualFeatures.getIdentifiersX() < 0.0001);
  }

  @Test
  public void testVisualXOperators() { 
    assertTrue(VISUAL_X_SCORE_OPERATORS - visualFeatures.getOperatorsX() < 0.0001);
  }

  @Test
  public void testVisualXNumbers() {
    assertTrue(VISUAL_X_SCORE_NUMBERS - visualFeatures.getNumbersX() < 0.0001);
  }

  @Test
  public void testVisualXStrings() {
    assertTrue(VISUAL_X_SCORE_STRINGS - visualFeatures.getStringsX() < 0.0001);
  }

  @Test
  public void testVisualXLiterals() {
    assertTrue(VISUAL_X_SCORE_LITERALS - visualFeatures.getLiteralsX() < 0.0001);
  }

  @Test
  public void testVisualXComments() {
    assertTrue(VISUAL_X_SCORE_COMMENTS - visualFeatures.getCommentsX() < 0.0001);
  }

  // Visual Y
  @Test
  public void testVisualYKeywords() {
    assertTrue(VISUAL_Y_SCORE_KEYWORDS - visualFeatures.getKeywordsY() < 0.0001);
  }

  @Test
  public void testVisualYIdentifiers() {
    assertTrue(VISUAL_Y_SCORE_IDENTIFIERS - visualFeatures.getIdentifiersY() < 0.0001);
  }

  @Test
  public void testVisualYOperators() {
    assertTrue(VISUAL_Y_SCORE_OPERATORS - visualFeatures.getOperatorsY() < 0.0001);
  }

  @Test
  public void testVisualYNumbers() {
    assertTrue(VISUAL_Y_SCORE_NUMBERS - visualFeatures.getNumbersY() < 0.0001);
  }

  @Test
  public void testVisualYStrings() {
    assertTrue(VISUAL_Y_SCORE_STRINGS - visualFeatures.getStringsY() < 0.0001);
  }

  @Test
  public void testVisualYLiterals() {
    assertTrue(VISUAL_Y_SCORE_LITERALS - visualFeatures.getLiteralsY() < 0.0001);
  }

  @Test
  public void testVisualYComments() {
    assertTrue(VISUAL_Y_SCORE_COMMENTS - visualFeatures.getCommentsY() < 0.0001);
  }

  // Visual Area
  @Test
  public void testVisualAreaKeywords() {
    assertTrue(VISUAL_AREA_KEYWORDS - visualFeatures.getKeywordsY() < 0.0001);
  }

  @Test
  public void testVisualAreaIdentifiers() {
    assertTrue(VISUAL_AREA_IDENTIFIERS - visualFeatures.getIdentifiersArea() < 0.0001);
  }

  @Test
  public void testVisualAreaOperators() {
    assertTrue(VISUAL_AREA_OPERATORS - visualFeatures.getOperatorsArea() < 0.0001);
  }

  @Test
  public void testVisualAreaNumbers() {
    assertTrue(VISUAL_AREA_NUMBERS - visualFeatures.getNumbersArea() < 0.0001);
  }

  @Test
  public void testVisualAreaStrings() {
    assertTrue(VISUAL_AREA_STRINGS - visualFeatures.getStringsArea() < 0.0001);
  }

  @Test
  public void testVisualAreaLiterals() {
    assertTrue(VISUAL_AREA_LITERALS - visualFeatures.getLiteralsArea() < 0.0001);
  }

  @Test
  public void testVisualAreaComments() {
    assertTrue(VISUAL_AREA_COMMENTS - visualFeatures.getCommentsArea() < 0.0001);
  }
}