package fraud.net;

import java.io.*;
import java.net.*;

/**
 * TCP Client for querying case status from the server.
 * Demonstrates: Socket programming, client-side TCP, character streams, try-with-resources.
 */
public class CaseStatusClient {

    private final String host;
    private final int port;

    public CaseStatusClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Query the server for a case's status.
     * Demonstrates: Socket, BufferedReader/PrintWriter over network streams.
     *
     * @param caseId the case ID to query
     * @return the server's response
     */
    public String queryCaseStatus(String caseId) {
        StringBuilder response = new StringBuilder();

        // Demonstrates: try-with-resources with TCP socket
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 5000); // 5s connect timeout
            socket.setSoTimeout(5000); // 5s read timeout

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            // Read welcome message
            String line;
            while ((line = in.readLine()) != null) {
                if (line.contains("Enter a Case ID")) break;
                response.append(line).append("\n");
            }

            // Send case ID query
            out.println(caseId);

            // Read response
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
                if (line.contains("--- End ---") || line.contains("Enter another")) break;
            }

            // Send quit
            out.println("quit");

        } catch (ConnectException e) {
            response.append("ERROR: Cannot connect to server at ").append(host).append(":").append(port)
                    .append(". Is the server running?");
        } catch (SocketTimeoutException e) {
            response.append("ERROR: Connection timed out.");
        } catch (IOException e) {
            response.append("ERROR: ").append(e.getMessage());
        }

        return response.toString();
    }

    /**
     * Check if the server is reachable.
     */
    public boolean isServerAvailable() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 2000);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
