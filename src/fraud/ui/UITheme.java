package fraud.ui;

import java.awt.*;

/**
 * Centralized theme/color constants for the application UI.
 * Provides a consistent, professional financial-investigation colour palette.
 */
public final class UITheme {

    private UITheme() {}

    // === Primary Palette ===
    public static final Color PRIMARY = new Color(26, 54, 93);       // Deep navy
    public static final Color PRIMARY_LIGHT = new Color(44, 82, 130);
    public static final Color PRIMARY_DARK = new Color(17, 36, 62);

    // === Accent ===
    public static final Color ACCENT = new Color(49, 130, 206);      // Professional blue
    public static final Color ACCENT_LIGHT = new Color(66, 153, 225);

    // === Background ===
    public static final Color BG_MAIN = new Color(237, 242, 247);    // Light grey-blue
    public static final Color BG_CARD = Color.WHITE;
    public static final Color BG_SIDEBAR = PRIMARY_DARK;
    public static final Color BG_HEADER = Color.WHITE;

    // === Text ===
    public static final Color TEXT_PRIMARY = new Color(26, 32, 44);
    public static final Color TEXT_SECONDARY = new Color(113, 128, 150);
    public static final Color TEXT_ON_DARK = new Color(226, 232, 240);
    public static final Color TEXT_WHITE = Color.WHITE;

    // === Status Colors ===
    public static final Color STATUS_REGISTERED = new Color(66, 153, 225);   // Blue
    public static final Color STATUS_ASSIGNED = new Color(237, 137, 54);     // Orange
    public static final Color STATUS_UNDER_INV = new Color(221, 107, 32);    // Dark orange
    public static final Color STATUS_RESOLVED = new Color(72, 187, 120);     // Green
    public static final Color STATUS_CLOSED = new Color(160, 174, 192);      // Grey

    // === Priority Colors ===
    public static final Color PRIORITY_LOW = new Color(72, 187, 120);
    public static final Color PRIORITY_MEDIUM = new Color(237, 137, 54);
    public static final Color PRIORITY_HIGH = new Color(245, 101, 101);
    public static final Color PRIORITY_CRITICAL = new Color(229, 62, 62);

    // === UI Elements ===
    public static final Color BORDER = new Color(226, 232, 240);
    public static final Color HOVER = new Color(235, 248, 255);
    public static final Color SELECTED = new Color(190, 227, 248);
    public static final Color SHADOW = new Color(0, 0, 0, 30);
    public static final Color SUCCESS = new Color(56, 161, 105);
    public static final Color WARNING = new Color(214, 158, 46);
    public static final Color ERROR = new Color(229, 62, 62);

    // === KPI Card Colors ===
    public static final Color KPI_BLUE = new Color(49, 130, 206);
    public static final Color KPI_GREEN = new Color(56, 161, 105);
    public static final Color KPI_ORANGE = new Color(221, 107, 32);
    public static final Color KPI_RED = new Color(229, 62, 62);
    public static final Color KPI_PURPLE = new Color(128, 90, 213);
    public static final Color KPI_TEAL = new Color(56, 178, 172);

    // === Fonts ===
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_SUBHEADING = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SIDEBAR = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_KPI_VALUE = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_KPI_LABEL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_TABLE_HEADER = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_TABLE = new Font("Segoe UI", Font.PLAIN, 12);

    // === Dimensions ===
    public static final int SIDEBAR_WIDTH = 220;
    public static final int HEADER_HEIGHT = 60;
    public static final int CARD_PADDING = 20;
    public static final int CARD_RADIUS = 12;
    public static final int BUTTON_RADIUS = 8;

    // === Utility Methods ===

    public static Color getStatusColor(String status) {
        if (status == null) return TEXT_SECONDARY;
        return switch (status.toUpperCase().replace(" ", "_")) {
            case "REGISTERED" -> STATUS_REGISTERED;
            case "ASSIGNED" -> STATUS_ASSIGNED;
            case "UNDER_INVESTIGATION" -> STATUS_UNDER_INV;
            case "RESOLVED" -> STATUS_RESOLVED;
            case "CLOSED" -> STATUS_CLOSED;
            default -> TEXT_SECONDARY;
        };
    }

    public static Color getPriorityColor(String priority) {
        if (priority == null) return TEXT_SECONDARY;
        return switch (priority.toUpperCase()) {
            case "LOW" -> PRIORITY_LOW;
            case "MEDIUM" -> PRIORITY_MEDIUM;
            case "HIGH" -> PRIORITY_HIGH;
            case "CRITICAL" -> PRIORITY_CRITICAL;
            default -> TEXT_SECONDARY;
        };
    }
}
