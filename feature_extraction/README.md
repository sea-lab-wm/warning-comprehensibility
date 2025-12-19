# Feature Extraction

This directory contains all the code that extracts code features from a set of Java code snippets. 
These features are used by ML models to perform code comprehensibility prediction experiments.


## Requirements and Execution
### Pre-requisites: 
* Java 8+ from OpenJDK (recommended) or another vendor
* Gradle 8.10.2
* Python 3.10
* Conda 23.1.0

### Environment setup
1. All the commands mentioned below should be executed in the root directory of the project. i.e. `feature_extraction/`

2. Execute below command to create a conda virtual environment using the given requirements.yml 
```conda env create --file requirements.yml```

## Execution (Optional)
> ⚠️ : **This step is required if you need to generate the dataset from the raw data. Unless please skip this step**

Execute ```sh main.sh``` to run the feature extraction processes and generates ```output/final_features_ds3.csv``` , ```output/final_features_ds6.csv``` files. Those output files are the input data for ML models. This script will execute the following steps sequentially:
    
  * 1st Step -  Runs ```./tools/execution-scripts/run_scc.sh```. This runs the SCC tool on the code snippets to extract Lines of Code, Blank lines etc.
  
  * 2nd Step - Runs ```./tools/execution-scripts/run_pmd.sh```. This runs the PMD tool to extract Cyclomatic Code complexity feature from the code snippets.
    
  * 3rd Step - Runs ```feature_extraction.Parser.java```. This class extracts the features from the code snippets using JavaParser and Regex.

  * 4th Step - Runs ```src/main/feature_extraction_main.py```. This script computes features that require WordNet python library. Also it merges the extracted features from the previous steps into final feature CSVs.



## How Feature Extraction works?
Code features are extracted in two ways:

1. ```FeatureVisitor.java``` computes features by parsing the code and traversing its Abstract Syntax Tree (AST) using the JavaParser: https://javaparser.org/. (Example feature: #IfStmts)
2. The ```SyntacticFeatureExtractor.java``` computes features that are easy to compute with regular expressions (regexes) or heuristics, rather than using the JavaParser. (Example feature: #parentheses) -- See this tutorial about Java regular expressions: https://www.vogella.com/tutorials/JavaRegularExpressions/article.html

Both classes are used in the class `Parser` to collect the features in `final_features_ds3.csv` and `final_features_ds6.csv` files.

These are the papers we used to define the features:
1. [Automatically Assessing Code Understandability](https://ieeexplore.ieee.org/document/8651396)
1. [A Simpler Model of Software Readability](https://dl.acm.org/doi/pdf/10.1145/1985441.1985454)
2. [Improving Code Readability Models with Textual Features](https://www.cs.wm.edu/~denys/pubs/ICPC'16-Readability.pdf)


Implemented Code Features:
Ledgend: ✅ = Implemented, * = Implemented Complementary, ❌ = Not Implemented

|  | Code Feature  | Completed Flavours | Definition | Differences with Referenced Paper (if any) |
| -- | ------------- | -- | ------------- | -- |
|1|Cyclomatic comp | <ul><li>✅ Non aggregated | This is the [McCabe’s Definition](https://docs.pmd-code.org/apidocs/pmd-java/7.6.0-SNAPSHOT/net/sourceforge/pmd/lang/java/metrics/JavaMetrics.html#CYCLO). This computes the program complexity using the Control Flow Graph of the program. Implemented using [PMD tool](https://github.com/pmd/pmd). | Same definition as ours |
|2|#nested blocks | <ul><li>✅ Avg | A block is a sequence of statements, local class declarations, and local variable declaration statements within braces as per [Java8 Spec definiton](https://docs.oracle.com/javase/specs/jls/se8/html/jls-14.html#jls-14.2). These are the steps on how we count nested blocks. If we find a BlockStmt, we check whether it has a parent node and whether the type of the parent node is one of these types (BlockStmt, ForStmt, ForEachStmt, WhileStmt, DoStmt, IfStmt, SwitchStmt, TryStmt, SynchronizedStmt, LocalClassDeclarationStmt and LabledStmt. We selected those statement types because those are the ones that can initiate a block. Further it supports counting nested blocks without curly braces. Note: This LabledStmt needs to be  ForStmt, ForEachStmt, WhileStmt, DoStmt, IfStmt, SwitchStmt, TryStmt, SynchronizedStmt.) Implemented using the AST i.e Java Parser.  | Code blocks nesting in the snippet. Not explicitly mention which blocks counted. |
|3|#parameters | <ul><li>✅ Non aggregated | Count the parameters in the method signature. AST | Same definition as ours |
|4|#statements | <ul><li>✅ Non aggregated | We cover all the statement types mentioned in the [Java8 Spec](https://docs.oracle.com/javase/specs/jls/se7/html/jls-15.html#jls-15.26). We count 19 types of statements.  BlockStmt, IfStmt, SwitchStmt, ForStmt, WhileStmt, ForEachStmt, AssertStmt, BreakStmt, ContinueStmt, DoStmt, EmptyStmt, ExplicitConstructorInvocationStmt, ExpressionStmt, LabeledStmt, LocalClassDeclarationStmt, ReturnStmt, SynchronizedStmt, ThrowStmt, TryStmt. AST | It is not clear how they counted which types of statements. |
|5|#assignments | <ul><li>✅ *Non aggregated </li> <li>✅ Avg </li> <li>✅ DFT </li></ul> | We count all the assignment operators mentioned in the Java Spec8. We count ASSIGN("=") - this includes variable declaration, PLUS("+="), MINUS("-="), MULTIPLY("*="), DIVIDE("/="), BINARY_AND("&="), BINARY_OR("$\vert$="), XOR("^="), REMAINDER("%="), LEFT_SHIFT("<<="), SIGNED_RIGHT_SHIFT(">>="), UNSIGNED_RIGHT_SHIFT(">>>="). Used AST | Count just "=" | 
|6|#blank lines | <ul><li>✅ Avg | We use [SCC tool](https://github.com/boyter/scc) to count blank lines. Then it is divided by the LOC = code lines | Used Code + Comment as LOC | 
|7|#characters | <ul><li>✅ Max  | Count the occurrences of each character. Take the maximum appearing character and its occurrence as #characters (max) Regex | Same definition as ours |
|8|#commas | <ul><li>✅ *Non aggregated </li> <li>✅ Avg </li> <li>✅ DFT </li></ul> | Count "," in the snippet. We ignore strings and comments. Regex | Count "," in the code and strings.|
|9|#comments | <ul><li>✅ Avg </li> <li>✅ DFT </li> <li>✅ Visual X </li> <li>✅ Visual Y </li> </ul> | Count single line (//), multiline (/* \*/), and JavaDoc comments(/** */). Even though multiline and JavDoc comments can be on multiple lines, we consider them as a single one. AST | Count single-line comments and multiline comments. If a multiline comment goes to multiple lines, consider each line as an individual comment. |
|10|#comparisons | <ul><li>✅ Avg </li> <li>✅ DFT </li></ul> | Count [relational comparison operators](https://docs.oracle.com/javase/specs/jls/se7/html/jls-15.html#jls-15.20.1)  and [Equality operators](https://docs.oracle.com/javase/specs/jls/se7/html/jls-15.html#jls-15.21)  ==, !=, <, <=, >, >=, instanceof for two operands. AST |Counts ==, !=, <, <=, >, >=|
|11|#conditionals | <ul><li>✅  Avg </li> <li>✅  DFT </li></ul> | Count "if" and "switch" from code (non comment + non blank lines). AST | Count "if" from the code (non- comment+non blank lines).  |
|12|#identifiers | <ul>  <li> ✅ *Non aggregated </li>  <li> ✅ *Min </li> <li> ✅ Avg </li> <li> ✅ Max </li> <li> ✅ DFT </li>  <li> ✅ Visual X </li> <li> ✅ Visual Y </li>  </ul> |Use SimpleName node in JavaParser, that counts the java identifiers, eg: variable names, method names | Same definition as ours |
|13|#keywords | <ul> <li>✅ Avg </li> <li>✅ Max </li> <li>✅ DFT </li> <li>✅ Visual X </li> <li>✅ Visual Y </li>  </ul> |We searched (based on regex not using the AST) for 50 keywords that are mentioned in the [orcale docs](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/_keywords.html) in the code snippet. (code = ignore strings+comments) | Not explicitly mentioned which keywords considered. |
|14|#literals | <ul> <li> ✅ *Non aggregated </li>  <li>  ✅ Visual X </li> <li> ✅ Visual Y </li>  </ul> | counts the java literals. literal types => boolean, character, integer, long, double, null, string, textblock(three double quote based strings). AST | No explicit definition. |
|15|#loops | <ul><li> ✅ Avg </li> <li> ✅ DFT </li></ul>  | Using the AST representation, we count "for", "while", "foreach"  and "dowhile" statements. | Count "do", "while" "for" keywords |
|16|#numbers  | <ul><li> ✅ Avg</li> <li>✅  Max</li> <li>✅  DFT </li> <li>  ✅ Visual X </li> <li> ✅ Visual Y </li> </ul> | counts integers, floats and doubles. AST | Use regex "\d+\.?\d* |
|17|#operators | <ul><li> ✅ Avg</li> <li>✅  DFT </li> <li>  ✅ Visual X </li> <li> ✅ Visual Y </li> </ul>  | counts operators. operator types => comparision operators (==, !=, <, <=,> ,>=, instanceof), arithmetic operators (+, -, *, /, %) , boolean operators (&&, $\vert\vert$, ^, &, $\vert$), unary operators (++, --  both postfix and prefix, !), shift (>>,<<, >>>), ternary operators (?:) | Count +, -  ,*, / , % |
|18|#parenthesis | <ul><li> ✅ Avg</li> <li>✅  DFT </li> </ul> | Consider only "(" as parenthesis. We treat each open/closed parenthesis as a separate one. Means not counting the pairs. Regex | considered both "(", "{" as parenthesis. Also, they treat each open/closed as one pair (i.e count only open ones only) |
|19|#periods | <ul><li> ✅ *Non aggregated </li><li> ✅ Avg</li> <li>✅  DFT </li> </ul> | counts "." using Regex | Same definition as ours |
|20|#spaces | <ul><li> ✅ *Non aggregated </li> <li> ✅ Avg</li> <li>✅  DFT </li> </ul> | counts spaces and tabs(=4 spaces) | Same definition as ours |
|21|#strings | <ul><li>  ✅ Visual X </li> <li> ✅ Visual Y </li> </ul> | counts the number of strings. i.e. terms inside double quotes. Regex| Same definition as ours |
|22|#words | <ul><li> ❌ Max</li> </ul> | “word” is is unclear. Is it a identifier, or a token or something else | No explicit definition |
|23|Indentation length | <ul> <li> ✅ Avg</li>  <li> ✅ Max</li>  <li> ✅ DFT </li>  </ul>  | counting spaces and tabs until it reaches a non-space, non-tab character. Note: only considers the spaces/tabs before the line starts. We consider tab as a single-character. Regex| Same definition as ours |
|24|Identifiers length | <ul> <li> ✅ Avg</li>  <li> ✅ Max</li></ul> | counts the character length of identifers. AST | Same definition as ours |
|26|Line length | <ul> <li> ✅ Avg</li>  <li> ✅ Max</li>  <li> ✅ DFT </li>  </ul>  | The line length counts all the characters of the given line (including the spaces/tabs/indentation in the beginning). Line = Split the code snippet using the new line character. Regex | Same definition as ours | 
|27|#aligned blocks | <ul><li>❌ Non aggregated  | This is Ambiguous! Not specific instructions mentioned in the papers how to implement | NOT IMPLEMENTED |
|28|Extent of aligned blocks | <ul><li>❌ Non aggregated  | This is Ambiguous! Not specific instructions mentioned in the papers how to implement | NOT IMPLEMENTED |
|29|Entropy | <ul><li>✅ Non aggregated </li></ul> | Term = Word separated by a space. We avoid Strings and comments. $p(term)= \frac{count(term)}{\sum_{j = 1}^{n} count(term)}$ where $count(term)$ is the number of occurrences of a term in the snippet and $n$ is the number of unique terms in the snippet. $H(snippet) = -\sum_{j=1}^{n} ( p(term) * log2(p(term)) )$  where $H(snippet)$ is the token entropy of the snippet. Regex  | Same definition as ours |
|30|LOC | <ul><li>✅ Non aggregated </li></ul>| Effective Code lines (remove comment + blank lines) SCC tool  | LOC taken from "Posnett" library (private int getLines(String pSource) {return pSource.split("\n").length;})". According to that definition, they consider both lines of code + comments as mentioned [referenced paper](https://dl.acm.org/doi/pdf/10.1145/1985441.1985454) paper. | 
|31|Volume | <ul><li>✅ Non aggregated </li></ul>  | $Volume=programLength()*Log2(programVocabulary())$; where $programLength$ = \#operators + \#operands , $programVocabulary$ = \#unique operators + \#unique operands.  | Same definition as ours |
|32|NMI (Narrow Meaning identifier) | <ul><li>✅  Avg </li> <li>✅  Max </li> </ul>  | $NMI(l) = \sum_{t∈l}specificity(t)$ where t is a term extracted from the line $l$ of the snippet and $specificity(t)$ represents the specificity of term $t$, which is computed as the number of hops from the node containing $t$ to the root node in the hypernym tree of $t$. Implemented using [WordNet](https://wordnet.princeton.edu/) and Regex. | Same definition as ours |
|33|NM | <ul><li> ✅  Avg </li> <li> ✅ Max </li> </ul> | Tokenized each line. It then iterates through each token to see the number of meanings a token has using WordNet. The total number of meanings is then added up for the line. [Reference paper](https://www.cs.wm.edu/~denys/pubs/ICPC'16-Readability.pdf). Implemented using WordNet and Regex. | Same definition as ours |
|34|ITID |<ul><li>✅ Min </li> <li>✅  Avg </li></ul> | Definition: $ITID(l) = \frac{Terms(l) \cap  Dictionary}{Terms(l)}$. We tokenized each line and counts the total number of real words by checking if the token has any meanings from WordNet. It then divides the total number of real words by the total number of identifier terms in the line to get the ITID for the given line. [Reference paper](https://www.cs.wm.edu/~denys/pubs/ICPC'16-Readability.pdf) and implemented using Regex | Same definition as ours |
|35|TC | <ul><li>✅ Min </li> <li>✅ Avg </li> <li>✅ Max </li> </ul>|  Text Coherence measures the term overlap between blocks. [Example](https://docs.google.com/document/d/1pE0-jPVlIek5ROMkvWsU1KPwkhB7ZVHH/edit?usp=sharing&ouid=111805447533160241433&rtpof=true&sd=true) and [Reference paper](https://www.cs.wm.edu/~denys/pubs/ICPC'16-Readability.pdf) Implemented using Regex. | Same definition as ours |
|36|Readability | <ul><li>❌ Non aggregated  | This is Ambiguous! Not specific instructions mentioned in the papers how to implement | NOT IMPLEMENTED |
|37|IMSQ | <ul><li>❌ Min</li><li>❌ Avg</li> <li>❌ Max</li></ul>  | This is Ambiguous! Not specific instructions mentioned in the papers how to implement | NOT IMPLEMENTED |
|38| CR | <ul><li>✅ Non aggregated </li></ul> | Comment Readability. The FleschKincaid (FK) index of a snippet S is empirically defined as: $FK(S) = 206.835−1.015\frac{words(S)}{phrases(S)} − 84.600\frac{syllables(S)}{words(S)}$; where a $word$ is a series of alphabetical characters separated by a space or a punctuation symbol; a $syllable$ is “a word or part of a word pronounced with a single, uninterrupted sounding of the voice consisting of a single sound of great sonority (usually a vowel) and generally one or more sounds of lesser sonority (usually consonants)” and a $phrase$ is a series of words that ends with a new-line symbol, or a strong punctuation point (e.g., a full-stop). [Reference paper](https://www.cs.wm.edu/~denys/pubs/ICPC'16-Readability.pdf) | Same definition as ours |
|39| CIC | <ul><li>✅ Avg </li> <li>✅ Max </li> </ul> | Comments and Identifiers Consistency measures the overlap between the terms used in a method comment and the terms used in the method body [Reference paper](https://www.cs.wm.edu/~denys/pubs/ICPC'16-Readability.pdf) $CIC(method)=\frac{\vert Comments(method)\cap Identifiers(method)\vert}{\vert Comments(method) \cup\ Identifiers(method)\vert}$; where $Comments(method)$ set include all the words in comments and $Identifiers(method)$ set include all the identifiers in the method. | Same definition as ours |
|39| CICsyn | <ul><li>✅ Avg </li> <li>✅ Max </li> </ul> | Comments and Identifiers Consistency - Synonyms. This is same as CIC. $CIC(method)sys= \frac{\vert Comments(method) ∩ (Identifiers(method) ∪ Syn(m)) \vert}{\vert Comments(method) ∪ Identifiers(method) ∪ Syn(method)\vert}$; where $Syn(method)$ is the set of all the synonyms of the terms in $Identifiers(method)$. | Same definition as ours |
|40| MIDQ | <ul><li>✅ Min </li><li>✅ Avg </li> <li>✅ Max </li> </ul> | Methods Internal Document. $MIDQ(method)=\frac{1}{2}\frac{\# documented items(method)}{\# documentable_items(method)} + The FleschKincaid (FK) index(comments))$; where $\# documented items(method)$ = Number of comments that include "\@return" or "\@throws" or "\@param"; $\#   documentable_items(method)$ = Number of ReturnStatments + Num of Parameters + Num of ThrowsStatments | Same definition as ours |
|41| AEDQ | <ul><li>❌ Min</li><li>❌ Avg</li> <li>❌ Max</li></ul>  | This is Ambiguous! Not specific instructions mentioned in the papers how to implement | NOT IMPLEMENTED |

## Avg, Min, Max features
Compute relevant features for each line of code in the code snippet. Then take the average, minimum, and maximum of these features across all lines in the code snippet. eg: #identifers (max)- count #identifiers in each line and take the maximum of these values across all of them. Here the lines are the lines of effectiver code lines (i.e. remove comment + blank lines). But majority of the features are computed based on all the lines in the snippet in the [original paper](https://ieeexplore.ieee.org/document/8651396).

## DFT based features
These features are based on [reference paper](https://ieeexplore.ieee.org/document/8651396)

 * First we calculate the DFT of the signal. Signal can be a 1D array of doubles.(e.g. #numbers per line) 
 * Then we calculate the standard deviation of the amplitudes of the DFT of the signal.
 * Then we find the maximum frequency of the signal which is more than the standard deviation.
 * Finally we calculate the bandwidth of the signal which is 2 * max_frequency + 1

Assumptions: sampling_rate = 10 and #bins = amplitudes.length. we need this information to calculate the frequency of the signal.

## Visual based features

These features are based on [reference paper](https://ieeexplore.ieee.org/document/8651396).
For these metrics, a "virtual" color is assigned to
each character in the code, based on the type of the token it
belongs to (e.g., characters of identifiers have color 1, while
characters of keywords have color 2), thus creating a matrix
of colors for the snippet. Then, the variation of colors is
computed both on the X and on the Y axis of such a matrix


## Resources

* Abstract Syntax Tree: https://en.wikipedia.org/wiki/Abstract_syntax_tree
* JavaParser book: https://drive.google.com/file/d/10qDU-QFnyLjnErYDVSGl3L3lgiCohem8/view?usp=sharing
* JavaParser API: https://www.javadoc.io/doc/com.github.javaparser/javaparser-core/latest/index.html
* Regular expressions in Java: https://www.vogella.com/tutorials/JavaRegularExpressions/article.html
* Useful VS code plugins: https://code.visualstudio.com/docs/sourcecontrol/github 
