package http;

import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author pparkar
 * Create a client pool with http keepAlive supported
 */
public class ClientPool extends ThreadPoolExecutor{
    private static final int activeConnection = 2;
    private static final int maxConnection = 4;
    private static long timeToLive = 30000;
    private static TimeUnit timeToLiveUnit = TimeUnit.MILLISECONDS;
    
    public ClientPool(){
        super(activeConnection, maxConnection, timeToLive, timeToLiveUnit, new LinkedBlockingQueue<Runnable>());
    }
    
    public ClientFuture submit(Request request, ClientCallback callback){
        Client client = new Client(request, callback);
        if (getPoolSize() == activeConnection && getCorePoolSize() == maxConnection) {
            System.out.println("Server is busy ...cannot accept any new connection");
            return null;
        } else {
            return (ClientFuture)submit(client);
        }
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
        ClientThreadStatus status = client.getStatus();
        
        future.initiateResponse();
    }
    
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        ClientFuture future = (ClientFuture)r;
        Client client = future.getCallable();
        ClientThreadStatus status = client.getStatus();
        if(status != null){
            System.out.println("****Client Thread Status "+status);
        }
        
        future.endResponse();
    }
}
