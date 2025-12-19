#!/bin/bash

## This script sets up, and runs PMD on an Ubuntu 20.04 or MacOS 11 machine.
###########
## SETUP ##
###########

function configure_pmd() {
    # create pmd dir if not exists
    mkdir -p "$1"

    ## create config.xml file
    echo "<?xml version='1.0'?>
    <ruleset name='Custom Rule for Cyclomatic Complexity'>
        <description>Cyclomatic complexity</description>
        <rule ref='category/java/design.xml/CyclomaticComplexity'>
            <properties>
                <property name='methodReportLevel' value='1' />
            </properties>
        </rule>
    </ruleset>" > "$1"/cyclomatic_complex_rules.xml
}

PMD_DIR=tools/pmd
configure_pmd "${PMD_DIR}"

## checks if the environment variable pmd is set
if ! command -v pmd &> /dev/null ; then
    echo "cannot find pmd on this machine."
    echo "This script will run tools/execution-scripts/pmd-setup.sh now if you are on an Ubuntu 20.04 machine or Mac OS 11+. Otherwise, read the instructions in that script on how to adapt it for other platforms, then follow them for yours."
    ## run pmd-setup.sh to setup pmd
    ./tools/execution-scripts/pmd_setup.sh

    PMD_PATH="${PMD_DIR}/pmd/pmd-bin-7.0.0-rc3/bin/pmd"
    PMD=$(realpath "$PMD_PATH")
    export PMD
else
    PMD=pmd
    echo "pmd installation found on this machine."
fi

#############
## RUN PMD ##
## ##########
echo "running pmd on the snippets..."
${PMD} check -d ${RAW_SNIPPETS_DIR} -R "${PMD_DIR}"/cyclomatic_complex_rules.xml -f csv -r ${OUTPUT_DIR}/raw_cyclomatic_complexity_data.csv