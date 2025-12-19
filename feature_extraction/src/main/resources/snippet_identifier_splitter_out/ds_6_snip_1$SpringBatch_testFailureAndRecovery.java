convertAndSend jmsTemplate 
RecoveryCallback Message recoveryCallback RecoveryCallback Message 
SECONDS TimeUnit poll processed assertEquals 
SECONDS TimeUnit poll recovered assertEquals 
Message recover context RetryContext 
getText msg TextMessage add recovered 
e JMSException 
e IllegalStateException 
msg 
RetryCallback Message Exception callback RetryCallback Message Exception 
Message doWithRetry context RetryContext Exception 
callback recoveryCallback getJMSMessageID msg DefaultRetryState execute retryTemplate 
e Exception 
getText msg TextMessage add processed 
e RuntimeException 
e JMSException 
e IllegalStateException 
testFailureAndRecovery Exception 
RetryTemplate retryTemplate RetryTemplate 
NeverRetryPolicy setRetryPolicy retryTemplate 
MessageListener setMessageListener container 
onMessage msg Message 
msg RuntimeException 
initializeProxy container 
start container 
