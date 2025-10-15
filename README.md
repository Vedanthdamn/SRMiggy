# SRMiggy - Campus Food Delivery Platform

SRMiggy is a full-stack food delivery website designed for SRM students to order food online from Java Canteen vendors and get it delivered to their doorstep.

## 🚀 Features

- **User Authentication**: JWT-based secure authentication with role-based access (Customer, Vendor, Admin, Rider)
- **Vendor Management**: Browse multiple vendors and their menus
- **Shopping Cart**: Add items to cart with real-time updates
- **Delivery Slots**: Fixed time slots for efficient delivery (7:00-7:30 PM, 7:30-8:00 PM, 8:00-8:30 PM)
- **Order Management**: Place orders with minimum value validation (₹100 + ₹2 platform fee)
- **Digital Wallet**: Students can add money to their wallet and use it for payments
- **Payment Integration**: Supports both wallet payment and mock payment provider for testing
- **Campus Loyalty Program**: Earn points on every order and redeem them for discounts (see below)
- **Admin Dashboard**: View statistics, manage orders and vendors
- **Mobile-First Design**: Responsive UI built with Tailwind CSS

## 🏫 Campus Loyalty Program

SRMiggy rewards students for every order with our Campus Loyalty Program!

### How It Works:
- **Earn Points**: Get 0.5 loyalty points for every ₹100 spent on food
- **Redeem Points**: Use your points for discounts (1 point = ₹1 off)
- **Automatic Tracking**: Points are automatically added to your account after successful payment
- **Flexible Usage**: Toggle points on/off in the cart to choose when to redeem

### Using Loyalty Points:
1. Add items to your cart and proceed to cart page
2. View your available loyalty points and points you'll earn from this order
3. Toggle "Use Loyalty Points" to apply your points as a discount
4. Your points will be deducted from the order total at checkout
5. After payment, new points from this order are automatically credited to your account

### Example:
- Order total: ₹200
- Available points: 50 pts
- Toggle on to save ₹50
- Final total: ₹150
- After payment, earn 1 point (0.5 × 2) from this ₹200 order

## 📋 Tech Stack

### Backend
- Java 17
- Spring Boot 3.2.0
- Spring Security (JWT)
- Spring Data JPA
- H2 Database (development) / **PostgreSQL via Supabase (production)**
- HikariCP Connection Pooling
- Maven

### Frontend
- React 18
- React Router DOM
- Axios
- Tailwind CSS
- Vite

## 🛠️ Setup Instructions

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Node.js 16+ and npm

### Backend Setup

1. Navigate to the backend directory:
```bash
cd backend
```

2. Run the application:
```bash
mvn spring-boot:run
```

The backend server will start on `http://localhost:8080`

**Database Tables**: Tables are automatically created on startup using Hibernate's `ddl-auto=update` mode. No manual SQL script execution is required. See [DATABASE_CONFIGURATION.md](DATABASE_CONFIGURATION.md) for detailed information.

**H2 Console Access**: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:srmiggydb`
- Username: `sa`
- Password: (leave empty)

### Frontend Setup

1. Navigate to the frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
npm run dev
```

The frontend will start on `http://localhost:5173`

## 🔑 Default Test Accounts

The application comes with pre-seeded data for testing:

### Customer Account
- Username: `customer`
- Password: `password`

### Admin Account
- Username: `admin`
- Password: `password`

### Vendor Account
- Username: `vendor1`
- Password: `password`

## 📊 Seeded Data

- **8 Vendors**: 
  - Biryani House (North Indian)
  - Dosa Corner (South Indian)
  - Burger Junction (American Fast Food)
  - Pizza Paradise (Italian Pizzas)
  - Thali Express (North Indian Thali)
  - Roll Junction (Rolls & Wraps)
  - Ice Cream Parlor (Ice Creams)
  - Dessert House (Desserts)
- **128 Menu Items**: 16 items per vendor with proper veg/non-veg classification and affordable options under ₹100
- **3 Delivery Slots**: Evening time slots
- **3 Users**: Customer, Admin, and Vendor accounts

## 🔄 Complete User Flow

1. **Browse Vendors**: Visit home page to see all available vendors
2. **View Menu**: Click on a vendor to view their menu items
3. **Add to Cart**: Select items and add them to your cart
4. **Checkout**: Proceed to checkout, select delivery slot and address
5. **Payment**: Choose between wallet payment or other payment methods
6. **Order Confirmation**: Receive order confirmation with order ID
7. **Track Orders**: View order history and status in "My Orders"
8. **Manage Wallet**: Add money to wallet and view transaction history

## 🎯 Business Rules

- **Minimum Order**: ₹100
- **Platform Fee**: ₹2 (automatically added)
- **Order Cutoff**: Orders accepted until 50 minutes before slot closing
- **Vendor Limitation**: Cart can only contain items from one vendor at a time
- **Batch Delivery**: Orders grouped by vendor and slot for efficient delivery

## 🔐 API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user

### Vendors
- `GET /api/vendors` - Get all active vendors
- `GET /api/vendors/{id}` - Get vendor details

### Menu
- `GET /api/menu/vendor/{vendorId}` - Get vendor menu

### Orders
- `POST /api/orders` - Create new order
- `GET /api/orders` - Get user's orders
- `GET /api/orders/{id}` - Get order details

### Payments
- `POST /api/payments/create-order` - Create payment order
- `POST /api/payments/verify` - Verify payment
- `POST /api/payments/pay-with-wallet` - Pay with wallet balance
- `GET /api/payments/order/{orderId}` - Get payment status

### Wallet
- `POST /api/wallet/add-money` - Add money to wallet
- `GET /api/wallet/balance` - Get wallet balance
- `GET /api/wallet/transactions` - Get transaction history
- `GET /api/wallet/loyalty-points` - Get current loyalty points
- `GET /api/wallet/calculate-loyalty-points?orderTotal={amount}` - Calculate points for an order

### Admin (Requires ADMIN role)
- `GET /api/admin/orders` - Get all orders
- `GET /api/admin/vendors` - Get all vendors
- `GET /api/admin/stats` - Get platform statistics
- `PUT /api/admin/orders/{id}/status` - Update order status

## 📁 Project Structure

```
SRMiggy2.0/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/srmiggy/
│   │   │   │   ├── config/          # Configuration classes
│   │   │   │   ├── controller/      # REST controllers
│   │   │   │   ├── dto/             # Data transfer objects
│   │   │   │   ├── model/           # Entity models
│   │   │   │   ├── repository/      # JPA repositories
│   │   │   │   ├── security/        # Security & JWT
│   │   │   │   ├── service/         # Business logic
│   │   │   │   └── SrmiggyApplication.java
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   └── pom.xml
│
├── frontend/
│   ├── src/
│   │   ├── components/          # Reusable components
│   │   ├── context/             # React context (Auth, Cart)
│   │   ├── pages/               # Page components
│   │   ├── utils/               # Utility functions & API
│   │   ├── App.jsx
│   │   └── main.jsx
│   ├── package.json
│   └── tailwind.config.js
│
└── README.md
```

## 🧪 Testing the Application

1. Start both backend and frontend servers
2. Open `http://localhost:5173` in your browser
3. Register a new account or login with test credentials
4. Browse vendors and add items to cart
5. Complete the checkout process with mock payment
6. View your order in "My Orders"
7. Login as admin to access the admin dashboard

## 🚀 Deployment Instructions

### Building for Production

#### Backend Deployment

1. Navigate to the backend directory:
```bash
cd backend
```

2. Build the JAR file:
```bash
mvn clean package -DskipTests
```

3. The JAR file will be created at `target/srmiggy-0.0.1-SNAPSHOT.jar`

4. Run the JAR file:
```bash
java -jar target/srmiggy-0.0.1-SNAPSHOT.jar
```

**Production Configuration:**
- Update `application.properties` with production database credentials
- Set a strong JWT secret key
- Configure CORS for your frontend domain
- Enable HTTPS for secure communication

#### Frontend Deployment (with Tailwind CSS)

1. Navigate to the frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Build the production bundle (includes compiled Tailwind CSS):
```bash
npm run build
```

The build output will be in the `dist/` directory with:
- Optimized JavaScript bundles
- Compiled and minified Tailwind CSS
- Static assets and HTML

4. Preview the production build locally:
```bash
npm run preview
```

5. Deploy the `dist/` folder to your hosting service:
   - **Vercel**: Connect your repository and set build command to `npm run build`
   - **Netlify**: Drag and drop `dist/` folder or connect via Git
   - **AWS S3 + CloudFront**: Upload `dist/` contents to S3 bucket
   - **Nginx/Apache**: Copy `dist/` contents to web server root

**Environment Variables for Production:**
Create a `.env.production` file in the frontend directory:
```env
VITE_API_URL=https://your-backend-domain.com/api
```

Update API calls in `src/utils/api.js` to use `import.meta.env.VITE_API_URL`

### Deployment Checklist

**Backend:**
- [ ] Update database configuration from H2 to PostgreSQL/MySQL
- [ ] Set strong JWT secret key
- [ ] Configure CORS for frontend domain
- [ ] Enable HTTPS
- [ ] Set up logging and monitoring
- [ ] Configure production server port
- [ ] Set up backup strategy for database

**Frontend:**
- [ ] Build production bundle with `npm run build`
- [ ] Verify Tailwind CSS styles are properly compiled in `dist/assets/*.css`
- [ ] Update API endpoints to production backend URL
- [ ] Enable HTTPS
- [ ] Configure CDN for static assets (optional)
- [ ] Set up monitoring and error tracking
- [ ] Test responsive design on multiple devices

**Tailwind CSS Verification:**
The production build automatically processes Tailwind CSS through PostCSS, generating optimized styles. To verify:
```bash
# After building, check the compiled CSS includes Tailwind
cat dist/assets/index-*.css | head -5
# Should show: /*! tailwindcss v4.1.14 | MIT License | https://tailwindcss.com */
```

### Recommended Hosting Options

**Backend:**
- Heroku (easy deployment with Git)
- AWS Elastic Beanstalk
- Google Cloud Run
- Railway
- Render

**Frontend:**
- Vercel (recommended for React + Vite)
- Netlify
- AWS Amplify
- Cloudflare Pages
- GitHub Pages (with SPA routing configuration)

**Full-Stack:**
- AWS EC2 with Docker
- DigitalOcean Droplets
- Railway (backend + frontend)
- Render (backend + frontend)

## 🐛 Troubleshooting

### Backend Issues
- **Port 8080 already in use**: Change port in `application.properties`
- **Database errors**: H2 is in-memory, restart will reset data
- **JWT errors**: Check secret key in `application.properties`

### Frontend Issues
- **API connection errors**: Ensure backend is running on port 8080
- **CORS errors**: Check CORS configuration in SecurityConfig
- **Build errors**: Delete `node_modules` and run `npm install` again
- **Tailwind styles not applying**: 
  - Verify `index.css` contains `@tailwind` directives
  - Check `tailwind.config.js` content paths include all component files
  - Rebuild with `npm run build` to regenerate CSS

### Deployment Issues
- **Blank page after deployment**: Check browser console for API endpoint errors
- **Tailwind CSS missing in production**: Verify `postcss.config.js` and `tailwind.config.js` are committed
- **404 on page refresh**: Configure your hosting for SPA routing (redirect all routes to `index.html`)
- **CORS errors in production**: Update backend CORS configuration with production frontend URL

## 📝 Notes

### Development Setup
- This is a development setup with H2 in-memory database
- **Tables are automatically created** on startup using Hibernate's `update` mode
- **No manual SQL scripts needed** - the DataInitializer seeds all data automatically
- See [DATABASE_CONFIGURATION.md](DATABASE_CONFIGURATION.md) for detailed database setup information
- **For Production**: Use Supabase PostgreSQL (see setup guides below)
- Tailwind CSS is configured with PostCSS and works in both dev and production builds
- Frontend uses Vite for fast development and optimized production builds

### Production Deployment with Supabase
- ✅ **Supabase PostgreSQL Integration Available**
- ✅ See [SUPABASE_SETUP.md](SUPABASE_SETUP.md) for complete setup guide
- ✅ See [MIGRATION_GUIDE.md](MIGRATION_GUIDE.md) for migrating from H2 to Supabase
- ✅ See [EXAMPLE_SERVICE_CODE.md](EXAMPLE_SERVICE_CODE.md) for code examples
- Replace mock payment with actual provider (Razorpay/Stripe)
- Update JWT secret key for production use
- Add proper error handling and validation
- Ensure Tailwind CSS is properly compiled in production build (automatically handled by Vite)
- Use environment variables for sensitive configuration
- Enable HTTPS for both frontend and backend
- Set up proper database backups (automatic with Supabase)

### Supabase Integration Features
- ✅ UUID primary keys for better security and scalability
- ✅ Row Level Security (RLS) for data isolation
- ✅ HikariCP connection pooling for performance
- ✅ Automatic timestamp tracking with database triggers
- ✅ Professional PostgreSQL schema with indexes
- ✅ Production-grade database with automatic backups

### Quick Start with Supabase
```bash
# 1. Create Supabase project at https://supabase.com
# 2. Run schema SQL in Supabase SQL Editor (backend/src/main/resources/supabase-schema.sql)
# 3. Run seed data SQL (backend/src/main/resources/supabase-seed-data.sql)
# 4. Update connection details in backend/src/main/resources/application-supabase.properties
# 5. Run backend with Supabase profile
mvn spring-boot:run -Dspring-boot.run.profiles=supabase
```

See detailed guides for complete instructions.

## 👥 Contributors

Built as a campus food delivery solution for SRM University students.

## 📄 License

This project is for educational purposes.
