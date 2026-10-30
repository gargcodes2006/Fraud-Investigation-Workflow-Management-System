package fraud.model;

import java.io.Serializable;

/**
 * Records a single entry in a case's history (status change / event).
 * Demonstrates: Immutable objects, Serializable.
 */
public class CaseHistoryEntry implements Serializable {
    private static final long serialVersionUID = 6L;

    private final String timestamp;
    private final String action;
    private final String performedBy;
    private final String details;
    private final CaseStatus fromStatus;
    private final CaseStatus toStatus;

    public CaseHistoryEntry(String timestamp, String action, String performedBy,
                            String details, CaseStatus fromStatus, CaseStatus toStatus) {
        this.timestamp = timestamp;
        this.action = action;
        this.performedBy = performedBy;
        this.details = details;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
    }

    /** Constructor without status change — for general history events. */
    public CaseHistoryEntry(String timestamp, String action, String performedBy, String details) {
        this(timestamp, action, performedBy, details, null, null);
    }

    // --- Getters ---
    public String getTimestamp() { return timestamp; }
    public String getAction() { return action; }
    public String getPerformedBy() { return performedBy; }
    public String getDetails() { return details; }
    public CaseStatus getFromStatus() { return fromStatus; }
    public CaseStatus getToStatus() { return toStatus; }

    @Override
    public String toString() {
        // Demonstrates StringBuffer
        StringBuffer buf = new StringBuffer();
        buf.append("[").append(timestamp).append("] ")
           .append(action).append(" by ").append(performedBy);
        if (fromStatus != null && toStatus != null) {
            buf.append(" (").append(fromStatus).append(" -> ").append(toStatus).append(")");
        }
        if (details != null && !details.isEmpty()) {
            buf.append(" - ").append(details);
        }
        return buf.toString();
    }

    public String toFileString() {
        String from = fromStatus != null ? fromStatus.name() : "";
        String to = toStatus != null ? toStatus.name() : "";
        return String.join("|", timestamp, action, performedBy, details, from, to);
    }

    public static CaseHistoryEntry fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 6) return null;
        CaseStatus from = parts[4].isEmpty() ? null : CaseStatus.valueOf(parts[4]);
        CaseStatus to = parts[5].isEmpty() ? null : CaseStatus.valueOf(parts[5]);
        return new CaseHistoryEntry(parts[0], parts[1], parts[2], parts[3], from, to);
    }
}
