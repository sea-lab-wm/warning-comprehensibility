import nltk
import os
import csv
from nltk.corpus import wordnet

nltk.download('wordnet')
nltk.download('punkt')  

CURRENT_DIR = os.path.dirname(os.path.realpath(__file__))

identifier_terms_metrics = []

def tokenize_to_identifier_terms(snippet):
    tokens = nltk.word_tokenize(snippet)
    identifier_terms = [token for token in tokens if is_identifier_term(token)]
    return identifier_terms

def is_identifier_term(term):
    return term[0].isalpha()

def is_actual_word(term):
    return len(wordnet.synsets(term)) > 0

#calculate ITID ratio for a line
def calculate_itid_ratio_for_line(line):
    identifier_terms = tokenize_to_identifier_terms(line)
    actual_word_identifier_terms = [term for term in identifier_terms if is_actual_word(term)]
    actual_word_count = len(actual_word_identifier_terms)
    total_identifier_count = len(identifier_terms)
    ratio = actual_word_count / total_identifier_count if total_identifier_count > 0 else 0
    return ratio

def read_all_java_files_for_identifiers(directory, identifier_terms_metrics):
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.endswith(".java"):
                file_path = os.path.join(root, file)
                with open(file_path, "r") as f:
                    lines = f.readlines()
                    line_ratios = [calculate_itid_ratio_for_line(line) for line in lines]
                    avg_ratio = sum(line_ratios) / len(line_ratios) if len(line_ratios) > 0 else 0
                    max_ratio = min(line_ratios)
                    result_row = {
                        'Filename': file,
                        'ITID (avg)': avg_ratio,
                        'ITID (min)': max_ratio
                    }
                    identifier_terms_metrics.append(result_row)

def list_to_csv_identifier_ratio_data(file_name, field_names, results):
    with open(file_name, 'w+', newline='') as f:
        writer = csv.DictWriter(f, fieldnames=field_names)
        writer.writeheader()
        writer.writerows(results)

def main(ROOT_PATH):
    SOURCE_DIR = ROOT_PATH + "/src/main/resources/snippet_identifier_splitter_out/"
    NEW_PATH = ROOT_PATH + "/output/itid.csv" 

    read_all_java_files_for_identifiers(SOURCE_DIR, identifier_terms_metrics)

    field_names_identifier_ratio_data = ['Filename', 'ITID (avg)', 'ITID (min)']
    list_to_csv_identifier_ratio_data(
        NEW_PATH, field_names_identifier_ratio_data, identifier_terms_metrics
    )