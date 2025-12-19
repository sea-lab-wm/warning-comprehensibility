# Machine Learning Model Execution for Code Comprehensibility Prediction

This directory contains all the code, data and results of the machine learning models that we used to predict code Comprehensibility of Java snippets.

## ML model Execution

### Pre-requisites: 
* Python 3.10
* pandas==2.2.3
* matplotlib==3.9.2
* scipy==1.14.1
* scikit-learn==1.5.2
* imblearn
* seaborn==0.13.2
* Conda 23.1.0
* tqdm

### Environment setup
Execute below command to create a conda virtual environment using the given requirements.yml (which can be found at `feature_extraction/requirements.yml`)
```conda env create --file /path/to/requirements.yml```

In addition to that you need to install cuml==25.08. Refer  (check https://docs.rapids.ai/install/ for the installation instructions)

## Execution of the Experiments
1. First change the configuration in the `ml-experiments/main.py` to point to the correct paths for the input and output files.
```
eg: For DS6 TASK 1 DEV-WISE--->
from helper import configs_TASK1_DS6_DEV_WISE as configs
```
2. Execute the below command inside `ml-experiments` directory to run the experiments. Needs to specify following arguments.

```--dev_features```: Give values "true" or "false" to use or not use developer features with selected code features.

```--output```: Name of the output file

```--config```: Specify which configuration need to run. Here are the set of configurations.
* configs_TASK1_DS6_DEV_WISE -  Dataset #1 with developer-wise data
* configs_TASK1_DS3_DEV_WISE - Dataset #2 with developer-wise data

Here is an example to run comprehensibility prediction with developer wise data for Dataset #1 and save results as `DS1_DEV_WISE` inside `TASK1_AbsoluteComprehensibility/results` directory

```
python3 main.py --config configs_TASK1_DS6_DEV_WISE --dev_features true --output DS1_DEV_WISE
```

## Feature Selection (Optional)

> ⚠️ : **This step is required if you need to generate the experiments for TASK1 for first time. Unless please skip this step**

1. First change the configuration in the TASK1 `featureselection/feature_selection.py` and corresponding `experiment_generator.py` to point to the correct paths for the input and output files.
```
eg: For DS1 ---> 
from helper import configs_TASK1_DS6 as configs
```
2. Execute the below command to generate the final dataset for 1.
```python3 /path/to/experiment_generator.py```

This will generate the experiments.jsonl for TASK1 in the `featureselection` directory.

## Machine Information

All the experiments were run on CPUs. Below we mention the machine information on which we have used to train and evaluate the models.

1. `Machine 1`
```
Intel(R) Xeon(R) Gold 6254 CPU @ 3.10GHz, Memory 1.0 TB
```
2. `Machine 2`
```
Intel(R) Xeon(R) CPU E5-4627 v2 @ 3.30GHz, Memory 500 GB
```

3. `Machine 3`
```
Apple M4 Max, Memory 36 GB
```