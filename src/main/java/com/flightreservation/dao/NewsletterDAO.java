package com.flightreservation.dao;

import com.flightreservation.database.DatabaseManager;
import com.flightreservation.model.Newsletter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Newsletter operations
 */
public class NewsletterDAO {
    private static final Logger logger = LoggerFactory.getLogger(NewsletterDAO.class);

    /**
     * Save a newsletter to database
     */
    public boolean saveNewsletter(Newsletter newsletter) {
        String sql = "INSERT INTO newsletters (subject, message, sent_date) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, newsletter.getSubject());
            stmt.setString(2, newsletter.getMessage());
            stmt.setTimestamp(3, Timestamp.valueOf(newsletter.getSentDate()));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    newsletter.setNewsletterId(generatedKeys.getInt(1));
                }
                logger.info("Newsletter saved: {}", newsletter.getSubject());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error saving newsletter", e);
        }
        return false;
    }

    /**
     * Get all newsletters ordered by date (newest first)
     */
    public List<Newsletter> getAllNewsletters() {
        List<Newsletter> newsletters = new ArrayList<>();
        String sql = "SELECT * FROM newsletters ORDER BY sent_date DESC";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                newsletters.add(mapResultSetToNewsletter(rs));
            }
            logger.info("Retrieved {} newsletters", newsletters.size());
        } catch (SQLException e) {
            logger.error("Error retrieving newsletters", e);
        }
        return newsletters;
    }

    /**
     * Get recent newsletters (last N)
     */
    public List<Newsletter> getRecentNewsletters(int limit) {
        List<Newsletter> newsletters = new ArrayList<>();
        String sql = "SELECT * FROM newsletters ORDER BY sent_date DESC LIMIT ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                newsletters.add(mapResultSetToNewsletter(rs));
            }
        } catch (SQLException e) {
            logger.error("Error retrieving recent newsletters", e);
        }
        return newsletters;
    }

    /**
     * Delete a newsletter
     */
    public boolean deleteNewsletter(int newsletterId) {
        String sql = "DELETE FROM newsletters WHERE newsletter_id = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newsletterId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Deleted newsletter ID: {}", newsletterId);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error deleting newsletter", e);
        }
        return false;
    }

    private Newsletter mapResultSetToNewsletter(ResultSet rs) throws SQLException {
        Newsletter newsletter = new Newsletter();
        newsletter.setNewsletterId(rs.getInt("newsletter_id"));
        newsletter.setSubject(rs.getString("subject"));
        newsletter.setMessage(rs.getString("message"));

        Timestamp sentDate = rs.getTimestamp("sent_date");
        if (sentDate != null) {
            newsletter.setSentDate(sentDate.toLocalDateTime());
        }

        return newsletter;
    }
}
