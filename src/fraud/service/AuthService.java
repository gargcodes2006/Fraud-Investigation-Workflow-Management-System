package fraud.service;

import fraud.model.*;
import fraud.util.*;

import java.io.IOException;
import java.util.*;

/**
 * Service for authentication and user management.
 * Demonstrates: HashMap usage, file persistence, simple authentication.
 */
public class AuthService {

    private static final String USERS_FILE = "users.txt";
    private final Map<String, User> userMap;
    private User currentUser;

    public AuthService() {
        this.userMap = new HashMap<>();
    }

    /**
     * Authenticate a user.
     * @return the authenticated User, or null if authentication fails.
     */
    public User authenticate(String username, String password) {
        User user = userMap.get(username);
        if (user != null && user.isActive() && user.verifyPassword(password)) {
            currentUser = user;
            AuditLogger.getInstance().log(username, "LOGIN", "User logged in successfully.");
            return user;
        }
        AuditLogger.getInstance().log(username != null ? username : "unknown", 
                "LOGIN_FAILED", "Failed login attempt.");
        return null;
    }

    public void logout() {
        if (currentUser != null) {
            AuditLogger.getInstance().log(currentUser.getUsername(), "LOGOUT", "User logged out.");
            currentUser = null;
        }
    }

    public User getCurrentUser() { return currentUser; }

    public void addUser(User user) {
        userMap.put(user.getUsername(), user);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userMap.values());
    }

    /**
     * Seed demo users with clearly documented credentials.
     * 
     * DEMO CREDENTIALS:
     * ┌──────────────┬──────────────┬─────────────────────┬───────────────┐
     * │ Username     │ Password     │ Full Name           │ Role          │
     * ├──────────────┼──────────────┼─────────────────────┼───────────────┤
     * │ admin        │ admin123     │ Admin User          │ ADMIN         │
     * │ investigator │ invest123    │ John Investigator   │ INVESTIGATOR  │
     * │ analyst      │ analyst123   │ Jane Analyst        │ ANALYST       │
     * └──────────────┴──────────────┴─────────────────────┴───────────────┘
     */
    public void seedDemoUsers() {
        addUser(new User("admin", User.hashPassword("admin123"), "Admin User", UserRole.ADMIN));
        addUser(new User("investigator", User.hashPassword("invest123"), "John Investigator", UserRole.INVESTIGATOR));
        addUser(new User("analyst", User.hashPassword("analyst123"), "Jane Analyst", UserRole.ANALYST));
    }

    public void saveToFile() throws IOException {
        List<String> lines = new ArrayList<>();
        for (User u : userMap.values()) {
            lines.add(u.toFileString());
        }
        FileHandler.writeLines(USERS_FILE, lines);
    }

    public void loadFromFile() throws IOException {
        userMap.clear();
        List<String> lines = FileHandler.readLines(USERS_FILE);
        for (String line : lines) {
            User u = User.fromFileString(line);
            if (u != null) {
                userMap.put(u.getUsername(), u);
            }
        }
    }
}
