"""
This script is for generating the experiments for ML experiments to be run with classification models.
The experiments are generated based on the kendall's tau correlation of the features with the target variable (absolute comprehensibility metrics)
This generating the featureselection/experiments_<DS3/DS6>.jsonl file to get the experiments.

Here is a sample experiment:
{"exp_id": "exp2", "drop_duplicates": true, "dataset": "ds_code", "target": <code comprehension target>, "K %": 10, "features": <feature list>, "feature_selection_method": "Kendalls", "use_oversampling": true}
"""

import os
import sys
# Add the root directory to the system path
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '../')))

###################	Import from helper 	###############################
### Change the configurations here appropriately ######################
### For DS6 Snippet-wise ---> from helper import configs_TASK1_DS6_SNIPPET_WISE as configs
### For DS3 Dev-wise ---> from helper import configs_TASK1_DS3_DEV_WISE as configs
### For DS6 Snippet wise ---> from helper import configs_TASK1_DS6_SNIPPET_WISE as configs
### For DS3 Dev-wise ---> from helper import configs_TASK1_DS3_DEV_WISE as configs
#######################################################################
from helper import configs_TASK1_DS3_DEV_WISE as configs

import json
import pandas as pd
import csv
import math
import sys
sys.path.append(configs.ROOT_PATH)

from featureselection.feature_selection import FeatureSelection


def experiment_generator(exp_id, drop_duplicates, dataset, target, K, features, fs_method, use_oversampling):
    experiment = {
        "exp_id": "exp" + str(exp_id),
        "drop_duplicates": drop_duplicates,
        "dataset": dataset,
        "target": target,
        "K %": K,
        "features": features,
        "feature_selection_method": fs_method,
        "use_oversampling": use_oversampling
    }
    return experiment

def experiments(experiment_id):
    drop_duplicates = True
    transformed_ds = configs.dataset_geneator(drop_duplicates, complete_df)
    datasets = {
        "ds_code": transformed_ds[ds_code],  
    }

    for target in targets:
        y = transformed_ds[target]
        for index, dataset_key in enumerate(datasets.keys()):
            dataset = datasets[dataset_key]
            experiment_id = kendalls_feature_extractor(experiment_id, drop_duplicates, target, y, dataset_key, dataset)

                            
    return experiment_id

def kendalls_feature_extractor(experiment_id, drop_duplicates, target, y, dataset_key, dataset):
    fs = FeatureSelection(dataset, y, drop_duplicates, dataset_key)
    kendals = fs.compute_kendals_tau()

    ## select top k=10%, 20%, 30%, 40%, 50%, 60%, 70%, 80%, 90% , 100% of the features
    for k in range(1, 11):
        k_best_features_kendal = fs.select_k_best(math.floor(int(kendals.shape[0] * k/10)), "kendalltau")

        with open(configs.ROOT_PATH + '/' + configs.OUTPUT_PATH, "a") as file:
            use_oversampling = True
            ###################
            ## FS = Kendalls ##
            ###################
            fs_method = "Kendalls"

            jsondump = json.dumps(experiment_generator(experiment_id, drop_duplicates, dataset_key, target, k*10, list(k_best_features_kendal), fs_method, use_oversampling))
            jsondump = jsondump.replace('"true"', 'true').replace('"false"', 'false')
            file.write(jsondump)
            file.write('\n')

            experiment_id += 1

            ## FIX: increment experiment_id by 10 after experiment_id == 10
            if experiment_id == 11:
                experiment_id += 10
    return experiment_id


if __name__ == "__main__":
    with open(configs.ROOT_PATH + "/" + configs.OUTPUT_PATH, "w+") as file:
        file.write("")
 
    ## data loading
    df = pd.read_csv(configs.ROOT_PATH + "/" + configs.DATA_PATH)

    ## these features are not needed for the feature selection because they are not numerical
    removed_features = configs.NOT_USEFUL_FEATURES
    complete_df = df.drop(columns=removed_features) 

    targets = configs.TARGETS

    ds_code = list(set(complete_df.columns) ^ set(targets)) ## only code features

    header_kendals = configs.KENDALS_HEADER

    output_file_kendalls = configs.KENDALS_OUTPUT_PATH

    ## write header for correlation output file
    with open(configs.ROOT_PATH + '/' + output_file_kendalls, "w+") as csv_file:
        writer = csv.DictWriter(csv_file, fieldnames=header_kendals.keys())
        writer.writeheader()

    experiment_id = 1 ## global variable to keep track of the experiment id

    ## just code features
    experiment_id = experiments(experiment_id) 
    print("Experiments generated successfully!")

    ## add developer features
    ## inlude developer features to "features" list in the jsonl file
    with open(configs.ROOT_PATH + "/" + configs.OUTPUT_PATH, "r") as file:
        data = file.readlines()
        ## convert the data to json
        data = [json.loads(line) for line in data]

    with open(configs.ROOT_PATH + "/" + configs.OUTPUT_WITH_DEV_FEATURES, "w+") as file1:
        ## add developer features to the features list
        for experiment in data:
            code_features = list(experiment["features"])

            ## append developer features
            dev_code_features = code_features + configs.DEVELOPER_FEATURES
            experiment["features"] = dev_code_features

            file1.write(json.dumps(experiment))
            file1.write('\n')

    print("Experiments with developer features generated successfully!")

