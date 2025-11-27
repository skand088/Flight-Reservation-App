# Flight Reservation System

A comprehensive Java Swing desktop application for managing flight reservations with MySQL database backend. Built with Maven for team collaboration and designed for easy deployment.

## 🚀 Features

- **Flight Management**: Search, view, and manage flight schedules
- **Reservation System**: Book flights with seat selection
- **Customer Management**: Store and manage customer information
- **Payment Processing**: Track payments and transaction history
- **User Access Control**: Role-based access (Admin, Agent, Customer Service)
- **Database Connection Pooling**: Efficient database connections using HikariCP

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK)** 11 or higher
- **Apache Maven** 3.6 or higher
- **MySQL** 8.0 or higher
- **Git** (for version control)

## 🛠️ Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/skand088/Flight-Reservation-App.git
cd Flight-Reservation-App
```

### 2. Set Up MySQL Database

Start your MySQL server and run the database setup scripts:

```bash
# Create database and schema
mysql -u root -p < database/schema.sql

# (Optional) Load sample data for testing
mysql -u root -p < database/sample_data.sql
```

For detailed database setup instructions, see [database/README.md](database/README.md)

### 3. Configure Database Connection

1. Navigate to `src/main/resources/`
2. Copy the template file:
   ```bash
   cp database.properties.template database.properties
   ```
3. Edit `database.properties` with your MySQL credentials:
   ```properties
   db.username=your_mysql_username
   db.password=your_mysql_password
   ```

**Note**: `database.properties` is gitignored to keep credentials secure.

### 4. Build the Project

```bash
mvn clean install
```

### 5. Run the Application

```bash
mvn exec:java -Dexec.mainClass="com.flightreservation.FlightReservationApp"
```

Or run the generated JAR:

```bash
java -jar target/flight-reservation-app-1.0.0.jar
```

## 📁 Project Structure

```
Flight-Reservation-App/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/flightreservation/
│   │   │       ├── FlightReservationApp.java    # Main entry point
│   │   │       ├── database/
│   │   │       │   └── DatabaseManager.java     # DB connection manager
│   │   │       └── ui/
│   │   │           └── MainFrame.java           # Main UI window
│   │   └── resources/
│   │       └── database.properties.template     # DB config template
│   └── test/
│       └── java/                                 # Unit tests
├── database/
│   ├── schema.sql                                # Database schema
│   ├── sample_data.sql                           # Sample data
│   └── README.md                                 # Database documentation
├── pom.xml                                       # Maven configuration
├── .gitignore                                    # Git ignore rules
└── README.md                                     # This file
```

## 👥 Team Collaboration

This project is set up for team collaboration:

### Git Workflow

1. **Create a feature branch** for your work:

   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make your changes** and commit regularly:

   ```bash
   git add .
   git commit -m "Description of changes"
   ```

3. **Push to remote** and create a pull request:
   ```bash
   git push origin feature/your-feature-name
   ```

### Coding Standards

- Follow Java naming conventions (camelCase for methods/variables, PascalCase for classes)
- Write meaningful commit messages
- Document complex logic with comments
- Keep methods focused and functions small
- Write unit tests for new features

### Database Changes

- Never commit `database.properties` (contains credentials)
- Document schema changes in migration scripts
- Test database changes locally before committing
- Update `schema.sql` if making structural changes

## 🔧 Development

### IDE Setup

**IntelliJ IDEA:**

1. Import as Maven project
2. Set JDK 11 or higher
3. Enable annotation processing

**Eclipse:**

1. Import as Existing Maven Project
2. Configure Java compiler to 11+

**VS Code:**

1. Install Java Extension Pack
2. Maven will be detected automatically

### Running Tests

```bash
mvn test
```

### Creating Executable JAR

```bash
mvn clean package
```

The JAR with dependencies will be in `target/flight-reservation-app-1.0.0.jar`

## 📊 Database Schema

The system includes the following main entities:

- **Airlines**: Airline company information
- **Airports**: Airport locations and details
- **Aircraft**: Aircraft models and seating
- **Flights**: Flight schedules and availability
- **Customers**: Customer profiles
- **Reservations**: Booking records
- **Payments**: Payment transactions
- **Users**: System access accounts

See [database/README.md](database/README.md) for detailed schema documentation.

## 🔐 Default Credentials

Default admin account (⚠️ change in production):

- Username: `admin`
- Password: `admin123`

## 🐛 Troubleshooting

### Database Connection Failed

- Ensure MySQL is running
- Check credentials in `database.properties`
- Verify database exists: `SHOW DATABASES;`

### Build Errors

- Verify JDK version: `java -version`
- Clean Maven cache: `mvn clean`
- Update dependencies: `mvn -U clean install`

### Application Won't Start

- Check logs in console output
- Verify all dependencies are installed
- Ensure port 3306 is not blocked

## 📝 License

This project is created for educational purposes.

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📧 Contact

For questions or issues, please create an issue in the GitHub repository.

---

**Happy Coding! ✈️**
