package com.srmiggy.repository;

import com.srmiggy.model.MenuItem;
import com.srmiggy.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, UUID> {
    List<MenuItem> findByVendor(Vendor vendor);
    List<MenuItem> findByVendorAndAvailableTrue(Vendor vendor);
}
