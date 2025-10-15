# Example Service and Controller Methods

This document provides example implementations for common operations using the Supabase-connected Spring Boot application.

## Table of Contents

1. [User Management](#user-management)
2. [Restaurant/Vendor Management](#restaurantvendor-management)
3. [Menu Item Management](#menu-item-management)
4. [Order Management](#order-management)
5. [Payment Processing](#payment-processing)
6. [Wallet Operations](#wallet-operations)

---

## User Management

### Service Layer Example

```java
package com.srmiggy.service;

import com.srmiggy.dto.RegisterRequest;
import com.srmiggy.dto.AuthResponse;
import com.srmiggy.model.User;
import com.srmiggy.model.UserRole;
import com.srmiggy.repository.UserRepository;
import com.srmiggy.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserManagementService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Register a new user
     */
    @Transactional
    public AuthResponse registerUser(RegisterRequest request) {
        // Validate unique username and email
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Create new user with UUID
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setRole(request.getRole() != null ? request.getRole() : UserRole.CUSTOMER);
        user.setEnabled(true);
        user.setWalletBalance(0.0);
        user.setLoyaltyPoints(0.0);

        User savedUser = userRepository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(savedUser.getUsername());

        return new AuthResponse(
            token,
            savedUser.getUsername(),
            savedUser.getEmail(),
            savedUser.getRole().name(),
            savedUser.getId(),
            savedUser.getWalletBalance()
        );
    }

    /**
     * Get user by ID
     */
    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    /**
     * Get user by username
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    /**
     * Update user profile
     */
    @Transactional
    public User updateUserProfile(UUID userId, String fullName, String phone, String address) {
        User user = getUserById(userId);
        
        if (fullName != null) user.setFullName(fullName);
        if (phone != null) user.setPhone(phone);
        if (address != null) user.setAddress(address);
        
        return userRepository.save(user);
    }

    /**
     * Get all customers
     */
    public List<User> getAllCustomers() {
        return userRepository.findByRole(UserRole.CUSTOMER);
    }
}
```

### Controller Example

```java
package com.srmiggy.controller;

import com.srmiggy.model.User;
import com.srmiggy.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserManagementController {

    @Autowired
    private UserManagementService userManagementService;

    /**
     * Get current user profile
     */
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Authentication authentication) {
        User user = userManagementService.getUserByUsername(authentication.getName());
        return ResponseEntity.ok(user);
    }

    /**
     * Get user by ID (admin only)
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable UUID userId) {
        User user = userManagementService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    /**
     * Update current user profile
     */
    @PutMapping("/me")
    public ResponseEntity<User> updateProfile(
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String address,
            Authentication authentication) {
        User currentUser = userManagementService.getUserByUsername(authentication.getName());
        User updated = userManagementService.updateUserProfile(
            currentUser.getId(), fullName, phone, address
        );
        return ResponseEntity.ok(updated);
    }
}
```

---

## Restaurant/Vendor Management

### Service Example

```java
package com.srmiggy.service;

import com.srmiggy.model.User;
import com.srmiggy.model.Vendor;
import com.srmiggy.repository.UserRepository;
import com.srmiggy.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class VendorManagementService {

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Create a new vendor/restaurant
     */
    @Transactional
    public Vendor createVendor(String name, String description, String imageUrl, String ownerUsername) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new RuntimeException("Owner user not found"));

        Vendor vendor = new Vendor();
        vendor.setName(name);
        vendor.setDescription(description);
        vendor.setImageUrl(imageUrl);
        vendor.setActive(true);
        vendor.setRating(0.0);
        vendor.setOwner(owner);

        return vendorRepository.save(vendor);
    }

    /**
     * Get all active vendors
     */
    public List<Vendor> getAllActiveVendors() {
        return vendorRepository.findByActiveTrue();
    }

    /**
     * Get vendor by ID
     */
    public Vendor getVendorById(UUID vendorId) {
        return vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found: " + vendorId));
    }

    /**
     * Update vendor details
     */
    @Transactional
    public Vendor updateVendor(UUID vendorId, String name, String description, String imageUrl) {
        Vendor vendor = getVendorById(vendorId);
        
        if (name != null) vendor.setName(name);
        if (description != null) vendor.setDescription(description);
        if (imageUrl != null) vendor.setImageUrl(imageUrl);
        
        return vendorRepository.save(vendor);
    }

    /**
     * Activate/deactivate vendor
     */
    @Transactional
    public Vendor toggleVendorStatus(UUID vendorId, boolean active) {
        Vendor vendor = getVendorById(vendorId);
        vendor.setActive(active);
        return vendorRepository.save(vendor);
    }

    /**
     * Update vendor rating
     */
    @Transactional
    public Vendor updateVendorRating(UUID vendorId, double rating) {
        Vendor vendor = getVendorById(vendorId);
        vendor.setRating(rating);
        return vendorRepository.save(vendor);
    }
}
```

### Controller Example

```java
package com.srmiggy.controller;

import com.srmiggy.model.Vendor;
import com.srmiggy.service.VendorManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vendor-management")
@CrossOrigin
public class VendorManagementController {

    @Autowired
    private VendorManagementService vendorManagementService;

    /**
     * Create new vendor (admin or vendor user)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDOR')")
    public ResponseEntity<Vendor> createVendor(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String imageUrl,
            org.springframework.security.core.Authentication authentication) {
        Vendor vendor = vendorManagementService.createVendor(
            name, description, imageUrl, authentication.getName()
        );
        return ResponseEntity.ok(vendor);
    }

    /**
     * Update vendor (owner or admin)
     */
    @PutMapping("/{vendorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDOR')")
    public ResponseEntity<Vendor> updateVendor(
            @PathVariable UUID vendorId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String imageUrl) {
        Vendor vendor = vendorManagementService.updateVendor(vendorId, name, description, imageUrl);
        return ResponseEntity.ok(vendor);
    }

    /**
     * Toggle vendor active status (admin only)
     */
    @PutMapping("/{vendorId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Vendor> toggleVendorStatus(
            @PathVariable UUID vendorId,
            @RequestParam boolean active) {
        Vendor vendor = vendorManagementService.toggleVendorStatus(vendorId, active);
        return ResponseEntity.ok(vendor);
    }
}
```

---

## Menu Item Management

### Service Example

```java
package com.srmiggy.service;

import com.srmiggy.model.MenuItem;
import com.srmiggy.model.Vendor;
import com.srmiggy.repository.MenuItemRepository;
import com.srmiggy.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MenuItemManagementService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private VendorRepository vendorRepository;

    /**
     * Add new menu item to vendor
     */
    @Transactional
    public MenuItem addMenuItem(UUID vendorId, String name, String description, 
                                Double price, String imageUrl, String category, Boolean isVeg) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        MenuItem menuItem = new MenuItem();
        menuItem.setVendor(vendor);
        menuItem.setName(name);
        menuItem.setDescription(description);
        menuItem.setPrice(price);
        menuItem.setImageUrl(imageUrl);
        menuItem.setCategory(category);
        menuItem.setIsVeg(isVeg != null ? isVeg : true);
        menuItem.setAvailable(true);

        return menuItemRepository.save(menuItem);
    }

    /**
     * Get all menu items for a vendor
     */
    public List<MenuItem> getVendorMenuItems(UUID vendorId, boolean availableOnly) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        
        if (availableOnly) {
            return menuItemRepository.findByVendorAndAvailableTrue(vendor);
        }
        return menuItemRepository.findByVendor(vendor);
    }

    /**
     * Update menu item
     */
    @Transactional
    public MenuItem updateMenuItem(UUID menuItemId, String name, String description, 
                                  Double price, String imageUrl, String category, Boolean isVeg) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));

        if (name != null) menuItem.setName(name);
        if (description != null) menuItem.setDescription(description);
        if (price != null) menuItem.setPrice(price);
        if (imageUrl != null) menuItem.setImageUrl(imageUrl);
        if (category != null) menuItem.setCategory(category);
        if (isVeg != null) menuItem.setIsVeg(isVeg);

        return menuItemRepository.save(menuItem);
    }

    /**
     * Toggle menu item availability
     */
    @Transactional
    public MenuItem toggleMenuItemAvailability(UUID menuItemId, boolean available) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));
        
        menuItem.setAvailable(available);
        return menuItemRepository.save(menuItem);
    }

    /**
     * Delete menu item
     */
    @Transactional
    public void deleteMenuItem(UUID menuItemId) {
        if (!menuItemRepository.existsById(menuItemId)) {
            throw new RuntimeException("Menu item not found");
        }
        menuItemRepository.deleteById(menuItemId);
    }
}
```

### Controller Example

```java
package com.srmiggy.controller;

import com.srmiggy.model.MenuItem;
import com.srmiggy.service.MenuItemManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/menu-management")
@CrossOrigin
public class MenuItemManagementController {

    @Autowired
    private MenuItemManagementService menuItemManagementService;

    /**
     * Add menu item (vendor or admin)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDOR')")
    public ResponseEntity<MenuItem> addMenuItem(
            @RequestParam UUID vendorId,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam Double price,
            @RequestParam(required = false) String imageUrl,
            @RequestParam String category,
            @RequestParam(required = false, defaultValue = "true") Boolean isVeg) {
        
        MenuItem menuItem = menuItemManagementService.addMenuItem(
            vendorId, name, description, price, imageUrl, category, isVeg
        );
        return ResponseEntity.ok(menuItem);
    }

    /**
     * Get all menu items for vendor
     */
    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<MenuItem>> getVendorMenuItems(
            @PathVariable UUID vendorId,
            @RequestParam(required = false, defaultValue = "true") boolean availableOnly) {
        
        List<MenuItem> items = menuItemManagementService.getVendorMenuItems(vendorId, availableOnly);
        return ResponseEntity.ok(items);
    }

    /**
     * Update menu item
     */
    @PutMapping("/{menuItemId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDOR')")
    public ResponseEntity<MenuItem> updateMenuItem(
            @PathVariable UUID menuItemId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Double price,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean isVeg) {
        
        MenuItem menuItem = menuItemManagementService.updateMenuItem(
            menuItemId, name, description, price, imageUrl, category, isVeg
        );
        return ResponseEntity.ok(menuItem);
    }

    /**
     * Toggle availability
     */
    @PutMapping("/{menuItemId}/availability")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDOR')")
    public ResponseEntity<MenuItem> toggleAvailability(
            @PathVariable UUID menuItemId,
            @RequestParam boolean available) {
        
        MenuItem menuItem = menuItemManagementService.toggleMenuItemAvailability(menuItemId, available);
        return ResponseEntity.ok(menuItem);
    }

    /**
     * Delete menu item
     */
    @DeleteMapping("/{menuItemId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDOR')")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable UUID menuItemId) {
        menuItemManagementService.deleteMenuItem(menuItemId);
        return ResponseEntity.noContent().build();
    }
}
```

---

## Order Management

The existing `OrderService` already handles order operations. Here's how to use it:

### Fetch User Orders

```java
@GetMapping("/my-orders")
public ResponseEntity<List<Order>> getMyOrders(Authentication authentication) {
    String username = authentication.getName();
    List<Order> orders = orderService.getCustomerOrders(username);
    return ResponseEntity.ok(orders);
}
```

### Fetch Orders by Vendor

```java
@GetMapping("/vendor/{vendorId}/orders")
@PreAuthorize("hasAnyRole('ADMIN', 'VENDOR')")
public ResponseEntity<List<Order>> getVendorOrders(@PathVariable UUID vendorId) {
    List<Order> orders = orderService.getVendorOrders(vendorId);
    return ResponseEntity.ok(orders);
}
```

### Update Order Status

```java
@PutMapping("/orders/{orderId}/status")
@PreAuthorize("hasAnyRole('ADMIN', 'VENDOR')")
public ResponseEntity<Order> updateOrderStatus(
        @PathVariable UUID orderId,
        @RequestParam OrderStatus status) {
    Order order = orderService.updateOrderStatus(orderId, status);
    return ResponseEntity.ok(order);
}
```

---

## Payment Processing

The existing `PaymentService` handles payments. Usage examples:

### Create Payment Order

```java
@PostMapping("/payments/create")
public ResponseEntity<PaymentOrderResponse> createPayment(@RequestParam UUID orderId) {
    PaymentOrderResponse response = paymentService.createPaymentOrder(orderId);
    return ResponseEntity.ok(response);
}
```

### Pay with Wallet

```java
@PostMapping("/payments/wallet")
public ResponseEntity<PaymentTransaction> payWithWallet(
        @RequestParam UUID orderId,
        Authentication authentication) {
    String username = authentication.getName();
    PaymentTransaction transaction = paymentService.payWithWallet(orderId, username);
    return ResponseEntity.ok(transaction);
}
```

---

## Wallet Operations

The existing `WalletService` handles wallet operations:

### Add Money to Wallet

```java
@PostMapping("/wallet/add-money")
public ResponseEntity<WalletTransaction> addMoney(
        @RequestParam Double amount,
        Authentication authentication) {
    String username = authentication.getName();
    WalletTransaction transaction = walletService.addMoney(username, amount);
    return ResponseEntity.ok(transaction);
}
```

### Get Wallet Balance

```java
@GetMapping("/wallet/balance")
public ResponseEntity<Double> getBalance(Authentication authentication) {
    String username = authentication.getName();
    Double balance = walletService.getBalance(username);
    return ResponseEntity.ok(balance);
}
```

---

## Testing with cURL

### Register User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "email": "newuser@example.com",
    "password": "password123",
    "fullName": "New User",
    "phone": "1234567890",
    "role": "CUSTOMER"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "password123"
  }'
```

### Create Order (with JWT token)
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -d '{
    "vendorId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
    "slotId": "a1b2c3d4-e5f6-4a5b-8c9d-0e1f2a3b4c5d",
    "deliveryAddress": "123 Main St",
    "customerPhone": "9876543210",
    "items": [
      {
        "menuItemId": "f1e2d3c4-b5a6-4958-8776-5e4d3c2b1a09",
        "quantity": 2
      }
    ],
    "useLoyaltyPoints": false
  }'
```

---

## Summary

All repositories now use **UUID** as primary keys. The examples above demonstrate:

1. ✅ User registration and authentication
2. ✅ Vendor/Restaurant CRUD operations
3. ✅ Menu item management
4. ✅ Order creation and tracking
5. ✅ Payment processing
6. ✅ Wallet operations

All existing services continue to work with the new UUID-based schema. Simply ensure your frontend sends UUID strings instead of integer IDs.
