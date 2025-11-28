package com.flightreservation;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flightreservation.database.DatabaseManager;
import com.flightreservation.ui.LoginFrame;

public class FlightReservationApp {
    private static final Logger logger = LoggerFactory.getLogger(FlightReservationApp.class);

    public static void main(String[] args) {
        logger.info("Starting Flight Reservation Application...");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            logger.warn("Failed to set system look and feel", e);
        }

        SwingUtilities.invokeLater(() -> {
            DatabaseManager dbManager = DatabaseManager.getInstance();

            if (dbManager.testConnection()) {
                logger.info("Database connection successful");
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            } else {
                logger.error("Failed to connect to database");
                JOptionPane.showMessageDialog(null,
                        "Failed to connect to database.\n" +
                                "Please check your database configuration in:\n" +
                                "src/main/resources/database.properties\n\n" +
                                "Make sure MySQL is running and the database exists.",
                        "Database Connection Error",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down application...");
            DatabaseManager.getInstance().shutdown();
        }));
    }
}
