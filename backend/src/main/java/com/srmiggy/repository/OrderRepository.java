package com.srmiggy.repository;

import com.srmiggy.model.Order;
import com.srmiggy.model.OrderStatus;
import com.srmiggy.model.User;
import com.srmiggy.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByCustomer(User customer);
    List<Order> findByVendor(Vendor vendor);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByCustomerOrderByCreatedAtDesc(User customer);
}
