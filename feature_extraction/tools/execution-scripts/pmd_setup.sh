#!/bin/bash

## This script sets up PMD on an Ubuntu 20.04 or MacOS 11 machine.

###########
## SETUP ##
###########

## checks if the environment variable pmd is set
if command -v pmd &> /dev/null ; then
    echo "pmd already setup on this machine."
    exit 2  
fi

PMD_DIR=pmd

## create pmd dir if not exists
mkdir -p tools/${PMD_DIR}

## navigate to pmd directory
cd tools/${PMD_DIR} || exit

## Check OS version
OS=$(uname -a | grep -o '\w*' | head -1)

if [ "$OS" == "Darwin" ]; then
    echo "MacOS detected"
    echo "Downloading PMD for MacOS..."
elif [ "$OS" == "Linux" ]; then
    echo "Ubuntu detected"
    echo "Downloading PMD for Ubuntu..."
else
    echo "OS not supported"
    exit 1
fi

## download pmd
wget https://github.com/pmd/pmd/releases/download/pmd_releases%2F7.0.0-rc3/pmd-dist-7.0.0-rc3-bin.zip

## extract pmd, move to pmd directory
unzip pmd-dist-7.0.0-rc3-bin.zip -d ${PMD_DIR}

## remove the zip file
rm pmd-dist-7.0.0-rc3-bin.zip