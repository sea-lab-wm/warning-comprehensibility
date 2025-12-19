SPLIT_TRANSACTION 
tag 
startTaskExecution 
TASK_SPLIT TaskExecutionFragment 
tag Object 
PRINT 
ManageCategories Intent i Intent 
getCurrentFragment tl TransactionList 
setAction i 
tl 
KEY_ACCOUNTID mAccountId putExtra i 
Bundle args Bundle 
tag 
KEY_FILTER TransactionList getFilterCriteria tl putSparseParcelableArray args 
tag Long year 
KEY_ROWID mAccountId putLong args 
tag Long groupingSecond 
beginTransaction getSupportFragmentManager 
a NONE Grouping grouping a putExtra i 
args TASK_PRINT newInstanceWithBundle TaskExecutionFragment add 
year putExtra i 
ASYNC_TAG ProtectionDelegate 
groupingSecond putExtra i 
contribFeatureCalled feature ContribFeature tag Serializable 
feature 
DISTRIBUTION 
mAccountId getInstanceFromDb Account a Account 
feature recordUsage 
progress_dialog_printing string R newInstance ProgressDialogFragment PROGRESS_TAG ProtectionDelegate add 
commit 
i startActivity 
