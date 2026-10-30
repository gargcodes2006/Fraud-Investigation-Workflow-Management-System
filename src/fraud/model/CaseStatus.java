package fraud.model;

/**
 * Enum representing the lifecycle status of a fraud investigation case.
 * Enforces the exact workflow: REGISTERED -> ASSIGNED -> UNDER_INVESTIGATION -> RESOLVED -> CLOSED.
 * 
 * Demonstrates: Enums, arrays, enhanced switch, encapsulation.
 */
public enum CaseStatus {
    REGISTERED("Registered", "#3498db"),
    ASSIGNED("Assigned", "#f39c12"),
    UNDER_INVESTIGATION("Under Investigation", "#e67e22"),
    RESOLVED("Resolved", "#2ecc71"),
    CLOSED("Closed", "#95a5a6");

    private final String displayName;
    private final String colorHex;

    CaseStatus(String displayName, String colorHex) {
        this.displayName = displayName;
        this.colorHex = colorHex;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColorHex() {
        return colorHex;
    }

    /**
     * Returns the valid next status in the workflow.
     * @return the next CaseStatus, or null if this is the terminal state (CLOSED).
     */
    public CaseStatus getNextStatus() {
        return switch (this) {
            case REGISTERED -> ASSIGNED;
            case ASSIGNED -> UNDER_INVESTIGATION;
            case UNDER_INVESTIGATION -> RESOLVED;
            case RESOLVED -> CLOSED;
            case CLOSED -> null;
        };
    }

    /**
     * Checks whether transitioning to the given target status is valid.
     * Only forward single-step transitions are allowed.
     */
    public boolean canTransitionTo(CaseStatus target) {
        return target != null && target.equals(getNextStatus());
    }

    /**
     * Returns all statuses as a String array (demonstrates arrays).
     */
    public static String[] getAllDisplayNames() {
        CaseStatus[] values = values();
        String[] names = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            names[i] = values[i].displayName;
        }
        return names;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
