package com.srmiggy.service;

import com.srmiggy.dto.CartItemRequest;
import com.srmiggy.dto.CreateOrderRequest;
import com.srmiggy.model.*;
import com.srmiggy.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private DeliverySlotRepository deliverySlotRepository;

    @Autowired
    private SettingsRepository settingsRepository;

    private static final Double MINIMUM_ORDER_VALUE = 100.0;
    private static final Double PLATFORM_FEE = 2.0;
    private static final Integer CUTOFF_MINUTES = 50;

    @Transactional
    public Order createOrder(CreateOrderRequest request, String username) {
        User customer = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Vendor vendor = vendorRepository.findById(request.getVendorId())
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        DeliverySlot slot = deliverySlotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new RuntimeException("Delivery slot not found"));

        // Validate slot timing
        LocalTime now = LocalTime.now();
        LocalTime cutoffTime = slot.getEndTime().minusMinutes(CUTOFF_MINUTES);
        if (now.isAfter(cutoffTime)) {
            throw new RuntimeException("Order cannot be placed. Slot closes in less than 50 minutes.");
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setVendor(vendor);
        order.setDeliverySlot(slot);
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setCustomerPhone(request.getCustomerPhone());
        order.setStatus(OrderStatus.PENDING);

        double subtotal = 0.0;
        for (CartItemRequest itemRequest : request.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("Menu item not found"));

            if (!menuItem.getAvailable()) {
                throw new RuntimeException("Menu item " + menuItem.getName() + " is not available");
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(menuItem.getPrice());
            orderItem.setSubtotal(menuItem.getPrice() * itemRequest.getQuantity());

            order.getItems().add(orderItem);
            subtotal += orderItem.getSubtotal();
        }

        if (subtotal < MINIMUM_ORDER_VALUE) {
            throw new RuntimeException("Minimum order value is ₹" + MINIMUM_ORDER_VALUE);
        }

        order.setSubtotal(subtotal);
        order.setPlatformFee(PLATFORM_FEE);
        
        double total = subtotal + PLATFORM_FEE;
        double pointsUsed = 0.0;
        
        // Handle loyalty points redemption
        if (request.getUsePoints() != null && request.getUsePoints() && customer.getLoyaltyPoints() > 0) {
            double availablePoints = customer.getLoyaltyPoints();
            // 1 point = ₹1 discount
            double maxDiscount = Math.min(availablePoints, total);
            pointsUsed = maxDiscount;
            total = total - maxDiscount;
            
            // Update customer's loyalty points
            customer.setLoyaltyPoints(customer.getLoyaltyPoints() - pointsUsed);
            userRepository.save(customer);
            
            order.setPointsUsed(pointsUsed);
        }
        
        order.setTotal(total);
        
        // Calculate points earned: 0.5 points for every ₹100 spent
        // Points are earned on subtotal (before fees and discounts)
        double pointsEarned = (subtotal / 100.0) * 0.5;
        order.setPointsEarned(pointsEarned);
        
        // Add earned points to customer's balance
        customer.setLoyaltyPoints(customer.getLoyaltyPoints() + pointsEarned);
        userRepository.save(customer);

        return orderRepository.save(order);
    }

    public List<Order> getCustomerOrders(String username) {
        User customer = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return orderRepository.findByCustomerOrderByCreatedAtDesc(customer);
    }

    public List<Order> getVendorOrders(Long vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        return orderRepository.findByVendor(vendor);
    }

    public Order getOrderById(Long orderId, String username) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user has access to this order
        if (!order.getCustomer().getId().equals(user.getId()) && 
            user.getRole() != UserRole.ADMIN &&
            (user.getRole() != UserRole.VENDOR || !order.getVendor().getOwner().getId().equals(user.getId()))) {
            throw new RuntimeException("Access denied");
        }

        return order;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        return orderRepository.save(order);
    }
}
