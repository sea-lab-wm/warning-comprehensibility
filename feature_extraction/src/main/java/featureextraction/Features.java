package featureextraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Data;

public @Data class Features {

  private Map<String, List<Integer>> lineNumberMap = new HashMap<String, List<Integer>>();

  private Map<String, Double> lineAssignmentExpressionMap = new HashMap<String, Double>();
  private Map<String, Double> lineCommaMap = new HashMap<String, Double>();
  private Map<String, Double> lineCommentMap = new HashMap<String, Double>();
  private Map<String, Double> lineComparisonMap = new HashMap<String, Double>();
  private Map<String, Double> lineConditionalMap = new HashMap<String, Double>();
  private Map<String, Double> lineKeywordMap = new HashMap<String, Double>();
  private Map<String, Double> lineLoopMap = new HashMap<String, Double>();
  private Map<String, Double> lineOperatorMap = new HashMap<String, Double>();
  private Map<String, Double> lineParenthesisMap = new HashMap<String, Double>();
  private Map<String, Double> linePeriodMap = new HashMap<String, Double>();
  private Map<String, Double> lineSpaceMap = new HashMap<String, Double>();
  private Map<String, Double> lineIndentationLengthMap = new HashMap<String, Double>();
  private Map<String, Double> lineLineLengthMap = new HashMap<String, Double>();
  private Map<String, Double> lineNestedBlockMap = new HashMap<String, Double>();


  // keeps track operands and operators
  private HashSet<String> operands = new HashSet<String>();
  private HashSet<String> operators = new HashSet<String>();

  // keep line number and identifiers per each line
  private HashMap<String, List<String>> lineNumber_Identifier_Map = new HashMap<String, List<String>>();
  // keep line number and keywords per each line
  private Map<String, List<String>> keywords = new HashMap<>();

  // keeps all the class names used in a snippet. This is used to compute AEDQ (Attributes External Documentation Quality)
  private Set<String> classNames = new HashSet<>();

  // keeps all the comments in a snippet
  private List<String> comments = new ArrayList<>();

  // keep throws, return, param with line number. This is required for compute MIDQ (Methods Internal Documentation Quality)
  private HashMap<String, List<String>> lineNumber_param_return_throws_Map = new HashMap<>(); 

  // feature 1: #parameters of method
  private int numOfParameters = 0;

  // feature 2: #if statements
  private int numOfIfStatements = 0;

  // feature 3: #loops
  private int numOfLoops = 0;

  // feature 4: #assignments expressions
  private int assignExprs = 0;

  // feature 5: #commas
  private int commas = 0;

  // feature 6: #periods
  private int periods = 0;

  // feature 7: #spaces
  private int spaces = 0;

  // feature 8: #comparisons
  private int comparisons = 0;

  // feature 9: #parenthesis
  private int parenthesis = 0;

  // feature 10: #literals
  private int literals = 0;

  // feature 11: #comments
  private int numOfComments = 0;

  // feature 12: #arithmeticOperators
  private int arithmeticOperators;

  // feature 13: #conditionals (if and switch statments)
  private int conditionals;

  // feature 14: avg #commas
  private float avgCommas = 0.0f;
  
  // feature 15: avg #parenthesis
  private float avgParenthesis = 0.0f;
  
  // feature 16: avg #perriods
  private float avgPeriods = 0.0f;
  
  // feature 17: avg #spaces
  private float avgSpaces = 0.0f;
  
  // feature 18: avg line length (beginning spaces + characters)
  private float avgLineLength = 0.0f;

  private int totalLineLength = 0;
  
  // feature 19: max line length
  private int maxLineLength = 0;
  
  private int totalIndentation = 0;
  
  // feature 20: avg indentation
  private float avgIndentation = 0;
  
  // feature 21: max indentation
  private int maxIndentation = 0;

  private int TotalBlankLines = 0;
  
  // feature 22: avg blank lines
  private float avgBlankLines = 0;
  
  // feature 23: #numbers
  private int numbers;

  // feature 24 #statements
  private int statements;

  // feature 25: #booleanOperators
  private int booleanOperators;

  // feature 26: #identifiers
  private int identifiers;

  // feature 27: #token entropy
  private double tokenEntropy;

  // feature 28: #nested blocks
  private int nestedBlocks;

  // feature 29: #words
  private int maxWords;

  // feature 30: #maxCharacatersCount
  private int maxCharacterCount;

  public void incrementNumOfIfStatements() {
    setNumOfIfStatements(getNumOfIfStatements() + 1);
  }

  public void incrementNumOfLoops() {
    setNumOfLoops(getNumOfLoops() + 1);
  }

  public void incrementNumOfLiterals() {
    setLiterals(getLiterals() + 1);
  }

  public void incrementNumOfComments() {
    setNumOfComments(getNumOfComments() + 1);
  }

  public void incrementNumOfArithmeticOperators() {
    setArithmeticOperators(getArithmeticOperators() + 1);
  }

  public void incrementNumOfConditionals() {
    setConditionals(getConditionals() + 1);
  }

  public void incrementNumOfNumbers() {
    setNumbers(getNumbers() + 1);
  }

  public void incrementNumOfStatements() {
    setStatements(getStatements() + 1);
  }

  public void incrementNumOfLogicalOperators() {
    setBooleanOperators(getBooleanOperators() + 1);
  }

  public void incrementNumOfIdentifiers() {
    setIdentifiers(getIdentifiers() + 1);
  }

  public void incrementNumOfNestedBlocks() {
    setNestedBlocks(getNestedBlocks() + 1);
  }

  /**
   * Searches through features.lineNumber_Identifier_Map, a HashMap<String, List<String>>
   * to find the line with the most indetifiers, or the List<String> with the largest size
   */
  public int getMaxIdentifierLength() {
    int max = 0;
    for (String lineNumber : lineNumber_Identifier_Map.keySet()) {
      List<String> identifierList = lineNumber_Identifier_Map.get(lineNumber);
      for (String identifier : identifierList) {
        if (identifier.length() > max) {
          max = identifier.length();
        }
      }
    }
    return max;
  }

  /**
   * Searches through features.lineNumberMap, a HashMap<String line_number, List<Integer> lineIntegers>
   * to find the line with the most integers, or the List<Integer> with the largest size
   */
  public int findMaxNumbers() {
    int max = 0;
    for (List<Integer> line : lineNumberMap.values()) {
      if (line.size() > max) {
        max = line.size();
      }
    }
    return max;
  }
}
