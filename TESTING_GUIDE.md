# Testing Guide for Order Placement Fix

## Prerequisites
1. Backend server running on http://localhost:8080
2. Frontend server running on http://localhost:5173
3. Current time is between 11 AM - 7 PM (server time) for order placement

## Test Accounts
- **Customer**: username: `customer`, password: `password`
- **Admin**: username: `admin`, password: `password`
- **Vendor**: username: `vendor1`, password: `password`

## Test Case 1: Order with Cash on Delivery (COD)

### Steps:
1. Open http://localhost:5173
2. Click "Login" and sign in with customer credentials
3. Browse vendors and click on any vendor (e.g., "Biryani House")
4. Add items to cart (total should be at least ₹50 for meaningful test)
5. Click "View Cart" or cart icon
6. Review cart items and click "Proceed to Checkout"
7. In checkout page:
   - Verify delivery address is pre-filled
   - Select a delivery slot (if available)
   - Select "Cash on Delivery" as payment method
8. Click "Place Order & Pay"
9. **Expected Result**: 
   - Should redirect to `/order-success/{orderId}` page
   - Success message should display: "✅ Order Placed Successfully"
   - Payment message should show: "Payment will be collected on delivery"
   - Live tracking map should load showing delivery rider location
   - Order details should be visible

### Verification:
- Check "My Orders" page - order should appear with status "CONFIRMED"
- Check backend logs - should show COD payment transaction created
- No error page should appear

## Test Case 2: Order with Wallet Payment

### Steps:
1. Login as customer
2. Go to "Wallet" page
3. Add money to wallet (e.g., ₹500)
4. Add items to cart from any vendor
5. Proceed to checkout
6. Select a delivery slot
7. Select "Wallet" as payment method
8. Click "Place Order & Pay"
9. **Expected Result**: 
   - Should redirect to order success page
   - Success message: "✅ Order Placed – Thank You!"
   - Payment message: "Payment has been deducted from your wallet"
   - Live tracking should be visible

### Verification:
- Check wallet balance - should be reduced by order total
- Check "My Orders" - order should be CONFIRMED
- Check "Wallet Transactions" - should show deduction

## Test Case 3: Order with Online Payment (Mock)

### Steps:
1. Login as customer
2. Add items to cart from any vendor
3. Proceed to checkout
4. Select a delivery slot
5. Select "Other Payment Methods" (Credit/Debit Card, UPI, Net Banking)
6. Click "Place Order & Pay"
7. **Expected Result**: 
   - Mock payment is automatically processed
   - Redirect to order success page
   - Success message: "✅ Order Placed Successfully"
   - Payment message: "Payment has been processed successfully"
   - Live tracking map should load

### Verification:
- Order status should be CONFIRMED
- Payment transaction should be created with provider "MOCK"

## Test Case 4: Order During Closed Hours

### Steps:
1. Access the application when server time is before 11 AM or after 7 PM
2. Login and try to place an order
3. Go to checkout
4. **Expected Result**: 
   - Slot selection shows: "⏰ Ordering Closed for Today"
   - Message: "Please come back tomorrow between 11 AM – 7 PM to place your order"
   - "Place Order" button should be disabled

## Test Case 5: Insufficient Wallet Balance

### Steps:
1. Login as customer
2. Check wallet balance (should be less than order total)
3. Add expensive items to cart (total > wallet balance)
4. Proceed to checkout
5. Select wallet as payment method
6. **Expected Result**: 
   - Warning message: "⚠️ Insufficient balance. Please add ₹X more"
   - "Place Order" button should be disabled

## Test Case 6: Loyalty Points Award

### Steps:
1. Login as customer
2. Check current loyalty points in wallet page
3. Place an order with any payment method
4. Complete the order
5. Go to wallet page
6. **Expected Result**: 
   - Loyalty points should increase
   - Points earned = (order subtotal / 100) * 0.5

### Example:
- Order subtotal: ₹200
- Points earned: 1 point (200/100 * 0.5)

## Test Case 7: Error Handling - Invalid Order

### Steps:
1. Try to create order with invalid data (using API directly)
2. **Expected Result**: 
   - Should display error message alert with specific error
   - Should redirect to order failed page
   - Error message should explain what went wrong

## API Testing (Using curl)

### Login and Get Token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "customer", "password": "password"}'
```

### Get Available Slots
```bash
curl -H "Authorization: Bearer YOUR_TOKEN" \
  http://localhost:8080/api/slots
```

### Get Vendors
```bash
curl http://localhost:8080/api/vendors
```

### Create Order
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "vendorId": "VENDOR_UUID",
    "slotId": 1,
    "deliveryAddress": "Test Address",
    "customerPhone": "1234567890",
    "items": [
      {
        "menuItemId": "MENU_ITEM_UUID",
        "quantity": 2
      }
    ],
    "useLoyaltyPoints": false
  }'
```

### Confirm COD Payment
```bash
curl -X POST "http://localhost:8080/api/payments/confirm-cod?orderId=ORDER_UUID" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## Expected Behaviors

### Success Scenarios:
- ✅ Order creation returns order object with ID
- ✅ Payment confirmation returns transaction object
- ✅ Order status changes from PENDING to CONFIRMED
- ✅ Loyalty points are awarded
- ✅ Redirect to success page with order tracking
- ✅ Map shows delivery rider moving to destination

### Error Scenarios:
- ❌ Ordering outside hours: Shows "Ordering Closed" message
- ❌ Insufficient wallet: Shows insufficient balance warning
- ❌ Invalid slot: Shows error and redirects to failed page
- ❌ Network error: Shows error message and redirects to failed page

## Debugging Tips

### Backend Logs
Check `/tmp/backend.log` for detailed error messages:
```bash
tail -f /tmp/backend.log
```

### Frontend Console
Open browser DevTools (F12) and check:
- Console tab for JavaScript errors
- Network tab for API call responses
- Look for 400/500 status codes

### Database
Access H2 console at http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (leave empty)

Check order status:
```sql
SELECT id, status, total FROM orders ORDER BY created_at DESC;
```

Check payment transactions:
```sql
SELECT id, order_id, provider, status, amount FROM payment_transactions ORDER BY created_at DESC;
```

## Known Limitations

1. **Time-based Ordering**: Orders only accepted 11 AM - 7 PM (server time)
2. **Mock Payments**: Online payment uses mock provider for development
3. **In-Memory Database**: H2 database resets on server restart
4. **Simulated Tracking**: Delivery tracking uses simulated movement

## Success Criteria

The fix is successful if:
1. ✅ COD orders complete successfully without errors
2. ✅ All payment methods (Wallet, COD, Online) work correctly
3. ✅ Order success page displays with tracking map
4. ✅ No redirect to error page for valid orders
5. ✅ Loyalty points are awarded correctly
6. ✅ Order status updates to CONFIRMED
7. ✅ Error messages are clear and helpful
