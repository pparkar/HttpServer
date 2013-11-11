package http;

import java.io.IOException;

public class ClientCallback {
    public void processClientBeginRequest(Client client){
        System.out.println("Inside callback: begin client request "+client.getRequest().isKeepAlive());
        if(client.getRequest().isKeepAlive()){
            System.out.println("Resuing same client socket "+client.getRequest().getClientSocket().getPort()+" for client "+client.getRequest().getClientID());
        }
    }
    
    public void processClientEndRequest(Request request){
        System.out.println("Inside callback: end client request "+request.isKeepAlive());
        try {
//            while(request.isKeepAlive() || !request.isTimeout()){
//                Thread.sleep(60000); //default keepAlive time to 60sec before closing client socket
//            }
            request.getClientSocket().close();
        } catch (IOException e) {
            System.out.println("ClientCallback:processClientEndRequest");
            e.printStackTrace();
        }
    }
}
