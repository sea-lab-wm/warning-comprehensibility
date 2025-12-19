package featureextraction;

import java.util.ArrayList;

import lombok.Data;

public @Data class VisualFeatures {

  private ArrayList<ArrayList<Integer>> visualFeaturesMatrix = new ArrayList<ArrayList<Integer>>();

  private ArrayList<Double> featuresX = new ArrayList<Double>();
  private ArrayList<Double> featuresY = new ArrayList<Double>();
  private ArrayList<Double> visualAreaArray = new ArrayList<Double>();

  public ArrayList<ArrayList<Integer>> getVisualFeaturesMatrix() {
    return visualFeaturesMatrix;
  }

  //Creates an empty matrix for the visual features
  public void makeVisualFeaturesMatrix(String[] snippet) {
    for (int i = 0; i < snippet.length; i++) {
      visualFeaturesMatrix.add(new ArrayList<Integer>());
      int lineLength = snippet[i].length();
      for (int j = 0; j < lineLength; j++) {
        visualFeaturesMatrix.get(i).add(0);
      }
    }
  }

  /**
   * Calculates Visual X & Visual Y:
   * the sum of the ratios of the target feature number to the total number of characters part of a visual feature in each row (X) or column (Y)
  */
  public void calculateVisualFeatures() {
    for (int featureNumber = 1; featureNumber <= 7; featureNumber++) {
      featuresX.add(findVisualX(featureNumber));
      featuresY.add(findVisualY(featureNumber));
      visualAreaArray.add(findVisualArea(featureNumber));
    }
    //Sum all literals for visual feature calculation
    featuresX.set(5, getNumbersX() + getStringsX() + getLiteralsX());
    featuresY.set(5, getNumbersY() + getStringsY() + getLiteralsY());
    visualAreaArray.set(5, getNumbersArea() + getStringsArea() + getLiteralsArea());
  }

  /**
   * Finds Visual X for a single visual feature
   * visual X is the sum of the ratio in each ROW
   * the ratio is calculated by:
   * (# of characters belonging to target visual feature) / (# of characters belonging to any visual feature) 
   * 
   * Example Matrix:
   * 1 1 1 0 0 0 2
   * 0 0 3 3 4
   * 0 0 0 0 2
   * 1 1 3
   * 
   * Visual X of 1 is (3/4) + (0/3) + (0/1) + (2/3) = ~1.42
  */
  private double findVisualX(int featureNumber) {
    double sumRatios = 0;   //keeps track of the sum of each line's ratio
    //Iterate through each line in the matrix
    for (ArrayList<Integer> line : visualFeaturesMatrix) {
      int sumX = 0;   //keeps track of the number of characters belonging to the target visual feature
      int sumFeatures = 0;  //keeps track of the number of characters belonging to any visual feature
      //Iterate through columns
      for (Integer num : line) {
        //Does character belong to any visual feature
        if (num != 0) {
          //Does character belong to target visual feature
          if (num == featureNumber) {
            sumX++;
          }
          sumFeatures++;
        }
      }
      //Add ratio (unless the row has no visual features)
      if (sumFeatures != 0) {
        sumRatios += (1.0 * sumX) / sumFeatures;
      }
    }
    return sumRatios;
  }

  /**
   * Finds Visual Y for a single visual feature
   * visual Y is the sum of the ratio in each COLUMN
   * the ratio is calculated by:
   * (# of characters belonging to target visual feature) / (# of characters belonging to any visual feature) 
   * 
   * Example Matrix:
   * 1 1 1 0 0 0 2
   * 0 0 3 3 4
   * 0 0 0 0 2
   * 1 1 3
   * 
   * Visual Y of 1 is (2/2) + (2/2) + (1/3) + (0/1) + (0/2) + (0/1) = ~2.33
   */
  private double findVisualY(int featureNumber) {
    double sumRatios = 0; //keeps track of the sum of each column's ratio
    int maxLineLength = findMaxLineLength(); //finds the number of columns
    //Iterate through columns
    for (int c = 0; c < maxLineLength; c++) {
      int sumFeatures = 0;  //keeps track of the number of characters belonging to the target visual feature
      int sumY = 0; //keeps track of the number of characters belonging to any visual feature
      //Iterate through rows
      for (ArrayList<Integer> line : visualFeaturesMatrix) {
        //Check if the row has enough columns
        if (c < line.size()) {
          int num = line.get(c);
          //Does character belong to any visual feature
          if (num != 0) {
            //Does character belong to target visual feature  
            if (num == featureNumber){
              sumY++;
            }
            sumFeatures++;
          }
        }
      }
      //Add ratio (unless the column has no visual features)
      if (sumFeatures != 0) {
        sumRatios += (1.0 * sumY) / sumFeatures;
      }
    }
    return sumRatios;
  }

  /**
   * Finds Visual Area for a single visual feature
   * visual area is the percentage of characters in the method belonging to the target feature
   * the ratio is calculated by:
   * (# of characters belonging to target visual feature) / (total # of characters) 
   * 
   * Example Matrix:
   * 1 1 1 0 0 0 2
   * 0 0 3 3 4
   * 0 0 0 0 2
   * 1 1 3
   * 
   * Visual Area of 1 is 5/20 = 0.25
   */
  public double findVisualArea(int featureNumber) {
    int numCharsOfTargetType = 0;
    int numChars = 0;
    //Iterate through each line in the matrix
    for (ArrayList<Integer> line : visualFeaturesMatrix) {
      //Iterate through columns
      for (Integer num : line) {
        //Does character belong to target visual feature
        if (num == featureNumber) {
          numCharsOfTargetType++;
        }
      }
      //Keep track of total number of characters
      numChars += line.size();
    }
    //Return the calculated percentage
    return (1.0*numCharsOfTargetType)/numChars;
  }

  private int findMaxLineLength() {
    int maxSize = 0;
    for (ArrayList<Integer> line : visualFeaturesMatrix) {
      if (line.size() > maxSize)
        maxSize = line.size();
    }
    return maxSize;
  }

  //Visual X getters
  public double getKeywordsX() {
    return featuresX.get(0);
  }

  public double getIdentifiersX() {
    return featuresX.get(1);
  }

  public double getOperatorsX() {
    return featuresX.get(2);
  }

  public double getNumbersX() {
    return featuresX.get(3);
  }

  public double getStringsX() {
    return featuresX.get(4);
  }

  public double getLiteralsX() {
    return featuresX.get(5);
  }

  public double getCommentsX() {
    return featuresX.get(6);
  }

  //Visual Y getters
  public double getKeywordsY() {
    return featuresY.get(0);
  }

  public double getIdentifiersY() {
    return featuresY.get(1);
  }

  public double getOperatorsY() {
    return featuresY.get(2);
  }

  public double getNumbersY() {
    return featuresY.get(3);
  }

  public double getStringsY() {
    return featuresY.get(4);
  }

  public double getLiteralsY() {
    return featuresY.get(5);
  }

  public double getCommentsY() {
    return featuresY.get(6);
  }

  //Visual Area getters
  public double getKeywordsArea() {
    return visualAreaArray.get(0);
  }

  public double getIdentifiersArea() {
    return visualAreaArray.get(1);
  }

  public double getOperatorsArea() {
    return visualAreaArray.get(2);
  }

  public double getNumbersArea() {
    return visualAreaArray.get(3);
  }

  public double getStringsArea() {
    return visualAreaArray.get(4);
  }

  public double getLiteralsArea() {
    return visualAreaArray.get(5);
  }

  public double getCommentsArea() {
    return visualAreaArray.get(6);
  }

  public String printMatrix() {
    StringBuilder output = new StringBuilder();
    for (ArrayList<Integer> line : visualFeaturesMatrix) {
      output.append(line.toString());
      output.append("\n");
    }
    return output.toString();
  }

  public String visualXYString() {
    StringBuilder output = new StringBuilder();
    output.append(featuresX.toString());
    output.append("\n");
    output.append(featuresY.toString());
    return output.toString();
  }
}
