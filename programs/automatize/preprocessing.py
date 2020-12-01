'''
Created on Jun, 2020

@author: Tarlis Portela
'''
# --------------------------------------------------------------------------------
# PREPROCESSOR - DATASETS
import os
from zipfile import ZipFile
import pandas as pd
import numpy as np 
import glob2
import random
from sklearn.model_selection import train_test_split
from sklearn.model_selection import KFold
import matplotlib.pyplot as plt
# --------------------------------------------------------------------------------


def readData_Folders_File(path, col_names, file_ext='.txt', delimiter=',', file_prefix=''):
    filelist = []
    filesList = []

    # 1: Build up list of files:
    for files in glob2.glob(os.path.join(path, '**/*'+file_ext )):
        fileName, fileExtension = os.path.splitext(files)
        filelist.append(fileName) #filename without extension
        filesList.append(files) #filename with extension
    
    # 2: Create and concatenate in a DF:
    ct = 1
    df = pd.DataFrame()
    for ijk in filesList:
        frame = pd.read_csv(ijk, names=col_names, sep=delimiter)
        s = ijk[len(folder):-4].split("/")
        
        fname = s[0][:-3]
        
        frame['id']    = s[1]
        frame['tid']   = ct
        frame['label'] = str(fname).replace(file_prefix, '')
        frame['time']  = frame.index +1
        df = pd.concat([df,frame])
        ct += 1
#     df['t'] = df.index +1
#     df = df[['time', 'signal', 'tid', 'id', 'label']]
#     df.reset_index(level=0, inplace=True)
    return df

def readData_Files(path, col_names, file_ext='.txt', delimiter=',', file_prefix=''):    
    filelist = []
    filesList = []

    # 1: Build up list of files:
    for files in glob2.glob(os.path.join(path, '*'+file_ext)):
        fileName, fileExtension = os.path.splitext(files)
        filelist.append(fileName) #filename without extension
        filesList.append(files) #filename with extension
    
    # 2: Create and concatenate in a DF:
    ct = 1
    df = pd.DataFrame()
    for ijk in filesList:
        frame = pd.read_csv(ijk, names=col_names, sep=delimiter)
        
        fname = os.path.basename(ijk)[:-4]
        frame['label'] = str(fname).replace(file_prefix, '')
        
        #2.1: split by activity:
        ls = splitframe(frame, 'activity_id')
        for frameAux in ls:
            frameAux['tid']   = ct
            ct += 1
            frameAux.insert(0, 'time', range(1, 1 + len(frameAux)))
            df = pd.concat([df,frameAux])
    
    df.reset_index()
    
    return df
# ----------------------------------------------------------------------------->>


# --------------------------------------------------------------------------------
def printFeaturesJSON(df, version=1, deftype='nominal', defcomparator='equals'):
    
    if isinstance(df, list):
        cols = df
    else:
        cols = df.columns
            
    if version == 1:
        s = '{\n   "readsDesc": [\n'

        order = 1
        for f in cols:
            s += ('	{\n          "order": '+str(order)+',\n          "type": "'+deftype+'",\n          "text": "'+f+'"\n        }')
            if len(cols) == order:
                s += ('\n')
            else:
                s += (',\n')
            order += 1

        s += ('      ],\n    "pointFeaturesDesc": [\n      ],\n    "subtrajectoryFeaturesDesc": [\n	  ],\n')
        s += ('    "trajectoryFeaturesDesc": [\n      ],\n    "pointComparisonDesc": {\n      "pointDistance": "euclidean",\n')
        s += ('      "featureComparisonDesc": [\n')

        order = 1
        for f in cols:
            if f != 'tid' and f != 'label':
                s += ('			{\n			  "distance": "'+defcomparator+'",\n			  "maxValue": -1,\n			  "text": "'+f+'"\n			}')
                if len(cols) == order:
                    s += ('\n')
                else:
                    s += (',\n')
            order += 1

        s += ('		]\n    },\n    "subtrajectoryComparisonDesc": {\n      "subtrajectoryDistance": "euclidean",\n')
        s += ('      "featureComparisonDesc": [\n			{\n			  "distance": "euclidean",\n			  "text": "points"\n')
        s += ('			}\n		]\n    }\n}')
    else:        
        s  = '{\n   "input": {\n          "train": ["train"],\n          "test": ["test"],\n          "format": "CZIP",\n'
        s += '          "loader": "interning"\n   },\n'
        s += '   "idFeature": {\n          "order": '+str(cols.index('tid')+1)+',\n          "type": "numeric",\n          "text": "tid"\n    },\n'
        s += '   "labelFeature": {\n          "order": '+str(cols.index('label')+1)+',\n          "type": "nominal",\n          "text": "label"\n    },\n'
        s += '   "attributes": [\n'
        
        order = 1
        for f in cols:
            if f != 'tid' and f != 'label':
                s += '	    {\n	          "order": '+str(order)+',\n	          "type": "'+deftype+'",\n	          "text": "'+str(f)+'",\n	          "comparator": {\n	            "distance": "'+defcomparator+'"\n	          }\n	    }'
                if len(cols) == order:
                    s += ('\n')
                else:
                    s += (',\n')
            order += 1
        s += '	]\n}'
        
    print(s)
    
#-------------------------------------------------------------------------->>
def countClasses(data_path, folder, file, class_col = 'label'):
    df = pd.read_csv(os.path.join(data_path, folder, file))
    group = df.groupby([class_col, 'tid'])
    df2 = group.apply(lambda x: x[class_col].unique())
    print(file + " Samples: " + str(len(df['tid'].unique())))
    print(df2.value_counts())
    
    return df2.value_counts()


#-------------------------------------------------------------------------->>
def trainAndTestSplit(data_path, df, train_size=0.7, random_num=1, class_col='label'):
    train = pd.DataFrame()
    test = pd.DataFrame()
    for label in df[class_col].unique():
        
        tids = df.loc[df[class_col] == label].tid.unique()
        print(label)
        print(tids)
        
        random.seed(random_num)
        train_index = random.sample(list(tids), int(len(tids)*train_size))
        test_index  = tids[np.isin(tids, train_index, invert=True)] #np.delete(test_index, train_index)

        train = pd.concat([train,df.loc[df['tid'].isin(train_index)]])
        test  = pd.concat([test, df.loc[df['tid'].isin(test_index)]])

        print("Train samples: " + str(train.loc[train[class_col] == label].tid.unique()))
        print("Test samples: " + str(test.loc[test[class_col] == label].tid.unique()))
    
    # WRITE Train / Test Files >> FOR MASTERMovelets:
    createZIP(data_path, train, 'train', class_col)
    createZIP(data_path, test, 'test', class_col)

    # WRITE Train / Test Files >> FOR V3:
    train.to_csv(os.path.join(data_path, "train.csv"), index = False)
    test.to_csv(os.path.join(data_path, "test.csv"), index = False)
    
    return train, test

#-------------------------------------------------------------------------->>
def kfold_trainAndTestSplit(data_path, k, df, random_num=1, class_col='label', fileprefix='', 
                            columns_order=None, ktrain=None, ktest=None):
    print(str(k)+"-fold train and test split in... " + data_path)
    
    if not ktrain:
        ktrain = []
        ktest = []
        for x in range(k):
            ktrain.append( pd.DataFrame() )
            ktest.append( pd.DataFrame() )


        print("Reading files...")
        for label in df[class_col].unique(): 
            tids = df.loc[df[class_col] == label].tid.unique()
    #         print("Trajectory IDs for label: " + label)
    #         print(tids)

            kfold = KFold(k, True, random_num)
            x = 0
            for train_idx, test_idx in kfold.split(tids):
                ktrain[x] = pd.concat([ktrain[x], df.loc[df['tid'].isin(tids[train_idx])]])
                ktest[x]  = pd.concat([ktest[x],  df.loc[df['tid'].isin(tids[test_idx])]])
                x += 1
        print("Done.")
    else:
        print("Train and test files provided.")
    
    print("Writing files...")
    for x in range(k):
        path = 'run'+str(x+1)
        
        if not os.path.exists(os.path.join(data_path, path)):
            os.makedirs(os.path.join(data_path, path))
            
        if columns_order is not None:
            train_aux = ktrain[x][columns_order]
            test_aux  = ktest[x][columns_order]
        else:
            train_aux = ktrain[x]
            test_aux  = ktest[x]
            
        
        # WRITE ZIP Train / Test Files >> FOR MASTERMovelets:
        createZIP(data_path, ktrain[x], os.path.join(path, fileprefix+'train'), class_col, select_cols=columns_order)
        createZIP(data_path, ktest[x], os.path.join(path,  fileprefix+'test'), class_col, select_cols=columns_order)

        # WRITE CSV Train / Test Files >> FOR HIPERMovelets:
        train_aux.to_csv(os.path.join(data_path, path, fileprefix+"train.csv"), index = False)
        test_aux.to_csv(os.path.join(data_path, path,  fileprefix+"test.csv"), index = False)
    print("Done.")
    print(" --------------------------------------------------------------------------------")
    
    return ktrain, ktest
        
#-------------------------------------------------------------------------->>
def splitframe(data, name='tid'):

    n = data[name][0]

    df = pd.DataFrame(columns=data.columns)

    datalist = []

    for i in range(len(data)):
        if data[name][i] == n:
            df = df.append(data.iloc[i])
        else:
            datalist.append(df)
            df = pd.DataFrame(columns=data.columns)
            n = data[name][i]
            df = df.append(data.iloc[i])

    return datalist
    
#-------------------------------------------------------------------------->>
def createZIP(data_path, df, file, class_col='label', tid_col='tid', select_cols=None):
    EXT = '.r2'
    if not os.path.exists(data_path):
        os.makedirs(data_path)
    zipf = ZipFile(os.path.join(data_path, file+'.zip'), 'w')
    
    n = len(str(len(df.index)))
    tids = df[tid_col].unique()
    for x in tids:
        filename = str(x).rjust(n, '0') + ' s' + str(x) + ' c' + str(df.loc[df[tid_col] == x][class_col].iloc[0]) + EXT
        data = df[df.tid == x]
        if select_cols is not None:
            data = data[select_cols]
        data.to_csv(filename, index=False, header=False)
        zipf.write(filename)
        os.remove(filename)
    
    # close the Zip File
    zipf.close()
    
#-------------------------------------------------------------------------->>
def zip2csv(folder, file, cols, class_col = 'label'):
    data = pd.DataFrame()
    print("Converting "+file+" data from... " + folder)
    with ZipFile(os.path.join(folder, file+'.zip')) as z:
        for filename in z.namelist():
#             data = filename.readlines()
            df = pd.read_csv(z.open(filename), names=cols)
#             print(filename)
            df['tid']   = filename.split(" ")[1][1:]
            df[class_col] = filename.split(" ")[2][1:-3]
            data = pd.concat([data,df])
    print("Done.")
    
    print("Saving dataset as: " + os.path.join(folder, file+'.csv'))
    data.to_csv(os.path.join(folder, file+'.csv'), index = False)
    print("Done.")
    print(" --------------------------------------------------------------------------------")
    return data

def convertToCSV(path): 
    dir_path = os.path.dirname(os.path.realpath(path))
    files = [x for x in os.listdir(dir_path) if x.endswith('.csv')]

    for file in files:
        try:
            df = pd.read_csv(file, sep=';', header=None)
            print(df)
            df.drop(0, inplace=True)
            print(df)
            df.to_csv(os.path.join(folder, file), index=False, header=None)
        except:
            pass
        
def joinTrainAndTest(dir_path, cols, train_file="train.csv", test_file="test.csv", class_col = 'label'):
    print("Joining train and test data from... " + dir_path)
    
    # Read datasets
    if '.csv' in train_file:
        print("Reading train file...")
        dataset_train = pd.read_csv(os.path.join(dir_path, train_file))
    else:
        print("Converting train file...")
        dataset_train = zip2csv(dir_path, train_file, cols, class_col)
    print("Done.")
        
    if '.csv' in test_file:
        print("Reading test file...")
        dataset_test  = pd.read_csv(os.path.join(dir_path, test_file))
    else:
        print("Converting test file...")
        dataset_test = zip2csv(dir_path, test_file, cols, class_col)
    print("Done.")
        
    print("Saving joined dataset as: " + os.path.join(dir_path, 'joined.csv'))
    dataset = pd.concat([dataset_train, dataset_test])

    dataset.sort_values([class_col, 'tid'])
    
    dataset.to_csv(os.path.join(dir_path, 'joined.csv'), index=False)
    print("Done.")
    print(" --------------------------------------------------------------------------------")
    
    return dataset