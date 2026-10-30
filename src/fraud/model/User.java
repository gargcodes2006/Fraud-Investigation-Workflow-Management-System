package fraud.model;

import java.io.Serializable;

/**
 * Represents a user account for authentication and authorization.
 * Demonstrates: Composition (has-a UserRole), Serializable interface.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String passwordHash; // Simple hash for demo; not production-grade
    private String fullName;
    private UserRole role;
    private boolean active;

    public User() {
        this.active = true;
    }

    public User(String username, String passwordHash, String fullName, UserRole role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.role = role;
        this.active = true;
    }

    // --- Getters and Setters ---
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    /**
     * Simple password verification.
     * Uses String.hashCode() for demo purposes only.
     */
    public boolean verifyPassword(String password) {
        return passwordHash != null && passwordHash.equals(String.valueOf(password.hashCode()));
    }

    /**
     * Create a hash from a plain-text password for storage.
     */
    public static String hashPassword(String password) {
        return String.valueOf(password.hashCode());
    }

    public String toFileString() {
        return String.join("|", username, passwordHash, fullName, role.name(), String.valueOf(active));
    }

    public static User fromFileString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length < 5) return null;
        User u = new User(parts[0], parts[1], parts[2], UserRole.valueOf(parts[3]));
        u.setActive(Boolean.parseBoolean(parts[4]));
        return u;
    }

    @Override
    public String toString() {
        return String.format("User[%s, %s, %s]", username, fullName, role);
    }
}
