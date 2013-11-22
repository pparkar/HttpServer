package http;

import java.net.Socket;
import java.util.concurrent.FutureTask;

public class ClientFuture extends FutureTask<ClientThreadStatus>{
    private Client callable = null;
    
    public ClientFuture(Client callable) {
        super(callable);
        this.callable = callable;
    }

    public Client getCallable(){
        return callable;
    }
    
    public void initiateResponse(){
        //System.out.println("Debug: Callback event started");
        Socket clientSocket = callable.getRequest().getClientSocket();
        System.out.println("Initiating dedicated serverThread to manage client "+clientSocket.toString());
        callable.getCallback().processClientBeginRequest(callable);
    }
    
    public void endResponse(){
        callable.getCallback().processClientEndRequest(callable.getRequest());
        Socket clientSocket = callable.getRequest().getClientSocket();
        System.out.println("Stopping dedicated serverThread to manage client "+clientSocket.toString());
        //System.out.println("Debug: Callback event ended");
    }
}
