# Supabase Data Migration Guide

## Overview
This guide explains how to migrate the hardcoded restaurant and menu data from the SRMiggy application to Supabase PostgreSQL database.

## What Was Changed

### 1. Data Extraction
All hardcoded restaurant and menu data was extracted from `DataInitializer.java`:
- **8 Vendors**: Biryani House, Dosa Corner, Burger Junction, Pizza Paradise, Thali Express, Roll Junction, Ice Cream Parlor, Dessert House
- **128 Menu Items**: Complete menu for all vendors with prices, images, categories, and veg/non-veg indicators

### 2. SQL Migration File Generated
A comprehensive SQL file was created: `supabase_migration.sql`
- Contains INSERT statements for all vendors (8 total)
- Contains INSERT statements for all menu items (128 total)
- Uses proper UUIDs for all IDs
- Maintains foreign key relationships between vendors and menu items

### 3. DataInitializer Modified
The `DataInitializer.java` class was updated to:
- **Remove** all hardcoded vendor and menu item creation
- **Keep** essential initialization (users, delivery slots, settings)
- **Add** a note directing users to run the SQL migration

### 4. Database Configuration
- **Local Development**: Uses H2 in-memory database for testing
- **Production**: Ready to use Supabase PostgreSQL (configuration in `application.properties`)

## How to Use

### For Local Development (H2 Database)
The application is configured to automatically load data from `data.sql` on startup:
```bash
cd backend
mvn spring-boot:run
```

The data will be loaded automatically thanks to these properties:
```properties
spring.jpa.defer-datasource-initialization=true
spring.sql.init.mode=always
```

### For Supabase PostgreSQL Production

#### Step 1: Update Configuration
Edit `backend/src/main/resources/application.properties`:
```properties
# Comment out H2 configuration
# spring.datasource.url=jdbc:h2:mem:testdb
# spring.datasource.driver-class-name=org.h2.Driver
# spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# Uncomment Supabase configuration
spring.datasource.url=jdbc:postgresql://db.awbmgncjszicqegrpsxj.supabase.co:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=Beluga91!
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

#### Step 2: Run SQL Migration on Supabase
1. Log in to your Supabase dashboard
2. Go to SQL Editor
3. Copy the contents of `supabase_migration.sql`
4. Execute the SQL script

Alternatively, use the Supabase CLI:
```bash
supabase db execute -f supabase_migration.sql
```

#### Step 3: Start the Application
```bash
cd backend
mvn spring-boot:run
```

## API Endpoints

### Get All Vendors
```bash
GET /api/vendors
```

Response:
```json
[
  {
    "id": "a66df724-8552-464a-9b29-d9856dcae72f",
    "name": "Biryani House",
    "description": "Authentic Hyderabadi Biryani",
    "imageUrl": "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=400",
    "active": true,
    "rating": 4.5
  },
  ...
]
```

### Get Menu for a Vendor
```bash
GET /api/menu/vendor/{vendorId}
```

Example:
```bash
curl http://localhost:8080/api/menu/vendor/a66df724-8552-464a-9b29-d9856dcae72f
```

Response:
```json
[
  {
    "id": "e2838fc8-8164-40ac-8ee6-890b052948c2",
    "name": "Chicken Biryani",
    "description": "Delicious chicken biryani",
    "price": 180.0,
    "imageUrl": "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=200",
    "available": true,
    "category": "Main Course",
    "isVeg": false
  },
  ...
]
```

## Frontend Integration

The frontend is already configured to fetch data from these APIs. No changes are needed to the frontend code.

**Home.jsx** fetches vendors:
```javascript
const response = await vendorAPI.getAll();
setVendors(response.data);
```

**VendorMenu.jsx** fetches menu items:
```javascript
const menuRes = await menuAPI.getByVendor(id);
setMenuItems(menuRes.data);
```

## Verification

### Test Backend APIs
```bash
# Test vendors endpoint
curl http://localhost:8080/api/vendors

# Test menu endpoint
curl http://localhost:8080/api/menu/vendor/a66df724-8552-464a-9b29-d9856dcae72f
```

### Test Full Application
1. Start backend: `cd backend && mvn spring-boot:run`
2. Start frontend: `cd frontend && npm run dev`
3. Open browser: `http://localhost:5173`
4. Verify:
   - Home page shows 8 vendors
   - Clicking a vendor shows menu items
   - Menu items have correct prices, images, and veg/non-veg indicators
   - Add to cart functionality works
   - Checkout process works

## Database Schema

### Vendors Table
```sql
CREATE TABLE vendors (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    image_url VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    rating DECIMAL(3,2) DEFAULT 0.0,
    user_id UUID REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Menu Items Table
```sql
CREATE TABLE menu_items (
    id UUID PRIMARY KEY,
    vendor_id UUID NOT NULL REFERENCES vendors(id),
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    price DECIMAL(10,2) NOT NULL,
    image_url VARCHAR(255),
    available BOOLEAN NOT NULL DEFAULT true,
    category VARCHAR(255),
    is_veg BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Troubleshooting

### Issue: Empty vendor list
**Solution**: Ensure the SQL migration has been run successfully in Supabase

### Issue: Connection timeout to Supabase
**Solutions**:
- Check if Supabase project is active
- Verify database credentials in `application.properties`
- Check if IP address is whitelisted in Supabase settings
- For testing, use H2 database locally

### Issue: Foreign key constraint errors
**Solution**: Ensure vendors are inserted before menu items in the SQL script

## Data Statistics
- **Total Vendors**: 8
- **Total Menu Items**: 128
- **Average Items per Vendor**: 16
- **Categories**: Main Course, Sides, Dessert, Beverage, Starter, Snacks
- **Veg Items**: ~75
- **Non-Veg Items**: ~53

## Next Steps
1. Add more vendors and menu items as needed
2. Implement vendor management UI for admins
3. Add image upload functionality for vendors
4. Implement menu item availability toggle
5. Add seasonal/featured items functionality
