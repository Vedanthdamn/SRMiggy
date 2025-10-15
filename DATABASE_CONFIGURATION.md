# Database Configuration Guide

## Overview
This document explains the database configuration for the SRMiggy application and clarifies how tables are created.

## Database Configuration Modes

### Development Mode (H2 In-Memory Database)
**Configuration File:** `application.properties`

**Key Settings:**
```properties
spring.datasource.url=jdbc:h2:mem:srmiggydb
spring.jpa.hibernate.ddl-auto=update
```

**Behavior:**
- Tables are automatically created on application startup
- Schema updates are applied automatically when entity models change
- Data is preserved during application runtime
- ⚠️ **Important:** Since H2 is in-memory, all data is lost when the application stops
- Perfect for development and testing

**Access H2 Console:**
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:srmiggydb`
- Username: `sa`
- Password: (leave empty)

### Production Mode (Supabase PostgreSQL)
**Configuration File:** `application-supabase.properties`

**Key Settings:**
```properties
spring.datasource.url=jdbc:postgresql://<your-project-ref>.supabase.co:5432/postgres
spring.jpa.hibernate.ddl-auto=update
```

**Behavior:**
- Tables are automatically created on first run
- Schema updates are applied automatically when entity models change
- Data persists permanently in PostgreSQL
- For production environments, you may want to change to `validate` after initial setup

## Tables Created

The application creates the following 10 tables automatically:

1. **users** - User accounts (customers, vendors, admins, riders)
2. **vendors** - Vendor/restaurant information
3. **menu_items** - Food items available for each vendor
4. **delivery_slots** - Available delivery time slots
5. **orders** - Customer orders
6. **order_items** - Items in each order
7. **payment_transactions** - Payment records
8. **wallet_transactions** - Wallet transaction history
9. **riders** - Delivery personnel
10. **settings** - Application settings

## Data Initialization

On first startup, the application automatically seeds the database with:
- **3 Users:** Customer, Admin, and Vendor accounts
- **8 Vendors:** Various food outlets
- **128 Menu Items:** 16 items per vendor
- **8 Delivery Slots:** Time slots for order delivery
- **3 Settings:** Application configuration

## How to Run

### Development Mode (Default - H2)
```bash
cd backend
mvn spring-boot:run
```

### Production Mode (Supabase)
```bash
cd backend
# Update application-supabase.properties with your Supabase credentials
mvn spring-boot:run -Dspring-boot.run.profiles=supabase
```

## Verifying Tables are Created

### Method 1: Check Application Logs
Look for these log messages during startup:
```
Hibernate: create table users (...)
Hibernate: create table vendors (...)
... (8 more tables)
Data initialization completed successfully!
Started SrmiggyApplication in X.XXX seconds
```

### Method 2: Access H2 Console (Development)
1. Start the application
2. Open browser to `http://localhost:8080/h2-console`
3. Login with credentials above
4. Run query: `SHOW TABLES;`

### Method 3: Test API Endpoints
```bash
# Check if vendors are available
curl http://localhost:8080/api/vendors

# Should return 8 vendors with full details
```

## Troubleshooting

### Issue: "Tables are not created"
**Solution:** Tables ARE created, but may not be visible because:
- H2 in-memory database loses data on shutdown (expected behavior)
- Check logs for `create table` statements during startup
- Verify application starts successfully with "Started SrmiggyApplication" message

### Issue: Application fails to start with Supabase
**Possible Causes:**
1. Invalid Supabase credentials in `application-supabase.properties`
2. Network connectivity issues
3. Database connection timeout

**Solution:**
1. Verify Supabase credentials are correct
2. Check Supabase project is active
3. Test database connection manually

### Issue: "Port 8080 already in use"
**Solution:**
```bash
# Find and kill the process using port 8080
lsof -ti:8080 | xargs kill -9

# Or change the port in application.properties
server.port=8081
```

## Configuration History

### Previous Configuration (Issues)
- **H2:** `ddl-auto=create-drop` - Tables were dropped on shutdown, causing confusion
- **Supabase:** `ddl-auto=validate` - Required manual table creation, causing startup failures

### Current Configuration (Fixed)
- **H2:** `ddl-auto=update` - Tables persist during runtime, automatically updated
- **Supabase:** `ddl-auto=update` - Tables created automatically on first run

## Best Practices

### Development
- Use default H2 configuration for quick development
- Data resets on each restart provide clean state for testing
- Enable SQL logging to debug issues

### Production
- Use Supabase or other PostgreSQL database
- Consider changing `ddl-auto` to `validate` after initial deployment
- Use database migrations (Flyway/Liquibase) for version control
- Keep regular backups (automatic with Supabase)

## Additional Resources

- **README.md** - Main project documentation
- **SUPABASE_SETUP.md** - Detailed Supabase configuration guide
- **MIGRATION_GUIDE.md** - Guide for migrating from H2 to Supabase
- **EXAMPLE_SERVICE_CODE.md** - Code examples for working with the database
