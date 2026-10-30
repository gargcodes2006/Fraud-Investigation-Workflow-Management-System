package fraud.ui;

import fraud.model.*;
import fraud.service.*;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Dashboard panel with KPI cards and status summaries.
 * Demonstrates: Swing layout, dynamic component creation.
 */
public class DashboardPanel extends JPanel {

    private final CaseService caseService;
    private final AssignmentService assignmentService;
    private final ReportService reportService;
    private JPanel kpiRow;
    private JPanel statusGrid;
    private JPanel recentPanel;

    public DashboardPanel(CaseService caseService, AssignmentService assignmentService,
                          ReportService reportService) {
        this.caseService = caseService;
        this.assignmentService = assignmentService;
        this.reportService = reportService;
        initUI();
    }

    private void initUI() {
        setBackground(UITheme.BG_MAIN);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // Title
        JLabel title = UIComponents.createSectionHeader("Dashboard Overview");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(15));

        // KPI Cards Row
        kpiRow = new JPanel(new GridLayout(1, 6, 15, 0));
        kpiRow.setOpaque(false);
        kpiRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        kpiRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        content.add(kpiRow);
        content.add(Box.createVerticalStrut(20));

        // Status Summary and Quick Info Row
        JPanel middleRow = new JPanel(new GridLayout(1, 2, 15, 0));
        middleRow.setOpaque(false);
        middleRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        middleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));

        // Status breakdown card
        statusGrid = UIComponents.createCardPanel();
        statusGrid.setLayout(new BorderLayout());
        middleRow.add(statusGrid);

        // Investigator summary card
        JPanel invSummary = UIComponents.createCardPanel();
        invSummary.setLayout(new BorderLayout());
        middleRow.add(invSummary);

        content.add(middleRow);
        content.add(Box.createVerticalStrut(20));

        // Recent Cases
        recentPanel = UIComponents.createCardPanel();
        recentPanel.setLayout(new BorderLayout());
        recentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(recentPanel);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(UITheme.BG_MAIN);
        scrollPane.getViewport().setBackground(UITheme.BG_MAIN);
        add(scrollPane, BorderLayout.CENTER);

        refresh();
    }

    public void refresh() {
        // Update KPI cards
        kpiRow.removeAll();
        kpiRow.add(UIComponents.createKPICard("Total Cases",
                String.valueOf(caseService.count()), UITheme.KPI_BLUE));
        kpiRow.add(UIComponents.createKPICard("Active Cases",
                String.valueOf(caseService.countByStatus(CaseStatus.ASSIGNED) +
                        caseService.countByStatus(CaseStatus.UNDER_INVESTIGATION)),
                UITheme.KPI_ORANGE));
        kpiRow.add(UIComponents.createKPICard("Resolved",
                String.valueOf(caseService.countByStatus(CaseStatus.RESOLVED)), UITheme.KPI_GREEN));
        kpiRow.add(UIComponents.createKPICard("Investigators",
                String.valueOf(assignmentService.getTotalInvestigators()), UITheme.KPI_PURPLE));
        kpiRow.add(UIComponents.createKPICard("Queue",
                String.valueOf(caseService.getWaitingQueueSize()), UITheme.KPI_RED));
        kpiRow.add(UIComponents.createKPICard("Total Amount",
                String.format("₹%.0fK", caseService.getTotalFraudAmount() / 1000), UITheme.KPI_TEAL));

        // Update status breakdown
        statusGrid.removeAll();
        JLabel statusTitle = new JLabel("  Case Status Breakdown");
        statusTitle.setFont(UITheme.FONT_SUBHEADING);
        statusTitle.setForeground(UITheme.TEXT_PRIMARY);
        statusTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        statusGrid.add(statusTitle, BorderLayout.NORTH);

        JPanel statusRows = new JPanel();
        statusRows.setOpaque(false);
        statusRows.setLayout(new BoxLayout(statusRows, BoxLayout.Y_AXIS));

        for (CaseStatus status : CaseStatus.values()) {
            int count = caseService.countByStatus(status);
            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);
            row.setBorder(BorderFactory.createEmptyBorder(6, 5, 6, 5));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

            JPanel leftPart = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            leftPart.setOpaque(false);

            // Color dot
            JPanel dot = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(UITheme.getStatusColor(status.name()));
                    g2.fillOval(2, 2, 10, 10);
                    g2.dispose();
                }
            };
            dot.setPreferredSize(new Dimension(14, 14));
            dot.setOpaque(false);
            leftPart.add(dot);

            JLabel nameLabel = new JLabel(status.getDisplayName());
            nameLabel.setFont(UITheme.FONT_BODY);
            nameLabel.setForeground(UITheme.TEXT_PRIMARY);
            leftPart.add(nameLabel);

            row.add(leftPart, BorderLayout.WEST);

            JLabel countLabel = new JLabel(String.valueOf(count) + "  ");
            countLabel.setFont(UITheme.FONT_SUBHEADING);
            countLabel.setForeground(UITheme.TEXT_PRIMARY);
            row.add(countLabel, BorderLayout.EAST);

            statusRows.add(row);
        }
        statusGrid.add(statusRows, BorderLayout.CENTER);

        // Update recent cases
        recentPanel.removeAll();
        JLabel recentTitle = new JLabel("  Recent Cases");
        recentTitle.setFont(UITheme.FONT_SUBHEADING);
        recentTitle.setForeground(UITheme.TEXT_PRIMARY);
        recentTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        recentPanel.add(recentTitle, BorderLayout.NORTH);

        JPanel recentList = new JPanel();
        recentList.setOpaque(false);
        recentList.setLayout(new BoxLayout(recentList, BoxLayout.Y_AXIS));

        java.util.List<FraudCase> cases = caseService.findAll();
        int showCount = Math.min(5, cases.size());
        for (int i = cases.size() - 1; i >= cases.size() - showCount && i >= 0; i--) {
            FraudCase fc = cases.get(i);
            JPanel caseRow = new JPanel(new BorderLayout());
            caseRow.setOpaque(false);
            caseRow.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER),
                    BorderFactory.createEmptyBorder(8, 5, 8, 5)));
            caseRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

            JLabel caseInfo = new JLabel(fc.getCaseId() + " — " + fc.getTitle());
            caseInfo.setFont(UITheme.FONT_BODY);
            caseInfo.setForeground(UITheme.TEXT_PRIMARY);
            caseRow.add(caseInfo, BorderLayout.WEST);

            JLabel badge = UIComponents.createStatusBadge(fc.getStatus().getDisplayName());
            caseRow.add(badge, BorderLayout.EAST);

            recentList.add(caseRow);
        }
        recentPanel.add(recentList, BorderLayout.CENTER);

        revalidate();
        repaint();
    }
}
