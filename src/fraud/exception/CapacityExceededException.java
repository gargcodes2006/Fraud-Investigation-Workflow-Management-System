package fraud.exception;

/**
 * Custom checked exception thrown when an investigator's case capacity is exceeded.
 * Demonstrates: Custom checked exception, informative error messages.
 */
public class CapacityExceededException extends Exception {
    private final String investigatorId;
    private final int currentLoad;
    private final int maxCapacity;

    public CapacityExceededException(String investigatorId, int currentLoad, int maxCapacity) {
        super("Investigator " + investigatorId + " has reached maximum capacity (" +
              currentLoad + "/" + maxCapacity + "). Case queued for assignment.");
        this.investigatorId = investigatorId;
        this.currentLoad = currentLoad;
        this.maxCapacity = maxCapacity;
    }

    public CapacityExceededException(String message) {
        super(message);
        this.investigatorId = "";
        this.currentLoad = 0;
        this.maxCapacity = 0;
    }

    public String getInvestigatorId() { return investigatorId; }
    public int getCurrentLoad() { return currentLoad; }
    public int getMaxCapacity() { return maxCapacity; }
}
