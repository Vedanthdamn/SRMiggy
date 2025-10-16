# Quick Start: Fixing Supabase Migration Errors

## Problem
Getting error: **`ERROR: 42P01: relation "vendors" does not exist`** when running `supabase_migration.sql`?

**Root Cause:** You're trying to insert data into tables that haven't been created yet!

## Solution - Run Scripts in Correct Order! ✅

The setup requires **TWO scripts** to be run in the correct sequence:

1. **FIRST:** `supabase-schema.sql` - Creates all tables
2. **THEN:** `supabase_migration.sql` - Inserts vendor and menu data

## Quick Start (4 Steps)

### Step 1: Create Tables (REQUIRED)
1. Open [Supabase SQL Editor](https://app.supabase.com)
2. Navigate to your project → SQL Editor
3. Copy entire contents of `backend/src/main/resources/supabase-schema.sql`
4. Paste and click **Run**
5. ✅ You should see: **Success. No rows returned**

### Step 2: Validate (Optional)
```bash
./validate_migration.sh
```
This checks the SQL file for common issues without needing a database.

### Step 3: Insert Data
1. In Supabase SQL Editor (same place as Step 1)
2. Copy entire contents of `supabase_migration.sql`
3. Paste and click **Run**
4. ✅ You should see: **Success. No rows returned**

### Step 4: Verify
Run queries from `verify_migration.sql` to confirm:
- 8 vendors inserted ✓
- 128 menu items inserted ✓
- All foreign keys valid ✓

## Expected Output

After Step 1 (Schema):
```
Success. No rows returned
```

After Step 3 (Data):
```
Success. No rows returned
```

After Step 4 (Verification):
```
✅ 8 vendors inserted
✅ 128 menu items across all vendors
✅ No errors or warnings
```

## Vendors Included
1. Biryani House (16 items)
2. Dosa Corner (16 items)
3. Burger Junction (16 items)
4. Pizza Paradise (16 items)
5. Thali Express (16 items)
6. Roll Junction (16 items)
7. Ice Cream Parlor (16 items)
8. Dessert House (16 items)

## Troubleshooting

### "ERROR: 42P01: relation 'vendors' does not exist"
❌ **You skipped Step 1!** 
✅ **Solution:** Run `backend/src/main/resources/supabase-schema.sql` FIRST to create tables

### "permission denied for table vendors"
✅ Fixed! The script now handles RLS automatically.

### "duplicate key value violates unique constraint"
✅ This is normal - it means data already exists. The script is idempotent and safe to re-run.

### "current transaction is aborted"
This means an earlier error occurred. Check the error message above it in the Supabase logs.

### How to start fresh?
If you want to recreate everything:
1. Drop all tables in Supabase SQL Editor:
   ```sql
   DROP SCHEMA public CASCADE;
   CREATE SCHEMA public;
   ```
2. Run Step 1 (schema) again
3. Run Step 3 (data) again

## Files
- **supabase_migration.sql** - Main migration script (fixed)
- **SUPABASE_MIGRATION_README.md** - Detailed documentation
- **validate_migration.sh** - Pre-flight validation script
- **verify_migration.sql** - Post-migration verification queries

## Need Help?
See [SUPABASE_MIGRATION_README.md](./SUPABASE_MIGRATION_README.md) for detailed documentation.
