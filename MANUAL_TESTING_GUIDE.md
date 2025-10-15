# Manual Testing Guide for Supabase Configuration

This guide provides step-by-step instructions to manually verify that the Supabase PostgreSQL configuration is working correctly.

## Prerequisites

Before testing, ensure you have:
1. ✅ Java 17 or higher installed
2. ✅ Maven 3.6 or higher installed
3. ✅ Network access to `db.awbmgncjszicqegrpsxj.supabase.co:5432`
4. ✅ The Supabase database is accessible and running

## Step 1: Verify Configuration

Run the configuration verification script:

```bash
cd backend
./verify-supabase-config.sh
```

**Expected Output:**
```
✓ Database URL is correctly configured
✓ Database username is correctly configured
✓ PostgreSQL driver is correctly configured
✓ PostgreSQL dependency found in pom.xml
✓ HikariCP connection pool is configured
✓ Project builds successfully
```

## Step 2: Test Network Connectivity

Before starting the application, verify network connectivity to the Supabase host:

```bash
# Test DNS resolution
nslookup db.awbmgncjszicqegrpsxj.supabase.co

# Test port connectivity (requires netcat)
nc -zv db.awbmgncjszicqegrpsxj.supabase.co 5432

# Test with PostgreSQL client (if psql is installed)
PGPASSWORD=Beluga91! psql -h db.awbmgncjszicqegrpsxj.supabase.co -p 5432 -U postgres -d postgres -c "SELECT version();"
```

**Expected Results:**
- DNS should resolve to an IP address
- Port 5432 should be open/reachable
- PostgreSQL client should connect successfully and return version info

## Step 3: Build the Application

```bash
cd backend
mvn clean package -DskipTests
```

**Expected Output:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: XX.XXX s
```

## Step 4: Start the Application

```bash
cd backend
mvn spring-boot:run
```

**Expected Console Output (Successful Connection):**

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.0)

INFO  - Starting SrmiggyApplication...
INFO  - The following 1 profile is active: "default"
INFO  - HikariPool-1 - Starting...
INFO  - HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@xxxxxxxx
INFO  - HikariPool-1 - Start completed.
INFO  - HHH000204: Processing PersistenceUnitInfo [name: default]
INFO  - HHH000412: Hibernate ORM core version 6.3.1.Final
INFO  - Using dialect: org.hibernate.dialect.PostgreSQLDialect
INFO  - HHH000490: Using JtaPlatform implementation: [org.hibernate.engine.transaction.jta.platform.internal.NoJtaPlatform]
INFO  - Initialized JPA EntityManagerFactory for persistence unit 'default'
INFO  - Started SrmiggyApplication in X.XXX seconds
INFO  - Application is running! Access URLs:
INFO  - Local: http://localhost:8080
```

**Look for these key indicators:**
1. ✅ "HikariPool-1 - Start completed" - Connection pool initialized
2. ✅ "Using dialect: org.hibernate.dialect.PostgreSQLDialect" - PostgreSQL detected
3. ✅ "Started SrmiggyApplication" - Application started successfully

## Step 5: Verify Tables Were Created

While the application is running, connect to the database and verify tables:

```bash
PGPASSWORD=Beluga91! psql -h db.awbmgncjszicqegrpsxj.supabase.co -p 5432 -U postgres -d postgres
```

In the psql prompt, run:

```sql
-- List all tables
\dt

-- Or query information_schema
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public' 
ORDER BY table_name;
```

**Expected Tables:**
```
delivery_slots
menu_items
order_items
orders
payment_transactions
riders
settings
users
vendors
wallet_transactions
```

## Step 6: Test API Endpoints

With the application running, test the API endpoints:

### Test 1: Health Check
```bash
curl http://localhost:8080/actuator/health
```

### Test 2: Get All Vendors
```bash
curl http://localhost:8080/api/vendors
```

**Expected:** JSON array of vendors (may be empty if no seed data)

### Test 3: Register a New User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "fullName": "Test User",
    "phone": "1234567890",
    "role": "CUSTOMER"
  }'
```

**Expected:** JSON response with user details and JWT token

### Test 4: Login with Created User
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

**Expected:** JSON response with JWT token

### Test 5: Verify User in Database
```sql
-- In psql prompt
SELECT id, username, email, full_name, role FROM users WHERE username = 'testuser';
```

**Expected:** One row with the user data you just created

## Step 7: Check Application Logs

Monitor the application logs for any errors or warnings:

```bash
# If running in foreground, check the console output
# Look for any errors related to:
# - Database connection
# - SQL execution
# - Table creation
# - Transaction management
```

**Good Signs:**
- No `org.postgresql` errors
- No connection pool errors
- SQL statements visible (if `show-sql=true`)
- No "Table not found" errors

## Step 8: Test Data Persistence

Test that data persists across application restarts:

1. **Create some data** (register a user, create a vendor, etc.)
2. **Stop the application** (Ctrl+C)
3. **Restart the application** (`mvn spring-boot:run`)
4. **Query the data again**

```bash
# Query vendors
curl http://localhost:8080/api/vendors

# Query users
curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d '{"username":"testuser","password":"password123"}'
```

**Expected:** Data created before restart should still be present

## Troubleshooting

### Issue: Connection Timeout

**Symptoms:**
```
org.postgresql.util.PSQLException: Connection to db.awbmgncjszicqegrpsxj.supabase.co:5432 refused
```

**Solutions:**
1. Verify network connectivity: `ping db.awbmgncjszicqegrpsxj.supabase.co`
2. Check firewall settings
3. Verify Supabase project is active
4. Check if you're on a restricted network

### Issue: Authentication Failed

**Symptoms:**
```
org.postgresql.util.PSQLException: FATAL: password authentication failed for user "postgres"
```

**Solutions:**
1. Verify the password is correct: `Beluga91!`
2. Check for special characters that might need escaping
3. Verify username is `postgres`
4. Reset password in Supabase Dashboard if needed

### Issue: Tables Not Created

**Symptoms:**
- Application starts but tables don't exist
- "Relation 'users' does not exist" errors

**Solutions:**
1. Check `spring.jpa.hibernate.ddl-auto=update` is set
2. Verify entities are properly annotated with `@Entity`
3. Check application logs for DDL statements
4. Manually create tables using the schema file if needed

### Issue: Port Already in Use

**Symptoms:**
```
Web server failed to start. Port 8080 was already in use.
```

**Solutions:**
```bash
# Find and kill process on port 8080
lsof -ti:8080 | xargs kill -9

# Or change port in application.properties
server.port=8081
```

## Success Criteria Checklist

- [ ] Configuration verification script passes all checks
- [ ] Network connectivity to Supabase confirmed
- [ ] Application builds without errors
- [ ] Application starts without errors
- [ ] Database connection pool initializes
- [ ] All tables are created automatically
- [ ] API endpoints respond correctly
- [ ] User registration works
- [ ] User login works
- [ ] Data persists across restarts
- [ ] No errors in application logs

## Performance Verification

### Check Connection Pool Stats

Monitor HikariCP connection pool:

```sql
-- In psql, check active connections
SELECT count(*) FROM pg_stat_activity WHERE datname = 'postgres';
```

**Expected:** 5-10 connections (based on min/max pool size)

### Check Query Performance

```sql
-- Enable timing in psql
\timing

-- Run sample queries
SELECT COUNT(*) FROM users;
SELECT COUNT(*) FROM vendors;
SELECT COUNT(*) FROM orders;
```

**Expected:** Queries should execute in < 100ms for most operations

## Additional Tests

### Test Concurrent Requests

Use Apache Bench or similar tool:

```bash
# Install Apache Bench if needed
sudo apt-get install apache2-utils

# Test concurrent requests
ab -n 100 -c 10 http://localhost:8080/api/vendors
```

**Expected:** All requests should succeed without connection errors

### Test Transaction Management

Create an order and verify all related records are created:

```bash
# Create an order (requires authentication)
# First, login and get token
TOKEN=$(curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}' \
  | jq -r '.token')

# Create order
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "vendorId": "...",
    "items": [...]
  }'
```

**Verify in database:**
```sql
SELECT * FROM orders WHERE user_id = (SELECT id FROM users WHERE username = 'testuser');
SELECT * FROM order_items WHERE order_id IN (SELECT id FROM orders WHERE user_id = ...);
```

## Conclusion

If all the above tests pass, your Supabase PostgreSQL configuration is working correctly, and the application is production-ready with the new database connection.

## Next Steps

After successful verification:
1. Consider loading seed data if needed
2. Set up backups (automatic with Supabase)
3. Configure monitoring and alerts
4. Review and adjust connection pool settings based on load
5. Consider changing `ddl-auto` to `validate` for production
6. Set up Row Level Security (RLS) in Supabase for additional security
