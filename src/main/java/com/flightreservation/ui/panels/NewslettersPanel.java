package com.flightreservation.ui.panels;

import com.flightreservation.dao.NewsletterDAO;
import com.flightreservation.model.Newsletter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel for customers to view newsletters
 */
public class NewslettersPanel extends JPanel {
    private final NewsletterDAO newsletterDAO;
    private JTable newslettersTable;
    private DefaultTableModel tableModel;
    private JTextArea detailsArea;

    public NewslettersPanel() {
        this.newsletterDAO = new NewsletterDAO();
        initializeUI();
        loadNewsletters();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("📬 Newsletters", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(createNewslettersList());
        splitPane.setBottomComponent(createDetailsPanel());
        splitPane.setDividerLocation(250);
        add(splitPane, BorderLayout.CENTER);

        // Refresh button at bottom
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("🔄 Refresh");
        refreshButton.addActionListener(e -> loadNewsletters());
        bottomPanel.add(refreshButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createNewslettersList() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("All Newsletters"));

        // Table
        String[] columns = { "Date", "Subject" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        newslettersTable = new JTable(tableModel);
        newslettersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        newslettersTable.setRowHeight(30);
        newslettersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                displaySelectedNewsletter();
            }
        });

        JScrollPane scrollPane = new JScrollPane(newslettersTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Newsletter Content"));

        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setFont(new Font("Arial", Font.PLAIN, 13));
        detailsArea.setMargin(new Insets(10, 10, 10, 10));
        detailsArea.setText("Select a newsletter to view its content");

        JScrollPane scrollPane = new JScrollPane(detailsArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadNewsletters() {
        tableModel.setRowCount(0);
        List<Newsletter> newsletters = newsletterDAO.getAllNewsletters();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

        for (Newsletter newsletter : newsletters) {
            String date = newsletter.getSentDate() != null ? newsletter.getSentDate().format(formatter) : "N/A";
            Object[] row = { date, newsletter.getSubject() };
            tableModel.addRow(row);
        }

        if (newsletters.isEmpty()) {
            detailsArea.setText("No newsletters available.\n\nCheck back later for updates!");
        }
    }

    private void displaySelectedNewsletter() {
        int selectedRow = newslettersTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        List<Newsletter> newsletters = newsletterDAO.getAllNewsletters();
        if (selectedRow < newsletters.size()) {
            Newsletter newsletter = newsletters.get(selectedRow);

            StringBuilder content = new StringBuilder();
            content.append("══════════════════════════════════════\n");
            content.append("  NEWSLETTER\n");
            content.append("══════════════════════════════════════\n\n");
            content.append("Subject: ").append(newsletter.getSubject()).append("\n");

            if (newsletter.getSentDate() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm");
                content.append("Date: ").append(newsletter.getSentDate().format(formatter)).append("\n");
            }

            content.append("\n──────────────────────────────────────\n\n");
            content.append(newsletter.getMessage());
            content.append("\n\n══════════════════════════════════════\n");

            detailsArea.setText(content.toString());
            detailsArea.setCaretPosition(0);
        }
    }
}
