package http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 
 * @author pparkar
 * Multithreaded http server which will handle incoming client request
 */
public class Server implements Runnable {
    private static boolean shutdown = false;
    public static ServerSocket serverSocket = null;
    ClientPool clientThreadPool = null;
    private static int DEFAULT_SERVER_PORT=8080;
    private static int serverPort;
    private static String serverIP; 
    
    public Server() {
        // 1. Start a client thread pool
        clientThreadPool = new ClientPool();
    }

    @Override
    public void run() {
        System.out.println("Server is running on port 8080");
        while (!shutdown) {
            try {
                Socket clientSocket = serverSocket.accept();
                Request request = new Request(clientSocket);

                // check if admin has issued a shutdown command
                shutdown = request.isShutdown();

                if(shutdown == false){
                    ClientFuture future = clientThreadPool.submit(request, new ClientCallback());
                }
            } catch (Exception e) {
                System.out.println("505..server has stopped running");
                e.printStackTrace();
            }
        }
        // kill all any open client connection
        killClientConnections();
        System.out.println("Server is shutdown");
    }

    private static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void killClientConnections() {
        System.out.println("Killing all open client connections");
        if (clientThreadPool != null) {
            clientThreadPool.shutdown();
        }
    }

    private static int getServerPort(String[] args){
        int serverPort = DEFAULT_SERVER_PORT;
        try{
            serverPort = Integer.parseInt(args[0]);
        }catch(Exception e){
            //ignore, assign 8080 as the default server port
        }
        return serverPort;
    }
    
    public static int getServerPort(){
        return serverPort;
    }
    
    public static String getServerIP(){
        return serverIP;
    }
    
    public static void main(String[] args) {
        try {
            serverPort = getServerPort(args);
            Server httpServer = new Server();

            // Open a server socket to accept incoming request
            //1. If you want to test the server locally
            serverSocket = new ServerSocket(serverPort, 1, InetAddress.getByName("localhost"));
            
            //2. If you want to test the server over network
            // serverSocket = new ServerSocket(serverPort);
            
            serverIP = serverSocket.getInetAddress().getHostAddress();

            // start the http server
            Thread serverThread = new Thread(httpServer);
            serverThread.start();
        } catch (IOException e) {
            System.out.println("Fatal Server Error!!!!");
            e.printStackTrace();
        }
    }
}
