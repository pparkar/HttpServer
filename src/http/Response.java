package http;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class Response {
    OutputStream out = null;
    String output = null;
    String input = null;

    //constant
    private final String SHUTDOWN = "/SHUTDOWN";
    
    private enum RequestType {
        LOAD, CONTINUE, SUBMIT, CLOSE, UNKNOWN
    };
    
    public Response(String input, OutputStream out){
        this.input = input;
        this.out = out;
        sendStaticResource();
    }

    private RequestType getType(String uri) {
        if(uri == null)
            return RequestType.UNKNOWN;
        
        if (uri.equals("/"))
            return RequestType.LOAD;
        else if (uri.equals("/?keepAlive=on"))
            return RequestType.SUBMIT;
        else if(uri.equals("Host: localhost:8010"))
            return RequestType.CONTINUE;
        else if (uri.equals("/?quit=on"))
            return RequestType.CLOSE;
        else if(uri.equals(SHUTDOWN))
            return RequestType.CLOSE;
        else
            return RequestType.UNKNOWN;
    }

    public void sendStaticResource() {
        System.out.println("Incoming Request===>" + input);
        try {
            RequestType type = getType(input);
            switch (type) {
            case LOAD:
                loadWelcomePage();
                break;
                
            case SUBMIT:
                output = "<h1>Congratulations, your KeepAlive socket is still in use!!</h1>";
                output += readWelcomeFile();
                String headerMessage = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/html\r\n" +
                        "Content-Length: "+(output.length()-1)+"\r\n"+
                        "\r\n";
                out.write(headerMessage.getBytes());
                break;

            case CONTINUE:
                output += readWelcomeFile();
                String headerMessage1 = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/html\r\n" +
                        "Content-Length: "+(output.length()-1)+"\r\n"+
                        "\r\n";
                out.write(headerMessage1.getBytes());
                break;
                
            case CLOSE:
                output = "<h1>Your request to close the KeepAlive socket connection succeeded!!</h1>";
                String headerMessage2 = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/html\r\n" +
                        "Content-Length: "+(output.length()-1)+"\r\n"+
                        "\r\n";
                out.write(headerMessage2.getBytes());
                break;
                
            default:
                // error message
                output = "<h1>404 Not Found</h1>";
                output += readWelcomeFile();
                output += "<p>Redirecting to welcome page</p>";
                String defaultMessage = "HTTP/1.1 400 Bad Request\r\n" +
                        "Content-Type: text/html\r\n" +
                        "Content-Length: "+(output.length()-1)+"\r\n"+
                        "\r\n";
                out.write(defaultMessage.getBytes());                        
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    private void loadWelcomePage() {
        //System.out.println("success in loading welcome page!!");
        try {
            String fileContent = readWelcomeFile();
            
            String headerMessage = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: text/html\r\n" +
                    "Content-Length: "+(fileContent.length()-1)+"\r\n"
                    +"\r\n";
            out.write(headerMessage.getBytes());            output += fileContent+"\r\n";

        } catch (Exception e) {
            e.printStackTrace();
            output += "Server is down, please try again later!!";
        }
    }
    private String readWelcomeFile() throws IOException{
        return readFile(Response.class, "webapp/welcome.html", true);        
    }
    

    private String readFile(Class<?> srcClass, String filePath, boolean addNewLine) throws IOException {
        ClassLoader classLoader = srcClass.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(filePath);
        BufferedReader buf = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        StringBuilder result = new StringBuilder();
        while ((line = buf.readLine()) != null) {
            result.append(line.trim());
            if (addNewLine) {
                result.append("\n");
            }
        }
        return result.toString();
    }

    private void loadErrorPage() {
        try {
            String fileContent = readFile(Response.class, "webapp/error.html", true);
            output += fileContent;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void close(Closeable io) {
        try {
            if (io != null)
                io.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
