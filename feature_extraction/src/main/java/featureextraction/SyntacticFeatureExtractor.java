package featureextraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SyntacticFeatureExtractor {

  private Features features = new Features();
  
  // source : https://docs.oracle.com/javase/tutorial/java/nutsandbolts/_keywords.html
  private String [] java_keywords = 
  {"abstract", 
  "assert", 
  "boolean", 
  "break", 
  "byte", 
  "case", 
  "catch", 
  "char", "class", "const", "continue",
  "default", "do", "double", "else", "enum", "extends",
  "final", "finally", "float", "for", "goto", "if",
  "implements", "import", "instanceof", "int", "interface",
  "long", "native", "new", "package", "private", "protected",
  "public", "return", "short", "static", "strictfp", "super",
  "switch", "synchronized", "this", "throw", "throws", "transient",
  "try", "void", "volatile", "while"};

  public SyntacticFeatureExtractor(Features features) {
    this.features = features;
  }

  /**
   * Extracts all the syntactic features into the given Feature Map
   *
   * @param snippet
   * @return the filled in Feature Map
   */
  public Features extract(String snippet) {
    
    int commas = count(snippet, ",");
    int periods = count(snippet, "\\.");
    int spaces = count(snippet, " ");
    int parenthesis = count(snippet, "\\(") * 2;

    features.setCommas(commas);
    features.setPeriods(periods);
    features.setSpaces(spaces);
    features.setParenthesis(parenthesis);

    features.setTokenEntropy(getTokenEntropy(snippet));
    features.setKeywords(getKeywords(snippet));

    extractLineWiseFeatures(snippet, ",");
    extractLineWiseFeatures(snippet, "\\.");
    extractLineWiseFeatures(snippet, " ");
    extractLineWiseFeatures(snippet, "\\(");
    extractLineWiseFeatures(snippet, "\\)");

    /* the line length counts all the characters of the given line 
    (including the spaces/tabs/indentation in the beginning) */
    String[] lines = snippet.split("\r?\n");
    int totalLength = 0;

    int lineNumber = 0;
    for (String line : lines) {
        totalLength += line.length();
        if (line.length() > 0) {
            features.getLineLineLengthMap().put(Integer.toString(lineNumber), (double)line.length());
        }
        lineNumber++;
    }
    features.setTotalLineLength(totalLength);
    
    int maxNumOfWords = 0;

    // get the maximum length in any line
    int maxLineLength = 0;
    for (String line : lines) {
      
      int lineLength = line.length();
      if (lineLength > maxLineLength) {
          maxLineLength = lineLength;
      }

      String[] words = line.split(" ");
      int numOfWords = words.length;
      if (numOfWords > maxNumOfWords) {
          maxNumOfWords = numOfWords;
      }
    }
    features.setMaxLineLength(maxLineLength);
    features.setMaxWords(maxNumOfWords);

    //indentation length
    /* iterates through each line character by character, 
    counting spaces and tabs until it reaches a non-space, non-tab character */
    int total_indentation = 0;
    int maxIndentation = 0;

    lineNumber = 0;
    for (String line : lines) {
      int line_indentation = 0;
      for (int i = 0; i < line.length(); i++) {
          char c = line.charAt(i);
          if (c == ' ' || c == '\t') {
              line_indentation++;
          } else {
              break;
          }
      }
      total_indentation += line_indentation;

      // Update indentation for each line
      if (line_indentation > 0) {
          features.getLineIndentationLengthMap().put(Integer.toString(lineNumber), (double)line_indentation);
      }
      lineNumber++;

      features.setTotalIndentation(total_indentation);
      // Update maximum indentation
      if (line_indentation > maxIndentation) {
          maxIndentation = line_indentation;
      }
    }
  
    // Maximum indentation
    features.setMaxIndentation(maxIndentation);
    
    // Blank line
    int blankLines = 0;
    for (String line : lines) {
        if (line.trim().isEmpty()) {
            blankLines++;
        }
    }

    features.setTotalBlankLines(blankLines);

    // set Max Character Count
    features.setMaxCharacterCount(maxCharacterCount(snippet));

    return features;
  }

  private int maxCharacterCount(String snippet) {
    // create a map to store the count of each character
    Map<Character, Integer> charCountMap = new HashMap<>();
    // convert the string to char array
    char[] strArray = snippet.toCharArray();
    // loop through the char array
    for (char c : strArray) {
      // if the character is not a space
      if (c != ' ') {
        // if the map contains the character
        if (charCountMap.containsKey(c)) {
          // increment the count
          charCountMap.put(c, charCountMap.get(c) + 1);
        } else {
          // add the character to the map
          charCountMap.put(c, 1);
        }
      }
    }
    
    // find the character with the maximum count
    int maxCount = 0;
    for (char c : charCountMap.keySet()) {
      if (charCountMap.get(c) > maxCount) {
        maxCount = charCountMap.get(c);
      }
    }
    return maxCount;
  }

  private void extractLineWiseFeatures(String snippet, String value) {
    String[] lines = snippet.split("\n");
    
    for (int i = 0; i < lines.length; i++) {
      // Use Matcher class of java.util.regex
      // to match the character
      String line = lines[i];
      String lineNumber = Integer.toString(i);
      Matcher matcher = Pattern.compile(value).matcher(line);
      double res = 0.0;
      while (matcher.find()) res++;
      
      switch (value) {
        case ",":  
          if (res > 0.0) {            
              features.getLineCommaMap().put(lineNumber, res);
          }
          break;
        case "\\.":
          if (res > 0.0) {
              features.getLinePeriodMap().put(lineNumber, res);
          }
          break;
        case " ":
          if (res > 0.0) {
              features.getLineSpaceMap().put(lineNumber, res);
          } 
          break;
        case "\\(":
          if (res > 0.0) {
            features.getLineParenthesisMap().put(lineNumber, res);
          }
          break; 
        case "\\)":
          if (res > 0.0) {
            if(features.getLineParenthesisMap().containsKey(lineNumber)){
              features.getLineParenthesisMap().put(lineNumber, features.getLineParenthesisMap().get(lineNumber) + res);
            } else {
              features.getLineParenthesisMap().put(lineNumber, res);
            }
          }
          break;
        default:
          break;
      }
    }  
  }

  private Map<String, List<String>> getKeywords(String snippet) {
    Map<String, List<String>> keywordList = new HashMap<>();
    String[] lines = snippet.split("\n");
    int lineNumber = 0;
    for (String line : lines) {
      String[] tokens = line.split(" ");
      List<String> keywordsList = new ArrayList<>();
      for (String token : tokens) {
        for (String keyword : java_keywords) {
          if (token.equals(keyword)) {
            keywordsList.add(keyword);
            break;
          }
        }
      }
      if (keywordsList.size() > 0) {
        keywordList.put(Integer.toString(lineNumber), keywordsList);
      }
      lineNumber++;
    }

    // print the keyword list
    // for (String key : keywordList.keySet()) {
    //   System.out.println(key + " : " + keywordList.get(key));
    // }
    return keywordList;
  }

  /**
   * This method computes Token Entropy of a snippet
   * 
   * p(term in snippet) = count(term in snippet) / SUM{j=1 to n} (count(term in snippet))
   * here count(term in snippet) is the number of occurences of a term in the snippet
   * and n is the number of unique terms in the snippet 
   * 
   * H(snippet) = - SUM{j=1 to n} (p(term in snippet) * log2(p(term in snippet)))
   * here H(snippet) is the token entropy of the snippet 
   */
  private double getTokenEntropy(String snippet) {
    HashMap<String, Integer> tokenCount = tokenCounter(snippet);
    double tokenEntropy = 0.0;
    double tokenOccurenceSum = 0.0;

    for (String token : tokenCount.keySet()) {
      tokenOccurenceSum += tokenCount.get(token);
    }

    for (String token : tokenCount.keySet()) {
      double p = tokenCount.get(token) / tokenOccurenceSum;
      tokenEntropy += p * (Math.log(p) / Math.log(2));
    }

    return -tokenEntropy;
  }


  /**
   * Tokenise the given string into a list of tokens
   * and count the number of occurences of each token
   * 
   */
  private HashMap<String, Integer> tokenCounter(String snippet) {
    HashMap<String, Integer> tokens = new HashMap<String, Integer>();
    String[] words = snippet.split(" ");

    for (String word : words) {
      if (tokens.containsKey(word)) {
        tokens.put(word, tokens.get(word) + 1);
      } else {
        tokens.put(word, 1);
      }
    }

    return tokens;
  }

  /**
   * Counts the occurences of the given character in the given string
   *
   * @param s
   * @param ch
   * @return the # of occurences
   */
  private int count(String s, String ch) {
    // Use Matcher class of java.util.regex
    // to match the character
    Matcher matcher = Pattern.compile(ch).matcher(s);
    int res = 0;

    while (matcher.find()) {
      // System.out.println("Found " + ch + " at index " + matcher.start());
      res++;
    }
    return res;
  }
}
