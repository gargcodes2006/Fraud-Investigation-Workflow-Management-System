package fraud.ui;

import fraud.model.*;
import fraud.service.*;
import fraud.net.*;
import fraud.util.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * Main application frame with sidebar navigation and content panels.
 * Demonstrates: Swing layout managers, CardLayout, event handling, composition.
 */
public class MainFrame extends JFrame {

    private final User currentUser;
    private final AuthService authService;
    private final CaseService caseService;
    private final AssignmentService assignmentService;
    private final ReportService reportService;

    // TCP Server
    private CaseStatusServer tcpServer;
    private Thread tcpServerThread;

    // UI components
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JPanel sidebarPanel;
    private JLabel userInfoLabel;
    private JLabel roleLabel;

    // Content panels
    private DashboardPanel dashboardPanel;
    private CaseListPanel caseListPanel;
    private CaseFormPanel caseFormPanel;
    private InvestigatorPanel investigatorPanel;
    private ReportsPanel reportsPanel;
    private AuditLogPanel auditLogPanel;
    private NetworkPanel networkPanel;

    // Sidebar buttons for highlighting
    private JButton[] sidebarButtons;
    private int selectedSidebarIndex = 0;

    public MainFrame(User currentUser, AuthService authService) {
        this.currentUser = currentUser;
        this.authService = authService;

        // Initialize services
        this.caseService = new CaseService();
        this.assignmentService = new AssignmentService(caseService);
        this.reportService = new ReportService(caseService, assignmentService);

        loadData();
        initUI();
        startServices();
    }

    private void loadData() {
        try {
            caseService.loadFromFile();
            assignmentService.loadFromFile();
            authService.loadFromFile();
        } catch (IOException e) {
            // First run — seed demo data
            DataSeeder seeder = new DataSeeder(caseService, assignmentService, authService);
            seeder.seedAll();
            try {
                caseService.saveToFile();
                assignmentService.saveToFile();
                authService.saveToFile();
            } catch (IOException ex) {
                System.err.println("Error saving initial data: " + ex.getMessage());
            }
        }

        // If no cases loaded (empty file), seed
        if (caseService.count() == 0) {
            DataSeeder seeder = new DataSeeder(caseService, assignmentService, authService);
            seeder.seedAll();
        }
    }

    private void startServices() {
        // Start queue processor daemon thread
        assignmentService.startQueueProcessor();

        // Start TCP server on port 9876
        tcpServer = new CaseStatusServer(9876, caseService);
        tcpServerThread = new Thread(tcpServer);
        tcpServerThread.setDaemon(true);
        tcpServerThread.setName("TCPServerThread");
        tcpServerThread.start();
    }

    private void initUI() {
        setTitle("FIWMS — Fraud Investigation Workflow Management System");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);

        // Window close handler — save data
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleExit();
            }
        });

        // Main layout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // === Sidebar ===
        sidebarPanel = createSidebar();
        mainPanel.add(sidebarPanel, BorderLayout.WEST);

        // === Top Header Bar ===
        JPanel headerPanel = createHeader();
        
        // === Content Area ===
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(headerPanel, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UITheme.BG_MAIN);

        // Create all panels
        dashboardPanel = new DashboardPanel(caseService, assignmentService, reportService);
        caseListPanel = new CaseListPanel(caseService, assignmentService, this);
        caseFormPanel = new CaseFormPanel(caseService, assignmentService, this);
        investigatorPanel = new InvestigatorPanel(assignmentService, caseService);
        reportsPanel = new ReportsPanel(reportService, caseService);
        auditLogPanel = new AuditLogPanel();
        networkPanel = new NetworkPanel(caseService, tcpServer);

        contentPanel.add(dashboardPanel, "dashboard");
        contentPanel.add(caseListPanel, "cases");
        contentPanel.add(caseFormPanel, "newcase");
        contentPanel.add(investigatorPanel, "investigators");
        contentPanel.add(reportsPanel, "reports");
        contentPanel.add(auditLogPanel, "audit");
        contentPanel.add(networkPanel, "network");

        rightPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
        showPanel("dashboard");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, UITheme.PRIMARY_DARK,
                        0, getHeight(), new Color(10, 25, 47));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        sidebar.setPreferredSize(new Dimension(UITheme.SIDEBAR_WIDTH, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        // Logo area
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 15, 15));
        logoPanel.setMaximumSize(new Dimension(UITheme.SIDEBAR_WIDTH, 80));

        JLabel logoLabel = new JLabel("  FIWMS");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logoLabel.setForeground(UITheme.TEXT_WHITE);
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoPanel.add(logoLabel);

        JLabel tagline = new JLabel("  Investigation Manager");
        tagline.setFont(UITheme.FONT_SMALL);
        tagline.setForeground(new Color(255, 255, 255, 100));
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoPanel.add(tagline);

        sidebar.add(logoPanel);
        sidebar.add(Box.createVerticalStrut(10));

        // Separator
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 30));
        sep.setMaximumSize(new Dimension(UITheme.SIDEBAR_WIDTH - 30, 1));
        sidebar.add(sep);
        sidebar.add(Box.createVerticalStrut(15));

        // Menu items
        String[][] menuItems = {
            {"📊", "Dashboard", "dashboard"},
            {"📁", "Cases", "cases"},
            {"➕", "New Case", "newcase"},
            {"👤", "Investigators", "investigators"},
            {"📈", "Reports", "reports"},
            {"📋", "Audit Log", "audit"},
            {"🌐", "Network", "network"}
        };

        sidebarButtons = new JButton[menuItems.length];

        for (int i = 0; i < menuItems.length; i++) {
            String icon = menuItems[i][0];
            String label = menuItems[i][1];
            String panelName = menuItems[i][2];

            // Check role-based access
            if (!shouldShowMenuItem(panelName)) continue;

            JButton btn = createSidebarButton(icon + "  " + label, panelName);
            sidebarButtons[i] = btn;
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(2));
        }

        sidebar.add(Box.createVerticalGlue());

        // User info at bottom
        JPanel userPanel = new JPanel();
        userPanel.setOpaque(false);
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        userPanel.setMaximumSize(new Dimension(UITheme.SIDEBAR_WIDTH, 90));

        JSeparator sep2 = new JSeparator();
        sep2.setForeground(new Color(255, 255, 255, 30));
        sep2.setMaximumSize(new Dimension(UITheme.SIDEBAR_WIDTH - 30, 1));
        userPanel.add(sep2);
        userPanel.add(Box.createVerticalStrut(10));

        userInfoLabel = new JLabel("  " + currentUser.getFullName());
        userInfoLabel.setFont(UITheme.FONT_SIDEBAR);
        userInfoLabel.setForeground(UITheme.TEXT_WHITE);
        userInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userPanel.add(userInfoLabel);

        roleLabel = new JLabel("  " + currentUser.getRole().getDisplayName());
        roleLabel.setFont(UITheme.FONT_SMALL);
        roleLabel.setForeground(new Color(255, 255, 255, 120));
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userPanel.add(roleLabel);
        userPanel.add(Box.createVerticalStrut(8));

        JButton logoutBtn = new JButton("  🚪 Logout");
        logoutBtn.setFont(UITheme.FONT_SMALL);
        logoutBtn.setForeground(new Color(255, 150, 150));
        logoutBtn.setBackground(new Color(255, 255, 255, 10));
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setContentAreaFilled(false);
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> handleLogout());
        userPanel.add(logoutBtn);

        sidebar.add(userPanel);

        return sidebar;
    }

    private boolean shouldShowMenuItem(String panelName) {
        UserRole role = currentUser.getRole();
        return switch (panelName) {
            case "dashboard", "cases", "reports", "newcase" -> true;
            case "investigators" -> role == UserRole.ADMIN;
            case "audit" -> role == UserRole.ADMIN || role == UserRole.ANALYST;
            case "network" -> role == UserRole.ADMIN;
            default -> true;
        };
    }

    private JButton createSidebarButton(String text, String panelName) {
        JButton btn = new JButton(text) {
            boolean isHovered = false;
            boolean isSelected = false;

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        repaint();
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        repaint();
                    }
                });
            }

            public void setSelected(boolean selected) {
                this.isSelected = selected;
                repaint();
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (isSelected) {
                    g2.setColor(new Color(49, 130, 206, 40));
                    g2.fillRoundRect(5, 0, getWidth() - 10, getHeight(), 8, 8);
                    // Left indicator
                    g2.setColor(UITheme.ACCENT);
                    g2.fillRoundRect(0, 4, 4, getHeight() - 8, 3, 3);
                } else if (isHovered) {
                    g2.setColor(new Color(255, 255, 255, 15));
                    g2.fillRoundRect(5, 0, getWidth() - 10, getHeight(), 8, 8);
                }
                g2.dispose();

                super.paintComponent(g);
            }
        };

        btn.setFont(UITheme.FONT_SIDEBAR);
        btn.setForeground(UITheme.TEXT_ON_DARK);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(UITheme.SIDEBAR_WIDTH, 42));
        btn.setPreferredSize(new Dimension(UITheme.SIDEBAR_WIDTH, 42));
        btn.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 10));

        btn.addActionListener(e -> showPanel(panelName));

        return btn;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.BG_HEADER);
        header.setPreferredSize(new Dimension(0, UITheme.HEADER_HEIGHT));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));

        JLabel pageTitle = new JLabel("Dashboard");
        pageTitle.setFont(UITheme.FONT_HEADING);
        pageTitle.setForeground(UITheme.TEXT_PRIMARY);
        header.add(pageTitle, BorderLayout.WEST);

        // Right side — quick actions
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightHeader.setOpaque(false);

        JButton saveBtn = UIComponents.createSecondaryButton("💾 Save");
        saveBtn.setPreferredSize(new Dimension(90, 32));
        saveBtn.addActionListener(e -> saveAllData());
        rightHeader.add(saveBtn);

        JButton backupBtn = UIComponents.createSecondaryButton("📦 Backup");
        backupBtn.setPreferredSize(new Dimension(100, 32));
        backupBtn.addActionListener(e -> createBackup());
        rightHeader.add(backupBtn);

        header.add(rightHeader, BorderLayout.EAST);
        return header;
    }

    // === Navigation ===

    public void showPanel(String panelName) {
        cardLayout.show(contentPanel, panelName);

        // Refresh panels
        switch (panelName) {
            case "dashboard" -> dashboardPanel.refresh();
            case "cases" -> caseListPanel.refresh();
            case "investigators" -> investigatorPanel.refresh();
            case "reports" -> reportsPanel.refresh();
            case "audit" -> auditLogPanel.refresh();
            case "network" -> networkPanel.refresh();
        }

        // Update sidebar selection
        updateSidebarSelection(panelName);
    }

    public void showCaseForm(FraudCase existingCase) {
        caseFormPanel.loadCase(existingCase);
        showPanel("newcase");
    }

    public void showNewCaseForm() {
        caseFormPanel.resetForm();
        showPanel("newcase");
    }

    private void updateSidebarSelection(String panelName) {
        String[][] menuItems = {
            {"dashboard"}, {"cases"}, {"newcase"}, {"investigators"},
            {"reports"}, {"audit"}, {"network"}
        };
        for (int i = 0; i < menuItems.length; i++) {
            if (sidebarButtons[i] != null) {
                // Use reflection-free approach — call repaint
                boolean isSelected = menuItems[i][0].equals(panelName);
                JButton btn = sidebarButtons[i];
                btn.putClientProperty("selected", isSelected);
                // Trigger custom paint
                if (isSelected) {
                    btn.setForeground(UITheme.TEXT_WHITE);
                } else {
                    btn.setForeground(UITheme.TEXT_ON_DARK);
                }
            }
        }
    }

    // === Data Operations ===

    private void saveAllData() {
        try {
            caseService.saveToFile();
            assignmentService.saveToFile();
            authService.saveToFile();
            AuditLogger.getInstance().log(currentUser.getUsername(), "DATA_SAVED", "All data saved.");
            UIComponents.showInfo(this, "All data saved successfully.", "Save Successful");
        } catch (IOException e) {
            UIComponents.showError(this, "Error saving data: " + e.getMessage(), "Save Error");
        }
    }

    private void createBackup() {
        try {
            String backupPath = caseService.createBackup();
            assignmentService.saveToFile();
            AuditLogger.getInstance().log(currentUser.getUsername(), "BACKUP_CREATED", 
                    "Backup created: " + backupPath);
            UIComponents.showInfo(this, "Backup created successfully:\n" + backupPath, "Backup Created");
        } catch (IOException e) {
            UIComponents.showError(this, "Error creating backup: " + e.getMessage(), "Backup Error");
        }
    }

    private void handleLogout() {
        if (UIComponents.showConfirm(this, "Save data before logging out?", "Logout")) {
            saveAllData();
        }
        authService.logout();
        dispose();
        new LoginFrame(authService).setVisible(true);
    }

    private void handleExit() {
        if (UIComponents.showConfirm(this, "Save data and exit?", "Exit Application")) {
            try {
                caseService.saveToFile();
                assignmentService.saveToFile();
                authService.saveToFile();
            } catch (IOException e) {
                System.err.println("Error saving on exit: " + e.getMessage());
            }
        }
        if (tcpServer != null) tcpServer.stop();
        assignmentService.stopQueueProcessor();
        dispose();
        System.exit(0);
    }

    // === Accessors for child panels ===
    public User getCurrentUser() { return currentUser; }
    public CaseService getCaseService() { return caseService; }
    public AssignmentService getAssignmentService() { return assignmentService; }
}
