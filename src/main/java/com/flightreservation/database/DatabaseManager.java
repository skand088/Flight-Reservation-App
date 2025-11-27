package com.flightreservation.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Manages database connections using HikariCP connection pooling
 * Thread-safe singleton for database access across the application
 */
public class DatabaseManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private static DatabaseManager instance;
    private HikariDataSource dataSource;

    private DatabaseManager() {
        initializeDataSource();
    }

    /**
     * Get the singleton instance of DatabaseManager
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Initialize HikariCP data source with configuration from properties file
     */
    private void initializeDataSource() {
        try {
            Properties props = loadDatabaseProperties();

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(buildJdbcUrl(props));
            config.setUsername(props.getProperty("db.username"));
            config.setPassword(props.getProperty("db.password"));

            // Connection pool settings
            config.setMaximumPoolSize(Integer.parseInt(
                    props.getProperty("db.pool.maxPoolSize", "10")));
            config.setMinimumIdle(Integer.parseInt(
                    props.getProperty("db.pool.minIdle", "2")));
            config.setConnectionTimeout(Long.parseLong(
                    props.getProperty("db.pool.connectionTimeout", "30000")));
            config.setIdleTimeout(Long.parseLong(
                    props.getProperty("db.pool.idleTimeout", "600000")));
            config.setMaxLifetime(Long.parseLong(
                    props.getProperty("db.pool.maxLifetime", "1800000")));

            // Additional settings
            config.setPoolName("FlightReservationPool");
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            dataSource = new HikariDataSource(config);
            logger.info("Database connection pool initialized successfully");

        } catch (IOException e) {
            logger.error("Failed to load database properties", e);
            throw new RuntimeException("Database configuration error", e);
        }
    }

    /**
     * Load database properties from file
     */
    private Properties loadDatabaseProperties() throws IOException {
        Properties props = new Properties();

        // Try to load database.properties first
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("database.properties")) {
            if (input != null) {
                props.load(input);
                return props;
            }
        }

        // Fall back to template if database.properties doesn't exist
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

    /**
     * Build JDBC URL from properties
     */
    private String buildJdbcUrl(Properties props) {
        String url = props.getProperty("db.url");
        if (url != null && !url.isEmpty()) {
            // Replace placeholders if present
            url = url.replace("${db.host}", props.getProperty("db.host", "localhost"));
            url = url.replace("${db.port}", props.getProperty("db.port", "3306"));
            url = url.replace("${db.name}", props.getProperty("db.name", "flight_reservation_db"));
            return url;
        }

        // Build URL from components
        String host = props.getProperty("db.host", "localhost");
        String port = props.getProperty("db.port", "3306");
        String dbName = props.getProperty("db.name", "flight_reservation_db");
        return String.format("jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                host, port, dbName);
    }

    /**
     * Get a database connection from the pool
     */
    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource not initialized");
        }
        return dataSource.getConnection();
    }

    /**
     * Test database connectivity
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            logger.error("Database connection test failed", e);
            return false;
        }
    }

    /**
     * Close the data source and release all connections
     */
    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Database connection pool shut down");
        }
    }
}
