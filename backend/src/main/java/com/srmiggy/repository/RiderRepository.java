package com.srmiggy.repository;

import com.srmiggy.model.Rider;
import com.srmiggy.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RiderRepository extends JpaRepository<Rider, UUID> {
    List<Rider> findByVendor(Vendor vendor);
    List<Rider> findByVendorAndAvailableTrue(Vendor vendor);
}
