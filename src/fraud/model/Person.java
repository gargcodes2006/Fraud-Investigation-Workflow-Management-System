package fraud.model;

import java.io.Serializable;

/**
 * Abstract base class for all persons in the system.
 * Demonstrates: Abstract classes, constructors, encapsulation, Serializable.
 */
public abstract class Person implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String email;
    private String phone;
    private String address;

    /** Default constructor (demonstrates constructor overloading). */
    protected Person() {
        this.id = "";
        this.name = "";
        this.email = "";
        this.phone = "";
        this.address = "";
    }

    /** Parameterized constructor. */
    protected Person(String id, String name, String email, String phone, String address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    /** Copy constructor (demonstrates another form of constructor overloading). */
    protected Person(Person other) {
        this.id = other.id;
        this.name = other.name;
        this.email = other.email;
        this.phone = other.phone;
        this.address = other.address;
    }

    // --- Abstract methods (demonstrates abstraction) ---
    public abstract String getRole();
    public abstract String getSummary();

    // --- Getters and Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    /**
     * Overriding toString() — demonstrates method overriding.
     */
    @Override
    public String toString() {
        return String.format("%s [ID=%s, Name=%s, Email=%s]", getRole(), id, name, email);
    }

    /**
     * Serialize to a pipe-delimited string for file persistence.
     */
    public String toFileString() {
        return String.join("|", id, name, email, phone, address);
    }
}
