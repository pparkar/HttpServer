package http;

public class ClientThreadStatus {
    String host = null;
    int port;
    int incomingRequestCount = 0;
    int successResponseCount = 0;
    int failureCount = 0;
    String errorMessage = new String();
            
    public ClientThreadStatus(String host, int port){
        this.host = host;
        this.port = port;
    }
    
    public ClientThreadStatus(){
        
    }
    
    public void incrementInputRequestCount(){
        incomingRequestCount++;
    }
    
    public void incrementSuccessCount(){
        successResponseCount++;
    }
    
    public void incrementFailureCount(){
        failureCount++;
    }
    
    public boolean isJobSucess(){
        return incomingRequestCount==successResponseCount;
    }
    
    public void addErrorMessage(String errorMsg){
        this.errorMessage += errorMsg;
    }
    
    public String getErrorMessage(){
        return errorMessage;
    }
    
    @Override
    public String toString() {
        return "ClientThreadStatus on exit: clientInfo="+host+":"+port+", incomingRequest="+incomingRequestCount+" ,successCount="+successResponseCount+" ,errorCount"+failureCount;
    }
}
