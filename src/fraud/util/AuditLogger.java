package fraud.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Audit logging service for tracking all system activities.
 * Demonstrates: Singleton pattern, synchronized methods, file I/O.
 */
public final class AuditLogger {
    private static final String AUDIT_FILE = "audit_log.txt";
    private static final AuditLogger INSTANCE = new AuditLogger();
    private final List<String> inMemoryLog;

    private AuditLogger() {
        inMemoryLog = new ArrayList<>();
        try {
            List<String> existing = FileHandler.readLines(AUDIT_FILE);
            inMemoryLog.addAll(existing);
        } catch (IOException e) {
            // Start with empty log
        }
    }

    public static AuditLogger getInstance() {
        return INSTANCE;
    }

    /**
     * Log an audit event (thread-safe).
     * Demonstrates: Synchronized methods for thread safety.
     */
    public synchronized void log(String user, String action, String details) {
        String entry = String.join("|", IDGenerator.timestamp(), user, action, details);
        inMemoryLog.add(entry);
        try {
            FileHandler.appendLine(AUDIT_FILE, entry);
        } catch (IOException e) {
            System.err.println("Failed to write audit log: " + e.getMessage());
        }
    }

    /** Get all audit log entries. */
    public synchronized List<String> getEntries() {
        return new ArrayList<>(inMemoryLog);
    }

    /** Get entries filtered by user. */
    public synchronized List<String> getEntriesByUser(String user) {
        List<String> filtered = new ArrayList<>();
        for (String entry : inMemoryLog) {
            if (entry.contains(user)) {
                filtered.add(entry);
            }
        }
        return filtered;
    }

    /** Get recent entries (last N). */
    public synchronized List<String> getRecentEntries(int count) {
        int start = Math.max(0, inMemoryLog.size() - count);
        return new ArrayList<>(inMemoryLog.subList(start, inMemoryLog.size()));
    }

    /** Clear log (for testing). */
    public synchronized void clear() {
        inMemoryLog.clear();
    }
}
