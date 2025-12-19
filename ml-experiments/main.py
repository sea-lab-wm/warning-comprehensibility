import os
import sys
# Add the root directory to the system path
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))

from helper.helper import Helper

from TASK1_AbsoluteComprehensibility.ds3.ds3 import DS3
from TASK1_AbsoluteComprehensibility.ds3.ds3_snippet_wise import DS3_SNIPPET_WISE
from TASK1_AbsoluteComprehensibility.ds6.ds6 import DS6
from TASK1_AbsoluteComprehensibility.ds6.ds6_snippet_wise import DS6_SNIPPET_WISE
from TASK2_RelativeComprehensibility.ds.ds import DS
from TASK2_RelativeComprehensibility.ds.ds_dev_wise import DS_DEV_WISE

import argparse
import importlib

def load_config(config_name):
    """Dynamically import the configuration module based on user input."""
    try:
        return importlib.import_module(f"helper.{config_name}")
    except ModuleNotFoundError:
        raise ValueError(f"Configuration '{config_name}' not found. Please check the name.")


###################	Import from helper 	###############################
### Change the configurations here appropriately ######################
### For TASK2 DS6 ---> from helper import configs_TASK2_DS6 as configs
### For TASK2 DS3 ---> from helper import configs_TASK2_DS3 as configs
### For TASK1 DS6 ---> from helper import configs_TASK1_DS6 as configs
### For TASK1 DS3 ---> from helper import configs_TASK1_DS3 as configs
#######################################################################
# from helper import configs_TASK1_DS3 as configs
# sys.path.append(configs.ROOT_PATH)

## hide warnings
import warnings
warnings.filterwarnings("ignore")

# import cudf.pandas
# cudf.pandas.install()

import pandas as pd

import csv
import json
import numpy as np

from sklearn.preprocessing import StandardScaler
from sklearn.model_selection import KFold

from imblearn.pipeline import Pipeline
from imblearn.over_sampling import SMOTE

def main():

    parser = argparse.ArgumentParser(description="Select a configuration file dynamically.")
    parser.add_argument(
        "--config",
        required=True,
        choices=["configs_TASK2_DS6_DEV_WISE", "configs_TASK2_DS3_DEV_WISE", "configs_TASK2_DS6_SNIPPET_WISE", "configs_TASK2_DS3_SNIPPET_WISE", "configs_TASK1_DS6_DEV_WISE", "configs_TASK1_DS3_DEV_WISE", "configs_TASK1_DS3_SNIPPET_WISE", "configs_TASK1_DS6_SNIPPET_WISE"],
        help="Specify the configuration module to use."
    )
    parser.add_argument(
        "--dev_features",
        required=False,
        default="false",
        choices=["true", "false"],
        help="Specify the experiments to use developer features"
    )
    parser.add_argument(
        "--output",
        required=False,
        default=None,
        help="Specify the output file name"
    )

    
    args = parser.parse_args()
    configs = load_config(args.config)
    dev_features = args.dev_features
    output = args.output

    # Now you can use `configs` in your script as usual
    print(f"Loaded configuration: {args.config}")

    sys.path.append(configs.ROOT_PATH)

    ## to make the results reproducible ##
    np.random.seed(configs.RANDOM_SEED)

    ## Dataset loading
    complete_df = pd.read_csv(configs.ROOT_PATH + "/" + configs.DATA_PATH, low_memory=False)
    
    helper = Helper()
    if configs.TASK == "TASK1":
        if configs.DS == "DS3":
            dataset = "DS3"
            if configs.USE_SNIPPET_WISE: 
                ds = DS3_SNIPPET_WISE(helper, dataset) ## snippet wise
            else:
                ds = DS3(helper, dataset) ## developer wise
        elif configs.DS == "DS6":
            dataset = "DS6"
            if configs.USE_SNIPPET_WISE:
                ds = DS6_SNIPPET_WISE(helper, dataset) ## snippet wise
            else:
                ds = DS6(helper, dataset) ## developer wise
    elif configs.TASK == "TASK2":
        if configs.DS == "DS3":
            dataset = "DS3"
            if configs.USE_SNIPPET_WISE:
                ds = DS(helper, dataset) ## snippet wise
            else:
                ds = DS_DEV_WISE(helper, dataset) ## developer wise
        elif configs.DS == "DS6":
            dataset = "DS6"
            if configs.USE_SNIPPET_WISE:
                ds = DS(helper, dataset) ## snippet wise
            else:
                ds = DS_DEV_WISE(helper, dataset) ## developer wise
    
    ## write header
    if output != None:
        output_file_name = "results/" + output + ".csv"
    else:
        output_file_name = configs.OUTPUT_ML_PATH
    with open(configs.ROOT_PATH + "/" + output_file_name, "w+") as csv_file:
        writer = csv.DictWriter(csv_file, fieldnames=ds.csv_data_dict.keys())
        writer.writeheader()

    ## read json file
    if dev_features == "true":
        experiments_NAME=configs.OUTPUT_WITH_DEV_FEATURES ## use the experiments with developer features
    else:
        experiments_NAME=configs.OUTPUT_PATH ## use the experiments with only code features
    with open(configs.ROOT_PATH + "/" + experiments_NAME) as jsonl_file:
        experiments = [json.loads(jline) for jline in jsonl_file.read().splitlines()]

    outer_cv = KFold(n_splits=10, shuffle=True, random_state=configs.RANDOM_SEED)
    inner_cv = KFold(n_splits=5, shuffle=True, random_state=configs.RANDOM_SEED)

    models = ['svc']
    ## models = ["randomForest_classifier", "svc"]

    for model_name in models:
        
        for experiment in experiments:
            

            ######################
            ### CONFIGURATIONS ###
            ######################
            if configs.TASK == "TASK1":
                drop_duplicates = experiment["drop_duplicates"]

            use_oversampling = experiment["use_oversampling"]
            fs_method = experiment["feature_selection_method"]
            
            if configs.TASK == "TASK2":
                drop_duplicates = True
                dynamic_epsilon = experiment["dynamic_epsilon"]

            ###################
            ## INPUTS/OUTPUTS##
            ###################
            if configs.TASK == "TASK1":
                target = experiment["target"]
            
            filtered_df = complete_df
            if configs.TASK == "TASK2":
                target = experiment["code_comprehension_target"]
                filtered_df = complete_df.query("target == @target" + " and dynamic_epsilon ==" + str(dynamic_epsilon)) ## filter the data based on the target
            
            feature_set = experiment["features"]

            if configs.TASK == "TASK1":
                feature_X = filtered_df[['snippet_id'] + feature_set] ## add snippet_id to the features because we need it to identify the snippets
            elif configs.TASK == "TASK2":
                feature_X = filtered_df[['s1','s2'] + feature_set]## add snippet_id to the features because we need it to identify the snippets
                if not configs.USE_SNIPPET_WISE:
                    feature_X = filtered_df[['s1', 's2', 'participant_id'] + feature_set] ## add participant_id to the features because we need it to identify the participants
            if configs.TASK == "TASK1":
                target_y = filtered_df[target]
                #target_y = filtered_df[target].astype(int)
            elif configs.TASK == "TASK2":
                target_y = filtered_df["(s2>s1)relative_comprehensibility"]

            ## convert target values to integers
            target_y = target_y.astype(int)
        

            all_fold_results = {}

            ######################
            ## Best Hyperparams ##
            ######################
            best_hyperparams = set()

            ########################################
            ## BEST HYPERPARAMETERS FOR EACH FOLD ##
            ########################################
            # for (fold, (train_index,test_index))  in (enumerate(outer_cv.split(feature_X, target_y))):
            for (fold, (train_index,test_index)) in enumerate(ds.getHelper().custom_K_fold_cv(feature_X, target_y, target, outer_cv, configs.TASK, configs.DS, configs.USE_SNIPPET_WISE)):
                ###################
                ## Code features ##
                ###################
                
                ## split the Code dataset into train and test
                X_train, X_test, y_train, y_test = (
                    pd.DataFrame(feature_X).loc[train_index],
                    pd.DataFrame(feature_X).loc[test_index],
                    pd.DataFrame(target_y).loc[train_index],
                    pd.DataFrame(target_y).loc[test_index]
                )



                model, param_grid = ds.getHelper().model_initialisation(model_name, parameters="") 
                pipeline = Pipeline(steps = [('scaler', StandardScaler()), (model_name, model)])
                
                ## CONFIG 1 ## - apply over sampling
                if use_oversampling:
                        smo = SMOTE(random_state=configs.RANDOM_SEED)
                        pipeline = Pipeline(steps = [
                        ('scaler', StandardScaler()),
                        ('smo', smo),
                        (model_name, model)])

                ## CONFIG 2 ## - remove duplicates from training set
                config = {"drop_duplicates": drop_duplicates, "task": configs.TASK}
                

                # best_params, best_score_ = custom_grid_search_cv(model_name, pipeline, param_grid, X_train, y_train, inner_cv, config, target)
                best_params, best_score_ = ds.getHelper().custom_grid_search_cv(model_name, pipeline, param_grid, X_train, y_train, inner_cv, config, target, ds.evaluate, ds.ds, configs.USE_SNIPPET_WISE)
                ds.getHelper().LOGGER.info("Best param searching for fold {} for code features...".format(fold))
                
                ## since we are using a set, we need to convert the dict to a hashable type
                best_hyperparams.add((frozenset(best_params)))

            #############################################
            # Train and Test with best hyperparameters ##
            #############################################
            for best_hyper_params in best_hyperparams:
                for (fold, (train_index,test_index)) in enumerate(ds.getHelper().custom_K_fold_cv(feature_X, target_y, target, outer_cv, configs.TASK, configs.DS, configs.USE_SNIPPET_WISE)):
                # for (fold, (train_index,test_index)) in enumerate(outer_cv.split(filtered_df[feature_set], target_y)):
                    
                    ###################
                    ## Code features ##
                    ###################
                    ## split the Code dataset into train and test
                    X_train, X_test, y_train, y_test = (
                        pd.DataFrame(feature_X).loc[train_index],
                        pd.DataFrame(feature_X).loc[test_index],
                        pd.DataFrame(target_y).loc[train_index],
                        pd.DataFrame(target_y).loc[test_index]
                    )
                

                    model, _ = ds.getHelper().model_initialisation(model_name, parameters=dict(best_hyper_params))
                    pipeline = Pipeline(steps = [('scaler', StandardScaler()), (model_name, model)])
                    
                    if experiment['use_oversampling']:
                        smo = SMOTE(random_state=configs.RANDOM_SEED)
                        pipeline = Pipeline(steps = [
                        ('scaler', StandardScaler()),    
                        ('smo', smo),
                        (model_name, model)])

                    config ={"drop_duplicates": drop_duplicates, "task": configs.TASK}

                    # Combine X_train_c and y_train_c
                    if drop_duplicates:
                        combined_df = pd.concat([X_train, y_train], axis=1)

                        # Identify duplicate rows
                        duplicates_mask = combined_df.duplicated()

                        # Remove duplicates from X_train_c and y_train_c
                        X_train = X_train[~duplicates_mask]
                        y_train = y_train[~duplicates_mask]

                    # if configs.TASK == "TASK2":
                    #     X_train, X_test, y_train, y_test = ds.getHelper().data_cleanup_TASK2(X_train, X_test, y_train, y_test, ds.ds)
                    model = ds.getHelper().train(pipeline, X_train, y_train, configs.TASK)
                    fold_results_ = ds.evaluate(model, X_test, y_test, target, configs.TASK)

                    all_fold_results[fold] = fold_results_

                    ## Per fold results 
                    csv_data_dict1 = ds.dict_data_generator(
                        csv_data_dict=ds.csv_data_dict, 
                        model=model_name,
                        iteration=fold, 
                        hyperparameters=best_hyper_params, 
                        target=target,
                        use_oversampling=use_oversampling, 
                        K=experiment["K %"], 
                        fs_method=fs_method,
                        fold_results=fold_results_,
                        y_train=y_train, 
                        experiment=experiment["exp_id"])

                    ds.getHelper().dict_to_csv(configs.ROOT_PATH + "/" + output_file_name, csv_data_dict1)


                ## aggregate the results from all the folds
                overall_results = ds.aggregate_results(all_fold_results, target, model_name, experiment["K %"], best_hyper_params, model) 
                
                csv_data_dict2 = ds.dict_data_generator(
                    csv_data_dict=ds.csv_data_dict,
                    model=model_name, 
                    iteration="Overall", 
                    hyperparameters=best_hyper_params, 
                    target=target, 
                    use_oversampling=use_oversampling,
                    K=experiment["K %"],
                    fs_method=fs_method,
                    fold_results=overall_results,
                    y_train=y_train,
                    experiment=experiment["exp_id"])
   
                ds.getHelper().dict_to_csv(configs.ROOT_PATH + "/" + output_file_name, csv_data_dict2)

if __name__ == "__main__":
    main()