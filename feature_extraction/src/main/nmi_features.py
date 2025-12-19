
#################################################################################
## These features computed based on https://ieeexplore.ieee.org/document/7503707 
#################################################################################
import nltk
import os
nltk.download('wordnet') # download the wordnet package
nltk.download('punkt') # download the punkt package
from nltk.corpus import wordnet
import pandas as pd
import csv

nmi_metrics = []

# tokenise a snippet
def tokenise_snippet(snippet):
    ## read the snippet line by line
    line_by_line = []
    lines = snippet.splitlines()
    for line in lines:
        # tokenise each line
        tokens = line.split(" ")
        # remove empty tokens
        tokens = [token for token in tokens if token != ""]
        # remove duplicates
        tokens = list(set(tokens))
        line_by_line.append(tokens)

    return line_by_line


# recusively read all java files in the directory
def read_all_java_files(directory,nmi_metrics, ROOT_PATH):
    for root, dirs, files in os.walk(directory):
        for file in files:
            
            if file.endswith(".java"):
                file_path = os.path.join(root, file)
                with open(file_path, "r") as f:
                    NMI_per_line = []
                    total_NMI = 0.0
                    result_row = {}
                    snippet = f.read()
                    tokenised_snippet = tokenise_snippet(snippet)
                    # compute the features
                    for line in tokenised_snippet:
                        NMI = 0.0
                        for term in line:
                            NMI += get_term_particularity(term)
                            print(term, get_term_particularity(term))
                        NMI_per_line.append(NMI) 
                    total_NMI = sum(NMI_per_line)       
                    result_row['Filename'] = file
                    result_row['NMI'] = total_NMI
                    result_row['NMI (avg)'] = total_NMI / get_loc(file, ROOT_PATH)
                    result_row['NMI (max)'] = max(NMI_per_line)
                    nmi_metrics.append(result_row)

def get_loc(file_name, ROOT_PATH):

    df = pd.read_csv(ROOT_PATH + '/output/raw_loc_data.csv')
    return df.loc[df['Filename'] == file_name]['Code'].values[0]

def get_term_particularity(term):
    '''
    computed as the number of hops from
    the node containing term to the root node (entity) in the hypernym tree of term
    '''
    # Get the synsets (groups of synonyms) for the term
    if len(wordnet.synsets(term)) == 0:
        return 0
    synset = wordnet.synsets(term)[0]

    # Compute the depth (number of hops) of a synset in the 
    # hypernym (specific meaning of the synset) tree
    depth = synset.min_depth()

    return depth

def list_to_csv(file_name, field_names, results):
    with open(file_name, 'w', newline='') as f:
        writer = csv.DictWriter(f, fieldnames=field_names)
        writer.writeheader()
        writer.writerows(results)


def list_to_csv_write_by_lines(file_name, field_names, results):
    with open(file_name, 'w', newline='') as f:
        writer = csv.DictWriter(f, fieldnames=field_names)
        writer.writeheader()
        writer.writerows(results)

def main(ROOT_PATH):

    SOURCE_DIR = ROOT_PATH + "/src/main/resources/snippet_identifier_splitter_out/"
    NEW_PATH = ROOT_PATH + "/output/nmi_data.csv"
    read_all_java_files(SOURCE_DIR, nmi_metrics, ROOT_PATH)

    field_names = ['Filename', 'NMI', 'NMI (avg)', 'NMI (max)']  
    list_to_csv_write_by_lines(NEW_PATH, field_names, nmi_metrics)