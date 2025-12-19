import os

current_path = os.path.abspath(os.path.join(os.path.dirname(__file__), '../TASK1_AbsoluteComprehensibility'))
ROOT_PATH = current_path

RANDOM_SEED=42

TASK="TASK1"
DS="DS3"
USE_SNIPPET_WISE=False

## output paths ##
OUTPUT_PATH="featureselection/experiments_DS3_dev_wise.jsonl"
OUTPUT_WITH_DEV_FEATURES="featureselection/experiments_DS3_dev_wise_with_dev_features.jsonl"

OUTPUT_ML_PATH="results/DS3_TASK1_DEV_WISE.csv"
KENDALS_OUTPUT_PATH="featureselection/kendals_features_DS3_dev_wise.csv"
FILTERED_EXPERIMENTS=OUTPUT_PATH

## Headers for the output files
KENDALS_HEADER={'feature':'', 'target': '', 'tau': 0, '|tau|':0 ,'p-value':0, 'dataset':'', 'drop_duplicates': ''}

## data paths ##
DATA_PATH="data/final_features_ds3_with_warnings.csv"

NOT_USEFUL_FEATURES=["dataset_id","snippet_id", "method_name","file", "college_year"]

DEVELOPER_FEATURES=["college_year"]

TARGETS=["readability_level"]

def dataset_geneator(drop_duplicates, dataframe):
    if drop_duplicates:
        return dataframe.drop_duplicates()
    return dataframe