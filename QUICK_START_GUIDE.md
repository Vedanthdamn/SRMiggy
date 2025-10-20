# Quick Start Guide - Order Placement Fix

## What Was Fixed

### The Problem
Users got "Failed to place order (403)" error when trying to place orders.

### The Solution
Four files were modified with minimal, surgical changes:

## Files Changed

### 1. Backend: CreateOrderRequest.java
**What changed**: Added one field to accept payment method
```java
private String paymentMethod; // "wallet", "card", "upi", "cod", or "mock"
```

### 2. Backend: OrderService.java
**What changed**: Added wallet payment handling during order creation
- Injects WalletService
- For wallet payment: deducts money immediately
- For wallet payment: marks order as CONFIRMED
- For other payments: order stays PENDING until processed

### 3. Frontend: Checkout.jsx
**What changed**: 
- Fixed: `slotId: parseInt(selectedSlot)` → `slotId: selectedSlot`
- Added: `paymentMethod: paymentMethod` to order data
- Simplified: Payment flow (wallet orders done in one API call)

### 4. Frontend: OrderSuccess.jsx
**What changed**:
- Updated: "30 minutes" → "20 minutes" for ETA
- Updated: "30 mins" → "20 mins" for display
- Added: Support for custom success messages

## How It Works Now

### Wallet Payment Flow
```
User clicks "Place Order" with Wallet selected
    ↓
Frontend sends: POST /api/orders with paymentMethod="wallet"
    ↓
Backend OrderService:
  - Creates order
  - Deducts from wallet (atomic transaction)
  - Sets order status to CONFIRMED
  - Awards loyalty points
    ↓
Frontend receives order with status=CONFIRMED
    ↓
Shows success page with:
  - "Order placed successfully!"
  - "Payment deducted from wallet"
  - Map tracking with 20-minute ETA
```

### Card/UPI/COD Flow
```
User clicks "Place Order" with Card/UPI/COD selected
    ↓
Frontend sends: POST /api/orders with paymentMethod="card|upi|cod"
    ↓
Backend OrderService:
  - Creates order
  - Sets status to PENDING (no wallet deduction)
    ↓
Frontend continues with payment processing:
  - For Card/UPI: Mock payment gateway
  - For COD: Direct confirmation
    ↓
Shows success page with:
  - "Order placed successfully!"
  - Appropriate payment message
  - Map tracking with 20-minute ETA
```

## Running the Application

### Backend
```bash
cd backend
mvn spring-boot:run
```
Access: http://localhost:8080

### Frontend
```bash
cd frontend
npm install
npm run dev
```
Access: http://localhost:5173

### Test Credentials
```
Username: customer
Password: password
Wallet Balance: ₹1000 (initial)
```

## Key Features

✅ Order creation with proper authentication
✅ Wallet payment (immediate deduction)
✅ Card/UPI payment (gateway simulation)
✅ Cash on Delivery option
✅ Loyalty points system
✅ Delivery slot selection (11 AM - 7 PM)
✅ Live order tracking simulation
✅ 20-minute ETA display

## Important Notes

1. **Ordering Hours**: 11:00 AM - 7:00 PM only
   - Outside these hours, slots won't be available
   - This is expected behavior

2. **Wallet Balance**: Must have sufficient balance
   - Frontend validates before submission
   - Backend validates during order creation

3. **Slot Cutoff**: Orders must be placed 50 minutes before slot ends
   - Prevents orders that can't be fulfilled

4. **Authentication**: JWT token required for all order APIs
   - Token expires after 24 hours
   - Obtained via /api/auth/login

## Troubleshooting

### "403 Forbidden" Error
✅ **FIXED** - This was caused by incorrect slotId format (integer vs UUID)

### "Insufficient wallet balance"
- Check balance: GET /api/wallet/balance
- Add money: POST /api/wallet/add-money with amount

### "Ordering closed"
- Check current time
- Ordering available only 11 AM - 7 PM

### "Delivery slot not found"
- Verify slot ID is valid UUID string
- Check if slot is within available time window

## Testing Checklist

- [x] Backend compiles successfully
- [x] Frontend builds successfully  
- [x] Authentication works
- [x] Wallet balance API accessible
- [x] Order creation API ready
- [x] Payment flow simplified
- [x] Success messages shown
- [x] ETA updated to 20 minutes

## Support

For issues or questions:
1. Check ORDER_PLACEMENT_FIX_DOCUMENTATION.md for detailed info
2. Review backend logs for error messages
3. Check browser console for frontend errors
4. Verify time is within ordering window (11 AM - 7 PM)

---
**Last Updated**: October 20, 2025
**Status**: ✅ All features implemented and tested
