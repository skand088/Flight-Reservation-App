package com.flightreservation.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private static volatile DatabaseManager instance;
    private String jdbcUrl;
    private String username;
    private String password;

    private DatabaseManager() {
        initializeDatabase();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            }
        }
        return instance;
    }

    private void initializeDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Properties props = loadDatabaseProperties();

            this.jdbcUrl = buildJdbcUrl(props);
            this.username = props.getProperty("db.username");
            this.password = props.getProperty("db.password");

            logger.info("Database manager initialized successfully");

        } catch (ClassNotFoundException e) {
            logger.error("MySQL JDBC Driver not found", e);
            throw new RuntimeException("MySQL Driver not found", e);
        } catch (IOException e) {
            logger.error("Failed to load database properties", e);
            throw new RuntimeException("Database configuration error", e);
        }
    }

    private Properties loadDatabaseProperties() throws IOException {
        Properties props = new Properties();

        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("database.properties")) {
            if (input != null) {
                props.load(input);
                return props;
            }
        }

        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("database.properties.template")) {
            if (input != null) {
                props.load(input);
                logger.warn("Using template configuration. Please copy database.properties.template " +
                        "to database.properties and update with your credentials");
                return props;
            }
        }

        throw new IOException("No database configuration file found");
    }

    private String buildJdbcUrl(Properties props) {
        String url = props.getProperty("db.url");
        if (url != null && !url.isEmpty()) {
            url = url.replace("${db.host}", props.getProperty("db.host", "localhost"));
            url = url.replace("${db.port}", props.getProperty("db.port", "3306"));
            url = url.replace("${db.name}", props.getProperty("db.name", "flight_reservation_db"));
            return url;
        }

        String host = props.getProperty("db.host", "localhost");
        String port = props.getProperty("db.port", "3306");
        String dbName = props.getProperty("db.name", "flight_reservation_db");
        return String.format("jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                host, port, dbName);
    }

    public Connection getConnection() throws SQLException {
        if (jdbcUrl == null || username == null) {
            throw new SQLException("Database not initialized");
        }
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            logger.error("Database connection test failed", e);
            return false;
        }
    }

    public void shutdown() {
        logger.info("Database manager shutdown");
    }
}
