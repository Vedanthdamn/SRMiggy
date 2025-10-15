# SRMiggy Backend - Supabase Connection Guide

## 🎯 Quick Answer: Is Backend Connecting to Supabase?

**Current Status:** The backend is configured for **H2 in-memory database** by default. To connect to Supabase, you need to:

1. **Configure credentials** (see below)
2. **Set active profile to `supabase`**
3. **Run schema SQL** in Supabase

## 🚀 Quick Setup (5 Minutes)

### Step 1: Get Supabase Credentials
1. Go to [https://supabase.com](https://supabase.com) → Your Project
2. Navigate to **Settings** → **Database**
3. Note down:
   - **Project Reference:** (e.g., `abcdefghijklmnop` from `db.abcdefghijklmnop.supabase.co`)
   - **Database Password:** Your project password

### Step 2: Configure Connection

**Option A: Environment Variables (Recommended)**
```bash
cd backend
cp .env.example .env
# Edit .env and set your credentials
```

**Option B: Direct File Edit**
Edit `src/main/resources/application-supabase.properties`:
```properties
spring.datasource.url=jdbc:postgresql://db.YOUR-PROJECT-REF.supabase.co:5432/postgres
spring.datasource.password=YOUR-PASSWORD
```

### Step 3: Create Database Schema
1. Open Supabase Dashboard → SQL Editor
2. Copy and run `src/main/resources/supabase-schema.sql`
3. (Optional) Run `src/main/resources/supabase-seed-data.sql`

### Step 4: Test Connection
```bash
# Run automated test
./test-supabase-connection.sh

# Or start application
mvn spring-boot:run -Dspring-boot.run.profiles=supabase
```

## 🧪 Testing Tools

### Automated Test Script
Validates configuration and tests connection:
```bash
./test-supabase-connection.sh
```

**What it checks:**
- ✅ Configuration validity
- ✅ Database connection
- ✅ Tables existence
- ✅ Application startup
- ✅ API endpoints

### Manual Connection Test
Test connection with Spring Boot validator:
```bash
mvn spring-boot:run \
  -Dspring-boot.run.profiles=supabase \
  -Dspring-boot.run.mainClass=com.srmiggy.util.SupabaseConnectionValidator
```

### Direct Database Test
If you have `psql` installed:
```bash
psql -h db.YOUR-PROJECT-REF.supabase.co \
     -p 5432 \
     -U postgres \
     -d postgres
```

## 🔧 Configuration Options

### Using Environment Variables (.env file)
```env
SUPABASE_PROJECT_REF=abcdefghijklmnop
SUPABASE_DB_PASSWORD=your-password
DATABASE_URL=jdbc:postgresql://db.abcdefghijklmnop.supabase.co:5432/postgres
SPRING_PROFILES_ACTIVE=supabase
```

### Using application-supabase.properties
```properties
spring.datasource.url=jdbc:postgresql://db.abcdefghijklmnop.supabase.co:5432/postgres
spring.datasource.password=your-password
```

Then activate profile:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=supabase
```

### Using System Properties
```bash
mvn spring-boot:run \
  -Dspring.profiles.active=supabase \
  -Dspring.datasource.url=jdbc:postgresql://db.xxx.supabase.co:5432/postgres \
  -Dspring.datasource.password=your-password
```

## 📊 Verifying Connection

### Successful Connection Indicators
Look for these in logs:
```
INFO  HikariDataSource - SRMiggyHikariCP - Starting...
INFO  HikariDataSource - SRMiggyHikariCP - Start completed.
INFO  SrmiggyApplication - Started SrmiggyApplication in X.XXX seconds
```

### Test API Endpoint
```bash
# Should return vendor list
curl http://localhost:8080/api/vendors

# Should return 401 (authentication required)
curl http://localhost:8080/api/orders
```

## 🐛 Common Issues and Solutions

### Issue: "Connection refused"
**Cause:** Cannot reach Supabase
**Fix:**
- Check internet connection
- Verify project reference is correct
- Ensure port 5432 not blocked

### Issue: "Authentication failed"
**Cause:** Wrong password
**Fix:**
- Double-check password in Supabase dashboard
- Reset password if needed
- Check for extra spaces

### Issue: "Relation 'users' does not exist"
**Cause:** Schema not created
**Fix:**
- Run `supabase-schema.sql` in SQL Editor
- Verify with: `SELECT * FROM information_schema.tables WHERE table_schema='public'`

### Issue: "Placeholder values detected"
**Cause:** Configuration not updated
**Fix:**
- Replace `<your-project-ref>` with actual value
- Replace `<your-supabase-password>` with actual password
- Set `SPRING_PROFILES_ACTIVE=supabase`

## 📁 Files Reference

```
backend/
├── .env.example              # Environment variables template
├── .env                      # Your credentials (gitignored)
├── test-supabase-connection.sh  # Automated test script
├── src/main/
│   ├── java/com/srmiggy/
│   │   └── util/
│   │       └── SupabaseConnectionValidator.java  # Java validator
│   └── resources/
│       ├── application.properties              # Default (H2)
│       ├── application-supabase.properties     # Supabase config
│       ├── supabase-schema.sql                 # Database schema
│       └── supabase-seed-data.sql              # Test data
```

## 🔄 Switching Between H2 and Supabase

### Use H2 (Development - Default)
```bash
mvn spring-boot:run
# OR
SPRING_PROFILES_ACTIVE=default mvn spring-boot:run
```

### Use Supabase (Production)
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=supabase
# OR with .env
SPRING_PROFILES_ACTIVE=supabase mvn spring-boot:run
```

## 📚 Additional Documentation

- **Comprehensive Setup:** See [SUPABASE_CONNECTION_GUIDE.md](../SUPABASE_CONNECTION_GUIDE.md)
- **Detailed Supabase Setup:** See [SUPABASE_SETUP.md](../SUPABASE_SETUP.md)
- **Migration Guide:** See [MIGRATION_GUIDE.md](../MIGRATION_GUIDE.md)
- **Code Examples:** See [EXAMPLE_SERVICE_CODE.md](../EXAMPLE_SERVICE_CODE.md)

## 🆘 Still Having Issues?

1. **Run automated test:**
   ```bash
   ./test-supabase-connection.sh
   ```

2. **Check full logs:**
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=supabase > app.log 2>&1
   tail -f app.log
   ```

3. **Validate with Java tool:**
   ```bash
   mvn spring-boot:run \
     -Dspring-boot.run.profiles=supabase \
     -Dspring-boot.run.mainClass=com.srmiggy.util.SupabaseConnectionValidator
   ```

4. **Check Supabase dashboard:**
   - Database status
   - Connection logs
   - Query performance

## ✅ Verification Checklist

Before considering setup complete:

- [ ] Supabase project created
- [ ] Connection credentials obtained
- [ ] `.env` file created from `.env.example`
- [ ] Credentials filled in (no placeholders)
- [ ] `SPRING_PROFILES_ACTIVE=supabase` set
- [ ] Schema SQL executed in Supabase
- [ ] Test script passes all checks
- [ ] Application starts without errors
- [ ] API returns data (not empty arrays)

## 🎓 Understanding the Configuration

### H2 vs Supabase
| Feature | H2 (Default) | Supabase |
|---------|--------------|----------|
| Database | In-memory | PostgreSQL |
| Data Persistence | Lost on restart | Permanent |
| Setup Complexity | None | 5 minutes |
| Production Ready | No | Yes |
| Auto-created Tables | Yes | Via SQL script |
| Connection | Local | Remote |

### Profile System
Spring Boot uses profiles to switch configurations:
- **No profile/default:** Uses `application.properties` (H2)
- **Profile=supabase:** Uses `application-supabase.properties` (PostgreSQL)

Activate with:
```bash
# In .env
SPRING_PROFILES_ACTIVE=supabase

# Or command line
mvn spring-boot:run -Dspring-boot.run.profiles=supabase

# Or in application.properties
spring.profiles.active=supabase
```

## 🔒 Security Notes

1. **Never commit credentials:**
   - `.env` is in `.gitignore`
   - Use environment variables in production

2. **Use strong passwords:**
   - Database password
   - JWT secret

3. **Production checklist:**
   - [ ] Change default JWT secret
   - [ ] Use environment variables
   - [ ] Enable HTTPS
   - [ ] Configure CORS properly
   - [ ] Set up Row Level Security in Supabase

## 📞 Support

- **Documentation:** Check all `.md` files in project root
- **Test Tools:** Use provided scripts before asking for help
- **Logs:** Always check application logs first
- **Supabase Dashboard:** Monitor database metrics and logs

---

**TL;DR:** Backend currently uses H2. To use Supabase: get credentials, update `.env`, run `./test-supabase-connection.sh`.
