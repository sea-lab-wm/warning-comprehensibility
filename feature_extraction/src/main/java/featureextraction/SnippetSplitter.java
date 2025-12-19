package featureextraction;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.LineComment;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class SnippetSplitter {

  private final String OUTPUT_DIR;
  private int count; // added to snippet file name to prevent overriding incase of duplicate snippet

  // Define which snippets were manually created
  private ArrayList<String> manuallyCreatedSnippets;
  /*
   * Constructs a SnippetSplitter object.
   *
   * @param manualInputDir The relative path of the directory which should contain the manually created snippets
   * @param outputDir The relative path of the directory which should contain the splitted snippets
   */
  public SnippetSplitter(String manualInputDir, String outputDir) {
    OUTPUT_DIR = outputDir;
    manuallyCreatedSnippets = new ArrayList<>();
    cleanOutputDir(manualInputDir);
  }

  /**
   * Deletes all previously generated files and then inserts the manually created files.
   *
   * @param manualInputDir
   */
  private void cleanOutputDir(String manualInputDir) {
    // Remove all the old output files from previous run
    File dir = new File(OUTPUT_DIR);
    try {
      Files.walk(dir.toPath()).filter(Files::isRegularFile).map(Path::toFile).forEach(File::delete);
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Copy the manually created files into the output directory
    new DirExplorer(
            (level, path, file) -> path.endsWith(".java"),
            (level, path, file) -> {
              String snippet = file.getName();
              manuallyCreatedSnippets.add(snippet);

              try {
                Files.createDirectories(
                    Paths.get(OUTPUT_DIR + path.substring(0, path.lastIndexOf("/"))));
                Files.copy(
                    Paths.get(manualInputDir + path),
                    Paths.get(OUTPUT_DIR + path),
                    StandardCopyOption.REPLACE_EXISTING);
              } catch (IOException e) {
                System.out.println(Paths.get(manualInputDir + path).toString());
                System.out.println(Paths.get(OUTPUT_DIR + path).toString());
                e.printStackTrace();
              }
            })
        .explore(new File(manualInputDir));
  }

  /**
   * Explores and splits the snippets out of a directory pertaining to a single specified dataset.
   *
   * @param datasetDir
   * @param dataset
   * @param commentText is used to differentiate the start of a snippet from a non snippet method
   */
  public void run(File datasetDir, String dataset, String commentText) {
    System.out.println("running on: " + datasetDir.getAbsolutePath());
    count = 0;
    new DirExplorer(
            (level, path, file) -> path.endsWith(".java"),
            (level, path, file) -> {
              try {
                extractMethods(file, dataset, commentText);
              } catch (IOException e) {
                e.printStackTrace();
              }
            })
        .explore(datasetDir);
  }

  /**
   * Given a single Java file, extracts its snippets and copies them to individual files.
   *
   * @param inputFile The Java file to extract snippets from - may contain multiple methods
   * @param outputDir The directory to write the splitted snippet to
   * @param dataset The identifier of the dataset the file belongs to
   * @param commentText is used to differentiate the start of a snippet from a non snippet method
   *     eg: SNIPPET_STARTS
   * @throws IOException
   */
  private void extractMethods(File inputFile, String dataset, String commentText)
      throws IOException {
    CompilationUnit cu = null;

    try {
      // Parse the input file with lexical preservation enabled
      // uncomment below two lines when dealing with raw snippets
      // JavaParser parser =
      //     new JavaParser(new ParserConfiguration().setLexicalPreservationEnabled(true));
      // cu = parser.parse(inputFile).getResult().get();
      cu = StaticJavaParser.parse(inputFile); // comment this line when dealing with raw snippets
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    int countWithinFile = 0;

    // For each method in the parsed file...
    for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
      // Find the comment immediately preceding the method
      Comment comment = method.getComment().orElse(null);

      if (comment instanceof LineComment && comment.getContent().contains(commentText)) {
        // If the comment matches, create a new file and write the method surrounded by
        // a dummy class
        String methodName = method.getNameAsString();
        String fileName;
        countWithinFile++; // current snippet within a single file
        count++; // current snippet within a single dataset

        // Construct the file name and check if counter already exists in
        // manuallyCreatedSnippets, if so, the counter needs to be skipped
        if (dataset.equals("6")) {
          if (checkForFile(
              Integer.toString(countWithinFile) + "$" + inputFile.getName().split("\\.")[0],
              dataset)) {
            countWithinFile++;
          }
          fileName =
              "ds_"
                  + dataset
                  + "_"
                  + "snip_"
                  + Integer.toString(countWithinFile)
                  + "$"
                  + inputFile.getName().split("\\.")[0]
                  + "_"
                  + methodName;
        } else if (dataset.equals("f")) {
          fileName =
              "ds_"
                  + dataset
                  + "_"
                  + "snip_"
                  + inputFile.getName().split("\\.")[0]
                  + "_"
                  + methodName;
        } else {
          if (checkForFile(Integer.toString(count), dataset)) {
            count++;
          }
          fileName = "ds_" + dataset + "_" + "snip_" + Integer.toString(count) + "_" + methodName;
        }

        File outputFile = new File(OUTPUT_DIR + "ds_" + dataset + "/" + fileName + ".java");

        if (outputFile.exists()) {
          System.out.println("Warning: file already exists");
        }

        // Surround the method with a dummy class
        String methodString = method.toString();
        String fullString =
            "package snippet_splitter_out.ds_"
                + dataset
                + ";\n"
                + "public class "
                + fileName
                + " {\n"
                + methodString
                + "\n}";

        // Create directory for the dataset if it does not exist
        Files.createDirectories(Paths.get(OUTPUT_DIR + "ds_" + dataset));

        // Write the dummy class to the output file
        try (FileWriter fileWriter = new FileWriter(outputFile)) {
          fileWriter.write(fullString);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * Extracts the methods and constructors from a single Java file and copies them to individual
   * files.
   * @throws IOException
   */
  private void extractMethodsFromSnippet(File inputFile, String dataset, String outputDir) throws IOException {
    CompilationUnit cu = null;
    JavaParser parser = new JavaParser(new ParserConfiguration().setLexicalPreservationEnabled(true));
    try {
      cu = parser.parse(inputFile).getResult().get();
      String constructor = cu.findFirst(ConstructorDeclaration.class)
          .map(constBody -> constBody.toString())
          .orElse("");
      String methodBody = cu.findFirst(MethodDeclaration.class)
          .map(method -> method.toString())
          .orElse(constructor);

      File outputFile = new File(outputDir + "ds_" + dataset + "/" + inputFile.getName());

      if (outputFile.exists()) {
        System.out.println("Warning: file already exists");
      }
      // Create directory for the dataset if it does not exist
      Files.createDirectories(Paths.get(outputDir + "ds_" + dataset));

      // Write the dummy class to the output file
      try (FileWriter fileWriter = new FileWriter(outputFile)) {
        fileWriter.write(methodBody);
      } catch (Exception e) {
        e.printStackTrace();
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * Navigating through a directory and extracting java files. Then call
   * extractMethodsFromSnippet() to extract methods from each java file.
   */
  public void exploreDir(String dirPath, String outputDir) {
    File projectDir = new File(dirPath);
    new DirExplorer(
        (level, path, file) -> path.endsWith(".java"),
        (level, path, file) -> {
          try {
            // extract the dataset name from the path
            String dataset_name = path.split("/")[path.split("/").length - 2].split("_")[1];
            extractMethodsFromSnippet(file, dataset_name, outputDir);
          } catch (IOException e) {
            e.printStackTrace();
          }
        })
        .explore(projectDir);
  }

  /**
   * Checks if a file exists for a given snippet number already. This is to accomodate for the
   * couple snippets which cannot be automatically split using the JavaParser.
   *
   * @return true if the target file exists, false otherwise
   */
  private boolean checkForFile(String snippet, String dataset) {
    for (int i = 0; i < manuallyCreatedSnippets.size(); i++) {
      if (manuallyCreatedSnippets.get(i).split("_")[3].equals(snippet)
          && manuallyCreatedSnippets.get(i).split("_")[1].equals(dataset)) {
        System.out.println("manually created snippet found: " + manuallyCreatedSnippets.get(i));
        System.out.println(snippet);
        return true;
      }
    }

    return false;
  }

  /*
   * Main method for splitting snippets
   * Refer "Split method into code snippets" section in README.md
   */
  public static void main(String[] args) {
    // Snippet splitting
    SnippetSplitter ss =
        new SnippetSplitter(
            "ml_model/src/main/resources/manually_created_snippets/",
            "ml_model/src/main/resources/snippet_splitter_out/");
    // "ml_model/src/main/resources/raw_snippet_splitter_out/"); // raw method output
    ss.run(
        new File("simple-datasets/src/main/java/cog_complexity_validation_datasets/One/"),
        "1",
        "SNIPPET_STARTS");
    ss.run(
        new File("simple-datasets/src/main/java/cog_complexity_validation_datasets/One/"),
        "2",
        "DATASET2START");
    ss.run(
        new File("simple-datasets/src/main/java/cog_complexity_validation_datasets/Three/"),
        "3",
        "SNIPPET_STARTS");
    ss.run(new File("dataset6/src/main/java/"), "6", "SNIPPET_STARTS");
    ss.run(new File("dataset9/src/main/java/"), "9$gc", "SNIPPET_STARTS_1");
    ss.run(new File("dataset9/src/main/java/"), "9$bc", "SNIPPET_STARTS_2");
    ss.run(new File("dataset9/src/main/java/"), "9$nc", "SNIPPET_STARTS_3");
    ss.run(new File("simple-datasets/src/main/java/fMRI_Study_Classes/"), "f", "SNIPPET_STARTS");

    // extract methods from snippets and save them in a separate directory
    ss.exploreDir("ml_model/src/main/resources/snippet_splitter_out/", "ml_model/src/main/resources/snippet_method_splitter_out/");
    // ss.exploreDir("ml_model/src/main/resources/raw_snippet_splitter_out/", "ml_model/src/main/resources/raw_snippet_method_splitter_out/");
  }
}
