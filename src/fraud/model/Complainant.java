package fraud.model;

/**
 * Represents a complainant who files a fraud complaint.
 * Extends Person — demonstrates inheritance and method overriding.
 */
public class Complainant extends Person {
    private static final long serialVersionUID = 3L;

    private String complaintDetails;
    private String dateOfComplaint;

    public Complainant() {
        super();
    }

    public Complainant(String id, String name, String email, String phone, String address,
                       String complaintDetails, String dateOfComplaint) {
        super(id, name, email, phone, address);
        this.complaintDetails = complaintDetails;
        this.dateOfComplaint = dateOfComplaint;
    }

    @Override
    public String getRole() {
        return "Complainant";
    }

    @Override
    public String getSummary() {
        return "Complainant: " + getName() + " | Filed: " + dateOfComplaint;
    }

    public String getComplaintDetails() { return complaintDetails; }
    public void setComplaintDetails(String complaintDetails) { this.complaintDetails = complaintDetails; }
    public String getDateOfComplaint() { return dateOfComplaint; }
    public void setDateOfComplaint(String dateOfComplaint) { this.dateOfComplaint = dateOfComplaint; }

    @Override
    public String toFileString() {
        return super.toFileString() + "|" + complaintDetails + "|" + dateOfComplaint;
    }

    public static Complainant fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 7) return null;
        return new Complainant(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6]);
    }
}
