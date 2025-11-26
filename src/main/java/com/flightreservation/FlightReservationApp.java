package com.flightreservation;

import com.flightreservation.ui.MainWindow;
import javax.swing.*;

/**
 * Main entry point for the Flight Reservation Application.
 */
public class FlightReservationApp {
    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default look and feel if system look and feel is not available
        }

        // Run the application on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainWindow mainWindow = new MainWindow();
            mainWindow.setVisible(true);
        });
    }
}
