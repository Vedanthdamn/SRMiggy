package com.srmiggy.controller;

import com.srmiggy.model.MenuItem;
import com.srmiggy.model.Vendor;
import com.srmiggy.repository.MenuItemRepository;
import com.srmiggy.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@CrossOrigin
public class MenuController {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<MenuItem>> getMenuByVendor(@PathVariable Long vendorId) {
        return vendorRepository.findById(vendorId)
                .map(vendor -> ResponseEntity.ok(menuItemRepository.findByVendorAndAvailableTrue(vendor)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getMenuItemById(@PathVariable Long id) {
        return menuItemRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
