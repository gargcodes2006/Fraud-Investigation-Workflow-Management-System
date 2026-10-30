package fraud.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class for generating unique IDs with prefixes.
 * Demonstrates: Static methods, String manipulation, thread safety with AtomicInteger.
 */
public final class IDGenerator {
    private static final AtomicInteger caseCounter = new AtomicInteger(1000);
    private static final AtomicInteger investigatorCounter = new AtomicInteger(100);
    private static final AtomicInteger complainantCounter = new AtomicInteger(200);
    private static final AtomicInteger suspectCounter = new AtomicInteger(300);
    private static final AtomicInteger evidenceCounter = new AtomicInteger(400);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private IDGenerator() {} // Prevent instantiation

    public static String generateCaseId() {
        return "FIC-" + LocalDateTime.now().format(DATE_FORMAT) + "-" + caseCounter.incrementAndGet();
    }

    public static String generateInvestigatorId() {
        return "INV-" + investigatorCounter.incrementAndGet();
    }

    public static String generateComplainantId() {
        return "CMP-" + complainantCounter.incrementAndGet();
    }

    public static String generateSuspectId() {
        return "SUS-" + suspectCounter.incrementAndGet();
    }

    public static String generateEvidenceId() {
        return "EVD-" + evidenceCounter.incrementAndGet();
    }

    /** Get current timestamp as string. */
    public static String timestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /** Get current date as string. */
    public static String currentDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /** Reset counters (for loading persisted max IDs). */
    public static void setCaseCounter(int value) { caseCounter.set(value); }
    public static void setInvestigatorCounter(int value) { investigatorCounter.set(value); }
    public static void setComplainantCounter(int value) { complainantCounter.set(value); }
    public static void setSuspectCounter(int value) { suspectCounter.set(value); }
    public static void setEvidenceCounter(int value) { evidenceCounter.set(value); }
}
