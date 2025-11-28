package com.flightreservation.ui.panels.admin;

import com.flightreservation.dao.NewsletterDAO;
import com.flightreservation.model.entities.Newsletter;

import javax.swing.*;
import java.awt.*;

public class NotificationPanel extends JPanel {
    private final NewsletterDAO newsletterDAO;
    private JTextField subjectField;
    private JTextArea messageArea;
    private JLabel statusLabel;

    public NotificationPanel() {
        this.newsletterDAO = new NewsletterDAO();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Send Newsletter to All Customers", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        add(createNotificationForm(), BorderLayout.CENTER);

        statusLabel = new JLabel("Compose a newsletter to send to all customers");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        add(statusLabel, BorderLayout.SOUTH);
    }

    private JPanel createNotificationForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Compose Newsletter"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel subjectLabel = new JLabel("Subject:*");
        subjectLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(subjectLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        subjectField = new JTextField(40);
        subjectField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(subjectField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel messageLabel = new JLabel("Message:*");
        messageLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(messageLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        messageArea = new JTextArea(18, 40);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(messageArea);
        panel.add(scrollPane, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weighty = 0;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));

        JButton sendButton = new JButton("Send Newsletter");
        sendButton.setFont(new Font("Arial", Font.BOLD, 14));
        sendButton.setBackground(new Color(40, 167, 69));
        sendButton.setFocusPainted(false);
        sendButton.setPreferredSize(new Dimension(180, 40));
        sendButton.addActionListener(e -> sendNewsletter());

        JButton clearButton = new JButton("Clear");
        clearButton.setFont(new Font("Arial", Font.PLAIN, 12));
        clearButton.setPreferredSize(new Dimension(100, 40));
        clearButton.addActionListener(e -> clearForm());

        buttonPanel.add(sendButton);
        buttonPanel.add(clearButton);

        panel.add(buttonPanel, gbc);

        return panel;
    }

    private void sendNewsletter() {
        String subject = subjectField.getText().trim();
        String message = messageArea.getText().trim();

        if (subject.isEmpty() || message.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in both subject and message",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Send this newsletter to all customers?",
                "Confirm Send",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Newsletter newsletter = new Newsletter(subject, message);

            if (newsletterDAO.saveNewsletter(newsletter)) {
                statusLabel.setText("Newsletter sent successfully!");
                JOptionPane.showMessageDialog(this,
                        "Newsletter sent successfully!\n\n" +
                                "Subject: " + subject + "\n\n" +
                                "All customers can now view this in their Newsletters page.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                clearForm();
            } else {
                statusLabel.setText("Failed to send newsletter");
                JOptionPane.showMessageDialog(this,
                        "Failed to send newsletter. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        subjectField.setText("");
        messageArea.setText("");
        statusLabel.setText("Compose a newsletter to send to all customers");
    }
}
