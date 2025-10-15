# 🔌 Supabase Connection Setup Guide

This guide will help you properly configure and test your SRMiggy backend connection to Supabase PostgreSQL database.

## 📋 Quick Status Check

**Current Status:** The backend is currently configured to use **H2 in-memory database** (development mode).

To use Supabase PostgreSQL (production mode), you need to:
1. Configure Supabase credentials
2. Set the active Spring profile to `supabase`
3. Ensure database schema is created
4. Test the connection

## 🚀 Step-by-Step Setup

### Step 1: Create Supabase Project

1. Go to [https://supabase.com](https://supabase.com) and sign up/login
2. Click **"New Project"**
3. Fill in project details:
   - **Name:** SRMiggy (or your preferred name)
   - **Database Password:** Create a strong password (SAVE THIS!)
   - **Region:** Choose closest to your users
   - **Plan:** Free tier is fine for development
4. Wait ~2 minutes for project provisioning

### Step 2: Get Connection Credentials

1. In Supabase Dashboard, go to **Settings** → **Database**
2. Note down:
   - **Host:** Will be like `db.xxxxxxxxxxxxx.supabase.co`
   - **Project Reference:** The `xxxxxxxxxxxxx` part from the host
   - **Database:** `postgres`
   - **Port:** `5432`
   - **User:** `postgres`
   - **Password:** The password you created in Step 1

**Example:**
```
Host: db.abcdefghijklmnop.supabase.co
Project Ref: abcdefghijklmnop
```

### Step 3: Create Database Schema

1. In Supabase Dashboard, go to **SQL Editor**
2. Click **"New Query"**
3. Copy entire contents of `backend/src/main/resources/supabase-schema.sql`
4. Paste into SQL Editor
5. Click **"Run"** (or press Ctrl/Cmd + Enter)
6. Wait for completion (should see "Success" message)

**Verify tables created:**
```sql
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public' 
ORDER BY table_name;
```

You should see 10 tables:
- users
- vendors
- menu_items
- orders
- order_items
- delivery_slots
- riders
- payment_transactions
- wallet_transactions
- settings

### Step 4: Load Seed Data (Optional but Recommended)

1. In SQL Editor, create another new query
2. Copy entire contents of `backend/src/main/resources/supabase-seed-data.sql`
3. Paste and run
4. This creates test users, vendors, menu items, etc.

**Test credentials after seeding:**
- Username: `customer` / Password: `password`
- Username: `admin` / Password: `password`
- Username: `vendor1` / Password: `password`

### Step 5: Configure Backend Application

#### Option A: Using Environment Variables (Recommended)

1. Navigate to backend directory:
   ```bash
   cd backend
   ```

2. Create `.env` file from template:
   ```bash
   cp .env.example .env
   ```

3. Edit `.env` file and replace placeholders:
   ```env
   # Replace with your actual values
   SUPABASE_PROJECT_REF=abcdefghijklmnop
   SUPABASE_DB_PASSWORD=your-actual-password
   DATABASE_URL=jdbc:postgresql://db.abcdefghijklmnop.supabase.co:5432/postgres
   SPRING_PROFILES_ACTIVE=supabase
   ```

4. The application will automatically load these variables

#### Option B: Direct Configuration File Edit

1. Open `backend/src/main/resources/application-supabase.properties`

2. Replace placeholders:
   ```properties
   # Before
   spring.datasource.url=jdbc:postgresql://<your-project-ref>.supabase.co:5432/postgres
   spring.datasource.password=<your-supabase-password>
   
   # After (with your actual values)
   spring.datasource.url=jdbc:postgresql://db.abcdefghijklmnop.supabase.co:5432/postgres
   spring.datasource.password=your-actual-password
   ```

3. Activate Supabase profile by adding to `application.properties`:
   ```properties
   spring.profiles.active=supabase
   ```

### Step 6: Test Connection

We've provided an automated test script:

```bash
cd backend
./test-supabase-connection.sh
```

**What this script does:**
1. ✅ Validates your configuration
2. ✅ Tests database connection
3. ✅ Checks if tables exist
4. ✅ Starts the application
5. ✅ Tests API endpoints
6. ✅ Provides detailed diagnostics

**Expected Output:**
```
========================================
  Supabase Connection Test Tool
========================================

📋 Loading configuration from .env...

🔍 Validating configuration...

✓ SUPABASE_PROJECT_REF: abcdefghijklmnop
✓ SUPABASE_DB_PASSWORD: ********
✓ DATABASE_URL: jdbc:postgresql://db.abcdefghijklmnop.supabase.co:5432/postgres
✓ SPRING_PROFILES_ACTIVE: supabase

✓ Configuration validation passed!

🔌 Testing database connection...

✓ Database connection successful!

✓ Found tables:
  - delivery_slots
  - menu_items
  - order_items
  - orders
  - payment_transactions
  - riders
  - settings
  - users
  - vendors
  - wallet_transactions

🚀 Testing with Spring Boot application...

✓ Application started successfully!
✓ API endpoint responding correctly (HTTP 200)
✓ Found 8 vendors in database

========================================
  ✓ All tests passed!
  Your Supabase connection is working!
========================================
```

### Step 7: Run Application with Supabase

Once configuration is correct, start the application:

```bash
cd backend

# If using .env file
mvn spring-boot:run

# Or explicitly specify profile
mvn spring-boot:run -Dspring-boot.run.profiles=supabase
```

**Verify startup:**
Look for these log messages:
```
INFO  HikariDataSource - SRMiggyHikariCP - Starting...
INFO  HikariDataSource - SRMiggyHikariCP - Start completed.
INFO  SrmiggyApplication - Started SrmiggyApplication
```

### Step 8: Test API Endpoints

With backend running, test endpoints:

```bash
# Get all vendors
curl http://localhost:8080/api/vendors

# Register a user
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

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "customer",
    "password": "password"
  }'
```

## 🐛 Troubleshooting

### Error: "Connection refused"

**Cause:** Cannot reach Supabase server

**Solutions:**
1. Check your internet connection
2. Verify project reference is correct
3. Ensure port 5432 is not blocked by firewall
4. Check if Supabase project is running (not paused)

### Error: "Authentication failed for user postgres"

**Cause:** Incorrect database password

**Solutions:**
1. Double-check password in Supabase Dashboard → Settings → Database
2. If forgotten, reset password in Supabase Dashboard
3. Ensure no extra spaces in password
4. Check if password contains special characters that need escaping

### Error: "Relation 'users' does not exist"

**Cause:** Database schema not created

**Solutions:**
1. Run `supabase-schema.sql` in Supabase SQL Editor
2. Verify tables exist with the query from Step 3
3. Check you're connected to correct database

### Error: "spring.datasource.url contains placeholder"

**Cause:** Configuration not updated

**Solutions:**
1. Verify `.env` file exists and has correct values
2. Check `application-supabase.properties` has real values, not `<your-project-ref>`
3. Ensure `SPRING_PROFILES_ACTIVE=supabase` is set
4. Restart the application after changes

### Error: "HikariPool - Exception during pool initialization"

**Cause:** Various connection issues

**Solutions:**
1. Run `./test-supabase-connection.sh` for detailed diagnostics
2. Check all configuration values
3. Verify Supabase project is accessible
4. Test connection with `psql` if available:
   ```bash
   psql -h db.yourprojectref.supabase.co -p 5432 -U postgres -d postgres
   ```

### Application starts but API returns empty arrays

**Cause:** No seed data loaded

**Solutions:**
1. Run `supabase-seed-data.sql` in SQL Editor
2. Verify data exists:
   ```sql
   SELECT COUNT(*) FROM vendors;
   SELECT COUNT(*) FROM users;
   ```

## 📊 Verification Checklist

Before considering setup complete, verify:

- [ ] Supabase project created and running
- [ ] Connection credentials obtained
- [ ] Database schema created (10 tables)
- [ ] Seed data loaded (optional but recommended)
- [ ] `.env` file configured with real values
- [ ] `SPRING_PROFILES_ACTIVE=supabase` set
- [ ] Test script passes all checks
- [ ] Application starts without errors
- [ ] API endpoints return data
- [ ] Can register and login users

## 🔄 Switching Between H2 and Supabase

### To use H2 (Development):
```bash
# Remove or comment in .env
# SPRING_PROFILES_ACTIVE=supabase

# Or run with default profile
mvn spring-boot:run
```

### To use Supabase (Production):
```bash
# Set in .env
SPRING_PROFILES_ACTIVE=supabase

# Or run with profile
mvn spring-boot:run -Dspring-boot.run.profiles=supabase
```

## 🔒 Security Best Practices

1. **Never commit credentials:**
   - Add `.env` to `.gitignore` (already done)
   - Use environment variables in production
   
2. **Strong passwords:**
   - Use strong database password
   - Change default JWT secret
   
3. **Access control:**
   - Enable Row Level Security (RLS) in Supabase
   - Configure IP whitelisting if needed
   
4. **Connection pooling:**
   - HikariCP is pre-configured
   - Adjust pool size based on load

## 📚 Additional Resources

- **Supabase Documentation:** [https://supabase.com/docs](https://supabase.com/docs)
- **Full Setup Guide:** See `SUPABASE_SETUP.md`
- **Migration Guide:** See `MIGRATION_GUIDE.md`
- **Code Examples:** See `EXAMPLE_SERVICE_CODE.md`

## 🆘 Still Having Issues?

If you've followed all steps and still have issues:

1. Run the test script and save output:
   ```bash
   ./test-supabase-connection.sh > test-output.txt 2>&1
   ```

2. Check application logs:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=supabase > app.log 2>&1
   ```

3. Verify Supabase project status in dashboard

4. Check Supabase logs in Dashboard → Logs

5. Try connecting with `psql` directly to isolate the issue

## ✅ Success Indicators

You'll know everything is working when:

1. ✅ Test script shows all green checkmarks
2. ✅ Application starts with "Started SrmiggyApplication" message
3. ✅ `/api/vendors` returns vendor data
4. ✅ Can login with test credentials
5. ✅ Data persists between application restarts (not lost like H2)

---

**Need help?** Check the documentation files or run `./test-supabase-connection.sh` for automated diagnostics.
