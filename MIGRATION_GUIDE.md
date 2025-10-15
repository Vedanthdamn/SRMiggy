# Migration Guide: H2 to Supabase PostgreSQL

This guide helps you migrate your existing SRMiggy application from H2 in-memory database to Supabase PostgreSQL.

## Overview of Changes

### Key Changes
- **Primary Keys**: Changed from `Long` (auto-increment) to `UUID` (randomly generated)
- **Database**: Changed from H2 to PostgreSQL
- **Connection Pooling**: Added HikariCP for production-grade connection management
- **Timestamps**: All tables now have `created_at` and `updated_at` with automatic triggers
- **Security**: Row Level Security (RLS) policies enabled for data isolation

### Breaking Changes

#### 1. ID Type Change
**Before (H2):**
```java
private Long id;
```

**After (Supabase):**
```java
private UUID id;
```

#### 2. API Response Format
**Before:**
```json
{
  "id": 1,
  "name": "Biryani House"
}
```

**After:**
```json
{
  "id": "a1111111-1111-4ef8-bb6d-6bb9bd380a11",
  "name": "Biryani House"
}
```

#### 3. Frontend URL Changes
**Before:**
```javascript
fetch('/api/vendors/1')
```

**After:**
```javascript
fetch('/api/vendors/a1111111-1111-4ef8-bb6d-6bb9bd380a11')
```

## Step-by-Step Migration

### Phase 1: Backup Current Data (Optional)

If you have important data in H2:

1. **Export H2 Data**
```bash
# Start your application with H2
mvn spring-boot:run

# Access H2 Console: http://localhost:8080/h2-console
# Run export queries for each table
SELECT * FROM users;
SELECT * FROM vendors;
-- etc.
```

2. **Save Export Results**
Save the query results as CSV or SQL insert statements.

### Phase 2: Set Up Supabase

1. **Create Supabase Project**
   - Go to https://supabase.com
   - Create new project
   - Save your database password

2. **Run Schema Creation**
   - Open Supabase SQL Editor
   - Copy contents of `backend/src/main/resources/supabase-schema.sql`
   - Execute the script
   - Verify all 10 tables were created

3. **Load Seed Data**
   - Open Supabase SQL Editor
   - Copy contents of `backend/src/main/resources/supabase-seed-data.sql`
   - Execute the script
   - Verify test data is loaded

### Phase 3: Update Configuration

1. **Update application-supabase.properties**
```properties
spring.datasource.url=jdbc:postgresql://db.YOUR_PROJECT_REF.supabase.co:5432/postgres
spring.datasource.password=YOUR_DATABASE_PASSWORD
```

2. **Set Active Profile**
```bash
# Option 1: Environment variable
export SPRING_PROFILES_ACTIVE=supabase

# Option 2: Command line
mvn spring-boot:run -Dspring-boot.run.profiles=supabase

# Option 3: application.properties
spring.profiles.active=supabase
```

### Phase 4: Update Frontend

#### 1. Update API Client

**Before:**
```javascript
// api.js
export const getVendor = (id) => {
  return axios.get(`/api/vendors/${id}`);
};
```

**After:**
```javascript
// api.js - no changes needed, just use UUID strings
export const getVendor = (id) => {
  return axios.get(`/api/vendors/${id}`); // id is now UUID string
};
```

#### 2. Update State Management

**Before:**
```javascript
const [selectedVendor, setSelectedVendor] = useState(null);

// Storing vendor ID
localStorage.setItem('vendorId', vendor.id); // stores number
```

**After:**
```javascript
const [selectedVendor, setSelectedVendor] = useState(null);

// Storing vendor ID - works the same
localStorage.setItem('vendorId', vendor.id); // stores UUID string
```

#### 3. Update URL Parameters

**React Router - Before:**
```javascript
<Route path="/vendor/:id" element={<VendorDetails />} />

// In component
const { id } = useParams();
const vendorId = parseInt(id); // parsing to number
```

**React Router - After:**
```javascript
<Route path="/vendor/:id" element={<VendorDetails />} />

// In component
const { id } = useParams();
const vendorId = id; // use directly as UUID string
```

#### 4. Update Forms

**Order Form - Before:**
```javascript
const createOrder = async () => {
  const orderData = {
    vendorId: parseInt(selectedVendor.id),
    slotId: parseInt(selectedSlot.id),
    items: cartItems.map(item => ({
      menuItemId: parseInt(item.id),
      quantity: item.quantity
    }))
  };
  
  await api.createOrder(orderData);
};
```

**Order Form - After:**
```javascript
const createOrder = async () => {
  const orderData = {
    vendorId: selectedVendor.id, // already UUID string
    slotId: selectedSlot.id,     // already UUID string
    items: cartItems.map(item => ({
      menuItemId: item.id,       // already UUID string
      quantity: item.quantity
    }))
  };
  
  await api.createOrder(orderData);
};
```

### Phase 5: Testing

#### 1. Backend Testing

```bash
# Start backend with Supabase profile
cd backend
mvn clean install
mvn spring-boot:run -Dspring-boot.run.profiles=supabase
```

Check logs for successful connection:
```
INFO  HikariDataSource - SRMiggyHikariCP - Starting...
INFO  HikariDataSource - SRMiggyHikariCP - Start completed.
```

#### 2. API Testing

**Test Authentication:**
```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "fullName": "Test User"
  }'

# Should return JWT token with UUID userId
```

**Test Vendors:**
```bash
# Get all vendors
curl http://localhost:8080/api/vendors

# Should return vendors with UUID ids
```

#### 3. Frontend Testing

```bash
# Start frontend
cd frontend
npm run dev
```

Test flows:
- ✅ User registration/login
- ✅ Browse vendors
- ✅ View menu items
- ✅ Add to cart
- ✅ Create order
- ✅ View order history
- ✅ Wallet operations

### Phase 6: Data Migration (If Needed)

If you have existing H2 data to migrate:

#### 1. Export from H2
```sql
-- Export users (excluding id)
SELECT username, email, password, full_name, phone, address, role, enabled, wallet_balance, loyalty_points
FROM users;
```

#### 2. Transform and Import to Supabase
```sql
-- Import to Supabase (generates new UUIDs)
INSERT INTO users (username, email, password, full_name, phone, address, role, enabled, wallet_balance, loyalty_points)
VALUES 
  ('user1', 'user1@example.com', '$2a$10$...', 'User One', '1234567890', 'Address 1', 'CUSTOMER', true, 100.0, 50.0),
  ('user2', 'user2@example.com', '$2a$10$...', 'User Two', '0987654321', 'Address 2', 'CUSTOMER', true, 200.0, 25.0);
```

**Note:** You'll need to maintain a mapping of old IDs to new UUIDs for relationships.

#### 3. Handle Relationships

For tables with foreign keys, you'll need to:
1. Export parent tables first (users, vendors, etc.)
2. Note the generated UUIDs
3. Use those UUIDs when importing child tables

Example:
```sql
-- After inserting vendors, get their UUIDs
SELECT id, name FROM vendors;

-- Use those UUIDs for menu items
INSERT INTO menu_items (vendor_id, name, price, ...)
VALUES ('uuid-of-vendor-1', 'Item Name', 100.0, ...);
```

## Common Issues and Solutions

### Issue 1: UUID Parse Exception

**Error:**
```
java.lang.IllegalArgumentException: Invalid UUID string
```

**Solution:**
Ensure frontend is sending UUID strings, not numbers:
```javascript
// ❌ Wrong
const vendorId = 1;

// ✅ Correct
const vendorId = "a1111111-1111-4ef8-bb6d-6bb9bd380a11";
```

### Issue 2: Connection Timeout

**Error:**
```
Connection timeout
```

**Solution:**
1. Check Supabase connection string
2. Verify IP whitelist in Supabase settings
3. Check firewall settings
4. Increase connection timeout in application-supabase.properties:
```properties
spring.datasource.hikari.connection-timeout=60000
```

### Issue 3: RLS Policy Blocking

**Error:**
```
new row violates row-level security policy
```

**Solution:**
The backend uses service role credentials, so this shouldn't happen. If it does:
1. Verify you're using the correct database credentials
2. Check RLS policies in Supabase dashboard
3. Temporarily disable RLS for testing:
```sql
ALTER TABLE table_name DISABLE ROW LEVEL SECURITY;
```

### Issue 4: Frontend Still Shows Numbers

**Problem:**
Frontend displays vendor ID as number instead of UUID.

**Solution:**
Clear browser cache and local storage:
```javascript
localStorage.clear();
sessionStorage.clear();
```

## Rollback Plan

If you need to rollback to H2:

1. **Switch Profile Back**
```bash
# Remove SPRING_PROFILES_ACTIVE or set to default
unset SPRING_PROFILES_ACTIVE

# Or run without profile
mvn spring-boot:run
```

2. **Restore H2 Data**
If you backed up H2 data, restore it using your export files.

3. **Revert Code Changes** (if needed)
```bash
git revert <commit-hash>
```

## Performance Comparison

### H2 (Before)
- ✅ Fast in-memory operations
- ✅ No network latency
- ❌ Data lost on restart
- ❌ Not suitable for production
- ❌ No concurrent access
- ❌ Limited features

### Supabase PostgreSQL (After)
- ✅ Production-grade reliability
- ✅ Data persistence
- ✅ Concurrent access support
- ✅ Advanced features (RLS, triggers, etc.)
- ✅ Automatic backups
- ✅ Scalable
- ⚠️ Slight network latency (mitigated by connection pooling)

## Next Steps

After successful migration:

1. ✅ Test all features thoroughly
2. ✅ Update documentation
3. ✅ Configure environment variables for production
4. ✅ Set up monitoring in Supabase dashboard
5. ✅ Enable SSL/TLS (already enabled by Supabase)
6. ✅ Configure backup schedule
7. ✅ Set up alerting for database metrics

## Resources

- [Supabase Documentation](https://supabase.com/docs)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Spring Data JPA with PostgreSQL](https://spring.io/guides/gs/accessing-data-jpa/)
- [UUID Best Practices](https://www.postgresql.org/docs/current/datatype-uuid.html)

---

**Need Help?**
- Check `SUPABASE_SETUP.md` for detailed setup instructions
- Check `EXAMPLE_SERVICE_CODE.md` for code examples
- Review Supabase dashboard for database metrics
- Check application logs for detailed error messages
