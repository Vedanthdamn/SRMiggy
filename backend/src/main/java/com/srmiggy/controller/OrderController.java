package com.srmiggy.controller;

import com.srmiggy.dto.CreateOrderRequest;
import com.srmiggy.model.Order;
import com.srmiggy.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest request, Authentication authentication) {
        try {
            Order order = orderService.createOrder(request, authentication.getName());
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Order>> getMyOrders(Authentication authentication) {
        List<Order> orders = orderService.getCustomerOrders(authentication.getName());
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable UUID id, Authentication authentication) {
        try {
            Order order = orderService.getOrderById(id, authentication.getName());
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
