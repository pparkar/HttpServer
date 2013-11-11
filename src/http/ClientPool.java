package http;

import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author pparkar
 * Create a client pool with or without socket reuse ability
 */
public class ClientPool extends ThreadPoolExecutor{
    public ClientPool(){
        super(100, 150, 60000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }
    
    public ClientFuture submit(Request request, ClientCallback callback){
        Client client = new Client(request, callback);
        return (ClientFuture)submit(client);
    }
    
    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        Client client = (Client)callable;
        return (RunnableFuture<T>)new ClientFuture(client);
    }
    
    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        ClientFuture future = (ClientFuture)r;
        Client client = future.getCallable();
        Response response = client.getResponse();
        
        future.initiateResponse();
    }
    
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        ClientFuture future = (ClientFuture)r;
        Client client = future.getCallable();
        Response response = client.getResponse();
        
        future.endResponse();
    }
}
