#!/bin/bash

## This is the main script that runs the entire pipeline ##
PROJECT_ROOT=$(cd "$(dirname "$0")" && pwd)
export PROJECT_ROOT
export RAW_SNIPPETS_DIR="$PROJECT_ROOT/src/main/resources/corrected_raw_snippets"
export OUTPUT_DIR="$PROJECT_ROOT/output"

## 1. Runs the SCC tool on the code snippets ##
./tools/execution-scripts/run_scc.sh

## 2. Runs the PMD tool on the code snippets ##
./tools/execution-scripts/run_pmd.sh

## 3. Runs Parser.java to parse the snippets ##
gradle run

## 4. Merging features and metrics ##
python3 ${PROJECT_ROOT}/src/main/feature_extraction_main.py