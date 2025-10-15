# Supabase PostgreSQL Integration Guide

This guide explains how to connect your SRMiggy application to Supabase PostgreSQL database and make it production-ready.

## Overview

The application has been updated to use:
- **UUID primary keys** instead of Long/Integer IDs
- **PostgreSQL** as the database (via Supabase)
- **HikariCP** connection pooling for optimal performance
- **Row Level Security (RLS)** for data isolation
- **Automatic timestamps** with database triggers

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Supabase Setup](#supabase-setup)
3. [Database Schema Creation](#database-schema-creation)
4. [Application Configuration](#application-configuration)
5. [Running the Application](#running-the-application)
6. [Data Migration](#data-migration)
7. [API Changes](#api-changes)
8. [Testing](#testing)
9. [Production Deployment](#production-deployment)

## Prerequisites

- Supabase account (free tier available at https://supabase.com)
- Java 17+
- Maven 3.6+
- Git

## Supabase Setup

### Step 1: Create a Supabase Project

1. Go to https://supabase.com and sign up/login
2. Click "New Project"
3. Fill in the details:
   - **Name**: SRMiggy
   - **Database Password**: Choose a strong password (save this!)
   - **Region**: Choose closest to your users
   - **Pricing Plan**: Free (for development)
4. Wait for the project to be provisioned (~2 minutes)

### Step 2: Get Your Connection Credentials

1. In your Supabase dashboard, go to **Settings** > **Database**
2. Note down the following:
   - **Host**: `db.<your-project-ref>.supabase.co`
   - **Database name**: `postgres`
   - **Port**: `5432`
   - **User**: `postgres`
   - **Password**: The password you set during project creation

Example connection string:
```
jdbc:postgresql://db.abcdefghijklmnop.supabase.co:5432/postgres
```

## Database Schema Creation

### Step 1: Run the SQL Schema

1. In your Supabase dashboard, go to **SQL Editor**
2. Click **New Query**
3. Copy the entire contents of `backend/src/main/resources/supabase-schema.sql`
4. Paste it into the SQL editor
5. Click **Run** (or press Ctrl/Cmd + Enter)
6. Verify that all tables were created successfully

The schema includes:
- ✅ 10 tables (users, vendors, menu_items, orders, order_items, delivery_slots, riders, payment_transactions, wallet_transactions, settings)
- ✅ UUID primary keys with `gen_random_uuid()`
- ✅ Foreign key relationships with appropriate CASCADE rules
- ✅ Indexes for performance optimization
- ✅ Automatic `updated_at` triggers
- ✅ Row Level Security (RLS) policies

### Step 2: Verify Tables

Run this query to verify all tables were created:

```sql
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public' 
ORDER BY table_name;
```

You should see:
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

## Application Configuration

### Step 1: Update Configuration File

1. Open `backend/src/main/resources/application-supabase.properties`
2. Replace the placeholder values with your Supabase credentials:

```properties
# Replace <your-project-ref> with your actual Supabase project reference
spring.datasource.url=jdbc:postgresql://db.abcdefghijklmnop.supabase.co:5432/postgres

# Replace <your-supabase-password> with your database password
spring.datasource.password=your-secure-password-here
```

### Step 2: Set Active Profile

You can activate the Supabase profile in several ways:

**Option A: Command Line (Recommended for testing)**
```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=supabase
```

**Option B: Environment Variable**
```bash
export SPRING_PROFILES_ACTIVE=supabase
mvn spring-boot:run
```

**Option C: application.properties**
Add this line to `application.properties`:
```properties
spring.profiles.active=supabase
```

**Option D: IDE Configuration**
In IntelliJ IDEA / Eclipse:
- Run Configurations → Environment Variables
- Add: `SPRING_PROFILES_ACTIVE=supabase`

### Step 3: Secure Your Credentials (Production)

For production, use environment variables instead of hardcoding:

```properties
spring.datasource.url=${DATABASE_URL}
spring.datasource.password=${DATABASE_PASSWORD}
jwt.secret=${JWT_SECRET}
```

Then set environment variables:
```bash
export DATABASE_URL="jdbc:postgresql://db.xxx.supabase.co:5432/postgres"
export DATABASE_PASSWORD="your-password"
export JWT_SECRET="your-long-random-secret-key"
```

## Running the Application

### Development Mode

```bash
cd backend
mvn clean install
mvn spring-boot:run -Dspring-boot.run.profiles=supabase
```

### Production Build

```bash
cd backend
mvn clean package -DskipTests
java -jar target/srmiggy-backend-1.0.0.jar --spring.profiles.active=supabase
```

### Verify Connection

Check application logs for successful connection:
```
INFO  HikariDataSource - SRMiggyHikariCP - Starting...
INFO  HikariDataSource - SRMiggyHikariCP - Start completed.
```

## Data Migration

### From H2 to Supabase (Optional)

If you have existing data in H2 and want to migrate:

#### Step 1: Export Data from H2

Create a data dump script in your application or use H2 Console:

```sql
-- Example export for users
SELECT * FROM users;
```

#### Step 2: Transform IDs to UUIDs

You'll need to generate UUIDs for existing records. Here's an example script:

```sql
-- Insert sample data with UUIDs
INSERT INTO users (id, username, email, password, role, enabled, wallet_balance, loyalty_points)
VALUES 
  (gen_random_uuid(), 'customer', 'customer@example.com', '$2a$10$...', 'CUSTOMER', true, 100.0, 50.0),
  (gen_random_uuid(), 'vendor1', 'vendor@example.com', '$2a$10$...', 'VENDOR', true, 0.0, 0.0),
  (gen_random_uuid(), 'admin', 'admin@example.com', '$2a$10$...', 'ADMIN', true, 0.0, 0.0);
```

### Seed Data Script

Create initial test data in Supabase SQL Editor:

```sql
-- Insert test users
INSERT INTO users (username, email, password, full_name, phone, role, enabled, wallet_balance, loyalty_points)
VALUES 
  ('customer', 'customer@test.com', '$2a$10$rqzJWZKJ3Cr.Yj6E1uHYme4xQFrHx4qJkFqBTHlCU8Yj9RvQPmU7O', 'Test Customer', '9876543210', 'CUSTOMER', true, 1000.0, 100.0),
  ('admin', 'admin@test.com', '$2a$10$rqzJWZKJ3Cr.Yj6E1uHYme4xQFrHx4qJkFqBTHlCU8Yj9RvQPmU7O', 'Admin User', '9876543211', 'ADMIN', true, 0.0, 0.0);

-- Note: Password for both users is 'password' (BCrypt encoded)
```

## API Changes

### UUID Format

All IDs are now UUIDs (e.g., `550e8400-e29b-41d4-a716-446655440000`).

**Before (with Long IDs):**
```json
{
  "userId": 1,
  "vendorId": 5
}
```

**After (with UUID IDs):**
```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "vendorId": "7c9e6679-7425-40de-944b-e07fc1f90ae7"
}
```

### Frontend Changes Required

Update your frontend API calls to handle UUID strings:

**Before:**
```javascript
const response = await fetch(`/api/vendors/1`);
```

**After:**
```javascript
const vendorId = "7c9e6679-7425-40de-944b-e07fc1f90ae7";
const response = await fetch(`/api/vendors/${vendorId}`);
```

### API Endpoint Examples

All existing endpoints work the same way, just with UUID parameters:

```bash
# Get vendor by UUID
GET /api/vendors/7c9e6679-7425-40de-944b-e07fc1f90ae7

# Get menu items for vendor
GET /api/menu/vendor/7c9e6679-7425-40de-944b-e07fc1f90ae7

# Create order with UUIDs
POST /api/orders
{
  "vendorId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
  "slotId": "a1b2c3d4-e5f6-4a5b-8c9d-0e1f2a3b4c5d",
  "items": [
    {
      "menuItemId": "f1e2d3c4-b5a6-4958-8776-5e4d3c2b1a09",
      "quantity": 2
    }
  ]
}
```

## Testing

### 1. Test Database Connection

```bash
curl -X GET http://localhost:8080/api/vendors
```

Expected: Empty array `[]` or list of vendors

### 2. Test User Registration

```bash
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
```

Expected: JWT token and user details with UUID

### 3. Test Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

### 4. Verify Data in Supabase

Go to Supabase Dashboard → Table Editor → users

You should see the newly created user with a UUID.

## Production Deployment

### Security Checklist

- [ ] Change default JWT secret
- [ ] Use environment variables for all sensitive data
- [ ] Enable SSL/TLS for database connections (Supabase provides this by default)
- [ ] Configure proper CORS origins
- [ ] Review and adjust RLS policies based on your needs
- [ ] Set up database backups in Supabase
- [ ] Monitor connection pool metrics
- [ ] Set appropriate HikariCP pool sizes based on load

### Recommended Settings for Production

Update `application-supabase.properties`:

```properties
# Production HikariCP settings
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=10

# Disable SQL logging
spring.jpa.show-sql=false
logging.level.org.hibernate.SQL=WARN

# Use validate mode (never update/create in production)
spring.jpa.hibernate.ddl-auto=validate
```

### Environment Variables for Production

```bash
DATABASE_URL=jdbc:postgresql://db.xxx.supabase.co:5432/postgres
DATABASE_PASSWORD=your-production-password
JWT_SECRET=your-very-long-and-secure-random-secret-key-min-256-bits
CORS_ORIGINS=https://yourdomain.com,https://www.yourdomain.com
SPRING_PROFILES_ACTIVE=supabase
```

### Monitoring

Supabase provides built-in monitoring:
- Go to Dashboard → Database → Usage
- Monitor:
  - Active connections
  - Query performance
  - Database size
  - Slow queries

## Troubleshooting

### Connection Refused

**Problem:** `Connection refused` or `Could not connect to database`

**Solution:**
1. Check if your IP is whitelisted in Supabase (Settings → Database → Connection Pooling)
2. Verify your connection string is correct
3. Ensure port 5432 is not blocked by firewall

### UUID Parse Error

**Problem:** `Invalid UUID string`

**Solution:**
- Ensure your frontend is sending UUID strings, not numbers
- UUIDs must be in format: `xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx`

### Foreign Key Violation

**Problem:** `Foreign key constraint violation`

**Solution:**
- Ensure referenced entities exist before creating relationships
- Check that UUIDs are valid and exist in parent tables

### RLS Policy Blocking Access

**Problem:** `new row violates row-level security policy`

**Solution:**
- The backend uses the service role, so RLS shouldn't block it
- Verify you're using correct connection credentials
- Check if you accidentally enabled RLS on tables that shouldn't have it

## Support and Resources

- **Supabase Documentation**: https://supabase.com/docs
- **Spring Data JPA**: https://spring.io/projects/spring-data-jpa
- **HikariCP**: https://github.com/brettwooldridge/HikariCP

## Next Steps

1. ✅ Database schema created
2. ✅ Application configured
3. ✅ Connection tested
4. ⏳ Create seed data for testing
5. ⏳ Update frontend to use UUIDs
6. ⏳ Deploy to production

---

**Congratulations!** Your SRMiggy application is now connected to Supabase PostgreSQL with a production-grade setup.
