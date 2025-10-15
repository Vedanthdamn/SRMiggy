package com.srmiggy.service;

import com.srmiggy.model.DeliverySlot;
import com.srmiggy.repository.DeliverySlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeliverySlotService {

    @Autowired
    private DeliverySlotRepository deliverySlotRepository;

    private static final LocalTime ORDERING_START_TIME = LocalTime.of(11, 0);
    private static final LocalTime ORDERING_END_TIME = LocalTime.of(19, 0);

    /**
     * Get available slots based on current time
     * Returns slots that start after the current time and before 7:00 PM
     * Returns empty list if current time is after 7:00 PM or before 11:00 AM
     */
    public List<DeliverySlot> getAvailableSlots() {
        LocalTime now = LocalTime.now();
        
        // If current time is after 7:00 PM or before 11:00 AM, no slots are available
        if (now.isAfter(ORDERING_END_TIME) || now.isBefore(ORDERING_START_TIME)) {
            return List.of();
        }
        
        // Get all active slots
        List<DeliverySlot> allActiveSlots = deliverySlotRepository.findByActiveTrue();
        
        // Filter slots that start after current time
        return allActiveSlots.stream()
                .filter(slot -> slot.getStartTime().isAfter(now))
                .collect(Collectors.toList());
    }

    /**
     * Check if ordering is currently allowed
     */
    public boolean isOrderingOpen() {
        LocalTime now = LocalTime.now();
        return !now.isBefore(ORDERING_START_TIME) && !now.isAfter(ORDERING_END_TIME);
    }
}
