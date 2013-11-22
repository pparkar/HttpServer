package http;

import java.io.IOException;

public class ClientCallback {
    public void processClientBeginRequest(Client client){
        //System.out.println("Inside callback: begin client request");
        if(client.getRequest().isKeepAlive()){
            System.out.println("Resuing same client socket "+client.getRequest().getClientSocket().getPort()+" for client "+client.getRequest().getClientID());
        }
    }
    
    public void processClientEndRequest(Request request){
        //System.out.println("Inside callback: end client request");
        String clientSocket = request.getClientSocket().toString();
        try {
            request.getClientSocket().close();
            System.out.println("Closing client socket "+clientSocket);
        } catch (IOException e) {
            System.out.println("ClientCallback:processClientEndRequest");
            e.printStackTrace();
        }
    }
}
