package fraud.model;

/**
 * Enum representing the type/category of a fraud investigation case.
 * Demonstrates: Enums with fields, constructor overloading concept.
 */
public enum CaseType {
    BANK_FRAUD("Bank Fraud"),
    INSURANCE_FRAUD("Insurance Fraud"),
    IDENTITY_THEFT("Identity Theft"),
    CYBER_FRAUD("Cyber Fraud"),
    CORPORATE_FRAUD("Corporate Fraud"),
    TAX_FRAUD("Tax Fraud"),
    INVESTMENT_FRAUD("Investment Fraud"),
    CREDIT_CARD_FRAUD("Credit Card Fraud");

    private final String displayName;

    CaseType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static String[] getAllDisplayNames() {
        CaseType[] values = values();
        String[] names = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            names[i] = values[i].displayName;
        }
        return names;
    }

    public static CaseType fromDisplayName(String name) {
        for (CaseType type : values()) {
            if (type.displayName.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
