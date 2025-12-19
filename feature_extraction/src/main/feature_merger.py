import traceback
import os

import pandas as pd

def process_cyclomatic_complexity_data(cyclomatic_complexity_df):
    """
    This function is used to aggregate cyclomatic complexity data of each method in a 
    file and return the sum of cyclomatic complexity of each file.
    """
    ## split the description by "/" and get the last element
    file_name = cyclomatic_complexity_df['File'].str.split('/').str[-1]
    ## replace the file name with the new file name
    cyclomatic_complexity_df['File'] = file_name

    ## rename the column to cyclomatic_complexity
    cyclomatic_complexity_df.rename(columns={"Description": "cyclomatic_complexity"}, inplace=True)
    ## split the cyclomatic complexity description by "has a cyclomatic complexity of" and get cyclomatic complexity value
    cyclomatic_complexity = cyclomatic_complexity_df['cyclomatic_complexity'].str.split('has a cyclomatic complexity of').str[-1].str.split('.').str[0]
    ## replace the column with the new cyclomatic complexity data
    cyclomatic_complexity_df['cyclomatic_complexity'] = cyclomatic_complexity
    ## convert the cyclomatic_complexity column to numeric
    cyclomatic_complexity_df['cyclomatic_complexity'] = pd.to_numeric(cyclomatic_complexity_df['cyclomatic_complexity'])
    
    ## merge similar File names and get the sum of cyclomatic complexity
    cyclomatic_complexity_df = cyclomatic_complexity_df.groupby(['File']).agg({'cyclomatic_complexity': 'sum'}).reset_index()
    ## remove the duplicate file names and keep the first one with the sum of cyclomatic complexity
    cyclomatic_complexity_df.drop_duplicates(subset=['File'], keep='first', inplace=True)
    return cyclomatic_complexity_df

def clean_intermediate_files(ROOT_PATH):
    """
    This function is used to clean the intermediate files created during the feature extraction process.
    """
    os.remove(ROOT_PATH + "/output/feature_data.csv")
    os.remove(ROOT_PATH + "/output/nmi_data.csv")
    os.remove(ROOT_PATH + "/output/nm_data.csv")
    os.remove(ROOT_PATH + "/output/itid.csv")
    os.remove(ROOT_PATH + "/output/raw_loc_data.csv")
    os.remove(ROOT_PATH + "/output/raw_cyclomatic_complexity_data.csv")
    print ("Intermediate files cleaned successfully")

def main(ROOT_PATH):
    try:

        ## Feature data from our code ##
        feature_data = pd.read_csv(ROOT_PATH + "/output/feature_data.csv")
        
        ## Read NMI data ##
        nmi_data = pd.read_csv(ROOT_PATH + "/output/nmi_data.csv")

        ## read NM data ##
        nm_data = pd.read_csv(ROOT_PATH + "/output/nm_data.csv")

        ## Read ITID data ##
        itid_data = pd.read_csv(ROOT_PATH + "/output/itid.csv")

        ## Read LOC data ##
        loc_data = pd.read_csv(ROOT_PATH + "/output/raw_loc_data.csv")

        raw_cyclomatic_complexity_data = pd.read_csv(ROOT_PATH + "/output/raw_cyclomatic_complexity_data.csv")

        ## read understandability metrics ##
        understandability_data = pd.read_csv(ROOT_PATH + "/src/main/resources/study_data/understandability_with_targets.csv")


    except FileNotFoundError as e:
        print(traceback.format_exc())
        quit()

    
    
    feature_data.reset_index()
    nmi_data.reset_index()
    nm_data.reset_index()
    itid_data.reset_index()
    loc_data.reset_index()
    raw_cyclomatic_complexity_data.reset_index()
    understandability_data.reset_index()

    ## Do preprocessing on cyclomatic_complexity_data
    cyclomatic_complexity_data = process_cyclomatic_complexity_data(raw_cyclomatic_complexity_data)
    
    ## Merge cyclomatic_complexity column in the cyclomatic_complexity_data with feature_data using file column
    all_data = pd.merge(feature_data, cyclomatic_complexity_data[["File", "cyclomatic_complexity"]], left_on="file", right_on="File", how="left")
    ## drop the File column
    all_data.drop(columns=["File"], inplace=True)

    ## change cyclomatic_complexity column name to Cyclomatic complexity
    all_data.rename(columns={"cyclomatic_complexity": "Cyclomatic complexity"}, inplace=True)
    
    ## Merge NMI data with all_data ##
    all_data = pd.merge(all_data, nmi_data[["Filename", "NMI (avg)", "NMI (max)"]], left_on="file", right_on="Filename", how="left")
    all_data.drop(columns=["Filename"], inplace=True)

    ## Merge ITID data with all_data ##
    all_data = pd.merge(all_data, itid_data[["Filename", "ITID (avg)", "ITID (min)"]], left_on="file", right_on="Filename", how="left")
    all_data.drop(columns=["Filename"], inplace=True)

    ## Merge NM data with all_data ##
    all_data = pd.merge(all_data, nm_data[["Filename", "NM (avg)", "NM (max)"]], left_on="file", right_on="Filename", how="left")
    all_data.drop(columns=["Filename"], inplace=True)

    ## Merge Code column with all_data ##
    all_data = pd.merge(all_data, loc_data[["Filename", "Code"]], left_on="file", right_on="Filename", how="left")
    all_data.drop(columns=["Filename"], inplace=True)
    ## rename the column to LOC
    all_data.rename(columns={"Code": "LOC"}, inplace=True)

    all_df = all_data

    # all_df.to_csv("ml_model/DS_3_ml_table.csv", index=False)
    all_df.to_csv(ROOT_PATH + "/final_features.csv", index=False)
    print ("Final features saved to final_features.csv - For all the datasets")
    ## filter only the dataset_id=3 data and save it to a file
    all_df[all_df['dataset_id'] == 3].to_csv(ROOT_PATH + "/output/final_features_ds3.csv", index=False)
    
    ## merge data/ds3_metrics_data.csv with final_features_ds3.csv on dataset_id,snippet_id
    ds3_metrics_data = pd.read_csv(ROOT_PATH + "/src/main/resources/study_data/ds3_metrics_data.csv")
    data_ds3 = pd.read_csv(ROOT_PATH + "/output/final_features_ds3.csv")
    data_ds3 = pd.merge(data_ds3, ds3_metrics_data, on=["dataset_id", "snippet_id"], how="left")
    data_ds3.to_csv(ROOT_PATH + "/output/final_features_ds3.csv", index=False)
    
    print ("Final features saved to output/final_features_ds3.csv - For dataset_id=3")
    all_df[all_df['dataset_id'] == 6].to_csv(ROOT_PATH + "/output/final_features_ds6.csv", index=False)

    
    data_ds6 = pd.read_csv(ROOT_PATH + "/output/final_features_ds6.csv")
    ## merge understandability_with_targets.csv with final_features_ds6.csv on system_name,developer_position,PE gen,PE spec (java)
    ## rename file_name to snipper_id in understandability_data
    understandability_data.rename(columns={"file_name": "snippet_id"}, inplace=True)
    data_ds6 = pd.merge(data_ds6, understandability_data[["PBU", "ABU", "ABU50", "BD", "BD50", "AU", "snippet_id", "developer_position", "PE gen", "PE spec (java)", "participant_id"]], on=["snippet_id"], how="left")    

    data_ds6.to_csv(ROOT_PATH + "/output/final_features_ds6.csv", index=False)   

    ## clean the intermediate files
    clean_intermediate_files(ROOT_PATH)

    ## polluted data removal
    ## This is because AU TAU ABU BD different for snippet 4-Pom participant=32
    ## and PBU AU ABU50 BD different for snippet 3-MyExpenses participant=35

    ## search for participant_id=32 and snippet_id=4-Pom and remove both the rows
    data_ds6 = data_ds6[~((data_ds6['participant_id'] == 32) & (data_ds6['snippet_id'] == '4-Pom'))]
    ## search for participant_id=35 and snippet_id=3-MyExpenses and remove both the rows
    data_ds6 = data_ds6[~((data_ds6['participant_id'] == 35) & (data_ds6['snippet_id'] == '3-MyExpenses'))]
    data_ds6.to_csv(ROOT_PATH + "/output/final_features_ds6.csv", index=False)
    print ("Final features saved to output/final_features_ds6.csv - For dataset_id=6")