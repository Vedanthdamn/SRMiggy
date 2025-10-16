-- ============================================
-- Verification Queries for Supabase Migration
-- Run these after executing supabase_migration.sql
-- ============================================

-- 1. Count total vendors (should be 8)
SELECT COUNT(*) as total_vendors FROM vendors;

-- 2. Count total menu items (should be 128)
SELECT COUNT(*) as total_menu_items FROM menu_items;

-- 3. View all vendors with their active status
SELECT 
    id,
    name,
    description,
    active,
    rating,
    created_at
FROM vendors
ORDER BY name;

-- 4. Count menu items per vendor (each should have 16)
SELECT 
    v.name as vendor_name,
    COUNT(mi.id) as item_count
FROM vendors v
LEFT JOIN menu_items mi ON v.id = mi.vendor_id
GROUP BY v.id, v.name
ORDER BY v.name;

-- 5. View menu items by category
SELECT 
    category,
    COUNT(*) as item_count
FROM menu_items
GROUP BY category
ORDER BY item_count DESC;

-- 6. Check vegetarian vs non-vegetarian items
SELECT 
    CASE WHEN is_veg THEN 'Vegetarian' ELSE 'Non-Vegetarian' END as food_type,
    COUNT(*) as item_count
FROM menu_items
GROUP BY is_veg;

-- 7. View sample menu items from each vendor
SELECT 
    v.name as vendor_name,
    mi.name as item_name,
    mi.category,
    mi.price,
    mi.is_veg
FROM vendors v
JOIN menu_items mi ON v.id = mi.vendor_id
ORDER BY v.name, mi.category, mi.name;

-- 8. Check price ranges
SELECT 
    MIN(price) as min_price,
    MAX(price) as max_price,
    AVG(price)::DECIMAL(10,2) as avg_price
FROM menu_items;

-- 9. Find most expensive items
SELECT 
    v.name as vendor_name,
    mi.name as item_name,
    mi.price,
    mi.category
FROM menu_items mi
JOIN vendors v ON mi.vendor_id = v.id
ORDER BY mi.price DESC
LIMIT 10;

-- 10. Find cheapest items
SELECT 
    v.name as vendor_name,
    mi.name as item_name,
    mi.price,
    mi.category
FROM menu_items mi
JOIN vendors v ON mi.vendor_id = v.id
ORDER BY mi.price ASC
LIMIT 10;

-- 11. Verify all foreign keys are valid
SELECT 
    mi.id,
    mi.name,
    mi.vendor_id,
    CASE 
        WHEN v.id IS NULL THEN 'INVALID - No matching vendor'
        ELSE 'Valid'
    END as fk_status
FROM menu_items mi
LEFT JOIN vendors v ON mi.vendor_id = v.id
WHERE v.id IS NULL;
-- Should return 0 rows

-- 12. Check for any NULL values in required fields
SELECT 
    'vendors' as table_name,
    COUNT(*) as null_names
FROM vendors
WHERE name IS NULL
UNION ALL
SELECT 
    'menu_items' as table_name,
    COUNT(*) as null_names
FROM menu_items
WHERE name IS NULL;
-- Both should return 0

-- ============================================
-- Expected Results Summary
-- ============================================
-- Total Vendors: 8
-- Total Menu Items: 128
-- Items per Vendor: 16 each
-- Categories: Main Course, Starter, Sides, Dessert, Beverage, Snacks
-- Price Range: ₹15 to ₹250
-- All vendors should have active = true
-- All vendors should have rating = 4.5
-- No NULL values in required fields
-- No orphaned menu items (all foreign keys valid)
-- ============================================
