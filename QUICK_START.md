# Quick Start Guide - Supabase Configuration

## ✅ What Has Been Done

Your SRMiggy backend has been **automatically configured** to use Supabase PostgreSQL with the exact credentials you provided:

```
Database URL: jdbc:postgresql://db.awbmgncjszicqegrpsxj.supabase.co:5432/postgres
Username: postgres
Password: Beluga91!
```

## 🎯 Zero Manual Changes Required

Everything has been configured automatically:
- ✅ Database connection updated in `application.properties`
- ✅ PostgreSQL driver configured
- ✅ HikariCP connection pool optimized
- ✅ JPA/Hibernate configured for PostgreSQL
- ✅ Automatic table creation enabled
- ✅ All code remains unchanged
- ✅ All endpoints work identically

## 🚀 How to Run (3 Simple Steps)

### Step 1: Navigate to Backend
```bash
cd backend
```

### Step 2: Run the Application
```bash
mvn spring-boot:run
```

### Step 3: Test an Endpoint
```bash
curl http://localhost:8080/api/vendors
```

That's it! 🎉

## 📋 What Happens When You Run

1. **Application Starts** → Connects to Supabase PostgreSQL
2. **Tables Created** → Automatically creates 10 tables (users, vendors, orders, etc.)
3. **APIs Available** → All endpoints work at `http://localhost:8080/api/`
4. **Data Persists** → Everything saved to PostgreSQL (no more data loss!)

## 🔍 Verify Configuration (Optional)

Run the verification script:
```bash
cd backend
./verify-supabase-config.sh
```

Expected output:
```
✓ Database URL is correctly configured
✓ Database username is correctly configured
✓ PostgreSQL driver is correctly configured
✓ Project builds successfully
```

## 🌐 Network Requirements

**Important:** Your machine needs network access to:
- Host: `db.awbmgncjszicqegrpsxj.supabase.co`
- Port: `5432`

### Quick Connectivity Test
```bash
nc -zv db.awbmgncjszicqegrpsxj.supabase.co 5432
```

If this fails, check:
- Internet connectivity
- Firewall settings
- VPN/proxy configuration

## 📊 Tables Created Automatically

On first startup, these tables are auto-created:

1. **users** - User accounts (customers, vendors, admins, riders)
2. **vendors** - Restaurant/food outlet information
3. **menu_items** - Available food items
4. **orders** - Customer orders
5. **order_items** - Items in each order
6. **delivery_slots** - Time slots for delivery
7. **riders** - Delivery personnel
8. **payment_transactions** - Payment records
9. **wallet_transactions** - Wallet history
10. **settings** - App configuration

## 🎮 Test the APIs

### Get Vendors
```bash
curl http://localhost:8080/api/vendors
```

### Register User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "email": "john@example.com",
    "password": "password123",
    "fullName": "John Doe",
    "phone": "1234567890",
    "role": "CUSTOMER"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "password": "password123"
  }'
```

## ❓ Common Issues & Solutions

### Issue: "Connection refused"
**Problem:** Can't reach Supabase database
**Solution:** 
- Check internet connection
- Verify port 5432 is not blocked
- Ensure Supabase project is active

### Issue: "Port 8080 already in use"
**Problem:** Another app using port 8080
**Solution:**
```bash
# Kill process on port 8080
lsof -ti:8080 | xargs kill -9
```

### Issue: "Authentication failed"
**Problem:** Wrong password
**Solution:** Verify password in Supabase Dashboard

## 📚 Documentation

For detailed information, see:
- **SUPABASE_CONFIGURATION_COMPLETE.md** - Complete configuration details
- **MANUAL_TESTING_GUIDE.md** - Comprehensive testing steps
- **SUPABASE_CONNECTION_GUIDE.md** - Original connection setup guide

## ✨ Key Benefits

Before vs After:

| Aspect | Before (H2) | After (Supabase) |
|--------|-------------|------------------|
| **Database** | H2 In-Memory | PostgreSQL |
| **Data Persistence** | Lost on restart | Permanent |
| **Production Ready** | No | Yes |
| **Scalability** | Limited | High |
| **Backup** | None | Automatic |
| **Access** | Local only | Cloud-based |

## 🔒 Security Notes

**Current Configuration:**
- Password is in plain text in `application.properties`
- **Recommendation:** Use environment variables in production

**For Production:**
```bash
# Set environment variables instead
export DB_PASSWORD=Beluga91!
export DATABASE_URL=jdbc:postgresql://db.awbmgncjszicqegrpsxj.supabase.co:5432/postgres

# Update application.properties to use them
spring.datasource.url=${DATABASE_URL}
spring.datasource.password=${DB_PASSWORD}
```

## 🎉 Success Indicators

You'll know it's working when you see:

```
INFO  - HikariPool-1 - Start completed.
INFO  - Using dialect: org.hibernate.dialect.PostgreSQLDialect
INFO  - Started SrmiggyApplication in X.XXX seconds
```

And when you test an endpoint:
```bash
curl http://localhost:8080/api/vendors
# Returns: JSON array of vendors
```

## 💡 Pro Tips

1. **Check Logs:** Look for "HikariPool-1 - Start completed" to confirm connection
2. **View SQL:** Set `spring.jpa.show-sql=true` to see all SQL queries
3. **Monitor Connections:** Use Supabase Dashboard to see active connections
4. **Performance:** Connection pool is pre-configured for optimal performance

## 🚨 Important Notes

1. **No Code Changes:** All your business logic, endpoints, and services work identically
2. **Automatic Schema:** Tables are created automatically - no manual setup needed
3. **Data Persistence:** Data now survives application restarts
4. **Same APIs:** All API endpoints remain exactly the same

## 🎯 Next Steps (Optional)

1. Load seed data if needed
2. Set up backups (automatic with Supabase)
3. Configure Row Level Security (RLS) in Supabase
4. Set up monitoring and alerts
5. Consider using environment variables for credentials

---

**Everything is configured and ready to go!** Just run `mvn spring-boot:run` and start using your application with Supabase PostgreSQL. 🚀
