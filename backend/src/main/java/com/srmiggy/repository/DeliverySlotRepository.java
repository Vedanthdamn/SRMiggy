package com.srmiggy.repository;

import com.srmiggy.model.DeliverySlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeliverySlotRepository extends JpaRepository<DeliverySlot, UUID> {
    List<DeliverySlot> findByActiveTrue();
}
