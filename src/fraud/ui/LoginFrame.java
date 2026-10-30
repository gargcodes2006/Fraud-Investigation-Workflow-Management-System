package fraud.ui;

import fraud.model.User;
import fraud.service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Login screen with role-aware navigation.
 * 
 * DEMO CREDENTIALS:
 * ┌──────────────┬──────────────┬───────────────┐
 * │ Username     │ Password     │ Role          │
 * ├──────────────┼──────────────┼───────────────┤
 * │ admin        │ admin123     │ ADMIN         │
 * │ investigator │ invest123    │ INVESTIGATOR  │
 * │ analyst      │ analyst123   │ ANALYST       │
 * └──────────────┴──────────────┴───────────────┘
 */
public class LoginFrame extends JFrame {

    private final AuthService authService;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;
    private JLabel credentialsLabel;

    public LoginFrame(AuthService authService) {
        this.authService = authService;
        initUI();
    }

    private void initUI() {
        setTitle("Fraud Investigation Workflow Management System — Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setMinimumSize(new Dimension(800, 550));
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with split layout
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        // === Left Panel: Branding ===
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                GradientPaint gp = new GradientPaint(0, 0, UITheme.PRIMARY_DARK, 
                        getWidth(), getHeight(), UITheme.PRIMARY);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Decorative circles
                g2.setColor(new Color(255, 255, 255, 15));
                g2.fillOval(-50, -50, 300, 300);
                g2.fillOval(getWidth() - 200, getHeight() - 200, 350, 350);
                g2.setColor(new Color(255, 255, 255, 10));
                g2.fillOval(100, getHeight() - 300, 250, 250);

                g2.dispose();
            }
        };
        leftPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(5, 30, 5, 30);

        // Shield icon (drawn)
        JLabel iconLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                
                // Shield shape
                g2.setColor(new Color(255, 255, 255, 200));
                int[] xPoints = {cx, cx + 35, cx + 30, cx, cx - 30, cx - 35};
                int[] yPoints = {cy - 35, cy - 15, cy + 20, cy + 40, cy + 20, cy - 15};
                g2.fillPolygon(xPoints, yPoints, 6);
                
                // Checkmark
                g2.setColor(UITheme.PRIMARY);
                g2.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(cx - 12, cy, cx - 2, cy + 12);
                g2.drawLine(cx - 2, cy + 12, cx + 15, cy - 8);
                
                g2.dispose();
            }
        };
        iconLabel.setPreferredSize(new Dimension(80, 80));
        gbc.gridy = 0;
        leftPanel.add(iconLabel, gbc);

        JLabel titleLabel = new JLabel("FIWMS");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(UITheme.TEXT_WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        leftPanel.add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("<html><center>Fraud Investigation<br>Workflow Management System</center></html>");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(UITheme.TEXT_ON_DARK);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 2;
        leftPanel.add(subtitleLabel, gbc);

        JLabel sdgLabel = new JLabel("SDG 16: Peace, Justice & Strong Institutions");
        sdgLabel.setFont(UITheme.FONT_SMALL);
        sdgLabel.setForeground(new Color(255, 255, 255, 120));
        sdgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 3;
        gbc.insets = new Insets(20, 30, 5, 30);
        leftPanel.add(sdgLabel, gbc);

        JLabel teamLabel = new JLabel("<html><center>Group 28 — CCSE0355<br>OOT using Java</center></html>");
        teamLabel.setFont(UITheme.FONT_SMALL);
        teamLabel.setForeground(new Color(255, 255, 255, 100));
        teamLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 4;
        gbc.insets = new Insets(5, 30, 5, 30);
        leftPanel.add(teamLabel, gbc);

        // === Right Panel: Login Form ===
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(UITheme.BG_MAIN);
        rightPanel.setLayout(new GridBagLayout());

        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        JLabel loginTitle = new JLabel("Sign In");
        loginTitle.setFont(UITheme.FONT_TITLE);
        loginTitle.setForeground(UITheme.TEXT_PRIMARY);
        loginTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(loginTitle);

        JLabel loginSubtitle = new JLabel("Enter your credentials to access the system");
        loginSubtitle.setFont(UITheme.FONT_BODY);
        loginSubtitle.setForeground(UITheme.TEXT_SECONDARY);
        loginSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(loginSubtitle);
        formPanel.add(Box.createVerticalStrut(30));

        // Username
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(UITheme.FONT_SUBHEADING);
        userLabel.setForeground(UITheme.TEXT_PRIMARY);
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(userLabel);
        formPanel.add(Box.createVerticalStrut(5));

        usernameField = UIComponents.createTextField("Enter your username");
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(15));

        // Password
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(UITheme.FONT_SUBHEADING);
        passLabel.setForeground(UITheme.TEXT_PRIMARY);
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(passLabel);
        formPanel.add(Box.createVerticalStrut(5));

        passwordField = new JPasswordField();
        passwordField.setFont(UITheme.FONT_BODY);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                new javax.swing.border.LineBorder(UITheme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(8));

        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setForeground(UITheme.ERROR);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(statusLabel);
        formPanel.add(Box.createVerticalStrut(10));

        // Login button
        JButton loginButton = UIComponents.createPrimaryButton("Sign In");
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.addActionListener(e -> performLogin());
        formPanel.add(loginButton);
        formPanel.add(Box.createVerticalStrut(20));

        // Demo credentials info
        credentialsLabel = new JLabel("<html><div style='font-size:10px; color:#718096;'>" +
                "<b>Demo Credentials:</b><br>" +
                "Admin: <b>admin</b> / <b>admin123</b><br>" +
                "Investigator: <b>investigator</b> / <b>invest123</b><br>" +
                "Analyst: <b>analyst</b> / <b>analyst123</b></div></html>");
        credentialsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(credentialsLabel);

        rightPanel.add(formPanel);

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        setContentPane(mainPanel);

        // Enter key triggers login
        passwordField.addActionListener(e -> performLogin());
        usernameField.addActionListener(e -> passwordField.requestFocus());

        // Focus username on open
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                usernameField.requestFocusInWindow();
            }
        });
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both username and password.");
            return;
        }

        User user = authService.authenticate(username, password);
        if (user != null) {
            statusLabel.setForeground(UITheme.SUCCESS);
            statusLabel.setText("Login successful! Redirecting...");
            
            // Open main frame after brief delay
            Timer timer = new Timer(500, e -> {
                dispose();
                new MainFrame(user, authService).setVisible(true);
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            statusLabel.setForeground(UITheme.ERROR);
            statusLabel.setText("Invalid username or password. Please try again.");
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }
}
