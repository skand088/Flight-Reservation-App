# Database Setup Instructions

This directory contains SQL scripts for setting up the Flight Reservation System database.

## Prerequisites

- MySQL 8.0 or higher installed and running
- MySQL client or MySQL Workbench

## Setup Steps

### 1. Create the Database and Schema (One Command!)

The schema file now includes everything: table creation, test users, and sample data.

Run the complete setup script:

```bash
mysql -u root -p < schema.sql
```

Or using MySQL Workbench:

- Open `schema.sql` in MySQL Workbench
- Execute the script

This will:

- Create the database and all tables
- Set up test users (admin, agent1, customer1, customer2)
- Load sample airlines, aircraft, routes, and flights
- Create sample reservations and payments

### 2. Configure Application

After creating the database, configure the application:

1. Navigate to `src/main/resources/`
2. Copy `database.properties.template` to `database.properties`
3. Update the credentials in `database.properties`:

```properties
db.username=your_mysql_username
db.password=your_mysql_password
```

## Database Schema Overview

### Main Tables

**User Management:**

- **users**: Base user table for all system users
- **customers**: Extended customer information
- **flight_agents**: Agent-specific details
- **system_administrators**: Admin-specific details

**Flight System:**

- **airlines**: Airline companies information
- **aircraft**: Aircraft fleet with seating configurations
- **routes**: Routes between airports
- **flights**: Flight schedules and availability
- **seats**: Individual seat inventory per flight

**Reservations:**

- **passengers**: Passenger information
- **reservations**: Flight booking records
- **reservation_passengers**: Links passengers to reservations and seats
- **payments**: Payment transaction records

**System:**

- **notifications**: Email notifications
- **sessions**: User session tracking

### Views

- **flight_details**: Complete flight information with routes and airlines
- **reservation_summary**: Reservation details with customer and flight data
- **passenger_manifest**: Passenger lists per flight

## Test User Credentials

The schema includes pre-configured test users:

| Role         | Username  | Password    |
| ------------ | --------- | ----------- |
| **Admin**    | admin     | admin123    |
| **Agent**    | agent1    | agent123    |
| **Customer** | customer1 | customer123 |
| **Customer** | customer2 | customer123 |

## Maintenance

### Backup Database

```bash
mysqldump -u root -p flight_reservation_db > backup_$(date +%Y%m%d).sql
```

### Restore Database

```bash
mysql -u root -p flight_reservation_db < backup_20251125.sql
```

## Troubleshooting

### Connection Issues

If you get connection errors:

1. Ensure MySQL server is running
2. Check firewall settings
3. Verify credentials in `database.properties`
4. Confirm database exists: `SHOW DATABASES;`

### Permission Issues

Grant necessary permissions:

```sql
GRANT ALL PRIVILEGES ON flight_reservation_db.* TO 'your_user'@'localhost';
FLUSH PRIVILEGES;
```
