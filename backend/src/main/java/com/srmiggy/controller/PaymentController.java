package com.srmiggy.controller;

import com.srmiggy.dto.PaymentOrderResponse;
import com.srmiggy.dto.PaymentVerifyRequest;
import com.srmiggy.model.PaymentTransaction;
import com.srmiggy.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create-order")
    public ResponseEntity<PaymentOrderResponse> createPaymentOrder(@RequestParam Long orderId) {
        try {
            PaymentOrderResponse response = paymentService.createPaymentOrder(orderId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<PaymentTransaction> verifyPayment(@RequestBody PaymentVerifyRequest request) {
        try {
            PaymentTransaction transaction = paymentService.verifyPayment(request);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentTransaction> getPaymentByOrderId(@PathVariable Long orderId) {
        PaymentTransaction transaction = paymentService.getPaymentByOrderId(orderId);
        if (transaction != null) {
            return ResponseEntity.ok(transaction);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/pay-with-wallet")
    public ResponseEntity<PaymentTransaction> payWithWallet(
            @RequestParam Long orderId,
            org.springframework.security.core.Authentication authentication) {
        try {
            String username = authentication.getName();
            PaymentTransaction transaction = paymentService.payWithWallet(orderId, username);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
