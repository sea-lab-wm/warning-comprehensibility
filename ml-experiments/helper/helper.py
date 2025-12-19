import os
from sklearn.utils import parallel_backend
from sklearn.utils.parallel import Parallel, delayed
from sklearn.model_selection import ParameterGrid

from sklearn.ensemble import RandomForestClassifier
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import multilabel_confusion_matrix
from sklearn.neighbors import KNeighborsClassifier
from sklearn.neural_network import MLPClassifier
## from sklearn.svm import SVC
## from sklearn.svm import LinearSVC as SVC
from sklearn.naive_bayes import GaussianNB

from sklearn.multiclass import OneVsRestClassifier

# from cuml.linear_model import LogisticRegression
# from cuml.neighbors import KNeighborsClassifier
# from cuml.ensemble import RandomForestClassifier
from cuml.svm import LinearSVC as SVC

import logging
import pandas as pd
import csv

import importlib

RANDOM_SEED = 42

## Logger
_LOG_FMT = '[%(asctime)s - %(levelname)s - %(name)s]-   %(message)s'
_DATE_FMT = '%m/%d/%Y %H:%M:%S'
logging.basicConfig(format=_LOG_FMT, datefmt=_DATE_FMT, level=logging.INFO)


class Helper:

    def __init__(self):
        self.LOGGER = logging.getLogger('__main__')

    def custom_grid_search_cv(self, model_name, pipeline, param_grid, X_train, y_train, cv, config, target,
                              evaluate_method, ds, use_snippet_wise):
        ## get the parameters for the model
        ## all the permutations of the hyperparameters ##
        candidate_params = list(ParameterGrid(param_grid))

        drop_duplicates = config["drop_duplicates"]
        task = config["task"]

        best_hyperparameters_ = {}  ## keep best hyperparameters for each fold and corresponding f1_weighted score

        # results=Parallel(n_jobs=1)(delayed(self.process_fold)(fold, train_index, test_index, candidate_params, drop_duplicates, model_name, pipeline, X_train, y_train, target, evaluate_method)
        #     for fold, (train_index, test_index) in enumerate(cv.split(X_train, y_train)))

        # Process each fold in parallel
        # with parallel_backend('loky'):
        results = Parallel(n_jobs=5)(
            delayed(self.process_fold)(fold, train_index, test_index, candidate_params, drop_duplicates, model_name,
                                    pipeline, X_train, y_train, target, evaluate_method, ds, task)
            for fold, (train_index, test_index) in
            enumerate(self.custom_K_fold_cv(X_train, y_train, target, cv, task, ds, use_snippet_wise)))

        # Collect results for each fold
        for (fold, best_param_, best_score_) in results:
            best_hyperparameters_[fold] = {"hyperparameters": best_param_, "f1_weighted": best_score_}

        best_score_ = best_hyperparameters_[0]["f1_weighted"]
        best_params_ = best_hyperparameters_[0]["hyperparameters"]

        # Determine the best hyperparameters across all folds
        for fold, best_hyperparameters in best_hyperparameters_.items():
            score_ = best_hyperparameters["f1_weighted"]
            if best_score_ < score_:
                best_score_ = score_
                best_params_ = best_hyperparameters["hyperparameters"]

        return best_params_, best_score_

    def model_initialisation(self, model_name, parameters):
        self.LOGGER.info("Launching model: " + model_name + "...")

        if model_name == "logisticregression":
            ## parameters for grid search
            ## We picked the parameters based on the following resources as believe those are the most important parameters to tune:
            ## https://medium.com/codex/do-i-need-to-tune-logistic-regression-hyperparameters-1cb2b81fca69
            param_grid = {
                "C": [1e-6, 1e-5, 1e-4, 0.001, 0.01, 0.1],
                "penalty": ["l2"],  ## l2 is recommended since less sensitive to outliers
                "solver": ["lbfgs"],  ## liblinear is recommended for small datasets and lbfgs for multi-class problems
                "max_iter": [8000],
                "multi_class": ["ovr"],
                "class_weight": ["balanced"],
                # "random_state": [RANDOM_SEED]
                # "C": [0.1],
                # "penalty": ["l2"], ## l2 is recommended since less sensitive to outliers
                # "solver": ["lbfgs"], ## liblinear is recommended for small datasets and lbfgs for multi-class problems
                # "max_iter": [8000],
                # "multi_class": ["ovr"],
                # "class_weight": ["balanced"],
                # "random_state": [RANDOM_SEED]

            }
            ## Pipeline requires the model name before the parameters
            param_grid = {f"{model_name}__{key}": value for key, value in param_grid.items()}
            model = LogisticRegression()
            #model = OneVsRestClassifier(model)
            if parameters:
                ## to initialize the model with the best hyperparameters model name should be removed
                parameters = {key.replace(f"{model_name}__", ""): value for key, value in parameters.items()}
                model = LogisticRegression(**parameters)
                #model = OneVsRestClassifier(model)

        elif model_name == "knn_classifier":
            ## https://www.kaggle.com/code/arunimsamudra/k-nn-with-hyperparameter-tuning?scriptVersionId=32640489&cellId=42
            param_grid = {
                "n_neighbors": [3, 4, 5],
                "weights": ["uniform"],  ## distance is given more weight to the closer points (weight = 1/distance)
                "metric": ["euclidean"],
                ## manhattan is the distance between two points measured along axes at right angles
                "algorithm": ["auto"],
                # "n_neighbors": [3],
                # "weights": ["uniform"], ## distance is given more weight to the closer points (weight = 1/distance)
                # "metric": ["euclidean"], ## manhattan is the distance between two points measured along axes at right angles
                # "algorithm": ["ball_tree"],
            }
            ## Pipeline requires the model name before the parameters
            param_grid = {f"{model_name}__{key}": value for key, value in param_grid.items()}
            model = KNeighborsClassifier()
            if parameters:
                ## to initialize the model with the best hyperparameters model name should be removed
                parameters = {key.replace(f"{model_name}__", ""): value for key, value in parameters.items()}
                model = KNeighborsClassifier(**parameters)

        elif model_name == "randomForest_classifier":
            ## https://towardsdatascience.com/hyperparameter-tuning-the-random-forest-in-python-using-scikit-learn-28d2aa77dd74
            param_grid = {
                "n_estimators": [150, 100, 50],
                "max_features": [1.0],  ## this will use all the features
                "min_impurity_decrease": [0.001],
                "max_depth": [15],
                "random_state": [RANDOM_SEED]
            }
            ## Pipeline requires the model name before the parameters
            param_grid = {f"{model_name}__{key}": value for key, value in param_grid.items()}
            model = RandomForestClassifier()
            if parameters:
                ## to initialize the model with the best hyperparameters model name should be removed
                parameters = {key.replace(f"{model_name}__", ""): value for key, value in parameters.items()}
                model = RandomForestClassifier(**parameters)

        elif model_name == "svc":
            ## https://medium.com/grabngoinfo/support-vector-machine-svm-hyperparameter-tuning-in-python-a65586289bcb#:~:text=The%20most%20critical%20hyperparameters%20for%20SVM%20are%20kernel%20%2C%20C%20%2C%20and,to%20make%20it%20linearly%20separable.
            param_grid = {
                "C": [1.0e-3, 1.0e-1, 1], ## higher value==> High panalty on misclaffications, lower value ==> lower panalty on missclaffications
                "tol": [1.0e-9, 1.0e-6], ## optimise convergence. sets the threshold for this convergence; small ==> Leads to a stricter convergence criterion, meaning the optimization algorithm will run for more iterations to achieve a more precise solution. larger ==> faster training, low accuracy 
                "probability": [True],
                # "kernel": ["linear"],
                # "random_state": [RANDOM_SEED],
                # "C": [1e-5],
                # "kernel": ["rbf"],
                # "tol": [1.0e-12],
                # "random_state": [RANDOM_SEED]
            }

            ## Pipeline requires the model name before the parameters
            param_grid = {f"{model_name}__{key}": value for key, value in param_grid.items()}
            model = SVC()
            if parameters:
                ## to initialize the model with the best hyperparameters model name should be removed
                parameters = {key.replace(f"{model_name}__", ""): value for key, value in parameters.items()}
                model = SVC(**parameters)

        elif model_name == "mlp_classifier":
            ## https://datascience.stackexchange.com/questions/36049/how-to-adjust-the-hyperparameters-of-mlp-classifier-to-get-more-perfect-performa
            param_grid = {
                # "hidden_layer_sizes": [7, (50,)], ## Single hidden layer with 7 nodes (Italian paper)
                # "learning_rate_init": [0.0001],
                # "momentum":[0.2, 0.9],
                # "activation": ["relu", "logistic"], ## logistic is sigmoid (Italian paper)
                # "solver": ["adam", "sgd"],
                # "max_iter":[2000],  # Adjust based on validation
                # "early_stopping": [True],
                # "random_state": [RANDOM_SEED]
                "hidden_layer_sizes": [(50,), (7,)],  ## Single hidden layer with 7 nodes (Italian paper)
                "learning_rate_init": [0.0001],
                "momentum": [0.2],
                "activation": ["relu"],  ## logistic is sigmoid (Italian paper)
                "solver": ["adam"],
                "max_iter": [2000],  # Adjust based on validation
                "early_stopping": [True],
                "random_state": [RANDOM_SEED]
            }
            ## Pipeline requires the model name before the parameters
            param_grid = {f"{model_name}__{key}": value for key, value in param_grid.items()}
            model = MLPClassifier()
            if parameters:
                ## to initialize the model with the best hyperparameters model name should be removed
                parameters = {key.replace(f"{model_name}__", ""): value for key, value in parameters.items()}
                model = MLPClassifier(**parameters)

        elif model_name == "bayes_network":
            ## https://coderzcolumn.com/tutorials/machine-learning/scikit-learn-sklearn-naive-bayes#3
            param_grid = {
                "var_smoothing": [0.01, 0.001, 0.0001, 1e-5, 1e-6, 1e-7, 1e-8, 1e-9, 1e-12, 1e-15]
                # "var_smoothing": [0.001]
            }
            ## Pipeline requires the model name before the parameters
            param_grid = {f"{model_name}__{key}": value for key, value in param_grid.items()}
            model = GaussianNB()
            if parameters:
                ## to initialize the model with the best hyperparameters model name should be removed
                parameters = {key.replace(f"{model_name}__", ""): value for key, value in parameters.items()}
                model = GaussianNB(**parameters)

        return model, param_grid

    def train(self, model, X_train, y_train, task):
        ## train the model on the train split
        ## drop 'snippet_id' column from the training data
        if task == "TASK1":
            X_train = X_train.drop(columns=['snippet_id'])
        if task == "TASK2":
            dropping_columns = ['s1', 's2']
            if 'participant_id' in X_train.columns:
                dropping_columns.append('participant_id')
            if 'pair_id' in X_train.columns:
                dropping_columns.append('pair_id')
            X_train = X_train.drop(columns=dropping_columns)
        model.fit(X_train.values, y_train.values.ravel())

        return model

    def process_fold(self, fold, train_index, test_index, candidate_params,
                     drop_duplicates, model_name, pipeline, X_train, y_train, target, evaluate_method, ds, task):

        ## best hyper params for the current fold
        hyper_param_dict = {}

        X_train_fold, X_val_fold = X_train.loc[train_index], X_train.loc[test_index]
        y_train_fold, y_val_fold = y_train.loc[train_index], y_train.loc[test_index]

        ## drop duplicates from the training folds only ##
        if drop_duplicates:
            combined_df = pd.concat([X_train_fold, y_train_fold], axis=1)  ## axis=1 for columns

            ## Identify duplicate rows
            duplicates_mask = combined_df.duplicated()

            ## Remove duplicates from X_train_c and y_train_c
            X_train_fold = X_train_fold[~duplicates_mask]
            y_train_fold = y_train_fold[~duplicates_mask]

        for (cand_idx, parameters) in enumerate(candidate_params):
            ## train the model with the current hyperparameters
            model = self.train(pipeline.set_params(**parameters), X_train_fold, y_train_fold, task)
            results = evaluate_method(model, X_val_fold, y_val_fold, target, task)

            ## remove the model name from the best hyperparameters keys
            hyperparam = ({key.replace(model_name + "__", ""): value for key, value in parameters.items()})

            ## convert the dict to a hashable type
            hyper_param_dict[frozenset(hyperparam.items())] = results

        ## get the best hyperparameters for the current fold ##
        best_score_ = hyper_param_dict[list(hyper_param_dict.keys())[0]]["f1_weighted"]
        best_param_ = list(hyper_param_dict.keys())[0]

        for hyper_param in hyper_param_dict:
            f1_weighted_score = hyper_param_dict[hyper_param]["f1_weighted"]
            if best_score_ < f1_weighted_score:
                best_param_ = hyper_param
                best_score_ = f1_weighted_score

        return fold, best_param_, best_score_

    def dict_to_csv(self, output_file_path, dict_data):
        with open(output_file_path, "a") as csv_file:
            writer = csv.DictWriter(csv_file, fieldnames=dict_data.keys())
            writer.writerow(dict_data)

    def generate_pairs(self, data, s1, s2):
        """
        This is for Snippet wise case.
        Generate pairs based on x and y values for bidirectional relationships.

        Parameters:
            data (pd.DataFrame): The original dataset.
            s1 (str): Column for s1 values.
            s2 (str): Column for s2 values.

        Returns:
            pd.Series: Pair IDs for each row in the dataset.
        """
        pair_ids = []
        for _, row in data.iterrows():
            pair_ids.append(f"{row[s1]}-{row[s2]}")
        return pair_ids

    def generate_pairs_dev_wise(self, data, s1, s2, participant_id):
        """
        This is for Developer wise case.
        Generate pairs based on x and y values for bidirectional relationships.

        Parameters:
            data (pd.DataFrame): The original dataset.
            s1 (str): Column for s1 values.
            s2 (str): Column for s2 values.
            participant_id (str): Column for participant_id values.

        Returns:
            pd.Series: Pair IDs for each row in the dataset.
        """
        pair_ids = []
        for _, row in data.iterrows():
            pair_ids.append(f"{row[s1]}-{row[s2]}-{row[participant_id]}")
        return pair_ids

    def get_oppo_pair(self, pair_id, ds):
        ## eg: 1-ClassifierPerformanceEvaluatorCustomizer-1-ModelPerformanceChart
        splitted_pair = pair_id.split('-')
        if ds == "DS6":
            ## only for DS6
            opposite_pair = (splitted_pair[2] + "-" + splitted_pair[3]) + "-" + (
                        splitted_pair[0] + "-" + splitted_pair[1])
        if ds == "DS3":
            ## only for DS3
            opposite_pair = (splitted_pair[1] + "-" + splitted_pair[0])
        return opposite_pair

    def get_opposite_pair_dev_wise(self, pair_id, ds):
        ## eg: 1-ClassifierPerformanceEvaluatorCustomizer-1-ModelPerformanceChart-4
        splitted_pair = pair_id.split('-')
        if ds == "DS6":
            ## only for DS6
            opposite_pair = (splitted_pair[2] + "-" + splitted_pair[3]) + "-" + (
                        splitted_pair[0] + "-" + splitted_pair[1]) + "-" + splitted_pair[4]
        ## eg for DS3 => 1-10-4
        if ds == "DS3":
            ## only for DS3
            opposite_pair = (splitted_pair[1] + "-" + splitted_pair[0]) + "-" + splitted_pair[2]
        return opposite_pair

    def custom_K_fold_cv(self, X_train, y_train, target, cv, task, ds, use_snippet_wise):

        if task == "TASK1":

            ## merge the X_train and y_train
            data = pd.concat([X_train, y_train], axis=1)

            ## take the first occurence of the x_col value
            unique_snippets_data = data.drop_duplicates(subset=['snippet_id'])

            index_lists = []

            for (train_index, test_index) in cv.split(unique_snippets_data.index):
                train_data = unique_snippets_data.iloc[train_index]  ## get index by row number. eg: 1 means 2nd row
                test_data = unique_snippets_data.iloc[test_index]

                ## search for the train_data[x_col] in data[x_col] and get the corresponding rows and add them to the train_data if they are not in the train_data
                train_data = data[data['snippet_id'].isin(train_data['snippet_id'])]

                test_data = data[data['snippet_id'].isin(test_data['snippet_id'])]

                ## move if the train_data contains only one sample per class to the test_data
                count_class_sample_count_train_list = count_class_sample_count(train_data, target)

                ## This is required because SMOTE will fail when there is only one sample per class ##
                for index in count_class_sample_count_train_list:
                    ## get the row from the train_data
                    row = train_data.loc[index]
                    row_to_append = pd.DataFrame([row], columns=train_data.columns)
                    ## add the row to the test_data
                    test_data = pd.concat([test_data, row_to_append], ignore_index=False)

                    ## drop the row from the train_data
                    train_data = train_data.drop(index)

                ## train_data and test_data indexes to array
                train_index = train_data.index
                test_index = test_data.index

                index_lists.append((train_index, test_index))

        if task == "TASK2":
            ## add pair_id to the data
            if use_snippet_wise:
                X_train['pair_id'] = self.generate_pairs(X_train, 's1', 's2')
            else:
                X_train['pair_id'] = self.generate_pairs_dev_wise(X_train, 's1', 's2', 'participant_id')

            ## merge the X_train and y_train
            data = pd.concat([X_train, y_train], axis=1, ignore_index=False)

            ## Create a set to track unique pairs
            unique_pair_ids = set()
            unique_snippets_data = []

            ## Identify and keep unique pairs
            def add_unique_pairs(row):
                pair_id = row['pair_id']
                opposite_pair = (
                    self.get_oppo_pair(pair_id, ds) if use_snippet_wise
                    else self.get_opposite_pair_dev_wise(pair_id, ds)
                )
                if pair_id not in unique_pair_ids and opposite_pair not in unique_pair_ids:
                    unique_pair_ids.add(pair_id)
                    return row
                return None

            # unique_snippets_data = pd.DataFrame(columns=data.columns)
            unique_snippets_data = data.apply(add_unique_pairs, axis=1).dropna()

            ## enumerate through the data and remove dup pairs. how to find the dup_pairs, self.get_oppo_pair(pair_id, ds)
            # for index, row in data.iterrows():
            #     pair_id = row['pair_id']
            #     if use_snippet_wise:
            #         opposite_pair = self.get_oppo_pair(pair_id, ds)
            #     else:
            #         opposite_pair = self.get_opposite_pair_dev_wise(pair_id, ds)

            #     ## add paid_id to the unique_snippets_data. if opposite_pair is already in the unique_snippets_data then do not add the pair_id
            #     if opposite_pair not in unique_snippets_data['pair_id'].values:
            #         if pair_id not in unique_snippets_data['pair_id'].values:
            #             ## add rows to the unique_snippets_data dataframe
            #             row_to_append = pd.DataFrame([row], columns=data.columns)
            #             unique_snippets_data = pd.concat([unique_snippets_data, row_to_append], ignore_index=False)

            index_lists = []

            for (train_index, test_index) in cv.split(unique_snippets_data.index):
                train_data = unique_snippets_data.iloc[train_index]
                test_data = unique_snippets_data.iloc[test_index]

                ## Collect all opposite pairs at once
                train_opposite_pairs = data[data['pair_id'].isin(train_data['pair_id'].apply(
                    lambda pid: self.get_oppo_pair(pid, ds) if use_snippet_wise else self.get_opposite_pair_dev_wise(
                        pid, ds)
                ))]

                test_opposite_pairs = data[data['pair_id'].isin(test_data['pair_id'].apply(
                    lambda pid: self.get_oppo_pair(pid, ds) if use_snippet_wise else self.get_opposite_pair_dev_wise(
                        pid, ds)
                ))]

                ## Combine original and opposite pair data
                train_data = pd.concat([train_data, train_opposite_pairs], ignore_index=False).drop_duplicates(
                    subset=['pair_id'])
                test_data = pd.concat([test_data, test_opposite_pairs], ignore_index=False).drop_duplicates(
                    subset=['pair_id'])

                ## Store indexes
                index_lists.append((train_data.index, test_data.index))

                # for index, row in train_data.iterrows():
                #     pair_id = row['pair_id']
                #     if use_snippet_wise:
                #         opposite_pair = self.get_oppo_pair(pair_id, ds)
                #     else:
                #         opposite_pair = self.get_opposite_pair_dev_wise(pair_id, ds)

                #     ## add the opposite pair to the train_data
                #     opposite_pair_data = data[data['pair_id'] == opposite_pair]
                #     train_data = pd.concat([train_data, opposite_pair_data], ignore_index=False)

                # for index, row in test_data.iterrows():
                #     pair_id = row['pair_id']
                #     if use_snippet_wise:
                #         opposite_pair = self.get_oppo_pair(pair_id, ds)
                #     else:
                #         opposite_pair = self.get_opposite_pair_dev_wise(pair_id, ds)

                #     ## add the opposite pair to the test_data
                #     opposite_pair_data = data[data['pair_id'] == opposite_pair]
                #     test_data = pd.concat([test_data, opposite_pair_data], ignore_index=False)

                ## drop duplicates based on pair_id
                # train_data = train_data.drop_duplicates(subset=['pair_id'])
                # test_data = test_data.drop_duplicates(subset=['pair_id'])

                # ## train_data and test_data indexes to array
                # train_index = train_data.index
                # test_index = test_data.index

                # index_lists.append((train_index, test_index))

        return index_lists


def count_class_sample_count(df, target):
    """
    Count the number of samples in each class.

    Parameters:
        df (pd.DataFrame): The dataset.
        target (str): The target column.

    Returns:
        pd.DataFrame: The class sample count.
    """

    result = df[target].value_counts().to_dict()
    value_1_list = []
    ## check if the result contains any values = 1
    if 1 in result.values():
        ## get the key where the value is 1
        for key in result:
            if result[key] == 1:
                ## search for the index of the target column where the value is 1 in df
                index = df[df[target] == key].index[0]
                value_1_list.append(index)

    return value_1_list







