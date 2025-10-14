# SRMiggy 2.0 - Campus Food Delivery Platform

SRMiggy is a full-stack food delivery website designed for SRM students to order food online from Java Canteen vendors and get it delivered to their doorstep.

## 🚀 Features

- **User Authentication**: JWT-based secure authentication with role-based access (Customer, Vendor, Admin, Rider)
- **Vendor Management**: Browse multiple vendors and their menus
- **Shopping Cart**: Add items to cart with real-time updates
- **Delivery Slots**: Fixed time slots for efficient delivery (7:00-7:30 PM, 7:30-8:00 PM, 8:00-8:30 PM)
- **Order Management**: Place orders with minimum value validation (₹100 + ₹2 platform fee)
- **Payment Integration**: Mock payment provider for testing
- **Admin Dashboard**: View statistics, manage orders and vendors
- **Mobile-First Design**: Responsive UI built with Tailwind CSS

## 📋 Tech Stack

### Backend
- Java 17
- Spring Boot 3.2.0
- Spring Security (JWT)
- Spring Data JPA
- H2 Database (development)
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

- **5 Vendors**: Biryani House, Dosa Corner, Burger Junction, Pizza Paradise, Thali Express
- **30 Menu Items**: 6 items per vendor with images and descriptions
- **3 Delivery Slots**: Evening time slots
- **3 Users**: Customer, Admin, and Vendor accounts

## 🔄 Complete User Flow

1. **Browse Vendors**: Visit home page to see all available vendors
2. **View Menu**: Click on a vendor to view their menu items
3. **Add to Cart**: Select items and add them to your cart
4. **Checkout**: Proceed to checkout, select delivery slot and address
5. **Payment**: Complete mock payment (automatic)
6. **Order Confirmation**: Receive order confirmation with order ID
7. **Track Orders**: View order history and status in "My Orders"

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
- `GET /api/payments/order/{orderId}` - Get payment status

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

## 🐛 Troubleshooting

### Backend Issues
- **Port 8080 already in use**: Change port in `application.properties`
- **Database errors**: H2 is in-memory, restart will reset data
- **JWT errors**: Check secret key in `application.properties`

### Frontend Issues
- **API connection errors**: Ensure backend is running on port 8080
- **CORS errors**: Check CORS configuration in SecurityConfig
- **Build errors**: Delete `node_modules` and run `npm install` again
- **M4 Mac compatibility**: This project uses standard Vite (v6.x) which has native Apple Silicon support. If you encounter issues, ensure you're using Node.js 16+ and run `npm install` to get the correct dependencies

## 📝 Notes

- This is a development setup with H2 in-memory database
- For production, configure PostgreSQL in `application.properties`
- Replace mock payment with actual provider (Razorpay/Stripe)
- Update JWT secret key for production use
- Add proper error handling and validation

## 👥 Contributors

Built as a campus food delivery solution for SRM University students.

## 📄 License

This project is for educational purposes.
