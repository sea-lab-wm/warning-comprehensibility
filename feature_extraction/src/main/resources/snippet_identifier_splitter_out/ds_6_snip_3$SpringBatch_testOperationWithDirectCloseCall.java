close con 
con1_2 con1 assertSame 
getConnection csds con2 Connection 
con2 con1 assertNotSame 
ds ExtendedConnectionDataSourceProxy csds ExtendedConnectionDataSourceProxy 
con1 stopCloseSuppression csds 
con1 shouldClose csds assertTrue 
getConnection csds con1 Connection 
con1_1 
con1 startCloseSuppression csds 
con1_2 
getConnection csds con1_1 Connection 
close con1 
con1_1 con1 assertSame 
testOperationWithDirectCloseCall SQLException 
Connection mock con Connection 
DataSource mock ds DataSource 
con thenReturn getConnection ds when 
close con 
con2 shouldClose csds assertTrue 
close con1_1 
close con2 
con thenReturn getConnection ds when 
getConnection csds con1_2 Connection 
