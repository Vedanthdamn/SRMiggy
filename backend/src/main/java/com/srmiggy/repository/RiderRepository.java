package com.srmiggy.repository;

import com.srmiggy.model.Rider;
import com.srmiggy.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RiderRepository extends JpaRepository<Rider, Long> {
    List<Rider> findByVendor(Vendor vendor);
    List<Rider> findByVendorAndAvailableTrue(Vendor vendor);
}
