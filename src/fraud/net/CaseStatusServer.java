package fraud.net;

import fraud.model.FraudCase;
import fraud.service.CaseService;
import fraud.util.AuditLogger;

import java.io.*;
import java.net.*;

/**
 * TCP Server for retrieving case status remotely.
 * Demonstrates: TCP sockets (ServerSocket, Socket), byte streams, character streams,
 * multithreading (one thread per client), Runnable interface, try-with-resources.
 * 
 * Protocol: Client sends a case ID, server responds with case status details.
 */
public class CaseStatusServer implements Runnable {

    private final int port;
    private final CaseService caseService;
    private volatile boolean running;
    private ServerSocket serverSocket;

    public CaseStatusServer(int port, CaseService caseService) {
        this.port = port;
        this.caseService = caseService;
        this.running = false;
    }

    /**
     * Start the TCP server.
     * Demonstrates: Runnable interface, Thread creation.
     */
    @Override
    public void run() {
        running = true;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(2000); // 2s timeout for accept() — allows clean shutdown
            AuditLogger.getInstance().log("System", "SERVER_STARTED",
                    "TCP Case Status Server started on port " + port);
            System.out.println("[TCP Server] Case Status Server started on port " + port);

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    // Handle each client in a separate thread (demonstrates multithreading)
                    Thread clientThread = new Thread(new ClientHandler(clientSocket, caseService));
                    clientThread.setName("ClientHandler-" + clientSocket.getPort());
                    clientThread.start();
                } catch (SocketTimeoutException e) {
                    // Expected — continue loop to check running flag
                }
            }
        } catch (IOException e) {
            if (running) {
                System.err.println("[TCP Server] Error: " + e.getMessage());
            }
        } finally {
            stop();
        }
    }

    /** Stop the server gracefully. */
    public void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("[TCP Server] Error closing server: " + e.getMessage());
        }
        AuditLogger.getInstance().log("System", "SERVER_STOPPED", "TCP server stopped.");
    }

    public boolean isRunning() {
        return running;
    }

    public int getPort() {
        return port;
    }

    // === Inner class: ClientHandler ===

    /**
     * Handles a single client connection.
     * Demonstrates: Inner class, Runnable, character streams over TCP.
     */
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final CaseService caseService;

        ClientHandler(Socket clientSocket, CaseService caseService) {
            this.clientSocket = clientSocket;
            this.caseService = caseService;
        }

        @Override
        public void run() {
            // Demonstrates: try-with-resources with character streams over socket
            try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(
                        new OutputStreamWriter(clientSocket.getOutputStream()), true)) {

                out.println("=== Fraud Investigation Case Status Server ===");
                out.println("Enter a Case ID to query (or 'quit' to disconnect):");

                String input;
                while ((input = in.readLine()) != null) {
                    input = input.trim();
                    if (input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("exit")) {
                        out.println("Goodbye!");
                        break;
                    }
                    if (input.isEmpty()) {
                        out.println("Please enter a valid Case ID.");
                        continue;
                    }

                    FraudCase fc = caseService.findById(input);
                    if (fc == null) {
                        out.println("ERROR: Case '" + input + "' not found.");
                    } else {
                        // Demonstrates: StringBuilder for building response
                        StringBuilder response = new StringBuilder();
                        response.append("--- Case Details ---\n");
                        response.append("Case ID:      ").append(fc.getCaseId()).append("\n");
                        response.append("Title:        ").append(fc.getTitle()).append("\n");
                        response.append("Status:       ").append(fc.getStatus().getDisplayName()).append("\n");
                        response.append("Type:         ").append(fc.getCaseType().getDisplayName()).append("\n");
                        response.append("Amount:       ₹").append(String.format("%.2f", fc.getFraudAmount())).append("\n");
                        response.append("Priority:     ").append(fc.getPriority()).append("\n");
                        response.append("Registered:   ").append(fc.getDateRegistered()).append("\n");
                        response.append("Last Updated: ").append(fc.getDateLastUpdated()).append("\n");
                        response.append("Investigator: ").append(
                                fc.getAssignedInvestigatorId() != null ? fc.getAssignedInvestigatorId() : "Unassigned")
                               .append("\n");
                        response.append("Evidence:     ").append(fc.getEvidenceList().size()).append(" items\n");
                        response.append("--- End ---");
                        out.println(response.toString());
                    }
                    out.println("\nEnter another Case ID (or 'quit'):");
                }

                AuditLogger.getInstance().log("System", "CLIENT_DISCONNECTED",
                        "TCP client disconnected: " + clientSocket.getInetAddress());

            } catch (IOException e) {
                System.err.println("[TCP Server] Client handler error: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException ignored) {}
            }
        }
    }
}
