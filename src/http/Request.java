package http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * 
 * @author pparkar
 * Represents incoming client request
 */
public class Request {
    Socket clientSocket = null;
    String hostname = null;
    String uri = null;
    boolean keepAlive = false;
    boolean quit = false;

    private final String SHUTDOWN = "/SHUTDOWN";

    public Request(Socket clientSocket) {
        this.clientSocket = clientSocket;
        parse();
    }
    
    public Request(String input){
        if(input!=null)
            parse(input);
        else
            System.out.println("request came as NULL");
    }

    public Request(String input, Socket clientSocket){
        this.clientSocket = clientSocket;
        if(input!=null)
            parse(input);
        else
            System.out.println("request came as NULL");
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
        parse(request.toString());
    }
    
    private void parse(String input){
        uri = parseUri(input);
        if(uri != null){
            setKeepAlive();
            setQuitFlag();
        }
    }

    private String parseUri(String requestString) {
//        System.out.println("***Request===>"+requestString);
//        System.out.println("Accepted connection from client "+clientSocket.getInetAddress()+":"+clientSocket.getPort());
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
    
    /**
     * Overrides browser's keepalive with explicit form post values, default value=false
     */
    public void setKeepAlive(){
        String keepAliveStr = uri; //EG:  "/?keepAlive=on"
        if(keepAliveStr.indexOf("keepAlive") > 0){
            int index = keepAliveStr.indexOf("=");
            
            if(index > 0){
                keepAliveStr = keepAliveStr.substring(index+1);
                keepAlive = keepAliveStr.equals("on");
            }
        }
    }
    
    public void setQuitFlag(){
        String quitStr = uri; //EG:  "/?quit=on"
        if(quitStr.indexOf("quit") > 0){
            int index = quitStr.indexOf("=");
            
            if(index > 0){
                quitStr = quitStr.substring(index+1);
                quit = quitStr.equals("on");
            }
        }
    }
    
    public boolean isKeepAlive(){
        return keepAlive;
    }
    
    public boolean isQuit(){
        return quit;
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
