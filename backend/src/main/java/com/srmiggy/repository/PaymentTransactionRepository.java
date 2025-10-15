package com.srmiggy.repository;

import com.srmiggy.model.PaymentTransaction;
import com.srmiggy.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, UUID> {
    Optional<PaymentTransaction> findByOrder(Order order);
    Optional<PaymentTransaction> findByProviderOrderId(String providerOrderId);
}
