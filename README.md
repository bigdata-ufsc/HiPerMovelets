# HIPERMovelets

Source code of the paper **HIPERMovelets: a greedy approach for efficient movelet extraction and dimensionality reduction**, accepted for publication in ...

\[ [publication](https://#) ] \[ [preprint](./reference/preprint.pdf) ] \[ [bibtex](./reference/bibliography.bib) ]


## Versions


This is a project with the HIPERMovelets (Portela, 2020) implementation, with three options of optimizations.


- *HIPERMovelets*: new optimization for MASTERMovelets, with greedy search (`-version hiper`).
- *HIPERMovelets-Log*: plus, limits the movelets size to the ln size of the trajectory (`-version hiper -Ms -3`).


- *HIPERPivots*: limits the movelets search space to the points that are neighbour of well qualified movelets of size one (`-version hiper-pvt`).
- *HIPERPivots-Log*: plus, limits the movelets size to the ln size of the trajectory (`-version hiper-pvt -Ms -3`).


**Includes re-implementations of the following methods (NOT EQUAL!):**


- *MASTERMovelets*: limits the movelets size to the ln size of the trajectory (`-version 2.0`).
- *MASTERMovelets-Log*: limits the movelets size to the ln size of the trajectory (`-version 2.0 -Ms -3`).


- *MASTERMovelets-Pivots*: it limit the movelets search space to the points that are neighbour of well qualified movelets of size one (`-version pivots`).
- *MASTERMovelets-Pivots-Log*: it limit the movelets search space to the points that are neighbour of well qualified movelets of size one (`-version pivots -Ms -3`).


- *SUPERMovelets*: a optimized approach that identifies the better regions for finding movelets (`-version super`).
- *SUPERMovelets-Log*: a optimized approach that identifies the better regions for finding movelets (`-version super -Ms -3`).


## Setup

A. In order to run the code you first need to install Java 8 (or superior). Be sure to have enough RAM memory available. 


------

(Optional) 

B. If you opt to use the test automatization in Python (under [dist/automatize](./dist/automatize/) folder), you first need to install Python, R, and dependencies. 

See [requirements.txt](./dist/automatize/requirements.txt) to Python dependencies. To install all dependencies you can use:

```Shell
pip install -r ./dist/automatize/requirements.txt
```

Install R, and the dependent packages. To enter the R environment:

```Shell
sudo apt-get install r-base
R
```

Install `data.table` and `dplyr` packages, and exit.

```R
install.packages("data.table")
install.packages("dplyr")
q()
```

## Usage

### 1. You can run the HIPERMovelets with the following command:

```Shell
-curpath "$BASIC_PATH" 
-respath "$RESULT_PATH" 
-descfile "$DESC_FILE"  
-version hiper
-nt 8
```


Where:
- `BASIC_PATH`: The path for the input CSV training and test files.
- `RESULT_PATH`: The destination folder for CSV results files.
- `DESC_FILE`: Path for the descriptor file. File that describes the dataset attributes and similarity measures.
- `-version`: Method to run (hiper, hiper-pvt, ...)
- `-nt`: Number of threads

    
### 2. For instance:

To run the HIPERMovelets you can run the java code with the following default entries as example:


```Shell
java -Xmx80G -jar HIPERMovelets.jar 
-curpath "$BASIC_PATH" -respath "$RESULT_PATH" -descfile "$DESC_FILE" 
-version hiper -nt 8 -ed true -samples 1 -sampleSize 0.5 -medium "none" -output "discrete" -lowm "false" -ms 1 -Ms -3 | tee -a "output.txt"
```


This will run with 80G memory limit, 8 threads, and save the output to the file output.txt`. 

It is the same as (without the output file):


```Shell
java -Xmx80G -jar HIPERMovelets.jar 
-curpath "$BASIC_PATH" -respath "$RESULT_PATH" -descfile "$DESC_FILE" 
-version hiper -nt 8
```

### Examples

**HIPERMovelets**


```Shell
java -jar HIPERMovelets.jar 
-curpath "$BASIC_PATH" -respath "$RESULT_PATH" -descfile "$DESC_FILE" 
-version hiper -nt 8 
```

**HIPERMovelets-Log**


```Shell
java -jar HIPERMovelets.jar 
-curpath "$BASIC_PATH" -respath "$RESULT_PATH" -descfile "$DESC_FILE" 
-version hiper -nt 8 -Ms -3
```

**HIPERMovelets-Pivots**


```Shell
java -jar HIPERMovelets.jar 
-curpath "$BASIC_PATH" -respath "$RESULT_PATH" -descfile "$DESC_FILE" 
-version hiper-pvt -nt 8 
```

**HIPERMovelets-Pivots-Log**


```Shell
java -jar HIPERMovelets.jar 
-curpath "$BASIC_PATH" -respath "$RESULT_PATH" -descfile "$DESC_FILE" 
-version hiper-pvt -nt 8 -Ms -3
```


## Extras

To run HIPERMovelts and classification methods with Python automatization ([automatize](./dist/automatize/)) see the exemaples in the jupyter notebook: [Automatize - Sample Code.ipynb](./dist/Automatize_Sample_Code.ipynb)
