#!/bin/bash

## This script sets up, and runs SCC on an Ubuntu 20.04 or MacOS 11 machine.

# Access the exported path from main.sh
echo "Accessing files in $RAW_SNIPPETS_DIR ..."

###########
## SETUP ##
###########
## checks if the environment variable SCC is set
if ! command -v scc &> /dev/null ; then
    echo "cannot find scc on this machine."
    echo "run the script tool-execution/scc-setup.sh if you are on an Ubuntu 20.04 machine or Mac OS 11+. Otherwise, read the instructions in that script on how to adapt it for other platforms, then follow them for yours."
    ## run scc-setup.sh to setup scc
    ./scc-setup.sh

    SCC_DIR=../scc
    export SCC=$(realpath ${SCC_DIR}/scc)

    #############
    ## RUN SCC ##
    ## ##########
    echo "running scc on the raw snippets..."
    ${SCC} --by-file -f csv -o ${OUTPUT_DIR}/raw_loc_data.csv ${RAW_SNIPPETS_DIR}

    # echo "running scc on the processed snippets..."
    # ${SCC} --by-file -f csv -o ../ml_model/loc_data.csv '../ml_model/src/main/resources/snippet_splitter_out'

else
    echo "scc installation found on this machine."
    echo "running scc on the raw snippets..."
    scc --by-file -f csv -o ${OUTPUT_DIR}/raw_loc_data.csv ${RAW_SNIPPETS_DIR}
    # echo "running scc on the processed snippets..."
    # scc --by-file -f csv -o ../ml_model/loc_data.csv ../ml_model/src/main/resources/snippet_splitter_out
fi
