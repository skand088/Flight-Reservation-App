package com.flightreservation.ui;

import com.flightreservation.dao.UserDAO;
import com.flightreservation.model.entities.User;
import com.flightreservation.util.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(LoginFrame.class);

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton exitButton;
    private UserDAO userDAO;

    public LoginFrame() {
        this.userDAO = new UserDAO();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Flight Reservation System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                Color color1 = new Color(33, 147, 176);
                Color color2 = new Color(109, 213, 237);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Flight Reservation System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("Please log in to continue");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridy = 1;
        mainPanel.add(subtitleLabel, gbc);

        JPanel loginPanel = new JPanel();
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setLayout(new GridBagLayout());
        loginPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)));

        GridBagConstraints loginGbc = new GridBagConstraints();
        loginGbc.insets = new Insets(8, 8, 8, 8);
        loginGbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        loginGbc.gridx = 0;
        loginGbc.gridy = 0;
        loginGbc.anchor = GridBagConstraints.WEST;
        loginPanel.add(usernameLabel, loginGbc);

        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        loginGbc.gridx = 1;
        loginPanel.add(usernameField, loginGbc);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 12));
        loginGbc.gridx = 0;
        loginGbc.gridy = 1;
        loginPanel.add(passwordLabel, loginGbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        loginGbc.gridx = 1;
        loginPanel.add(passwordField, loginGbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 12));
        loginButton.setBackground(new Color(33, 147, 176));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setPreferredSize(new Dimension(100, 35));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> handleLogin());

        exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.BOLD, 12));
        exitButton.setBackground(new Color(150, 150, 150));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setBorderPainted(false);
        exitButton.setPreferredSize(new Dimension(100, 35));
        exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(loginButton);
        buttonPanel.add(exitButton);

        loginGbc.gridx = 0;
        loginGbc.gridy = 2;
        loginGbc.gridwidth = 2;
        loginGbc.insets = new Insets(15, 8, 8, 8);
        loginPanel.add(buttonPanel, loginGbc);

        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        mainPanel.add(loginPanel, gbc);

        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        registerPanel.setOpaque(false);

        JLabel registerLabel = new JLabel("Don't have an account?");
        registerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        registerLabel.setForeground(Color.WHITE);

        JButton registerButton = new JButton("Register");
        registerButton.setFont(new Font("Arial", Font.BOLD, 12));
        registerButton.setForeground(new Color(33, 147, 176));
        registerButton.setContentAreaFilled(false);
        registerButton.setBorderPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(e -> openRegistrationDialog());

        registerPanel.add(registerLabel);
        registerPanel.add(registerButton);

        gbc.gridy = 3;
        gbc.insets = new Insets(10, 10, 10, 10);
        mainPanel.add(registerPanel, gbc);

        JLabel infoLabel = new JLabel("Default: admin / $2a$10$dummyhash");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        infoLabel.setForeground(Color.WHITE);
        gbc.gridy = 4;
        gbc.insets = new Insets(5, 10, 10, 10);
        mainPanel.add(infoLabel, gbc);

        passwordField.addActionListener(e -> handleLogin());

        add(mainPanel);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password.",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Authenticating...");

        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() {
                return userDAO.authenticate(username, password);
            }

            @Override
            protected void done() {
                try {
                    User user = get();

                    if (user != null) {
                        String sessionId = userDAO.createSession(user.getUserId(), "localhost");
                        SessionManager.getInstance().startSession(user, sessionId);

                        logger.info("User logged in: {} ({})", user.getUsername(), user.getRole());

                        openDashboard(user);

                        dispose();

                    } else {
                        JOptionPane.showMessageDialog(LoginFrame.this,
                                "Invalid username or password.\nPlease try again.",
                                "Login Failed",
                                JOptionPane.ERROR_MESSAGE);
                        passwordField.setText("");
                        usernameField.requestFocus();
                    }

                } catch (Exception e) {
                    logger.error("Error during login", e);
                    JOptionPane.showMessageDialog(LoginFrame.this,
                            "An error occurred during login.\nPlease try again.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                } finally {
                    loginButton.setEnabled(true);
                    loginButton.setText("Login");
                }
            }
        };

        worker.execute();
    }

    private void openDashboard(User user) {
        SwingUtilities.invokeLater(() -> {
            switch (user.getRole()) {
                case CUSTOMER:
                    new CustomerDashboard().setVisible(true);
                    break;
                case AGENT:
                    new AgentDashboard().setVisible(true);
                    break;
                case ADMIN:
                    new AdminDashboard().setVisible(true);
                    break;
                default:
                    logger.error("Unknown user role: {}", user.getRole());
                    JOptionPane.showMessageDialog(this,
                            "Unknown user role. Please contact administrator.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void openRegistrationDialog() {
        RegistrationDialog dialog = new RegistrationDialog(this);
        dialog.setVisible(true);
    }
}
