package com.flightreservation.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flightreservation.dao.UserDAO;
import com.flightreservation.model.entities.User;
import com.flightreservation.ui.decorators.ButtonDecoratorFactory;
import com.flightreservation.util.SessionManager;

/**
 * base class for all dashboard implementations.
 */
public abstract class BaseDashboard extends JFrame {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected User currentUser;
    protected JPanel contentPanel;

    public BaseDashboard() {
        this.currentUser = SessionManager.getInstance().getCurrentUser();
        initializeUI();
    }

    protected void initializeUI() {
        setTitle(getDashboardTitle());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        createMenuBar();

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel welcomePanel = createWelcomePanel();
        contentPanel.add(welcomePanel, BorderLayout.CENTER);

        add(contentPanel);
    }

    protected JMenu createStyledMenu(String text) {
        JMenu menu = new JMenu(text);
        menu.setFont(new Font("Arial", Font.BOLD, 14));
        return menu;
    }

    protected JMenuItem createStyledMenuItem(String text) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(new Font("Arial", Font.PLAIN, 12));
        return item;
    }

    protected JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setPreferredSize(new Dimension(180, 50));

        return ButtonDecoratorFactory.createEnhancedActionButton(button, "Click to access " + text.toLowerCase());
    }

    protected JPanel createNavigationPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(getBackgroundColor());

        JButton backButton = new JButton("← Back to Home");
        backButton.setFont(new Font("Arial", Font.BOLD, 11));
        backButton.setBackground(new Color(108, 117, 125));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setOpaque(true);
        backButton.setContentAreaFilled(true);
        backButton = ButtonDecoratorFactory.createEnhancedNavigationButton(backButton, "Return to dashboard home");
        backButton.addActionListener(e -> showHome());

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 11));
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setOpaque(true);
        logoutButton.setContentAreaFilled(true);
        logoutButton = ButtonDecoratorFactory.createEnhancedNavigationButton(logoutButton, "Sign out of your account");
        logoutButton.addActionListener(e -> handleLogout());

        panel.add(backButton);
        panel.add(logoutButton);

        return panel;
    }

    protected void showHome() {
        contentPanel.removeAll();
        JPanel welcomePanel = createWelcomePanel();
        contentPanel.add(welcomePanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    protected void showProfile() {
        String profileInfo = String.format(
                "%s Profile\n\n" +
                        "Username: %s\n" +
                        "Email: %s\n" +
                        "Phone: %s\n" +
                        "Role: %s\n" +
                        "Account Status: %s",
                getRoleDisplayName(),
                currentUser.getUsername(),
                currentUser.getEmail(),
                currentUser.getPhoneNumber() != null ? currentUser.getPhoneNumber() : "Not set",
                currentUser.getRole(),
                currentUser.getAccountStatus());

        JOptionPane.showMessageDialog(this,
                profileInfo,
                getRoleDisplayName() + " Profile",
                JOptionPane.INFORMATION_MESSAGE);
    }

    protected void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            UserDAO userDAO = new UserDAO();
            userDAO.endSession(SessionManager.getInstance().getSessionId());
            SessionManager.getInstance().endSession();

            logger.info("{} logged out", getRoleDisplayName());

            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    protected void switchContent(String message) {
        contentPanel.removeAll();
        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        contentPanel.add(label, BorderLayout.CENTER);
        contentPanel.add(createNavigationPanel(), BorderLayout.SOUTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    protected abstract String getDashboardTitle();

    protected abstract Color getBackgroundColor();

    protected abstract String getRoleDisplayName();

    protected abstract void createMenuBar();

    protected abstract JPanel createWelcomePanel();
}
