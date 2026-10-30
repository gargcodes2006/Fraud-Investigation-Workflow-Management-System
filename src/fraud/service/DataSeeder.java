package fraud.service;

import fraud.model.*;
import fraud.exception.*;
import fraud.util.*;

import java.io.IOException;
import java.util.*;

/**
 * Seed data service for populating the application with fictional demo records.
 * All data is clearly labelled as fictional and for demonstration purposes only.
 * 
 * Demonstrates: Object creation, constructors, collections, composition.
 */
public class DataSeeder {

    private final CaseService caseService;
    private final AssignmentService assignmentService;
    private final AuthService authService;

    public DataSeeder(CaseService caseService, AssignmentService assignmentService, AuthService authService) {
        this.caseService = caseService;
        this.assignmentService = assignmentService;
        this.authService = authService;
    }

    /**
     * Seed all demo data. Called on first run or when no data files exist.
     * All persons, amounts, and details are entirely fictional.
     */
    public void seedAll() {
        seedUsers();
        seedInvestigators();
        seedCases();
        AuditLogger.getInstance().log("System", "DATA_SEEDED", 
                "Demo data seeded successfully. All records are fictional.");
    }

    private void seedUsers() {
        authService.seedDemoUsers();
    }

    private void seedInvestigators() {
        // Fictional investigators — demonstrates object creation
        Investigator[] investigators = {
            new Investigator("INV-101", "Rajesh Kumar", "rajesh.kumar@demo.gov.in", 
                "9876543210", "Demo Address, New Delhi", "Financial Crimes", "Bank Fraud", 5),
            new Investigator("INV-102", "Priya Sharma", "priya.sharma@demo.gov.in",
                "9876543211", "Demo Address, Mumbai", "Cyber Crimes", "Cyber Fraud", 4),
            new Investigator("INV-103", "Amit Singh", "amit.singh@demo.gov.in",
                "9876543212", "Demo Address, Bangalore", "Corporate Fraud", "Corporate Fraud", 5),
            new Investigator("INV-104", "Sneha Patel", "sneha.patel@demo.gov.in",
                "9876543213", "Demo Address, Hyderabad", "Insurance Division", "Insurance Fraud", 3),
            new Investigator("INV-105", "Vikram Reddy", "vikram.reddy@demo.gov.in",
                "9876543214", "Demo Address, Chennai", "Identity Crimes", "Identity Theft", 4)
        };

        for (Investigator inv : investigators) {
            try {
                assignmentService.addInvestigator(inv);
            } catch (DuplicateEntityException e) {
                System.err.println("Investigator already exists: " + e.getMessage());
            }
        }
        IDGenerator.setInvestigatorCounter(105);
    }

    private void seedCases() {
        // Fictional cases — all amounts and details are simulated
        try {
            // Case 1: Bank Fraud — Registered, then Assigned
            Complainant comp1 = new Complainant("CMP-201", "Ananya Verma (Fictional)", 
                "ananya@demo.com", "9812345670", "Demo Street, Delhi",
                "Unauthorized bank transfers detected in demo account", "2026-10-01");
            Suspect sus1 = new Suspect("SUS-301", "Unknown Actor Alpha (Fictional)",
                "unknown@demo.com", "0000000000", "Unknown",
                "Suspected of initiating unauthorized transfers", "HIGH");

            FraudCase c1 = caseService.registerCase(
                "Unauthorized Bank Transfers [DEMO]",
                "Multiple unauthorized transfers detected from demo savings account ending in XXXX1234. Fictional scenario for demonstration.",
                CaseType.BANK_FRAUD, 250000.00, "HIGH", comp1, sus1);
            try {
                assignmentService.assignCase(c1.getCaseId());
            } catch (CapacityExceededException ignored) {}

            // Case 2: Insurance Fraud — Under Investigation
            Complainant comp2 = new Complainant("CMP-202", "Demo Insurance Corp (Fictional)",
                "claims@demoinsurance.com", "1800123456", "Demo Tower, Mumbai",
                "Suspicious insurance claim with falsified documents", "2026-09-15");
            Suspect sus2 = new Suspect("SUS-302", "Rahul Mehra (Fictional)",
                "rahul.m@demo.com", "9876000001", "Demo Road, Pune",
                "Filed multiple claims with inconsistent documentation", "CRITICAL");

            FraudCase c2 = caseService.registerCase(
                "Falsified Insurance Claim [DEMO]",
                "Insurance claim for vehicle damage with suspected falsified repair receipts. All details fictional.",
                CaseType.INSURANCE_FRAUD, 500000.00, "CRITICAL", comp2, sus2);
            try {
                assignmentService.assignCase(c2.getCaseId());
                caseService.transitionStatus(c2.getCaseId(), CaseStatus.UNDER_INVESTIGATION, "INV-104");
            } catch (Exception ignored) {}

            // Case 3: Cyber Fraud — Assigned
            Complainant comp3 = new Complainant("CMP-203", "TechDemo Solutions (Fictional)",
                "security@techdemo.com", "1800654321", "Demo Tech Park, Bangalore",
                "Phishing attack targeting employee credentials", "2026-10-10");
            Suspect sus3 = new Suspect("SUS-303", "External Threat Actor (Fictional)",
                "none@demo.com", "0000000000", "Unknown IP Range",
                "Sophisticated phishing campaign targeting corporate email", "HIGH");

            FraudCase c3 = caseService.registerCase(
                "Corporate Phishing Campaign [DEMO]",
                "Organized phishing campaign targeting 50+ demo employees with spoofed emails. Fictional scenario.",
                CaseType.CYBER_FRAUD, 150000.00, "HIGH", comp3, sus3);
            try {
                assignmentService.assignCase(c3.getCaseId());
            } catch (CapacityExceededException ignored) {}

            // Case 4: Identity Theft — Resolved
            Complainant comp4 = new Complainant("CMP-204", "Suresh Gupta (Fictional)",
                "suresh.g@demo.com", "9123456780", "Demo Colony, Jaipur",
                "Identity used to open fraudulent demo bank accounts", "2026-08-20");
            Suspect sus4 = new Suspect("SUS-304", "Deepak Yadav (Fictional)",
                "deepak.y@demo.com", "9876000002", "Demo Area, Lucknow",
                "Used stolen identity documents for financial gain", "MEDIUM");

            FraudCase c4 = caseService.registerCase(
                "Identity Document Misuse [DEMO]",
                "Stolen identity documents used to open 3 bank accounts and apply for credit cards. All fictional.",
                CaseType.IDENTITY_THEFT, 75000.00, "MEDIUM", comp4, sus4);
            try {
                assignmentService.assignCase(c4.getCaseId());
                caseService.transitionStatus(c4.getCaseId(), CaseStatus.UNDER_INVESTIGATION, "INV-105");
                caseService.transitionStatus(c4.getCaseId(), CaseStatus.RESOLVED, "INV-105");
            } catch (Exception ignored) {}

            // Case 5: Corporate Fraud — Registered (unassigned)
            Complainant comp5 = new Complainant("CMP-205", "Demo Holdings Ltd (Fictional)",
                "compliance@demoholdings.com", "1800999888", "Demo Business District, Gurgaon",
                "Suspected embezzlement by senior management", "2026-10-25");
            Suspect sus5 = new Suspect("SUS-305", "Senior Executive (Fictional)",
                "exec@demo.com", "9876000003", "Demo Tower, Gurgaon",
                "Redirected corporate funds to personal demo accounts", "CRITICAL");

            caseService.registerCase(
                "Corporate Embezzlement Suspicion [DEMO]",
                "Suspected diversion of ₹10L in corporate funds through shell demo companies. Fictional scenario.",
                CaseType.CORPORATE_FRAUD, 1000000.00, "CRITICAL", comp5, sus5);

            // Case 6: Tax Fraud — Assigned
            Complainant comp6 = new Complainant("CMP-206", "Demo Tax Authority (Fictional)",
                "enforcement@demotax.gov.in", "1800111222", "Demo Bhawan, New Delhi",
                "Discrepancy in reported income vs actual transactions", "2026-10-05");
            Suspect sus6 = new Suspect("SUS-306", "Demo Business Owner (Fictional)",
                "owner@demobiz.com", "9876000004", "Demo Market, Delhi",
                "Significant under-reporting of business income", "HIGH");

            FraudCase c6 = caseService.registerCase(
                "Income Under-Reporting [DEMO]",
                "Business demo entity reported ₹5L income while bank records show ₹25L in transactions. Fictional.",
                CaseType.TAX_FRAUD, 2000000.00, "HIGH", comp6, sus6);
            try {
                assignmentService.assignCase(c6.getCaseId());
            } catch (CapacityExceededException ignored) {}

            // Case 7: Closed case
            Complainant comp7 = new Complainant("CMP-207", "Demo Bank (Fictional)",
                "fraud@demobank.com", "1800333444", "Demo Finance District, Mumbai",
                "Fraudulent loan application with forged documents", "2026-07-01");
            Suspect sus7 = new Suspect("SUS-307", "Loan Applicant (Fictional)",
                "applicant@demo.com", "9876000005", "Demo Suburb, Mumbai",
                "Submitted forged income certificates for loan approval", "MEDIUM");

            FraudCase c7 = caseService.registerCase(
                "Forged Loan Documents [DEMO]",
                "Loan of ₹15L approved based on forged income and employment certificates. Case resolved. Fictional.",
                CaseType.BANK_FRAUD, 1500000.00, "MEDIUM", comp7, sus7);
            try {
                assignmentService.assignCase(c7.getCaseId());
                caseService.transitionStatus(c7.getCaseId(), CaseStatus.UNDER_INVESTIGATION, "INV-101");
                caseService.transitionStatus(c7.getCaseId(), CaseStatus.RESOLVED, "INV-101");
                caseService.transitionStatus(c7.getCaseId(), CaseStatus.CLOSED, "admin");
                assignmentService.unassignCase(c7.getCaseId());
            } catch (Exception ignored) {}

            // Case 8: Credit Card Fraud
            Complainant comp8 = new Complainant("CMP-208", "Meena Kapoor (Fictional)",
                "meena.k@demo.com", "9345678901", "Demo Apartments, Kolkata",
                "Unauthorized credit card transactions at multiple demo vendors", "2026-10-20");
            Suspect sus8 = new Suspect("SUS-308", "Card Skimmer Group (Fictional)",
                "unknown@demo.com", "0000000000", "Unknown",
                "Organized credit card skimming operation at demo ATMs", "HIGH");

            FraudCase c8 = caseService.registerCase(
                "Credit Card Skimming Ring [DEMO]",
                "Series of unauthorized transactions totaling ₹3.5L from skimmed credit card data. Fictional.",
                CaseType.CREDIT_CARD_FRAUD, 350000.00, "HIGH", comp8, sus8);
            try {
                assignmentService.assignCase(c8.getCaseId());
            } catch (CapacityExceededException ignored) {}

            // Add some evidence to cases
            seedEvidence(c1.getCaseId());
            seedEvidence(c2.getCaseId());

            IDGenerator.setComplainantCounter(208);
            IDGenerator.setSuspectCounter(308);

        } catch (ValidationException | DuplicateEntityException e) {
            System.err.println("Error seeding cases: " + e.getMessage());
        }
    }

    private void seedEvidence(String caseId) {
        FraudCase fc = caseService.findById(caseId);
        if (fc == null) return;

        Evidence e1 = new Evidence(IDGenerator.generateEvidenceId(), caseId, "Document",
                "Bank statements showing unauthorized transactions [DEMO]", "2026-10-15",
                "INV-101", "Verified");
        Evidence e2 = new Evidence(IDGenerator.generateEvidenceId(), caseId, "Digital",
                "IP access logs from banking portal [DEMO]", "2026-10-16",
                "INV-102", "Under Analysis");
        Evidence e3 = new Evidence(IDGenerator.generateEvidenceId(), caseId, "Testimonial",
                "Witness statement from bank manager [DEMO]", "2026-10-17",
                "INV-101", "Collected");

        fc.addEvidence(e1);
        fc.addEvidence(e2);
        fc.addEvidence(e3);
        caseService.update(fc);
    }
}
