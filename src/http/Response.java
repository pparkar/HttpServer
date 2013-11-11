package http;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class Response {
    OutputStream output = null;
    Request request = null;

    private enum RequestType {
        LOAD, SUBMIT, UNKNOWN
    };

    public Response(OutputStream output) {
        this.output = output;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    private RequestType getType(String uri) {
        if (uri.equals("/"))
            return RequestType.LOAD;
        else if (uri.equals("/?keepAlive=on"))
            return RequestType.SUBMIT;
        else
            return RequestType.UNKNOWN;
    }

    public void sendStaticResource() throws IOException {
        System.out.println("Incoming Request===>" + request.getUri());
        try {
            RequestType type = getType(request.getUri());
            switch (type) {
            case LOAD:
                loadWelcomePage();
                break;
            case SUBMIT:
                output.write("you hve succesfully submitted request to reuse the sme socket connection for future request".getBytes());
                break;
            default:
                // error message
                String defaultMessage = "HTTP/1.1 400 Bad Request\r\n" +
                        "Content-Type: text/html\r\n" +
                        "Content-Length: 25\r\n" +
                        "\r\n" +
                        "<h1>400 Invalid Request</h1>";
                output.write(defaultMessage.getBytes());
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    private void loadWelcomePage() {
        try {
            String headerMessage = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: text/html\r\n" +
                    "Content-Length: 1000\r\n";
            // "\r\n" +
            // "<h1>400 Invalid Request</h1>";
            output.write(headerMessage.getBytes());

            String fileContent = readFile(Response.class, "webapp/welcome.html", true);
            output.write(fileContent.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            try {
                output.write("Server is down, please try again later!!".getBytes());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
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
            output.write(fileContent.getBytes());
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
