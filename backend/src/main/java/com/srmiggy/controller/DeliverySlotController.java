package com.srmiggy.controller;

import com.srmiggy.model.DeliverySlot;
import com.srmiggy.repository.DeliverySlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/slots")
@CrossOrigin
public class DeliverySlotController {

    @Autowired
    private DeliverySlotRepository deliverySlotRepository;

    @GetMapping
    public ResponseEntity<List<DeliverySlot>> getActiveSlots() {
        return ResponseEntity.ok(deliverySlotRepository.findByActiveTrue());
    }
}
