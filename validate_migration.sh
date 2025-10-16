#!/bin/bash
# SQL Syntax Validation Script for supabase_migration.sql
# This script performs basic validation checks without requiring a database

echo "==================================="
echo "SQL Migration Validation Script"
echo "==================================="
echo ""

SQL_FILE="supabase_migration.sql"

if [ ! -f "$SQL_FILE" ]; then
    echo "❌ ERROR: $SQL_FILE not found!"
    exit 1
fi

echo "✓ File found: $SQL_FILE"
echo ""

# Count various elements
echo "📊 Statistics:"
echo "-----------------------------------"
echo "Total lines: $(wc -l < $SQL_FILE)"
echo "INSERT statements: $(grep -c "^INSERT INTO" $SQL_FILE)"
echo "  - Vendors: $(grep -c "INSERT INTO vendors" $SQL_FILE)"
echo "  - Menu items: $(grep -c "INSERT INTO menu_items" $SQL_FILE)"
echo "ON CONFLICT clauses: $(grep -c "ON CONFLICT (id) DO NOTHING" $SQL_FILE)"
echo "Comment lines: $(grep -c "^--" $SQL_FILE)"
echo ""

# Validation checks
echo "🔍 Validation Checks:"
echo "-----------------------------------"

# Check for BEGIN/COMMIT
if grep -q "^BEGIN;" $SQL_FILE && grep -q "^COMMIT;" $SQL_FILE; then
    echo "✓ Transaction wrapper present (BEGIN...COMMIT)"
else
    echo "❌ Missing transaction wrapper"
fi

# Check for RLS handling
if grep -q "DISABLE ROW LEVEL SECURITY" $SQL_FILE && grep -q "ENABLE ROW LEVEL SECURITY" $SQL_FILE; then
    echo "✓ RLS handling present (DISABLE...ENABLE)"
else
    echo "⚠️  Warning: RLS handling may be missing"
fi

# Check that all INSERTs have ON CONFLICT
INSERT_COUNT=$(grep -c "^INSERT INTO" $SQL_FILE)
CONFLICT_COUNT=$(grep -c "ON CONFLICT (id) DO NOTHING" $SQL_FILE)

if [ "$INSERT_COUNT" -eq "$CONFLICT_COUNT" ]; then
    echo "✓ All $INSERT_COUNT INSERT statements have ON CONFLICT clauses"
else
    echo "❌ Mismatch: $INSERT_COUNT INSERTs but only $CONFLICT_COUNT ON CONFLICT clauses"
fi

# Check for UUID format in vendor inserts
echo ""
echo "🔑 UUID Validation:"
echo "-----------------------------------"
UUID_PATTERN="^INSERT INTO vendors \(id"
if grep "$UUID_PATTERN" $SQL_FILE | head -1 | grep -qE "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"; then
    echo "✓ UUIDs appear to be in valid format"
else
    echo "⚠️  Warning: UUID format might be incorrect"
fi

# Check for common SQL syntax issues
echo ""
echo "🐛 Common Issues Check:"
echo "-----------------------------------"

# Check for missing semicolons (lines that should end with semicolon but don't)
if grep -E "^(INSERT|UPDATE|DELETE|ALTER)" $SQL_FILE | grep -v ";$" | grep -q .; then
    echo "⚠️  Warning: Some SQL statements might be missing semicolons"
else
    echo "✓ No obvious missing semicolons"
fi

# Check for single quotes in strings (should be escaped)
if grep -o "VALUES.*'.*'.*'" $SQL_FILE | grep -q "''"; then
    echo "⚠️  Warning: Found escaped quotes (this is fine if intentional)"
else
    echo "✓ No escaped quotes found (or properly handled)"
fi

# Check file size
FILE_SIZE=$(wc -c < $SQL_FILE)
if [ $FILE_SIZE -gt 100000 ]; then
    echo "⚠️  Large file ($FILE_SIZE bytes) - may take time to execute"
else
    echo "✓ File size reasonable ($FILE_SIZE bytes)"
fi

echo ""
echo "==================================="
echo "✅ Validation Complete"
echo "==================================="
echo ""
echo "📝 Next Steps:"
echo "1. Review any warnings above"
echo "2. Open Supabase SQL Editor"
echo "3. Run supabase-schema.sql (if not done)"
echo "4. Copy and paste supabase_migration.sql"
echo "5. Execute and verify with queries in SUPABASE_MIGRATION_README.md"
echo ""
