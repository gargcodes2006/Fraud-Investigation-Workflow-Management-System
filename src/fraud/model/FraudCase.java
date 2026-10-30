package fraud.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a fraud investigation case — the central domain entity.
 * 
 * Demonstrates: Encapsulation, composition (has-a relationships with Complainant, Suspect,
 * Investigator, Evidence, CaseHistoryEntry), ArrayList, generics, Serializable.
 */
public class FraudCase implements Serializable {
    private static final long serialVersionUID = 7L;

    private String caseId;
    private String title;
    private String description;
    private CaseType caseType;
    private CaseStatus status;
    private double fraudAmount;
    private String dateRegistered;
    private String dateLastUpdated;
    private String priority;  // LOW, MEDIUM, HIGH, CRITICAL

    // Composition relationships
    private Complainant complainant;
    private Suspect suspect;
    private String assignedInvestigatorId;

    // Collections — demonstrates ArrayList and generics
    private List<Evidence> evidenceList;
    private List<CaseHistoryEntry> history;
    private List<String> notes;

    /** Default constructor. */
    public FraudCase() {
        this.status = CaseStatus.REGISTERED;
        this.evidenceList = new ArrayList<>();
        this.history = new ArrayList<>();
        this.notes = new ArrayList<>();
        this.priority = "MEDIUM";
    }

    /** Full parameterized constructor. */
    public FraudCase(String caseId, String title, String description, CaseType caseType,
                     double fraudAmount, String dateRegistered, String priority) {
        this();
        this.caseId = caseId;
        this.title = title;
        this.description = description;
        this.caseType = caseType;
        this.fraudAmount = fraudAmount;
        this.dateRegistered = dateRegistered;
        this.dateLastUpdated = dateRegistered;
        this.priority = priority;
    }

    // --- Business Methods ---

    public void addEvidence(Evidence evidence) {
        evidenceList.add(evidence);
    }

    public void removeEvidence(String evidenceId) {
        evidenceList.removeIf(e -> e.getEvidenceId().equals(evidenceId));
    }

    public void addHistoryEntry(CaseHistoryEntry entry) {
        history.add(entry);
    }

    public void addNote(String note) {
        notes.add(note);
    }

    // --- Getters and Setters ---
    public String getCaseId() { return caseId; }
    public void setCaseId(String caseId) { this.caseId = caseId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public CaseType getCaseType() { return caseType; }
    public void setCaseType(CaseType caseType) { this.caseType = caseType; }
    public CaseStatus getStatus() { return status; }
    public void setStatus(CaseStatus status) { this.status = status; }
    public double getFraudAmount() { return fraudAmount; }
    public void setFraudAmount(double fraudAmount) { this.fraudAmount = fraudAmount; }
    public String getDateRegistered() { return dateRegistered; }
    public void setDateRegistered(String dateRegistered) { this.dateRegistered = dateRegistered; }
    public String getDateLastUpdated() { return dateLastUpdated; }
    public void setDateLastUpdated(String dateLastUpdated) { this.dateLastUpdated = dateLastUpdated; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public Complainant getComplainant() { return complainant; }
    public void setComplainant(Complainant complainant) { this.complainant = complainant; }
    public Suspect getSuspect() { return suspect; }
    public void setSuspect(Suspect suspect) { this.suspect = suspect; }
    public String getAssignedInvestigatorId() { return assignedInvestigatorId; }
    public void setAssignedInvestigatorId(String assignedInvestigatorId) { this.assignedInvestigatorId = assignedInvestigatorId; }
    public List<Evidence> getEvidenceList() { return evidenceList; }
    public void setEvidenceList(List<Evidence> evidenceList) { this.evidenceList = evidenceList; }
    public List<CaseHistoryEntry> getHistory() { return history; }
    public void setHistory(List<CaseHistoryEntry> history) { this.history = history; }
    public List<String> getNotes() { return notes; }
    public void setNotes(List<String> notes) { this.notes = notes; }

    @Override
    public String toString() {
        return String.format("FraudCase[%s] %s — %s (%s) Amount: %.2f",
                caseId, title, status, caseType, fraudAmount);
    }
}
