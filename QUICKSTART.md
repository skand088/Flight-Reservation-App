814# Quick Start Guide

## Get Up and Running in 5 Minutes

### Step 1: Verify Prerequisites (1 minute)

```bash
# Check Java version (need 11+)
java -version

# Check Maven (need 3.6+)
mvn -version

# Check MySQL (need 8.0+)
mysql --version
```

### Step 2: Setup Database (1 minute)

```bash
# Windows
setup-db.bat

# Mac/Linux
chmod +x setup-db.sh
./setup-db.sh
```

When prompted:

- MySQL username: `root` (or your username)
- MySQL password: (enter your password)

### Step 3: Configure Connection (1 minute)

1. Go to: `src/main/resources/`
2. Copy `database.properties.template` to `database.properties`
3. Edit `database.properties`:
   ```properties
   db.username=root
   db.password=your_password
   ```

### Step 4: Run the Application (1 minute)

```bash
# Windows
run.bat

# Mac/Linux
chmod +x run.sh
./run.sh
```

### Step 5: Login & Explore (1 minute)

**Try as Customer:**

- Username: `customer1`
- Password: `customer123`
- Features: Search flights, view reservations

**Try as Admin:**

- Username: `admin`
- Password: `admin123`
- Features: Manage flights, routes, aircraft

---

## Common Issues & Solutions

### Issue: "MySQL connection failed"

**Solution:**

```bash
# Windows
net start MySQL80

# Mac/Linux
sudo systemctl start mysql
```

### Issue: "Database not found"

**Solution:** Run the setup script again:

```bash
./setup-db.sh  # or setup-db.bat on Windows
```

### Issue: "Maven not found"

**Solution:**

- Download from: https://maven.apache.org/download.cgi
- Add to PATH environment variable

### Issue: "Compilation errors"

**Solution:**

```bash
mvn clean compile
```

---

## What Can You Do?

### As Customer (customer1 / customer123)

1. **Search Flights**

   - Click "Flights" → "Search Flights"
   - Try: From=JFK, To=LAX, Date=2024-06-01
   - Click "Search"

2. **View Reservations**

   - Click "My Reservations" → "View All Reservations"
   - See your bookings with status
   - Select a reservation and click "View Details"

3. **Cancel Booking**
   - Select a reservation
   - Click "Cancel Reservation"
   - Confirm cancellation

### As Admin (admin / admin123)

1. **Add a Flight**

   - Click "Flights" → "Add New Flight"
   - Fill in details:
     - Flight Number: AA123
     - Select Route and Aircraft
     - Enter dates/times
     - Set price
   - Click "Save"

2. **View All Flights**

   - Click "Flights" → "View All Flights"
   - See complete flight list
   - Select and edit or delete

3. **Manage Routes**
   - Click "Routes" → "View All Routes"
   - Add new routes between cities

---

## Next Steps

### For Developers

1. Explore the code in `src/main/java/com/flightreservation/`
2. Check out `IMPLEMENTATION.md` for architecture details
3. Read database schema in `database/schema.sql`

### For Users

1. Create a new customer account (via Agent dashboard - coming soon)
2. Book a flight (booking UI - coming soon)
3. Check your reservation history

### For Administrators

1. Add more routes and aircraft
2. Schedule flights for upcoming months
3. Monitor system usage (reports - coming soon)

---

## Project Structure Quick Reference

```
📁 Flight-Reservation-App/
├── 📄 run.bat / run.sh          ← Run the app
├── 📄 setup-db.bat / setup-db.sh ← Setup database
├── 📁 src/main/java/
│   └── com/flightreservation/
│       ├── 📄 FlightReservationApp.java  ← Main entry
│       ├── 📁 model/           ← Data entities
│       ├── 📁 dao/             ← Database access
│       ├── 📁 controller/      ← Business logic
│       ├── 📁 ui/              ← User interface
│       ├── 📁 database/        ← DB connection
│       └── 📁 util/            ← Utilities
├── 📁 database/
│   └── 📄 schema.sql           ← Database structure
└── 📁 src/main/resources/
    ├── 📄 database.properties.template  ← Config template
    └── 📄 logback.xml          ← Logging config
```

---

## Test Data Reference

### Test Users

| Role     | Username  | Password    |
| -------- | --------- | ----------- |
| Admin    | admin     | admin123    |
| Agent    | agent1    | agent123    |
| Customer | customer1 | customer123 |
| Customer | customer2 | customer123 |

### Sample Flights (after setup)

- AA123: JFK → LAX
- UA456: LAX → ORD
- DL789: ORD → MIA

### Sample Routes

- New York JFK → Los Angeles LAX
- Los Angeles LAX → Chicago ORD
- Chicago ORD → Miami MIA

---

## Need Help?

1. **Check logs**: Look at console output for error messages
2. **Review README.md**: Detailed setup instructions
3. **Check IMPLEMENTATION.md**: Architecture and technical details
4. **Database issues**: Verify MySQL is running and credentials are correct

---

**Happy Flying! ✈️**

For detailed information, see:

- `README.md` - Complete documentation
- `IMPLEMENTATION.md` - Technical implementation details
- `database/schema.sql` - Database structure
