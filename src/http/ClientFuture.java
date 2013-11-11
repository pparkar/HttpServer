package http;

import java.util.concurrent.FutureTask;

public class ClientFuture extends FutureTask<Response>{
    private Client callable = null;
    
    public ClientFuture(Client callable) {
        super(callable);
        this.callable = callable;
    }

    public Client getCallable(){
        return callable;
    }
    
    public void initiateResponse(){
        System.out.println("Callback event started");
        callable.getCallback().processClientBeginRequest(callable);
    }
    
    public void endResponse(){
        callable.getCallback().processClientEndRequest(callable.getRequest());
        System.out.println("Callback event ended");
    }
}
