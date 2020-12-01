# AUTOMATIZE

Automatize is a modular project to in Python for experiments automatization. This includes: run methods (single run and k-fold cross valitadion), analitycs (Multi-layer perceptron, Random Forests and Support Vector Machines) and preprocessing methods.

### Setup

1. In order to run the code you first need to install all the Python dependencies listed in `requirements.txt`. You can do that with pip (works only with Python 3):
    ```
    pip install -r requirements.txt
    ```

2. Or if you use the [Miniconda](https://docs.conda.io/en/latest/miniconda.html) environment manager you can just run the following to create an environment with Python 3.6.7 and all required dependencies (replace `ENV_NAME` with whatever name you'd like):
    ```
    conda env create -f environment.yml --name ENV_NAME
    ```
    And then activate it with:
    ```
    conda activate ENV_NAME
    ```

### Usage

Refer to [programs/Automatize_Sample_Code.ipynb](./programs/Automatize_Sample_Code.ipynb) for usage examples.