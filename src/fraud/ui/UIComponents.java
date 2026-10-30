package fraud.ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Reusable custom UI components for a polished, modern look.
 * Demonstrates: Swing components, custom painting, event handling.
 */
public final class UIComponents {

    private UIComponents() {}

    // === Rounded Panel ===

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.BG_CARD);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(),
                        UITheme.CARD_RADIUS, UITheme.CARD_RADIUS));
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(
                UITheme.CARD_PADDING, UITheme.CARD_PADDING,
                UITheme.CARD_PADDING, UITheme.CARD_PADDING));
        return panel;
    }

    // === KPI Card ===

    public static JPanel createKPICard(String label, String value, Color accentColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Card background
                g2.setColor(UITheme.BG_CARD);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));

                // Accent stripe on left
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, 5, getHeight(), 5, 5);

                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 16));
        card.setPreferredSize(new Dimension(200, 95));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(UITheme.FONT_KPI_VALUE);
        valueLabel.setForeground(UITheme.TEXT_PRIMARY);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nameLabel = new JLabel(label);
        nameLabel.setFont(UITheme.FONT_KPI_LABEL);
        nameLabel.setForeground(UITheme.TEXT_SECONDARY);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(valueLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(nameLabel);

        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    // === Styled Button ===

    public static JButton createButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }

                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(),
                        UITheme.BUTTON_RADIUS, UITheme.BUTTON_RADIUS));
                g2.dispose();

                // Draw text centered
                FontMetrics fm = g.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g.setFont(getFont());
                g.setColor(fgColor);
                g.drawString(getText(), x, y);
            }
        };
        button.setFont(UITheme.FONT_BUTTON);
        button.setForeground(fgColor);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(140, 36));
        return button;
    }

    public static JButton createPrimaryButton(String text) {
        return createButton(text, UITheme.ACCENT, UITheme.TEXT_WHITE);
    }

    public static JButton createSuccessButton(String text) {
        return createButton(text, UITheme.SUCCESS, UITheme.TEXT_WHITE);
    }

    public static JButton createDangerButton(String text) {
        return createButton(text, UITheme.ERROR, UITheme.TEXT_WHITE);
    }

    public static JButton createSecondaryButton(String text) {
        return createButton(text, UITheme.BG_MAIN, UITheme.TEXT_PRIMARY);
    }

    // === Status Badge ===

    public static JLabel createStatusBadge(String status) {
        Color bgColor = UITheme.getStatusColor(status);
        JLabel badge = new JLabel(status) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 30));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(UITheme.FONT_SMALL);
        badge.setForeground(bgColor);
        badge.setHorizontalAlignment(SwingConstants.CENTER);
        badge.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        badge.setOpaque(false);
        return badge;
    }

    // === Styled Text Field ===

    public static JTextField createTextField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(UITheme.TEXT_SECONDARY);
                    g2.setFont(UITheme.FONT_BODY);
                    Insets insets = getInsets();
                    g2.drawString(placeholder, insets.left + 2, getHeight() / 2 + 4);
                    g2.dispose();
                }
            }
        };
        field.setFont(UITheme.FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UITheme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        field.setPreferredSize(new Dimension(250, 34));
        return field;
    }

    // === Search Field ===

    public static JTextField createSearchField(String placeholder) {
        JTextField field = createTextField(placeholder);
        field.setPreferredSize(new Dimension(300, 34));
        return field;
    }

    // === Styled Combo Box ===

    public static JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(UITheme.FONT_BODY);
        combo.setBackground(UITheme.BG_CARD);
        combo.setPreferredSize(new Dimension(200, 34));
        return combo;
    }

    // === Section Header ===

    public static JLabel createSectionHeader(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.FONT_HEADING);
        label.setForeground(UITheme.TEXT_PRIMARY);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        return label;
    }

    // === Info Dialog ===

    public static void showInfo(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showError(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public static boolean showConfirm(Component parent, String message, String title) {
        int result = JOptionPane.showConfirmDialog(parent, message, title,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }
}
