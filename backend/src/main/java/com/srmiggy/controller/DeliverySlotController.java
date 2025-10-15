package com.srmiggy.controller;

import com.srmiggy.model.DeliverySlot;
import com.srmiggy.service.DeliverySlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/slots")
@CrossOrigin
public class DeliverySlotController {

    @Autowired
    private DeliverySlotService deliverySlotService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getActiveSlots() {
        List<DeliverySlot> availableSlots = deliverySlotService.getAvailableSlots();
        boolean isOrderingOpen = deliverySlotService.isOrderingOpen();
        
        Map<String, Object> response = new HashMap<>();
        response.put("slots", availableSlots);
        response.put("isOrderingOpen", isOrderingOpen);
        
        return ResponseEntity.ok(response);
    }
}
