package com.srmiggy.repository;

import com.srmiggy.model.MenuItem;
import com.srmiggy.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByVendor(Vendor vendor);
    List<MenuItem> findByVendorAndAvailableTrue(Vendor vendor);
}
