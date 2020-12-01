import sys, os
sys.path.insert(0, os.path.abspath(os.path.join('.')))
from automatize.analysis import def_random_seed, ACC4All

def_random_seed(random_num=1, seed_num=1)

if len(sys.argv) < 2:
    print('Please run as:')
    print('\tpython acc4all.py', 'res_path', 'prefix')
    print('OR as:')
    print('\tpython acc4all.py', 'res_path', 'prefix', 'save_results', 'modelfolder')
    exit()
    
    
res_path = sys.argv[1]
prefix   = sys.argv[2]

save_results = True
modelfolder='model'

if len(sys.argv) > 3:
    save_results = sys.argv[3]
    modelfolder  = sys.argv[4]

print('Starting analysis in: ', res_path, prefix)
ACC4All(res_path, prefix, save_results, modelfolder)