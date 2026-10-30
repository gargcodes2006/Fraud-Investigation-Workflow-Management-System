package fraud.exception;

/**
 * Custom checked exception for duplicate entity registration.
 * Demonstrates: Custom checked exception with descriptive messages.
 */
public class DuplicateEntityException extends Exception {
    private final String entityType;
    private final String entityId;

    public DuplicateEntityException(String entityType, String entityId) {
        super(entityType + " with ID '" + entityId + "' already exists.");
        this.entityType = entityType;
        this.entityId = entityId;
    }

    public String getEntityType() { return entityType; }
    public String getEntityId() { return entityId; }
}
