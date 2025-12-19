import os

current_path = os.path.abspath(os.path.join(os.path.dirname(__file__), '../TASK1_AbsoluteComprehensibility'))
ROOT_PATH = current_path

RANDOM_SEED=42

TASK="TASK1"
DS="DS6"
USE_SNIPPET_WISE=False

## data paths ##
DATA_PATH = "data/final_features_ds6_with_warnings.csv"

## output paths ##
OUTPUT_PATH="featureselection/experiments_DS6_dev_wise.jsonl"
OUTPUT_WITH_DEV_FEATURES="featureselection/experiments_DS6_dev_wise_with_dev_features.jsonl"

OUTPUT_ML_PATH="results/DS6_TASK1_DEV_WISE.csv"
FILTERED_EXPERIMENTS=OUTPUT_PATH


KENDALS_OUTPUT_PATH="featureselection/kendals_features_DS6_dev_wise.csv"

## Headers for the output files
KENDALS_HEADER={'feature':'', 'target': '', 'tau': 0, '|tau|':0 ,'p-value':0, 'dataset':'', 'drop_duplicates': ''}

NOT_USEFUL_FEATURES=["dataset_id","snippet_id","participant_id","developer_position","PE gen",
                     "PE spec (java)","method_name","file"]

DEVELOPER_FEATURES=["developer_position", "PE gen", "PE spec (java)"]

TARGETS=["AU", "ABU", "ABU50", "BD", "BD50", "PBU"]

def dataset_geneator(drop_duplicates, dataframe):
    if drop_duplicates:
        return dataframe.drop_duplicates()
    return dataframe