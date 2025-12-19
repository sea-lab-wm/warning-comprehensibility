import nltk
import os
import csv
from nltk.corpus import wordnet

nltk.download('wordnet') 
nltk.download('punkt')  

# CURRENT_DIR = os.path.dirname(os.path.realpath(__file__))


metrics_data = []

def tokenize_to_identifier_terms(snippet):
    tokens = nltk.word_tokenize(snippet)
    identifier_terms = [token for token in tokens if is_identifier_term(token)]
    return identifier_terms

def is_identifier_term(term):
    return term[0].isalpha()

def calculate_number_of_meanings_for_term(term):
    return len(wordnet.synsets(term))

def calculate_polysemy_for_line(line):
    terms = tokenize_to_identifier_terms(line)
    number_of_meanings_list = [calculate_number_of_meanings_for_term(term) for term in terms]
    
    #calculate average and max
    avg_meanings = sum(number_of_meanings_list) / len(number_of_meanings_list) if len(number_of_meanings_list) > 0 else 0
    max_meanings = max(number_of_meanings_list) if len(number_of_meanings_list) > 0 else 0
    
    return avg_meanings, max_meanings

def read_all_java_files_for_metrics(directory, metrics_data):
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.endswith(".java"):
                file_path = os.path.join(root, file)
                with open(file_path, "r") as f:
                    lines = f.readlines()
                    line_metrics = [calculate_polysemy_for_line(line) for line in lines]
                    avg_metrics = [sum(x) / len(x) if len(x) > 0 else 0 for x in zip(*line_metrics)]
                    max_metrics = [max(x) if len(x) > 0 else 0 for x in zip(*line_metrics)]
                    
                    result_row = {
                        'Filename': file,
                        'NM (avg)': avg_metrics[0],
                        'NM (max)': max_metrics[1]
                    }
                    metrics_data.append(result_row)

def list_to_csv_metrics_data(file_name, field_names, results):
    with open(file_name, 'w+', newline='') as f:
        writer = csv.DictWriter(f, fieldnames=field_names)
        writer.writeheader()
        writer.writerows(results)

def main(ROOT_PATH):
    SOURCE_DIR = ROOT_PATH + "/src/main/resources/snippet_identifier_splitter_out/"
    NEW_PATH = ROOT_PATH + "/output/nm_data.csv"
    read_all_java_files_for_metrics(SOURCE_DIR, metrics_data)

    field_names_metrics_data = ['Filename', 'NM (avg)', 'NM (max)']
    list_to_csv_metrics_data(NEW_PATH, field_names_metrics_data, metrics_data)