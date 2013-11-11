package http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

public class Server implements Runnable {
    private static boolean shutdown = false;
    private static ServerSocket serverSocket = null;
    ClientPool clientThreadPool = null;
    HashSet<Client> cachedClient = new HashSet<>();

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
//                    future.endResponse();
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

    public static void main(String[] args) {
        try {
            Server httpServer = new Server();

            // Open a server socket to accept incoming request
            serverSocket = new ServerSocket(8080, 1, InetAddress.getByName("localhost"));

            // start the http server
            Thread serverThread = new Thread(httpServer);
            serverThread.start();
        } catch (IOException e) {
            System.out.println("Fatal Server Error!!!!");
            e.printStackTrace();
        }
    }
}
