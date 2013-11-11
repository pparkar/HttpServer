package http;

import java.util.concurrent.Callable;

public class Client implements Callable<Response>{
    Request request = null;
    Response response = null;
    ClientCallback callback = null;
    
    public Client(Request request, ClientCallback callback){
        this.request = request;
        this.callback = callback;
    }

    @Override
    public Response call() throws Exception {
//        while(request.isKeepAlive()){
        // create Response object
        response = new Response(request.getClientSocket().getOutputStream());
        response.setRequest(request);
        response.sendStaticResource();
//        request.getClientSocket().close();
        
        return response;
//        }
    }
    
    public ClientCallback getCallback(){
        return callback;
    }
    
    public Request getRequest(){
        return request;
    }
    
    public Response getResponse(){
        return response;
    }
}
