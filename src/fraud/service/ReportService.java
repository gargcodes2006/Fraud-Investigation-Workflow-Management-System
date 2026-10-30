package fraud.service;

import fraud.model.*;
import fraud.util.*;

import java.util.*;

/**
 * Service for generating reports and statistics.
 * Demonstrates: Generic methods, lambda expressions, collections processing.
 */
public class ReportService {

    private final CaseService caseService;
    private final AssignmentService assignmentService;

    public ReportService(CaseService caseService, AssignmentService assignmentService) {
        this.caseService = caseService;
        this.assignmentService = assignmentService;
    }

    /**
     * Generate a summary report as a list of key-value pairs.
     */
    public Map<String, String> generateSummaryReport() {
        Map<String, String> report = new LinkedHashMap<>();
        report.put("Total Cases", String.valueOf(caseService.count()));
        report.put("Registered", String.valueOf(caseService.countByStatus(CaseStatus.REGISTERED)));
        report.put("Assigned", String.valueOf(caseService.countByStatus(CaseStatus.ASSIGNED)));
        report.put("Under Investigation", String.valueOf(caseService.countByStatus(CaseStatus.UNDER_INVESTIGATION)));
        report.put("Resolved", String.valueOf(caseService.countByStatus(CaseStatus.RESOLVED)));
        report.put("Closed", String.valueOf(caseService.countByStatus(CaseStatus.CLOSED)));
        report.put("Total Fraud Amount", String.format("₹%.2f", caseService.getTotalFraudAmount()));
        report.put("Total Investigators", String.valueOf(assignmentService.getTotalInvestigators()));
        report.put("Available Investigators", String.valueOf(assignmentService.getAvailableInvestigators()));
        report.put("Waiting Queue Size", String.valueOf(caseService.getWaitingQueueSize()));
        return report;
    }

    /**
     * Generate investigator workload report.
     */
    public List<String[]> generateInvestigatorReport() {
        List<String[]> rows = new ArrayList<>();
        for (Investigator inv : assignmentService.getAllInvestigators()) {
            rows.add(new String[]{
                inv.getId(),
                inv.getName(),
                inv.getDepartment(),
                inv.getSpecialization(),
                inv.getCurrentLoad() + "/" + inv.getMaxCaseCapacity(),
                inv.hasCapacity() ? "Available" : "At Capacity",
                inv.isAvailable() ? "Active" : "Inactive"
            });
        }
        return rows;
    }

    /**
     * Generate case type distribution report.
     * Demonstrates: Jagged arrays.
     */
    public String[][] generateTypeDistribution() {
        return caseService.getCaseTypeDistribution();
    }

    /**
     * Generic method to format report data as a table string.
     * Demonstrates: Generic methods.
     *
     * @param <T> the type of data in the list
     */
    public static <T> String formatTable(List<T> items, String[] headers) {
        StringBuilder sb = new StringBuilder();
        // Header
        for (String h : headers) {
            sb.append(String.format("%-20s", h));
        }
        sb.append("\n");
        sb.append("-".repeat(headers.length * 20)).append("\n");
        // Data
        for (T item : items) {
            sb.append(item.toString()).append("\n");
        }
        return sb.toString();
    }
}
