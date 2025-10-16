# Quick Start: Fixing Supabase Migration Errors

## Problem
The `supabase_migration.sql` file was showing errors when running in Supabase due to:
- Row Level Security (RLS) blocking direct inserts
- Missing idempotency (couldn't re-run safely)
- No transaction management

## Solution
✅ **All issues fixed!** The migration script has been updated with:
- Transaction wrapper (BEGIN...COMMIT)
- RLS handling (temporary disable/enable)
- Idempotent inserts (ON CONFLICT DO NOTHING)

## Quick Start (3 Steps)

### Step 1: Validate (Optional)
```bash
./validate_migration.sh
```
This checks the SQL file for common issues without needing a database.

### Step 2: Run Migration
1. Open [Supabase SQL Editor](https://app.supabase.com)
2. Navigate to your project → SQL Editor
3. Copy entire contents of `supabase_migration.sql`
4. Paste and click **Run**

### Step 3: Verify
Run queries from `verify_migration.sql` to confirm:
- 8 vendors inserted ✓
- 128 menu items inserted ✓
- All foreign keys valid ✓

## Expected Output
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

### "permission denied for table vendors"
✅ Fixed! The script now handles RLS automatically.

### "duplicate key value violates unique constraint"
✅ Fixed! Script is idempotent - safe to re-run.

### "current transaction is aborted"
This means an earlier error occurred. Check the error message above it in the Supabase logs.

## Files
- **supabase_migration.sql** - Main migration script (fixed)
- **SUPABASE_MIGRATION_README.md** - Detailed documentation
- **validate_migration.sh** - Pre-flight validation script
- **verify_migration.sql** - Post-migration verification queries

## Need Help?
See [SUPABASE_MIGRATION_README.md](./SUPABASE_MIGRATION_README.md) for detailed documentation.
