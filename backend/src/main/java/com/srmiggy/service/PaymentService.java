package com.srmiggy.service;

import com.srmiggy.dto.PaymentOrderResponse;
import com.srmiggy.dto.PaymentVerifyRequest;
import com.srmiggy.model.Order;
import com.srmiggy.model.OrderStatus;
import com.srmiggy.model.PaymentStatus;
import com.srmiggy.model.PaymentTransaction;
import com.srmiggy.repository.OrderRepository;
import com.srmiggy.repository.PaymentTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private WalletService walletService;

    @Autowired
    private LoyaltyService loyaltyService;

    @Transactional
    public PaymentOrderResponse createPaymentOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Order is not in pending state");
        }

        // Create payment transaction
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setOrder(order);
        transaction.setAmount(order.getTotal());
        transaction.setStatus(PaymentStatus.PENDING);
        transaction.setProvider("MOCK");
        
        // Generate mock provider order ID
        String providerOrderId = "MOCK_" + UUID.randomUUID().toString();
        transaction.setProviderOrderId(providerOrderId);

        paymentTransactionRepository.save(transaction);

        return new PaymentOrderResponse(
            providerOrderId,
            order.getTotal(),
            "MOCK",
            order.getId().toString()
        );
    }

    @Transactional
    public PaymentTransaction verifyPayment(PaymentVerifyRequest request) {
        PaymentTransaction transaction = paymentTransactionRepository.findByProviderOrderId(request.getProviderOrderId())
                .orElseThrow(() -> new RuntimeException("Payment transaction not found"));

        // Mock payment verification - in real scenario, verify signature
        // For mock provider, we'll accept any payment
        transaction.setProviderPaymentId(request.getProviderPaymentId());
        transaction.setProviderSignature(request.getProviderSignature());
        transaction.setStatus(PaymentStatus.SUCCESS);

        // Update order status
        Order order = transaction.getOrder();
        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        // Award loyalty points to customer
        if (order.getLoyaltyPointsEarned() > 0) {
            loyaltyService.addLoyaltyPoints(
                order.getCustomer().getUsername(), 
                order.getLoyaltyPointsEarned()
            );
        }

        return paymentTransactionRepository.save(transaction);
    }

    public PaymentTransaction getPaymentByOrderId(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return paymentTransactionRepository.findByOrder(order)
                .orElse(null);
    }

    @Transactional
    public PaymentTransaction payWithWallet(UUID orderId, String username) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Order is not in pending state");
        }

        // Deduct money from wallet
        walletService.deductMoney(username, order.getTotal(), "Payment for Order #" + orderId);

        // Create payment transaction
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setOrder(order);
        transaction.setAmount(order.getTotal());
        transaction.setStatus(PaymentStatus.SUCCESS);
        transaction.setProvider("WALLET");
        transaction.setProviderOrderId("WALLET_" + UUID.randomUUID().toString());
        transaction.setProviderPaymentId("WALLET_PAY_" + orderId);

        // Update order status
        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        // Award loyalty points to customer
        if (order.getLoyaltyPointsEarned() > 0) {
            loyaltyService.addLoyaltyPoints(username, order.getLoyaltyPointsEarned());
        }

        return paymentTransactionRepository.save(transaction);
    }
}
