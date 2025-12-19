"""
This module contains the ml model result output file header, evaluatin methods, best baseline models, 
results aggregation methods for DS3 for the TASK1_AbsoluteComprehensibility.
"""
from sklearn.metrics import classification_report, cohen_kappa_score, f1_score, matthews_corrcoef, precision_score, recall_score, multilabel_confusion_matrix, roc_auc_score
from sklearn.utils.multiclass import unique_labels
from collections import defaultdict
import numpy as np

class DS3:
    csv_data_dict = {
            "model": "",
            "iteration": "",
            "hyperparameters": "",
            "target": "",
            "drop_duplicates": "",
            "use_oversampling": False,
            "dataset": "",
            "K": 0,
            "fs_method": "",

            "n_instances": 0,

            "mae": 0.0,

            "experiment": ""
        }
    def __init__(self, helper, ds):
        self.helper = helper
        self.csv_data_dict = DS3.csv_data_dict
        self.ds = ds
    
    def evaluate(self, model, X_test, y_test, target, task):
        if task == "TASK1":
            X_test = X_test.drop(columns=['snippet_id'])
        X_test = X_test.values
        
        ## predict on the test split ##
        y_pred = model.predict(X_test)

        ## get the probabilities ##
        y_pred_proba = model.predict_proba(X_test)

        ## all predicted classes in the model
        all_predicted_classes = model.classes_
        if hasattr(model.classes_, "to_output"):
            all_predicted_classes = model.classes_.to_output("numpy")
            

        ## Identify unique classes in test data
        present_classes = unique_labels(y_test)

        # Map class index to column in y_pred_proba
        class_to_index = {label: i for i, label in enumerate(all_predicted_classes)}

        # Get indices of classes actually present in y_test
        present_indices = [class_to_index[cls] for cls in present_classes]

        # Filter y_pred_proba columns
        filtered_y_pred_proba = y_pred_proba[:, present_indices]

        # Normalize to ensure rows sum to 1
        filtered_y_pred_proba /= filtered_y_pred_proba.sum(axis=1, keepdims=True)

        results_dict = {}

        classifi_report = classification_report(y_test, y_pred, output_dict=True, zero_division=0) ## this ensures that the f1 score is 0 if there are no predicted instances
        prediction_report = multilabel_confusion_matrix (y_test, y_pred, labels=present_classes)

        ## get baseline f1 scores and class weights ##
        baseline_f1_1, baseline_f1_2, baseline_f1_3, baseline_f1_4, baseline_f1_5, baseline_f1_weighted = self.getBestBaselineModel(target)

        if len(present_classes) == 2:
            ## roc_auc_score for the positive class (i.e probability of the class with the greater label, i.e the index 1)
            roc_for_postive_class = roc_auc_score(y_test, filtered_y_pred_proba[:, 1])

            ## Flip the positive to negative
            y_true_negative_as_positive = (y_test == present_classes[0]).astype(int)

            ## https://stackoverflow.com/questions/42059805/how-should-i-get-the-auc-for-the-negative-class
            roc_for_negative_class = roc_auc_score(y_true_negative_as_positive, filtered_y_pred_proba[:,1])

            roc_auc_scores = [
                roc_for_negative_class,  # negative class
                roc_for_postive_class   # positive class
            ]
            unique, counts = np.unique(y_test.values,return_counts=True)
            values_count = dict(zip(unique,counts))
            ## weighted roc_auc
            ## Note: Can't use the sklearn API because in the binary case, it ignores the average
            roc_auc_weighted = (values_count[present_classes[0]] / len(y_test.values)) * roc_for_negative_class + ( values_count[present_classes[1]] / len(y_test.values)) * roc_for_postive_class

            roc_auc_macro = (roc_for_negative_class + roc_for_postive_class) / 2

        else:
            roc_auc_scores = roc_auc_score(y_test, filtered_y_pred_proba, labels=present_classes, multi_class="ovr", average=None)
            roc_auc_weighted = roc_auc_score(y_test, filtered_y_pred_proba, multi_class="ovr", labels=present_classes, average="weighted")
            roc_auc_macro = roc_auc_score(y_test, filtered_y_pred_proba, multi_class="ovr", labels=present_classes, average="macro")



        # Iterate through present classes and store evaluation metrics ##
        for idx, cls in enumerate(present_classes):
            cls_str = str(cls)
            results_dict[f"tp_{cls}"] = prediction_report[idx][1][1]
            results_dict[f"tn_{cls}"] = prediction_report[idx][0][0]
            results_dict[f"fp_{cls}"] = prediction_report[idx][0][1]
            results_dict[f"fn_{cls}"] = prediction_report[idx][1][0]
            results_dict[f"support_{cls}"] = classifi_report[cls_str]["support"]
            results_dict[f"f1_{cls}"] = classifi_report[cls_str]["f1-score"]
            results_dict[f"precision_{cls}"] = classifi_report[cls_str]["precision"]
            results_dict[f"recall_{cls}"] = classifi_report[cls_str]["recall"]

            results_dict[f"roc_auc_{cls}"] = roc_auc_scores[idx]



        ## compute the weighted scores ##
        f1_weighted = f1_score(y_test, y_pred, average="weighted", labels=present_classes, zero_division=0) ## zero_division=0 to avoid nan values to include in the average
        precision_weighted = classifi_report["weighted avg"]["precision"]
        recall_weighted = classifi_report["weighted avg"]["recall"]
        
        ## compute the macro scores ##
        f1_macro = f1_score(y_test, y_pred, average="macro", labels=present_classes, zero_division=0)
        precision_macro = classifi_report["macro avg"]["precision"]
        recall_macro = classifi_report["macro avg"]["recall"]

        F1_weighted_improved_over_baseline = 1 if f1_weighted > baseline_f1_weighted else 0
        RI_F1_weighted = (f1_weighted - baseline_f1_weighted) / baseline_f1_weighted

        roc_auc_improved_over_baseline = 1 if roc_auc_weighted > 0.5 else 0
        RI_roc_auc_weighted = (roc_auc_weighted - 0.5) / 0.5

        ## compute MCC ##
        mcc = matthews_corrcoef(y_test, y_pred)

        ## compute Cohen Kappa
        cohen_kappa = cohen_kappa_score(y_test, y_pred, labels=present_classes)


        results_dict["n_instances"] = len(y_test)
        
        results_dict["f1_weighted"] = f1_weighted
        results_dict["precision_weighted"] = precision_weighted
        results_dict["recall_weighted"] = recall_weighted
        results_dict["roc_auc_weighted"] = roc_auc_weighted

        results_dict["f1_macro"] = f1_macro
        results_dict["precision_macro"] = precision_macro
        results_dict["recall_macro"] = recall_macro
        results_dict["roc_auc_macro"] = roc_auc_macro

        results_dict["y_test_index"] = y_test.index.values
        results_dict["y_actual"] = y_test.values
        results_dict["y_pred"] = y_pred

        results_dict["F1_weighted > baseline"] = F1_weighted_improved_over_baseline
        results_dict["RI=(F1_weighted-baseline)/baseline"] = RI_F1_weighted

        results_dict["y_pred_proba"] = y_pred_proba

        results_dict["matthews_corrcoef"] = mcc
        results_dict["cohen_kappa"] = cohen_kappa

        results_dict["roc_auc_weighted > baseline"] = roc_auc_improved_over_baseline
        results_dict["RI=(roc_auc_weighted-baseline)/baseline"] =  RI_roc_auc_weighted


        return results_dict
    
    def getBestBaselineModel(self, target):
        if target == "readability_level": ## best baseline= random (distribution)
            baseline_1_f1 = 0.0734711
            baseline_2_f1 = 0.2050413
            baseline_3_f1 = 0.2677686
            baseline_4_f1 = 0.2719008
            baseline_5_f1 = 0.1818182

            baseline_f1_weighted = 0.2261279

        ## AC - DS3 - Snippet wise
        # if target == "readability_level":
        #     baseline_1_f1 = "-"
        #     baseline_2_f1 = 0.029911504
        #     baseline_3_f1 = 0.268888889
        #     baseline_4_f1 = 0.258601399
        #     baseline_5_f1 = "-"

        #     baseline_f1_weighted = 0.395400000
            

        return baseline_1_f1, baseline_2_f1, baseline_3_f1, baseline_4_f1, baseline_5_f1, baseline_f1_weighted  
    
    def dict_data_generator(self, csv_data_dict, model, iteration, hyperparameters, 
                        target, use_oversampling, K, 
                        fs_method, fold_results, y_train, experiment
                        ):
        csv_data_dict["model"] = model
        csv_data_dict["iteration"] = iteration
        csv_data_dict["hyperparameters"] = str(sorted(eval(str(hyperparameters))))
        csv_data_dict["target"] = target
        csv_data_dict["drop_duplicates"] = True
        csv_data_dict["use_oversampling"] = use_oversampling
        csv_data_dict["dataset"] = "ds_code"
        csv_data_dict["K"] = K
        csv_data_dict["fs_method"] = fs_method

        
        # Dynamically extract class labels from y_train
        existing_classes = set(y_train[target].unique())

        for class_label in existing_classes:
            csv_data_dict[f"tp_{class_label}"] = fold_results.get(f"tp_{class_label}", 0)
            csv_data_dict[f"tn_{class_label}"] = fold_results.get(f"tn_{class_label}", 0)
            csv_data_dict[f"fp_{class_label}"] = fold_results.get(f"fp_{class_label}", 0)
            csv_data_dict[f"fn_{class_label}"] = fold_results.get(f"fn_{class_label}", 0)
            csv_data_dict[f"support_{class_label}"] = fold_results.get(f"support_{class_label}", 0)
            csv_data_dict[f"train_support_{class_label}"] = y_train[y_train[target] == class_label].shape[0]

            csv_data_dict[f"f1_{class_label}"] = fold_results.get(f"f1_{class_label}", 0)
            csv_data_dict[f"precision_{class_label}"] = fold_results.get(f"precision_{class_label}", 0)
            csv_data_dict[f"recall_{class_label}"] = fold_results.get(f"recall_{class_label}", 0)

            csv_data_dict[f"roc_auc_{class_label}"] = fold_results.get(f"roc_auc_{class_label}", 0)

        csv_data_dict["n_instances"] = fold_results["n_instances"]

        csv_data_dict["f1_weighted"] = fold_results["f1_weighted"]
        csv_data_dict["precision_weighted"] = fold_results["precision_weighted"]
        csv_data_dict["recall_weighted"] = fold_results["recall_weighted"]
        csv_data_dict["roc_auc_weighted"] = fold_results["roc_auc_weighted"]

        csv_data_dict["f1_macro"] = fold_results["f1_macro"]
        csv_data_dict["precision_macro"] = fold_results["precision_macro"]
        csv_data_dict["recall_macro"] = fold_results["recall_macro"]
        csv_data_dict["roc_auc_macro"] = fold_results["roc_auc_macro"]

        csv_data_dict["F1_weighted > baseline"] = fold_results["F1_weighted > baseline"]
        csv_data_dict["RI=(F1_weighted-baseline)/baseline"] = fold_results["RI=(F1_weighted-baseline)/baseline"]

        csv_data_dict["matthews_corrcoef"] = fold_results["matthews_corrcoef"]
        csv_data_dict["cohen_kappa"] = fold_results["cohen_kappa"]

        csv_data_dict["roc_auc_weighted > baseline"] = fold_results["roc_auc_weighted > baseline"]
        csv_data_dict["RI=(roc_auc_weighted-baseline)/baseline"] = fold_results["RI=(roc_auc_weighted-baseline)/baseline"]

        csv_data_dict["experiment"] = experiment
        
        return csv_data_dict
    
    def aggregate_results(self, results, target, model_name, K, hyperparam, model):
        """
        Aggregate the results from all the folds dynamically based on the classes present in the test data.
        """
        y_index_all = []
        y_pred_all = []
        y_true_all = []

        y_pred_proba_all = []

        # Dictionaries to store TP, TN, FP, FN dynamically
        metrics = defaultdict(lambda: defaultdict(int))
        
        # Collect all true labels and predictions
        for value in results.values():
            y_true_all.extend(value["y_actual"])
            y_index_all.extend(value["y_test_index"])
            y_pred_all.extend(value["y_pred"])

            y_pred_proba_all.extend(value["y_pred_proba"])

        # Identify unique classes dynamically
        unique_classes = unique_labels(np.concatenate(y_true_all).ravel())
        
        # Aggregate confusion matrix values
        for value in results.values():
            for clss in unique_classes:
                metrics[clss]["tp"] += value.get(f"tp_{clss}", 0)
                metrics[clss]["tn"] += value.get(f"tn_{clss}", 0)
                metrics[clss]["fp"] += value.get(f"fp_{clss}", 0)
                metrics[clss]["fn"] += value.get(f"fn_{clss}", 0)
        
        # Compute class-wise precision, recall, and F1 scores
        f1_scores = f1_score(y_true_all, y_pred_all, average=None, zero_division=0)
        precision_scores = precision_score(y_true_all, y_pred_all, average=None, zero_division=0)
        recall_scores = recall_score(y_true_all, y_pred_all, average=None, zero_division=0)
        
        roc_auc_scores = roc_auc_score(y_true_all, y_pred_proba_all, multi_class='ovr', labels=unique_classes, average=None)


        overall_results_dict = {
            "f1_weighted": f1_score(y_true_all, y_pred_all, average="weighted", zero_division=0),
            "f1_macro": f1_score(y_true_all, y_pred_all, average="macro", zero_division=0),
            "precision_weighted": precision_score(y_true_all, y_pred_all, average="weighted", zero_division=0),
            "precision_macro": precision_score(y_true_all, y_pred_all, average="macro", zero_division=0),
            "recall_weighted": recall_score(y_true_all, y_pred_all, average="weighted", zero_division=0),
            "recall_macro": recall_score(y_true_all, y_pred_all, average="macro", zero_division=0),
            "n_instances": len(y_true_all),
            "roc_auc_weighted": roc_auc_score(y_true_all, y_pred_proba_all, multi_class='ovr', labels=unique_classes, average='weighted'),
            "roc_auc_macro": roc_auc_score(y_true_all, y_pred_proba_all, multi_class='ovr', labels=unique_classes, average='macro')
        }
        
        for idx, cls in enumerate(unique_classes):
            overall_results_dict[f"f1_{cls}"] = f1_scores[idx]
            overall_results_dict[f"precision_{cls}"] = precision_scores[idx]
            overall_results_dict[f"recall_{cls}"] = recall_scores[idx]
            overall_results_dict[f"tp_{cls}"] = metrics[cls]["tp"]
            overall_results_dict[f"tn_{cls}"] = metrics[cls]["tn"]
            overall_results_dict[f"fp_{cls}"] = metrics[cls]["fp"]
            overall_results_dict[f"fn_{cls}"] = metrics[cls]["fn"]

            overall_results_dict[f"roc_auc_{cls}"] = roc_auc_scores[idx]

        ## get baseline f1 scores and class weights ##
        _,_,_,_,_, baseline_f1_weighted = self.getBestBaselineModel(target)
        
        F1_weighted_improved_over_baseline = 1 if overall_results_dict["f1_weighted"] > baseline_f1_weighted else 0
        RI_F1_weighted = (overall_results_dict["f1_weighted"] - baseline_f1_weighted) / baseline_f1_weighted

        roc_auc_weighted_improved_over_baseline = 1 if overall_results_dict["roc_auc_weighted"] > 0.5 else 0
        RI_roc_auc_weighted = (overall_results_dict["roc_auc_weighted"] - 0.5) / 0.5

        overall_results_dict["F1_weighted > baseline"] = F1_weighted_improved_over_baseline
        overall_results_dict["RI=(F1_weighted-baseline)/baseline"] = RI_F1_weighted

        overall_results_dict["roc_auc_weighted > baseline"] = roc_auc_weighted_improved_over_baseline
        overall_results_dict["RI=(roc_auc_weighted-baseline)/baseline"] = RI_roc_auc_weighted

        ## MCC ##
        overall_mcc = matthews_corrcoef(y_true_all, y_pred_all)

        ## Cohen's Kappa ##
        overall_cohens_kappa = cohen_kappa_score(y_true_all, y_pred_all, labels=unique_classes)

        overall_results_dict["matthews_corrcoef"] = overall_mcc
        overall_results_dict["cohen_kappa"] = overall_cohens_kappa


        return overall_results_dict

    
    def getHelper(self):
        return self.helper