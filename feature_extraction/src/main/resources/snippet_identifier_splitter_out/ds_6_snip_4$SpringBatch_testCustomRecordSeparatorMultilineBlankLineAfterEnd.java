read reader assertEquals 
isEndOfRecord line String 
read reader assertEquals 
line hasText StringUtils 
pair pair 
String preProcess record String 
record 
pair 
testCustomRecordSeparatorMultilineBlankLineAfterEnd Exception 
RecordSeparatorPolicy setRecordSeparatorPolicy reader 
pair 
getInputResource setResource reader 
String postProcess record String 
executionContext open reader 
record hasText StringUtils record 
