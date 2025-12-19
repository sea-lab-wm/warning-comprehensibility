package featureextraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.json.JSONObject;

import edu.mit.jwi.IRAMDictionary;
import edu.mit.jwi.RAMDictionary;
import edu.mit.jwi.data.ILoadPolicy;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;


/*
 * Document Related Features 
 * These implmentations are based on https://www.cs.wm.edu/~denys/pubs/ICPC'16-Readability.pdf
 */
public class DocumentRelatedFeatures {

    private Features features;
    private String userDir = System.getProperty("userDir");

    public DocumentRelatedFeatures(Features features) {
        this.features= features;
    }


    // Comment Readability
    public double getFleschKincaidIndex(){
        List<String> comments = this.features.getComments();
        int numSentences = 0;
        int numWords = 0;
        int numSyllables = 0;

        double fleschKincaidIndex = 0;
        String complete_comment = "";

        if (comments.size() == 0){
            return fleschKincaidIndex;
        }

        for(String comment : comments){
            if (comment.contains("// removed to allow compilation")){
                continue;
            }
            comment = comment.trim();
            if (comment.contains(".")){
                complete_comment += comment;
            } else {
                complete_comment += comment.trim() + ".";
            }
        }

        numSentences = complete_comment.split("\\.").length; //series of words ends with a period
        numWords = complete_comment.split("\\s+").length; //series of characters separated by spaces
        numSyllables = DocumentRelatedFeatures.countConsecutiveVowelGroups(complete_comment);
        
        fleschKincaidIndex = 206.835 - 1.015 * (numWords / numSentences) - 84.6 * (numSyllables / numWords);
        return fleschKincaidIndex;
    }

    /*
     * Instead of counting individual vowels in the context of comments, we count the 
     * number of groups of consecutive vowels. 
     * E.g.: definition --> #groups of consecutive vowels = len(e,i,i,io) = 4
     */
    private static int countConsecutiveVowelGroups(String comment) {
        int count = 0;
        boolean prevVowel = false;
        for (int i = 0; i < comment.length(); ++i) {
            boolean isVowel = isVowel(comment.charAt(i));
            if (isVowel && !prevVowel) {
                count++;
            }
            prevVowel = isVowel;
        }
        return count;
    }

    private static boolean isVowel(char c) {
        return "aeiou".indexOf(c) != -1;
    }

    // Comments and Identifiers Consistency (CIC)
    public List<Double> getCommentsAndIdentifiersConsistency(){
        
        List<String> comments = this.features.getComments();
        HashMap<String, List<String>> indetifierMap = this.features.getLineNumber_Identifier_Map();
        
        // CIC per line = | comments(method) intersection identifiers(method) | / | comments(method) union identifiers(method) |
        List <Double>lineCICList = new ArrayList<>();
        
        for (String lineNumber : indetifierMap.keySet()) {
            List<String> identifierList = indetifierMap.get(lineNumber);
            Set<String> words_in_Comments = new HashSet<>();
            double lineCIC = 0;
            double comment_identifier_intersection = 0;

            for (String identifier : identifierList) {
                for (String comment : comments) {
                    if (comment.contains("// removed to allow compilation")){
                        continue;
                    }
                    // counts the intersection of comments and identifiers
                    if (comment.contains(identifier)) {
                        comment_identifier_intersection++; // |comments(method) intersection identifiers(method)|
                    }
                    // splits the comments into words
                    String[] words = comment.split("\\s+");
                    // adds the words to the list
                    for (String word : words) {
                        words_in_Comments.add(word);
                    }
                }
            }
            // union of words_in_Comments and identifierList
            words_in_Comments.addAll(identifierList);

            // |comments(method) union identifiers(method)| is the size of the words_in_Comments
            lineCIC = comment_identifier_intersection / words_in_Comments.size();
            lineCICList.add(lineCIC);
          }

        return lineCICList;
    }

    // Comments and Identifiers Consistency (CIC)syn
    public List<Double> getCommentsAndIdentifiersConsistencySyn(){
        
        List<String> comments = this.features.getComments(); 
        HashMap<String, List<String>> indetifierMap = this.features.getLineNumber_Identifier_Map();

        // CIC per line = | comments(method) intersection (identifiers(method) union syn(method) ) | / | comments(method) union identifiers(method) union syn(method) |        
        List <Double>lineCICList = new ArrayList<>();

        for (String lineNumber : indetifierMap.keySet()) {
            
            Set<String> identifierList = new HashSet<>(indetifierMap.get(lineNumber));
            Set<String> synList = new HashSet<>(); // keeps the synonyms of the identifiers
            
            Set<String> words_in_Comments = new HashSet<>();
            
            double lineCIC = 0;
            double comment_identifier_and_idsyn_intersection = 0;
            
            IRAMDictionary dict = null;
            try {
                dict = new RAMDictionary(new File(userDir + "/src/main/resources/corpora/dict"),ILoadPolicy.NO_LOAD);
                dict.open();
                
                for (String identifier : identifierList) {
                    for (POS p : POS.values()) {
                        IIndexWord idxWord = dict.getIndexWord(identifier, p);
                        if (idxWord != null) {
                            IWordID wordID = idxWord.getWordIDs().get(0);
                            IWord word = dict.getWord(wordID);
                            ISynset synset = word.getSynset();
                            for (IWord w : synset.getWords()) {
                                synList.add(w.getLemma());
                            }
                        }
                    }
                }

                // union of identifierList and synList
                identifierList.addAll(synList);

                for (String identifier: identifierList){
                    for (String comment : comments) {
                        if (comment.contains("// removed to allow compilation")){
                            continue;
                        }
                        // counts the intersection of comments and identifiers
                        if (comment.contains(identifier)) {
                            comment_identifier_and_idsyn_intersection++; // |comments(method) intersection identifiers(method)|
                        }
                        // splits the comments into words
                        String[] words = comment.split("\\s+");
                        // adds the words to the list
                        for (String word : words) {
                            words_in_Comments.add(word);
                        }
                    }
                }

                // union of words_in_Comments and identifierList
                words_in_Comments.addAll(identifierList);

                // |comments(method) union identifiers(method) union IdSysList(method)| is the size of the words_in_Comments
                lineCIC = comment_identifier_and_idsyn_intersection / words_in_Comments.size();
                lineCICList.add(lineCIC);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                dict.close();
            }
          }
        return lineCICList;
    }

    /**
     * This method calculates the Documented Items Ratio (DIR) for a method
     * @return DIR = #documented items(method) / #documentable items(method)
     */
    private List<Double> getDIR () {

        List<Double> lineDIR = new ArrayList<>();
        List <String> comments = this.features.getComments(); 
        HashMap<String, List<String>> lineNumber_param_return_throws_Map = this.features.getLineNumber_param_return_throws_Map();
        
        // Number of documented items
        int numOfDocumentedItems = 0;
        for (String comment : comments) {
            if (comment.contains("// removed to allow compilation")){
                continue;
            }
            if (comment.contains("@return") || comment.contains("@throws") || comment.contains("@param")) {
                numOfDocumentedItems++;
            }
        }

        for (String lineNumber: lineNumber_param_return_throws_Map.keySet()){
            int numOfReturnStatments = 0;
            int numnOfParameters = 0;
            int numOfThrowsStatments = 0;
            for (String param_return_throws: lineNumber_param_return_throws_Map.get(lineNumber)){
                if (param_return_throws.contains("return")){
                    numOfReturnStatments++;
                } else if (param_return_throws.contains("param")){
                    numnOfParameters++;
                } else if (param_return_throws.contains("throws")){
                    numOfThrowsStatments++;
                }
            }
            int numOfDocumentableItems = numOfReturnStatments + numnOfParameters + numOfThrowsStatments;
            
            if (numOfDocumentableItems != 0){
                // Documented Items Ratio (DIR) = #documented items(method) / #documentable items(method)
                lineDIR.add((double) (numOfDocumentedItems / numOfDocumentableItems));
            }
        }

        return lineDIR;
    }

    /**
     * This method calculates the Methods Internal Documentation Quality (MIDQ) for a method
     * @return MIDQ = 0.5 * (method_doc_items_ratio + comment_readability)
     */
    public List<Double> getMIDQ(){
        List<Double> MIDQList = new ArrayList<>();
        double comment_readability = getFleschKincaidIndex();
        for (double lineDIR: getDIR()){
            double method_doc_items_ratio = lineDIR;
            // MIDQ = 0.5 * (method_doc_items_ratio + comment_readability)
            double lineMIDQ = 0.5 * (method_doc_items_ratio + comment_readability);
            MIDQList.add(lineMIDQ);
        }
        return MIDQList;
    }

    /**
     * This method computes the AEDQ (API External Documentation Quality) for the APIs used in the method
     * Since this outputs a sum of votes for each API, the output is a list of integers
     */
    public List<Integer> getAEDQ(){

        List<Integer> AEDQList = new ArrayList<>();

        // This is the list of APIs used in the method
        Set<String> classNameList = this.features.getClassNames();

        for (String className: classNameList){
            boolean hasSpecialChar = className.matches(".*[^a-zA-Z0-9 ].*");
            
            try {

                if (hasSpecialChar) {
                    className = URLEncoder.encode(className, StandardCharsets.UTF_8.toString());
                }
                String urlStr = "https://api.stackexchange.com/2.3/search/advanced?order=desc&sort=activity&accepted=True&title=how%20to%20" + className + "&tagged=java&site=stackoverflow";
            
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                // Since StackExchange API provides GZIP compressed data, we need to decompress it
                // https://api.stackexchange.com/docs/compression 
                BufferedReader in = new BufferedReader(new InputStreamReader(new GZIPInputStream(conn.getInputStream())));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }

                // Close connections
                in.close();
                conn.disconnect();

                int score = 0;
                // Convert the content string to a JSON object
                JSONObject json = new JSONObject(content.toString());
                for (int i = 0; i < json.getJSONArray("items").length(); i++){
                    JSONObject item = json.getJSONArray("items").getJSONObject(i);
                    score += item.getInt("score");
                }
                AEDQList.add(score);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return AEDQList;
    }
}
