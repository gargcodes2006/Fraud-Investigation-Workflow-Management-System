package fraud.service;

import fraud.model.*;
import fraud.exception.*;
import fraud.util.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing fraud investigation cases.
 * Implements Repository<FraudCase, String> — demonstrates generic interface implementation.
 * 
 * Demonstrates: HashMap, HashSet, TreeSet, ArrayList, LinkedList, lambda expressions,
 * generics, synchronized methods, custom exceptions, try/catch/finally.
 */
public class CaseService implements Repository<FraudCase, String> {

    private static final String CASES_FILE = "cases.txt";
    private static final String EVIDENCE_FILE = "evidence.txt";
    private static final String HISTORY_FILE = "history.txt";

    // HashMap — stores cases by ID for O(1) lookup
    private final Map<String, FraudCase> caseMap;

    // HashSet — tracks unique case IDs to prevent duplicates
    private final Set<String> caseIdSet;

    // TreeSet — maintains sorted case IDs (sorted order)
    private final TreeSet<String> sortedCaseIds;

    // LinkedList — waiting queue for cases when all investigators are full
    private final LinkedList<String> waitingQueue;

    private final AuditLogger auditLogger;

    public CaseService() {
        this.caseMap = new HashMap<>();
        this.caseIdSet = new HashSet<>();
        this.sortedCaseIds = new TreeSet<>();
        this.waitingQueue = new LinkedList<>();
        this.auditLogger = AuditLogger.getInstance();
    }

    // === Repository<FraudCase, String> Implementation ===

    @Override
    public synchronized void save(FraudCase fraudCase) {
        caseMap.put(fraudCase.getCaseId(), fraudCase);
        caseIdSet.add(fraudCase.getCaseId());
        sortedCaseIds.add(fraudCase.getCaseId());
    }

    @Override
    public synchronized void update(FraudCase fraudCase) {
        if (caseMap.containsKey(fraudCase.getCaseId())) {
            fraudCase.setDateLastUpdated(IDGenerator.timestamp());
            caseMap.put(fraudCase.getCaseId(), fraudCase);
        }
    }

    @Override
    public synchronized void delete(String id) {
        caseMap.remove(id);
        caseIdSet.remove(id);
        sortedCaseIds.remove(id);
        waitingQueue.remove(id);
    }

    @Override
    public FraudCase findById(String id) {
        return caseMap.get(id);
    }

    @Override
    public List<FraudCase> findAll() {
        return new ArrayList<>(caseMap.values());
    }

    @Override
    public int count() {
        return caseMap.size();
    }

    /**
     * Search cases by keyword across multiple fields.
     * Demonstrates: Lambda expressions, streams.
     */
    @Override
    public List<FraudCase> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        String lowerKeyword = keyword.toLowerCase();
        // Demonstrates: Lambda expression with stream filter
        return caseMap.values().stream()
                .filter(c -> c.getCaseId().toLowerCase().contains(lowerKeyword)
                        || c.getTitle().toLowerCase().contains(lowerKeyword)
                        || c.getDescription().toLowerCase().contains(lowerKeyword)
                        || c.getStatus().getDisplayName().toLowerCase().contains(lowerKeyword)
                        || c.getCaseType().getDisplayName().toLowerCase().contains(lowerKeyword)
                        || (c.getAssignedInvestigatorId() != null &&
                            c.getAssignedInvestigatorId().toLowerCase().contains(lowerKeyword)))
                .collect(Collectors.toList());
    }

    // === Business Logic ===

    /**
     * Register a new fraud case with validation.
     * Demonstrates: Custom checked exceptions, try/catch, throws.
     */
    public synchronized FraudCase registerCase(String title, String description, CaseType caseType,
                                                double fraudAmount, String priority,
                                                Complainant complainant, Suspect suspect)
            throws ValidationException, DuplicateEntityException {

        // Input validation
        InputValidator.requireNonEmpty(title, "Title");
        InputValidator.requireNonEmpty(description, "Description");
        InputValidator.validatePositiveAmount(fraudAmount, "Fraud Amount");

        String caseId = IDGenerator.generateCaseId();

        // Check for duplicate (demonstrates HashSet usage)
        if (caseIdSet.contains(caseId)) {
            throw new DuplicateEntityException("Case", caseId);
        }

        FraudCase fraudCase = new FraudCase(caseId, title, description, caseType,
                fraudAmount, IDGenerator.currentDate(), priority);
        fraudCase.setComplainant(complainant);
        fraudCase.setSuspect(suspect);

        // Add initial history entry
        fraudCase.addHistoryEntry(new CaseHistoryEntry(
                IDGenerator.timestamp(), "CASE_REGISTERED", "System",
                "Case registered with ID " + caseId, null, CaseStatus.REGISTERED));

        save(fraudCase);
        auditLogger.log("System", "CASE_REGISTERED", "Case " + caseId + " registered: " + title);

        return fraudCase;
    }

    /**
     * Transition a case to the next workflow status.
     * Demonstrates: Custom checked exceptions, throw/throws, try/catch/finally.
     */
    public synchronized void transitionStatus(String caseId, CaseStatus newStatus, String performedBy)
            throws InvalidTransitionException {

        FraudCase fraudCase = caseMap.get(caseId);
        if (fraudCase == null) {
            throw new InvalidTransitionException("Case " + caseId + " not found.");
        }

        CaseStatus currentStatus = fraudCase.getStatus();

        // Validate transition
        if (!currentStatus.canTransitionTo(newStatus)) {
            throw new InvalidTransitionException(
                    currentStatus.getDisplayName(), newStatus.getDisplayName());
        }

        CaseStatus oldStatus = fraudCase.getStatus();
        fraudCase.setStatus(newStatus);
        fraudCase.setDateLastUpdated(IDGenerator.timestamp());

        // Record history
        fraudCase.addHistoryEntry(new CaseHistoryEntry(
                IDGenerator.timestamp(), "STATUS_CHANGED", performedBy,
                "Status changed from " + oldStatus + " to " + newStatus,
                oldStatus, newStatus));

        update(fraudCase);
        auditLogger.log(performedBy, "STATUS_TRANSITION",
                "Case " + caseId + ": " + oldStatus + " -> " + newStatus);
    }

    // === Search & Filter Methods ===

    /** Filter by status. Demonstrates: Lambda expression. */
    public List<FraudCase> filterByStatus(CaseStatus status) {
        return caseMap.values().stream()
                .filter(c -> c.getStatus() == status)
                .collect(Collectors.toList());
    }

    /** Filter by case type. */
    public List<FraudCase> filterByType(CaseType type) {
        return caseMap.values().stream()
                .filter(c -> c.getCaseType() == type)
                .collect(Collectors.toList());
    }

    /** Filter by fraud amount range. */
    public List<FraudCase> filterByAmountRange(double min, double max) {
        return caseMap.values().stream()
                .filter(c -> c.getFraudAmount() >= min && c.getFraudAmount() <= max)
                .collect(Collectors.toList());
    }

    /** Filter by assigned investigator. */
    public List<FraudCase> filterByInvestigator(String investigatorId) {
        return caseMap.values().stream()
                .filter(c -> investigatorId.equals(c.getAssignedInvestigatorId()))
                .collect(Collectors.toList());
    }

    // === Waiting Queue (LinkedList demonstration) ===

    public synchronized void addToWaitingQueue(String caseId) {
        if (!waitingQueue.contains(caseId)) {
            waitingQueue.addLast(caseId);
        }
    }

    public synchronized String pollFromWaitingQueue() {
        return waitingQueue.pollFirst();
    }

    public synchronized int getWaitingQueueSize() {
        return waitingQueue.size();
    }

    public synchronized List<String> getWaitingQueue() {
        return new ArrayList<>(waitingQueue);
    }

    // === Statistics ===

    public int countByStatus(CaseStatus status) {
        return (int) caseMap.values().stream()
                .filter(c -> c.getStatus() == status)
                .count();
    }

    public double getTotalFraudAmount() {
        return caseMap.values().stream()
                .mapToDouble(FraudCase::getFraudAmount)
                .sum();
    }

    /** 
     * Get case type distribution as a jagged array.
     * Demonstrates: Jagged arrays.
     */
    public String[][] getCaseTypeDistribution() {
        Map<CaseType, Long> dist = caseMap.values().stream()
                .collect(Collectors.groupingBy(FraudCase::getCaseType, Collectors.counting()));

        // Jagged array — each row might conceptually vary
        String[][] result = new String[dist.size()][];
        int i = 0;
        for (Map.Entry<CaseType, Long> entry : dist.entrySet()) {
            result[i] = new String[]{entry.getKey().getDisplayName(), String.valueOf(entry.getValue())};
            i++;
        }
        return result;
    }

    // === Persistence ===

    /**
     * Save all cases to files.
     * Demonstrates: Character streams via FileHandler, try/catch/finally.
     */
    public synchronized void saveToFile() throws IOException {
        List<String> caseLines = new ArrayList<>();
        List<String> evidenceLines = new ArrayList<>();
        List<String> historyLines = new ArrayList<>();

        for (FraudCase fc : caseMap.values()) {
            // Case main line
            StringBuilder sb = new StringBuilder();
            sb.append(fc.getCaseId()).append("|")
              .append(fc.getTitle()).append("|")
              .append(fc.getDescription()).append("|")
              .append(fc.getCaseType().name()).append("|")
              .append(fc.getStatus().name()).append("|")
              .append(fc.getFraudAmount()).append("|")
              .append(fc.getDateRegistered()).append("|")
              .append(fc.getDateLastUpdated()).append("|")
              .append(fc.getPriority()).append("|")
              .append(fc.getAssignedInvestigatorId() != null ? fc.getAssignedInvestigatorId() : "");

            // Complainant
            if (fc.getComplainant() != null) {
                sb.append("|").append(fc.getComplainant().toFileString());
            } else {
                sb.append("|");
            }
            // Suspect
            if (fc.getSuspect() != null) {
                sb.append("|").append(fc.getSuspect().toFileString());
            } else {
                sb.append("|");
            }
            // Notes
            sb.append("|").append(String.join(";;", fc.getNotes()));

            caseLines.add(sb.toString());

            // Evidence
            for (Evidence e : fc.getEvidenceList()) {
                evidenceLines.add(e.toFileString());
            }

            // History
            for (CaseHistoryEntry h : fc.getHistory()) {
                historyLines.add(fc.getCaseId() + "|" + h.toFileString());
            }
        }

        // Waiting queue
        List<String> queueLines = new ArrayList<>(waitingQueue);

        FileHandler.writeLines(CASES_FILE, caseLines);
        FileHandler.writeLines(EVIDENCE_FILE, evidenceLines);
        FileHandler.writeLines(HISTORY_FILE, historyLines);
        FileHandler.writeLines("waiting_queue.txt", queueLines);
    }

    /**
     * Load all cases from files.
     * Demonstrates: File I/O, parsing, try/catch.
     */
    public synchronized void loadFromFile() throws IOException {
        caseMap.clear();
        caseIdSet.clear();
        sortedCaseIds.clear();
        waitingQueue.clear();

        List<String> caseLines = FileHandler.readLines(CASES_FILE);
        List<String> evidenceLines = FileHandler.readLines(EVIDENCE_FILE);
        List<String> historyLines = FileHandler.readLines(HISTORY_FILE);

        int maxCaseNum = 1000;

        for (String line : caseLines) {
            try {
                String[] parts = line.split("\\|", -1);
                if (parts.length < 10) continue;

                FraudCase fc = new FraudCase();
                fc.setCaseId(parts[0]);
                fc.setTitle(parts[1]);
                fc.setDescription(parts[2]);
                fc.setCaseType(CaseType.valueOf(parts[3]));
                fc.setStatus(CaseStatus.valueOf(parts[4]));
                fc.setFraudAmount(Double.parseDouble(parts[5]));
                fc.setDateRegistered(parts[6]);
                fc.setDateLastUpdated(parts[7]);
                fc.setPriority(parts[8]);
                fc.setAssignedInvestigatorId(parts[9].isEmpty() ? null : parts[9]);

                // Parse complainant if present
                if (parts.length > 10 && !parts[10].isEmpty()) {
                    Complainant comp = Complainant.fromFileString(parts[10]);
                    fc.setComplainant(comp);
                }
                // Parse suspect if present
                if (parts.length > 11 && !parts[11].isEmpty()) {
                    Suspect sus = Suspect.fromFileString(parts[11]);
                    fc.setSuspect(sus);
                }
                // Parse notes if present
                if (parts.length > 12 && !parts[12].isEmpty()) {
                    String[] notes = parts[12].split(";;");
                    for (String n : notes) {
                        if (!n.isEmpty()) fc.addNote(n);
                    }
                }

                save(fc);

                // Track max ID number
                try {
                    String idNum = fc.getCaseId().substring(fc.getCaseId().lastIndexOf('-') + 1);
                    int num = Integer.parseInt(idNum);
                    if (num > maxCaseNum) maxCaseNum = num;
                } catch (Exception ignored) {}

            } catch (Exception e) {
                System.err.println("Error loading case: " + e.getMessage());
            }
        }

        // Load evidence into cases
        for (String line : evidenceLines) {
            try {
                Evidence ev = Evidence.fromFileString(line);
                if (ev != null) {
                    FraudCase fc = caseMap.get(ev.getCaseId());
                    if (fc != null) fc.addEvidence(ev);
                }
            } catch (Exception e) {
                System.err.println("Error loading evidence: " + e.getMessage());
            }
        }

        // Load history into cases
        for (String line : historyLines) {
            try {
                int firstPipe = line.indexOf('|');
                if (firstPipe < 0) continue;
                String caseId = line.substring(0, firstPipe);
                String rest = line.substring(firstPipe + 1);
                CaseHistoryEntry entry = CaseHistoryEntry.fromFileString(rest);
                if (entry != null) {
                    FraudCase fc = caseMap.get(caseId);
                    if (fc != null) fc.addHistoryEntry(entry);
                }
            } catch (Exception e) {
                System.err.println("Error loading history: " + e.getMessage());
            }
        }

        // Load waiting queue
        try {
            List<String> queueLines = FileHandler.readLines("waiting_queue.txt");
            waitingQueue.addAll(queueLines);
        } catch (Exception ignored) {}

        IDGenerator.setCaseCounter(maxCaseNum);
    }

    /**
     * Create backups of all data files.
     */
    public String createBackup() throws IOException {
        saveToFile(); // Save current state first
        String backup1 = FileHandler.createBackup(CASES_FILE);
        try { FileHandler.createBackup(EVIDENCE_FILE); } catch (Exception ignored) {}
        try { FileHandler.createBackup(HISTORY_FILE); } catch (Exception ignored) {}
        return backup1;
    }

    // === Getters ===
    public Set<String> getCaseIdSet() { return new HashSet<>(caseIdSet); }
    public TreeSet<String> getSortedCaseIds() { return new TreeSet<>(sortedCaseIds); }
}
