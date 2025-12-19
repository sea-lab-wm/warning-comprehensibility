## get the current working directory
import os
ROOT_PATH = os.getcwd()

import nmi_features as nmi
import create_nm_data as nm
import create_itid_data as itid
import feature_merger as fm


## 2.run nmi_features.py to get NMI(avg) and NMI(max) features
nmi.main(ROOT_PATH)

# # ## 3.run create_itid_data.py to get ITID(avg) and ITID(max) features
itid.main(ROOT_PATH)

# # ## 4. run create_nm_date.py to get NM(avg) and NM(max) features
nm.main(ROOT_PATH)

# ## 5. feature_merger.py to merge all the features
fm.main(ROOT_PATH)