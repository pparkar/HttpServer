package http;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.Callable;

/**
 * 
 * @author pparkar
 * Server thread that will manage communication with a specific client
 * The thread is active until the client initiate a 'quit' request
 */
public class Client implements Callable<ClientThreadStatus> {
    Request request = null;
    Response response = null;
    ClientCallback callback = null;
    ClientThreadStatus status = null;

    public Client(Request request, ClientCallback callback) {
        this.request = request;
        this.callback = callback;
    }
    
    private boolean ignoreInput(String input) {
        return input.equals("Host: "+Server.getServerIP()+":"+Server.getServerPort())
                || input.startsWith("User-Agent")
                || input.startsWith("Accept")
                || input.startsWith("Accept")
                || input.startsWith("Accept-Encoding")
                || input.startsWith("Referer")
                || input.startsWith("Connection")
                || input.isEmpty();
    }

    @Override
    public ClientThreadStatus call() throws Exception {
        Socket clientSocket = request.getClientSocket();
        status = new ClientThreadStatus(clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort());
        try (
                OutputStream out = clientSocket.getOutputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {

            Response response = new Response(request.getUri(), out);
            out.write(response.output.getBytes());
            // out.flush();

            String inputLine;
            // loops until socket is alive
            while ((inputLine = in.readLine()) != null) {
                // System.out.println("socket status--->"+clientSocket.getKeepAlive()+" "+clientSocket.isBound()+" "+clientSocket.isClosed()+" "+clientSocket.isConnected()+" "+clientSocket.isInputShutdown()+" "+clientSocket.isOutputShutdown());
                // System.out.println("$$$$$$" + inputLine);
                if (ignoreInput(inputLine))
                    continue;
                request = new Request(inputLine, clientSocket);
                Response response2 = new Response(request.getUri(), out);
                out.write(response2.output.getBytes());
                // out.flush();

                if (request.isQuit())
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            close(clientSocket);
            System.out.println("****Client thread ended abruptly");
        }
        return status;
    }

    private void close(Closeable stream) {
        try {
            stream.close();
        } catch (IOException e) {
            System.out.println("Error while closing client socket" + ((Socket) stream).getPort());
            e.printStackTrace();
        }
    }

    public ClientCallback getCallback() {
        return callback;
    }

    public Request getRequest() {
        return request;
    }

    public Response getResponse() {
        return response;
    }
    
    public ClientThreadStatus getStatus(){
        return status;
    }
}
