package fraud.service;

import fraud.model.*;
import fraud.exception.*;
import fraud.util.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

/**
 * Service for managing investigator assignment with thread safety.
 * 
 * Demonstrates: Multithreading, Runnable, synchronization, thread priorities,
 * daemon threads, ConcurrentHashMap, lambda expressions, LinkedList as queue.
 */
public class AssignmentService {

    private static final String INVESTIGATORS_FILE = "investigators.txt";

    // Thread-safe map of investigators
    private final Map<String, Investigator> investigatorMap;

    // Reference to case service
    private final CaseService caseService;

    // Lock object for synchronization demonstration
    private final Object assignmentLock = new Object();

    private final AuditLogger auditLogger;

    // Daemon thread for processing waiting queue
    private Thread queueProcessorThread;
    private volatile boolean running = true;

    public AssignmentService(CaseService caseService) {
        this.investigatorMap = new ConcurrentHashMap<>(); // Thread-safe collection
        this.caseService = caseService;
        this.auditLogger = AuditLogger.getInstance();
    }

    // === Investigator Management ===

    public void addInvestigator(Investigator investigator) throws DuplicateEntityException {
        if (investigatorMap.containsKey(investigator.getId())) {
            throw new DuplicateEntityException("Investigator", investigator.getId());
        }
        investigatorMap.put(investigator.getId(), investigator);
        auditLogger.log("System", "INVESTIGATOR_ADDED",
                "Investigator " + investigator.getId() + " - " + investigator.getName());
    }

    public Investigator getInvestigator(String id) {
        return investigatorMap.get(id);
    }

    public List<Investigator> getAllInvestigators() {
        return new ArrayList<>(investigatorMap.values());
    }

    public void updateInvestigator(Investigator investigator) {
        investigatorMap.put(investigator.getId(), investigator);
    }

    public void removeInvestigator(String id) {
        investigatorMap.remove(id);
    }

    // === Least-Loaded Assignment Algorithm ===

    /**
     * Find the least-loaded investigator who has capacity.
     * Demonstrates: Collections.min with Comparable, synchronized block.
     */
    public Investigator findLeastLoaded() {
        synchronized (assignmentLock) {
            List<Investigator> available = new ArrayList<>();
            for (Investigator inv : investigatorMap.values()) {
                if (inv.isAvailable() && inv.hasCapacity()) {
                    available.add(inv);
                }
            }
            if (available.isEmpty()) return null;
            // Uses Comparable<Investigator> (compareTo compares by load)
            return Collections.min(available);
        }
    }

    /**
     * Assign a case to the least-loaded investigator.
     * Demonstrates: Synchronized blocks, custom exceptions, try/catch/finally.
     * 
     * @return the assigned investigator, or null if queued
     */
    public Investigator assignCase(String caseId) throws CapacityExceededException {
        synchronized (assignmentLock) {
            FraudCase fraudCase = caseService.findById(caseId);
            if (fraudCase == null) {
                throw new CapacityExceededException("Case " + caseId + " not found.");
            }

            Investigator leastLoaded = findLeastLoaded();

            if (leastLoaded == null) {
                // All investigators at capacity — add to waiting queue
                caseService.addToWaitingQueue(caseId);
                auditLogger.log("System", "CASE_QUEUED",
                        "Case " + caseId + " added to waiting queue (all investigators at capacity).");
                throw new CapacityExceededException(
                        "All investigators are at capacity. Case " + caseId + " has been added to the waiting queue.");
            }

            // Assign the case
            boolean assigned = leastLoaded.assignCase(caseId);
            if (!assigned) {
                caseService.addToWaitingQueue(caseId);
                throw new CapacityExceededException(
                        leastLoaded.getId(), leastLoaded.getCurrentLoad(), leastLoaded.getMaxCaseCapacity());
            }

            fraudCase.setAssignedInvestigatorId(leastLoaded.getId());
            try {
                caseService.transitionStatus(caseId, CaseStatus.ASSIGNED, "System");
            } catch (InvalidTransitionException e) {
                // Case may already be assigned; just update the investigator field
                fraudCase.setAssignedInvestigatorId(leastLoaded.getId());
                caseService.update(fraudCase);
            }

            auditLogger.log("System", "CASE_ASSIGNED",
                    "Case " + caseId + " assigned to investigator " + leastLoaded.getName() +
                    " (" + leastLoaded.getId() + "). Load: " + leastLoaded.getCurrentLoad() +
                    "/" + leastLoaded.getMaxCaseCapacity());

            return leastLoaded;
        }
    }

    /**
     * Manually assign a case to a specific investigator.
     */
    public void assignCaseTo(String caseId, String investigatorId) 
            throws CapacityExceededException, InvalidTransitionException {
        synchronized (assignmentLock) {
            Investigator inv = investigatorMap.get(investigatorId);
            if (inv == null) {
                throw new CapacityExceededException("Investigator " + investigatorId + " not found.");
            }
            if (!inv.hasCapacity()) {
                throw new CapacityExceededException(inv.getId(), inv.getCurrentLoad(), inv.getMaxCaseCapacity());
            }

            FraudCase fraudCase = caseService.findById(caseId);
            if (fraudCase == null) {
                throw new CapacityExceededException("Case " + caseId + " not found.");
            }

            inv.assignCase(caseId);
            fraudCase.setAssignedInvestigatorId(investigatorId);

            if (fraudCase.getStatus() == CaseStatus.REGISTERED) {
                caseService.transitionStatus(caseId, CaseStatus.ASSIGNED, "System");
            } else {
                caseService.update(fraudCase);
            }

            auditLogger.log("System", "CASE_MANUALLY_ASSIGNED",
                    "Case " + caseId + " manually assigned to " + inv.getName());
        }
    }

    /**
     * Unassign a case from its investigator (e.g., when case is closed).
     */
    public void unassignCase(String caseId) {
        synchronized (assignmentLock) {
            FraudCase fraudCase = caseService.findById(caseId);
            if (fraudCase != null && fraudCase.getAssignedInvestigatorId() != null) {
                Investigator inv = investigatorMap.get(fraudCase.getAssignedInvestigatorId());
                if (inv != null) {
                    inv.unassignCase(caseId);
                }
            }
        }
    }

    // === Queue Processor (Daemon Thread) ===

    /**
     * Start a daemon thread that periodically processes the waiting queue.
     * Demonstrates: Daemon threads, Runnable interface, Thread class, thread priority.
     */
    public void startQueueProcessor() {
        // Demonstrates: Runnable interface with lambda expression
        Runnable queueProcessor = () -> {
            Thread.currentThread().setName("QueueProcessorDaemon");
            while (running) {
                try {
                    processWaitingQueue();
                    Thread.sleep(5000); // Check every 5 seconds
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        };

        queueProcessorThread = new Thread(queueProcessor);
        queueProcessorThread.setDaemon(true); // Daemon thread
        queueProcessorThread.setPriority(Thread.MIN_PRIORITY); // Low priority
        queueProcessorThread.start();

        auditLogger.log("System", "QUEUE_PROCESSOR_STARTED", "Daemon queue processor thread started.");
    }

    /**
     * Process one item from the waiting queue if an investigator has capacity.
     */
    private void processWaitingQueue() {
        synchronized (assignmentLock) {
            if (caseService.getWaitingQueueSize() == 0) return;

            Investigator leastLoaded = findLeastLoaded();
            if (leastLoaded == null) return;

            String caseId = caseService.pollFromWaitingQueue();
            if (caseId == null) return;

            FraudCase fc = caseService.findById(caseId);
            if (fc == null) return;

            leastLoaded.assignCase(caseId);
            fc.setAssignedInvestigatorId(leastLoaded.getId());

            try {
                if (fc.getStatus() == CaseStatus.REGISTERED) {
                    caseService.transitionStatus(caseId, CaseStatus.ASSIGNED, "QueueProcessor");
                }
            } catch (InvalidTransitionException e) {
                fc.setAssignedInvestigatorId(leastLoaded.getId());
                caseService.update(fc);
            }

            auditLogger.log("QueueProcessor", "QUEUE_ASSIGNMENT",
                    "Case " + caseId + " dequeued and assigned to " + leastLoaded.getName());
        }
    }

    /** Stop the queue processor. */
    public void stopQueueProcessor() {
        running = false;
        if (queueProcessorThread != null) {
            queueProcessorThread.interrupt();
        }
    }

    // === Persistence ===

    public void saveToFile() throws IOException {
        List<String> lines = new ArrayList<>();
        for (Investigator inv : investigatorMap.values()) {
            lines.add(inv.toFileString());
        }
        FileHandler.writeLines(INVESTIGATORS_FILE, lines);
    }

    public void loadFromFile() throws IOException {
        investigatorMap.clear();
        List<String> lines = FileHandler.readLines(INVESTIGATORS_FILE);
        int maxId = 100;
        for (String line : lines) {
            try {
                Investigator inv = Investigator.fromFileString(line);
                if (inv != null) {
                    investigatorMap.put(inv.getId(), inv);
                    try {
                        int num = Integer.parseInt(inv.getId().replace("INV-", ""));
                        if (num > maxId) maxId = num;
                    } catch (Exception ignored) {}
                }
            } catch (Exception e) {
                System.err.println("Error loading investigator: " + e.getMessage());
            }
        }
        IDGenerator.setInvestigatorCounter(maxId);
    }

    // === Statistics ===

    public int getTotalInvestigators() {
        return investigatorMap.size();
    }

    public int getAvailableInvestigators() {
        return (int) investigatorMap.values().stream()
                .filter(i -> i.isAvailable() && i.hasCapacity())
                .count();
    }

    public int getTotalAssignedCases() {
        return investigatorMap.values().stream()
                .mapToInt(Investigator::getCurrentLoad)
                .sum();
    }
}
