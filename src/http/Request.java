package http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class Request {
    Socket clientSocket = null;
    String hostname = null;
    String uri = null;
    boolean keepAlive = false;

    private final String SHUTDOWN = "/SHUTDOWN";

    public Request(Socket clientSocket) {
        this.clientSocket = clientSocket;
        parse();
    }

    public void parse() {
        // Read a set of characters from the socket
        StringBuffer request = new StringBuffer(2048);
        int i;
        byte[] buffer = new byte[2048];

        try {
            InputStream input = clientSocket.getInputStream();
            i = input.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            i = -1;
        }

        for (int j = 0; j < i; j++) {
            request.append((char) buffer[j]);
        }
        
        uri = parseUri(request.toString());
        setKeepAlive();
    }

    private String parseUri(String requestString) {
//        System.out.println("***Request===>"+requestString);
        System.out.println("Server started listening for client on port "+clientSocket.getPort());
        int index1, index2;
        index1 = requestString.indexOf(' ');

        if (index1 != -1) {
            index2 = requestString.indexOf(' ', index1 + 1);
            if (index2 > index1)
                return requestString.substring(index1 + 1, index2);
        }

        return null;
    }

    public String getUri() {
        return uri;
    }
    
    public void setKeepAlive(){
        String keepAliveStr = uri; //EG:  "/?keepAlive=on"
        int index = keepAliveStr.indexOf("=");
        
        if(index > 0){
            keepAliveStr = keepAliveStr.substring(index);
            keepAlive = keepAliveStr.equals("on");
        }
    }
    
    public boolean isKeepAlive(){
        return keepAlive;
    }
    
    public Socket getClientSocket(){
        return clientSocket;
    }

    public String getClientID(){
        return hostname;
    }
    
    public boolean isShutdown() {
        boolean shutdown = uri.equals(SHUTDOWN);
        if(shutdown){
            System.out.println("Server shutdown in progress.....");
        }
        return shutdown;
    }
}
