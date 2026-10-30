package fraud.model;

import java.io.Serializable;

/**
 * Enum representing user roles in the system.
 * Demonstrates: Enums with behavior, Serializable interface.
 */
public enum UserRole implements Serializable {
    ADMIN("Administrator", new String[]{"dashboard", "cases", "investigators", "reports", "audit", "settings"}),
    INVESTIGATOR("Investigator", new String[]{"dashboard", "cases", "reports"}),
    ANALYST("Analyst", new String[]{"dashboard", "cases", "reports", "audit"});

    private final String displayName;
    private final String[] allowedModules; // Demonstrates arrays

    UserRole(String displayName, String[] allowedModules) {
        this.displayName = displayName;
        this.allowedModules = allowedModules;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String[] getAllowedModules() {
        return allowedModules.clone(); // Defensive copy
    }

    public boolean hasAccess(String module) {
        for (String m : allowedModules) {
            if (m.equalsIgnoreCase(module)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
