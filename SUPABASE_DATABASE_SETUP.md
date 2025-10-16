# Supabase Database Setup Guide

## Error: "relation vendors does not exist"?

**You're here because:** You tried to run `supabase_migration.sql` and got an error.

**The fix:** Run the scripts in the correct order!

---

## 🚀 Complete Setup (2 Scripts, 4 Steps)

### Prerequisites
- Supabase account ([sign up free](https://supabase.com))
- New Supabase project created

### Script Execution Order

```
1. supabase-schema.sql    ← Creates tables (MUST RUN FIRST)
2. supabase_migration.sql ← Inserts data (MUST RUN SECOND)
```

---

## Step-by-Step Instructions

### 🔧 Step 1: Create Database Tables

**File:** `backend/src/main/resources/supabase-schema.sql`

**What it does:** Creates all tables, indexes, constraints, and security policies

**How to run:**
1. Log in to [Supabase Dashboard](https://app.supabase.com)
2. Select your project
3. Go to **SQL Editor** (left sidebar)
4. Click **New query**
5. Copy **entire contents** of `backend/src/main/resources/supabase-schema.sql`
6. Paste into the SQL editor
7. Click **Run** (or press Ctrl/Cmd + Enter)

**Expected result:** ✅ `Success. No rows returned`

**Tables created:**
- users
- vendors
- menu_items
- delivery_slots
- riders
- orders
- order_items
- payment_transactions
- wallet_transactions
- settings

---

### 📊 Step 2: Insert Vendor and Menu Data

**File:** `supabase_migration.sql` (in root directory)

**What it does:** Inserts 8 vendors and 128 menu items

**How to run:**
1. In the same **SQL Editor** in Supabase
2. Create a new query (or clear the previous one)
3. Copy **entire contents** of `supabase_migration.sql`
4. Paste into the SQL editor
5. Click **Run**

**Expected result:** ✅ `Success. No rows returned`

**Data inserted:**
- 8 vendors (Biryani House, Dosa Corner, Burger Junction, Pizza Paradise, Thali Express, Roll Junction, Ice Cream Parlor, Dessert House)
- 128 menu items (16 items per vendor)

---

### ✅ Step 3: Verify Setup

**File:** `verify_migration.sql` (in root directory)

**How to run:**
1. In the same **SQL Editor** in Supabase
2. Copy queries from `verify_migration.sql`
3. Run them one by one or all together

**Expected results:**
```sql
-- Query 1: Count vendors
8

-- Query 2: Count menu items  
128

-- Query 3: Items per vendor
Each vendor should have 16 items
```

---

### 🔌 Step 4: Configure Your Application

**File:** `backend/src/main/resources/application-supabase.properties`

Update with your Supabase credentials:

```properties
# Supabase PostgreSQL Connection
spring.datasource.url=jdbc:postgresql://db.[YOUR-PROJECT-REF].supabase.co:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=[YOUR-DATABASE-PASSWORD]
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

# Connection Pool Settings
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000

# Hibernate settings
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true
```

**To find your credentials:**
1. Go to your Supabase project
2. Click **Settings** → **Database**
3. Copy **Connection String** (use the one for Java/Spring)
4. Password is the one you set when creating the project

**Run with Supabase profile:**
```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=supabase
```

---

## 🐛 Common Issues & Solutions

### ❌ Error: "relation 'vendors' does not exist"
**Problem:** You tried to run `supabase_migration.sql` BEFORE `supabase-schema.sql`

**Solution:** Run `supabase-schema.sql` first (Step 1), then run `supabase_migration.sql` (Step 2)

---

### ❌ Error: "permission denied for table vendors"
**Problem:** Row Level Security (RLS) is blocking the insert

**Solution:** The script handles this automatically. If you still see this error, make sure you're using the Supabase **service_role** key or run the script as the **postgres** user in SQL Editor (which you are by default).

---

### ❌ Error: "duplicate key value violates unique constraint"
**Problem:** Data already exists in the database

**Solution:** This is actually fine! The script uses `ON CONFLICT DO NOTHING` to make it safe to re-run. This message means some/all data was already inserted.

---

### ❌ Error: "current transaction is aborted"
**Problem:** An earlier SQL statement in the script failed

**Solution:** Look for the **first error** in the Supabase SQL Editor output. Fix that error first, then re-run the script.

---

### ❓ How to start over (reset database)?
If you want to completely reset and start fresh:

```sql
-- ⚠️ WARNING: This deletes ALL data and tables!
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
```

Then run Steps 1-3 again.

---

## 📋 File Reference

| File | Location | Purpose |
|------|----------|---------|
| `supabase-schema.sql` | `backend/src/main/resources/` | Creates all database tables |
| `supabase_migration.sql` | Root directory | Inserts vendor and menu data |
| `verify_migration.sql` | Root directory | Verification queries |
| `QUICKSTART_MIGRATION.md` | Root directory | Quick reference guide |

---

## 🎯 Quick Reference

### Correct Order
1. ✅ Run `supabase-schema.sql` (creates tables)
2. ✅ Run `supabase_migration.sql` (inserts data)
3. ✅ Run `verify_migration.sql` (verifies setup)

### Incorrect Order
1. ❌ Run `supabase_migration.sql` first → **ERROR: relation does not exist**

---

## 📚 Additional Resources

- [Supabase Documentation](https://supabase.com/docs)
- [SUPABASE_SETUP.md](./SUPABASE_SETUP.md) - Detailed Supabase setup guide
- [MIGRATION_GUIDE.md](./MIGRATION_GUIDE.md) - Migration from H2 to Supabase
- [README.md](./README.md) - Complete project documentation

---

## ✨ That's it!

After completing these steps, your Supabase database should be fully set up with:
- ✅ All tables created
- ✅ 8 vendors with images and descriptions
- ✅ 128 menu items across all vendors
- ✅ Proper relationships and constraints
- ✅ Row Level Security configured
- ✅ Ready for your application to connect

Need help? Check the [QUICKSTART_MIGRATION.md](./QUICKSTART_MIGRATION.md) for additional troubleshooting.
