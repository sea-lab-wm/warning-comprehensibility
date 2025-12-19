## Replication Package for the paper: "Verifier Warnings Do Not Improve Comprehensibility Prediction" ##

This package contains the data, code, and results for the paper "Verifier Warnings Do Not Improve Comprehensibility Prediction".

<div style="border: 2px solid black; padding: 10px; width: fit-content;">

* For the ease of explanation, from here on, we will refer to Code Comprehensibility prediction related experiments as `TASK1`.


* Also we will refer to __Dataset #1 as `DS6`__ and __Dataset #2 as `DS3`__. This is because, in the paper, we used the notation of Dataset #1 and #2, but for implementation in this replication package, we based it on the nomenclature from Muñoz et al. (https://dl.acm.org/doi/abs/10.1145/3382494.3410636)

</div>

## Directory Index ##

### Data
1. Java Code Snippets - `feature_extraction/src/main/resources/corrected_raw_snippets`
2. Extracted code features from Feature Extraction - `feature_extraction/output`
2. Code comprehension metric data from prior studies DS3(a simplified version of data) and DS6 - `feature_extraction/src/main/resources/study_data`.
3. Data for Code Comprehensibility prediction - `ml-experiments/TASK1_AbsoluteComprehensibility/data`

### Code
1. Feature Extraction - `feature_extraction/src`. Main starting point for feature extraction is `feature_extraction/main.sh` script.
2. Machine Learning Experiments - `ml-experiments`. Main starting point  for model execution is `ml-experiments/main.py` script.


### Results
Results for code comprehensibility prediction - `ml-experiments/TASK1_AbsoluteComprehensibility/results`




## Package Structure:

### ```feature_extraction``` 
This directory contains the data and code for extracting features from the code snippets. Refer the [README.md](feature_extraction/README.md) in the directory for more details.

### Directory Outline:
* `src` - This is the source folder contains all the code related to extract features from java snippets. It has two sub directories; main and test.
    * `main` - In side we have below files and directories.
        * `Parser.java` - This is the main file that runs JavaParser and use regex to extract the features from the code snippets.
        * `DirExplorer.java` - Navigate through the directories and files.
        * `Features.java` - Keep the extracted features as a map.
        * `FeatureVisitor.java` - AST visitors to extract features.
        * `SyntacticFeatureExtractor.java` - Using Regex to extract the features.
        * `DFT_Features.java` - Computes DFT (Discreate Fourier Transform) related features.
        * `DocumentRelatedFeatures.java` - Computes Document related features such as Comment Readaibility, CIC (Comment Identifier Consistency).
        * `HalsteadMetrics.java` - Computes Halstead features such as Entropy, Volume.
        * `VisualFeatureVisitor.java` - Computes Visual features such as Comments (Visual X), Indentation (Visual Y).
        * `VisualFeatures.java` - Contains the extracted visual features as a map.
        * `SnippetSplitter.java` - Splits the code snippets from the original datasets into standalone snippets.
        * `StringLiteralReplacer.java` - Replaces the string literals with a placeholder. This is used to remove the string literals from the code snippets when computing the features.
        * `create_itid_data.py` - computes ITID features.
        * `create_nm_data.py` - computes NM features.
        * `nmi_features.py` - computes NMI features.
        * `resources` - This folder contains resources such as java snippets, corpus from WordNet, original datasets.
        * `feature_merger.py` - Runs above scripts to extract NM, NMI, ITID features and merge the features with JavaParser extracted features into a single CSV file.
        * `feature_extraction_main.py` - This is the main file that runs all above python scripts to extract features from the code snippets.

    * `test` - Contains the test cases for the above java methods.
    * `tools` - Contains the tools and scripts to run tools such as SCC, PMD that we used to extract the features like LOC, Cyclomatic Complexity. 

* `main.sh` - This is the main script that runs the tools and other feature extraction code. This script runs the `feature_extraction_main.py` file and tools.
           

### ```ml-experiments``` 
This directory contains the data, code and results for the machine learning experiments. Refer the [README.md](ml-experiments/README.md) in the directory for more details.


### Directory Outline:

* `helper` - Contains all the helper functions that are used in the scripts.
    * `configs_TASK1_DS3_DEV_WISE.py` - Same as previous, this is for TASK1 Dataset #2 (DS3) but for developer-wise experiments.
    * `configs_TASK1_DS6_DEV_WISE.py` - Same as previous, this is for TASK1 Dataset #1 (DS6) but for developer-wise experiments.

*  `TASK1_AbsoluteComprehensibility` - Contains all the code, data and results of the machine learning models that we used to predict Absolute Comprehensibility of Java snippets.
    * `data` - This has four CSV files, final_features_ds3.csv, final_features_ds3_with_warnings.csv and final_features_ds6.csv, final_features_ds6_with_warnings.csv which are the final feature sets for Dataset #2 and Dataset #1 respectively. The files with the postfix `with_warnings` contains the features with verifier tool warnings.

    * `ds3` - Contains `ds3.py`. This class includes all required methods, unique data for DS3 TASK1; Such as output file header, evaluatin method, best baseline models, results aggregation methods for dataset #2 for the TASK1_AbsoluteComprehensibility with developer-wise data. 

    * `ds6` - Contains `ds6.py`. This class includes all required methods, unique data for DS6 TASK1; Such as output file header, evaluatin method, best baseline models, results aggregation methods for dataset #1 for the TASK1_AbsoluteComprehensibility.

    * `featureselection` - Contains `feature_selection.py`. This class includes all required methods for selecting top 10\% to 100\% code features using Kendalls' TAU correlation test.
    Also this directory contins `experiments_DS3_dev_wise_with_dev_features.jsonl`, '`experiments_DS6_dev_wise_with_dev_features.jsonl`' files. These files contain the experiments with configurations that we used to run modeles for DS3 and DS6 for develope-wise data. Further `kendals_features_DS3_dev_wise.csv` and `kendals_features_DS6_dev_wise.csv` files contain the Kendalls' test results for DS3 and DS6 for developer-wise data.

    * `results` - Contains all the results of the models for DS3 and DS6. This includes 2 sub-directories. 
        * `dev-wise-with-dev-features` - Contains the box plots of the ml models for developer-wise data for both the datasets.

    
    * Delta RI statistical test results can be found in the CSV files with the prefix `statistics_`.

    * task wise statistical test results can be found in the CSV files with the prefix `task_baseline_statistics_`.
