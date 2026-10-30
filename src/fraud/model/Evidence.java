package fraud.model;

import java.io.Serializable;

/**
 * Represents a piece of evidence associated with a fraud case.
 * Demonstrates: Encapsulation, Serializable interface, toString overriding.
 */
public class Evidence implements Serializable {
    private static final long serialVersionUID = 5L;

    private String evidenceId;
    private String caseId;
    private String type;        // Document, Digital, Physical, Testimonial
    private String description;
    private String dateCollected;
    private String collectedBy;
    private String status;      // Collected, Under Analysis, Verified, Rejected

    public Evidence() {}

    public Evidence(String evidenceId, String caseId, String type, String description,
                    String dateCollected, String collectedBy, String status) {
        this.evidenceId = evidenceId;
        this.caseId = caseId;
        this.type = type;
        this.description = description;
        this.dateCollected = dateCollected;
        this.collectedBy = collectedBy;
        this.status = status;
    }

    // --- Getters and Setters ---
    public String getEvidenceId() { return evidenceId; }
    public void setEvidenceId(String evidenceId) { this.evidenceId = evidenceId; }
    public String getCaseId() { return caseId; }
    public void setCaseId(String caseId) { this.caseId = caseId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDateCollected() { return dateCollected; }
    public void setDateCollected(String dateCollected) { this.dateCollected = dateCollected; }
    public String getCollectedBy() { return collectedBy; }
    public void setCollectedBy(String collectedBy) { this.collectedBy = collectedBy; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("Evidence[%s] %s - %s (%s)", evidenceId, type, description, status);
    }

    public String toFileString() {
        return String.join("|", evidenceId, caseId, type, description, dateCollected, collectedBy, status);
    }

    public static Evidence fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 7) return null;
        return new Evidence(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6]);
    }
}
