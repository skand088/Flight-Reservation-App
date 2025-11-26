# Flight Reservation App

A simple Java Swing application for flight booking with MySQL database support.

## Project Structure

```
src/main/java/com/flightreservation/
├── FlightReservationApp.java    # Main entry point
├── db/
│   └── DatabaseConnection.java  # MySQL database connection utility
├── model/
│   ├── Flight.java              # Flight model
│   ├── User.java                # User model
│   └── Booking.java             # Booking model
└── ui/
    └── MainWindow.java          # Main application window
sql/
└── schema.sql                   # Database schema and sample data
```

## Requirements

- Java 8 or higher
- MySQL 5.7 or higher
- MySQL Connector/J (JDBC driver)

## Database Setup

1. Install MySQL and start the MySQL server
2. Run the schema script to create the database:
   ```bash
   mysql -u root -p < sql/schema.sql
   ```

## Configuration

Update the database connection settings in `src/main/java/com/flightreservation/db/DatabaseConnection.java`:

```java
private static final String URL = "jdbc:mysql://localhost:3306/flight_reservation";
private static final String USERNAME = "root";
private static final String PASSWORD = "your_password";
```

## Compilation and Running

### Compile
```bash
javac -d out src/main/java/com/flightreservation/**/*.java
```

### Run
```bash
java -cp "out:mysql-connector-j-8.0.33.jar" com.flightreservation.FlightReservationApp
```

## Features

- View available flights in a table
- Search flights by origin and destination
- Book flights with confirmation dialog
- Simple and intuitive user interface

## Screenshots

The application displays a main window with:
- Search panel for filtering flights
- Table showing flight information (number, origin, destination, times, seats, price)
- Book and Refresh buttons for actions