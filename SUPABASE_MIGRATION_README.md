# Supabase Migration Guide

## Overview
This guide helps you migrate restaurant and menu data into your Supabase database for the SRMiggy application.

## Prerequisites
1. A Supabase project set up
2. The database schema already created (run `supabase-schema.sql` first if not done)
3. Access to Supabase SQL Editor

## Files
- **`supabase-schema.sql`**: Creates all tables, indexes, and RLS policies (run this first)
- **`supabase_migration.sql`**: Inserts all vendor and menu item data (run this second)

## Migration Steps

### Step 1: Prepare Your Supabase Project

1. Go to your Supabase project dashboard
2. Navigate to: **SQL Editor** (left sidebar)
3. Make sure the schema is already created. If not, run `supabase-schema.sql` first.

### Step 2: Run the Migration Script

The migration script (`supabase_migration.sql`) has been updated to handle common issues:

✅ **Idempotent**: Safe to run multiple times (uses `ON CONFLICT DO NOTHING`)
✅ **Transactional**: All-or-nothing - if any insert fails, everything rolls back
✅ **RLS-aware**: Temporarily disables Row Level Security during migration

#### Option A: Using Supabase SQL Editor (Recommended)

1. Open Supabase SQL Editor
2. Click **New Query**
3. Copy the entire contents of `supabase_migration.sql`
4. Paste into the editor
5. Click **Run** or press `Ctrl+Enter`

The script will:
- Start a transaction
- Temporarily disable RLS on vendors and menu_items tables
- Insert 8 vendors
- Insert 128 menu items across all vendors
- Re-enable RLS
- Commit all changes

#### Option B: Using Supabase CLI

```bash
# Make sure you're logged in
supabase login

# Link your project (if not already linked)
supabase link --project-ref your-project-ref

# Run the migration
supabase db execute -f supabase_migration.sql
```

### Step 3: Verify the Migration

Run this query in SQL Editor to verify:

```sql
-- Count vendors
SELECT COUNT(*) as vendor_count FROM vendors;
-- Should return: 8

-- Count menu items
SELECT COUNT(*) as menu_item_count FROM menu_items;
-- Should return: 128

-- See all vendors with their menu item counts
SELECT 
    v.name as vendor_name,
    COUNT(mi.id) as menu_items_count
FROM vendors v
LEFT JOIN menu_items mi ON v.id = mi.vendor_id
GROUP BY v.id, v.name
ORDER BY v.name;
```

Expected output:
- Biryani House: 16 items
- Burger Junction: 16 items
- Dessert House: 16 items
- Dosa Corner: 16 items
- Ice Cream Parlor: 16 items
- Pizza Paradise: 16 items
- Roll Junction: 16 items
- Thali Express: 16 items

## Troubleshooting

### Error: "permission denied for table vendors"
**Cause**: RLS policies blocking the insert

**Solution**: The updated migration script handles this automatically by temporarily disabling RLS. If you still see this error, make sure you're running the script as a service role user in Supabase.

### Error: "duplicate key value violates unique constraint"
**Cause**: Data already exists in the database

**Solution**: The script now includes `ON CONFLICT (id) DO NOTHING` clauses. Re-running the script will safely skip existing records.

### Error: "syntax error at or near"
**Cause**: Possible copy-paste issue or file encoding problem

**Solution**: 
1. Make sure you copied the entire file content
2. Check for any hidden characters
3. Try downloading the raw file from GitHub instead of copying

### Transaction Rolled Back
**Cause**: One or more inserts failed (e.g., foreign key constraint violation)

**Solution**: 
1. Make sure the schema is properly created first
2. Check that the vendors table exists
3. Verify all UUIDs are valid format

## Data Overview

The migration includes:

### Vendors (8 total)
1. **Biryani House** - Authentic Hyderabadi Biryani
2. **Dosa Corner** - South Indian Delicacies
3. **Burger Junction** - American Fast Food
4. **Pizza Paradise** - Italian Pizzas
5. **Thali Express** - North Indian Thali
6. **Roll Junction** - Delicious Rolls & Wraps
7. **Ice Cream Parlor** - Premium Ice Creams
8. **Dessert House** - Sweet Delights

### Menu Items (128 total)
Each vendor has 16 carefully curated menu items across various categories:
- Main Course
- Starters
- Sides
- Desserts
- Beverages

## Re-running the Migration

The script is **idempotent**, meaning you can run it multiple times safely:
- Existing records will be skipped (thanks to `ON CONFLICT DO NOTHING`)
- New records will be inserted
- No data will be duplicated

## Next Steps

After successful migration:

1. **Test the Application**: Start your backend and verify vendors and menu items appear
2. **Add More Data**: Use the same pattern to add more vendors/menu items
3. **Configure Application**: Update `application-supabase.properties` with your Supabase credentials

## Need Help?

If you encounter issues not covered here:
1. Check Supabase logs in the dashboard
2. Verify your Supabase project is active
3. Ensure you have proper permissions
4. Review the error message carefully - it often indicates exactly what's wrong

## Schema Updates

If you modify the schema:
1. Update `supabase-schema.sql`
2. Update `supabase_migration.sql` to match new structure
3. Test in a development environment first
4. Consider creating versioned migration files (e.g., `migration_v2.sql`)
