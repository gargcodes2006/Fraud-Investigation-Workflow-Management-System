package fraud.model;

/**
 * Represents a suspect in a fraud investigation case.
 * Extends Person — demonstrates inheritance and method overriding.
 */
public class Suspect extends Person {
    private static final long serialVersionUID = 4L;

    private String suspicionDetails;
    private String riskLevel; // LOW, MEDIUM, HIGH, CRITICAL

    public Suspect() {
        super();
        this.riskLevel = "MEDIUM";
    }

    public Suspect(String id, String name, String email, String phone, String address,
                   String suspicionDetails, String riskLevel) {
        super(id, name, email, phone, address);
        this.suspicionDetails = suspicionDetails;
        this.riskLevel = riskLevel;
    }

    @Override
    public String getRole() {
        return "Suspect";
    }

    @Override
    public String getSummary() {
        return "Suspect: " + getName() + " | Risk: " + riskLevel;
    }

    public String getSuspicionDetails() { return suspicionDetails; }
    public void setSuspicionDetails(String suspicionDetails) { this.suspicionDetails = suspicionDetails; }
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    @Override
    public String toFileString() {
        return super.toFileString() + "|" + suspicionDetails + "|" + riskLevel;
    }

    public static Suspect fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 7) return null;
        return new Suspect(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6]);
    }
}
