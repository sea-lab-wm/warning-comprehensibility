"""
This script is used by the experiment_generator.py to generate the experiments for the feature selection.
Kendall's tau is used to select the best features.
"""
import sys
import os

# Add the root directory to the system path
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '../../')))

###################	Import from helper 	###############################
### Change the configurations here appropriately ######################
### For DS6 Snippet-wise ---> from helper import configs_TASK1_DS6_SNIPPET_WISE as configs
### For DS3 Dev-wise ---> from helper import configs_TASK1_DS3_DEV_WISE as configs
### For DS6 Snippet wise ---> from helper import configs_TASK1_DS6_SNIPPET_WISE as configs
### For DS3 Dev-wise ---> from helper import configs_TASK1_DS3_DEV_WISE as configs
#######################################################################
from helper import configs_TASK1_DS3_DEV_WISE as configs

import pandas as pd
from scipy.stats import kendalltau

class FeatureSelection:
    def __init__(self, feature_df, y, drop_duplicates, dataset_key):
        self.feature_df = feature_df
        self.drop_duplicates = drop_duplicates
        self.dataset = dataset_key
        self.X = feature_df
        self.X = self.X.dropna(axis=1) ## drop columns with NaN values
        self.y = y
        

    def compute_kendals_tau(self):
        """
        This function computes the kendal's tau correlation of each feature with the target variable
        Parameters 
            X: input features
            y: target variable (discrete)
        returns: the list of the features with their kendal's tau correlation
        """
        for feature in self.X.columns:
            tau, p_value = kendalltau(self.X[feature], self.y, nan_policy='omit')
            
            if self.dataset == "ds_code":
                with open(configs.ROOT_PATH + '/' + configs.KENDALS_OUTPUT_PATH, "a") as csv_file:
                    csv_file.write(feature + "," + self.y.name + ',' + str(tau) + ',' + str(abs(tau)) + ',' + str(p_value) + ',' + self.dataset + ',' + str(self.drop_duplicates) + '\n')

        if self.dataset == "ds_code":
            kendall = pd.read_csv(configs.ROOT_PATH + '/' + configs.KENDALS_OUTPUT_PATH)
        
        kendall = kendall[(kendall['target'] == self.y.name) & (kendall['dataset'] == self.dataset) & (kendall['drop_duplicates'] == self.drop_duplicates)]

        self.kendall = kendall
        return kendall

    def select_k_best(self, k, method):
        """
        This function selects the k best features based on the kendal's tau correlation
        Parameters 
            k: number of best features to select
        returns: 
            the list of the features with their mutual information in natural unit of information (nat)
        """
        if method == "kendalltau":
            ## order |tau| in descending order
            kendall = self.kendall.sort_values(by='|tau|', ascending=False)
            ## select the k best features
            k_best_features = kendall.head(k)['feature']
        return k_best_features    