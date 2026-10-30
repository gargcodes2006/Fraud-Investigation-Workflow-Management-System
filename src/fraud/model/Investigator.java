package fraud.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an investigator who handles fraud cases.
 * Extends Person (demonstrates inheritance), implements Comparable (demonstrates interfaces).
 * 
 * Demonstrates: Inheritance, interface implementation, ArrayList, method overriding,
 * constructor chaining with super(), encapsulation.
 */
public class Investigator extends Person implements Comparable<Investigator> {
    private static final long serialVersionUID = 2L;

    private String department;
    private String specialization;
    private int maxCaseCapacity;
    private List<String> assignedCaseIds; // ArrayList — demonstrates generics and collections
    private boolean available;

    /** Default constructor. */
    public Investigator() {
        super(); // Demonstrates super() call
        this.assignedCaseIds = new ArrayList<>();
        this.maxCaseCapacity = 5;
        this.available = true;
    }

    /** Parameterized constructor — demonstrates constructor overloading. */
    public Investigator(String id, String name, String email, String phone, String address,
                        String department, String specialization, int maxCaseCapacity) {
        super(id, name, email, phone, address); // Constructor chaining
        this.department = department;
        this.specialization = specialization;
        this.maxCaseCapacity = maxCaseCapacity;
        this.assignedCaseIds = new ArrayList<>();
        this.available = true;
    }

    // --- Overriding abstract methods from Person ---

    @Override
    public String getRole() {
        return "Investigator";
    }

    @Override
    public String getSummary() {
        // Demonstrates StringBuilder
        StringBuilder sb = new StringBuilder();
        sb.append("Investigator: ").append(getName())
          .append(" | Dept: ").append(department)
          .append(" | Cases: ").append(assignedCaseIds.size())
          .append("/").append(maxCaseCapacity);
        return sb.toString();
    }

    // --- Business Methods ---

    /** Check if investigator can accept more cases. */
    public boolean hasCapacity() {
        return assignedCaseIds.size() < maxCaseCapacity;
    }

    /** Get current case load count. */
    public int getCurrentLoad() {
        return assignedCaseIds.size();
    }

    /** Get remaining capacity. */
    public int getRemainingCapacity() {
        return maxCaseCapacity - assignedCaseIds.size();
    }

    /** Assign a case to this investigator. */
    public synchronized boolean assignCase(String caseId) {
        if (!hasCapacity()) return false;
        if (!assignedCaseIds.contains(caseId)) {
            assignedCaseIds.add(caseId);
        }
        return true;
    }

    /** Remove a case from this investigator. */
    public synchronized void unassignCase(String caseId) {
        assignedCaseIds.remove(caseId);
    }

    /**
     * Comparable implementation: compare by current load (ascending).
     * Used for least-loaded assignment algorithm.
     * Demonstrates: Interface implementation, Comparable.
     */
    @Override
    public int compareTo(Investigator other) {
        return Integer.compare(this.getCurrentLoad(), other.getCurrentLoad());
    }

    // --- Getters and Setters ---
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public int getMaxCaseCapacity() { return maxCaseCapacity; }
    public void setMaxCaseCapacity(int maxCaseCapacity) { this.maxCaseCapacity = maxCaseCapacity; }
    public List<String> getAssignedCaseIds() { return new ArrayList<>(assignedCaseIds); }
    public void setAssignedCaseIds(List<String> ids) { this.assignedCaseIds = new ArrayList<>(ids); }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toFileString() {
        String caseIdsStr = String.join(",", assignedCaseIds);
        return super.toFileString() + "|" + 
               String.join("|", department, specialization, 
                           String.valueOf(maxCaseCapacity), caseIdsStr, String.valueOf(available));
    }

    public static Investigator fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 10) return null;
        Investigator inv = new Investigator(parts[0], parts[1], parts[2], parts[3], parts[4],
                                            parts[5], parts[6], Integer.parseInt(parts[7]));
        if (!parts[8].isEmpty()) {
            String[] caseIds = parts[8].split(",");
            for (String cid : caseIds) {
                if (!cid.isEmpty()) inv.assignedCaseIds.add(cid);
            }
        }
        inv.setAvailable(Boolean.parseBoolean(parts[9]));
        return inv;
    }
}
