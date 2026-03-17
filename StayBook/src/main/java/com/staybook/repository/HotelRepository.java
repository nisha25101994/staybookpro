package com.staybook.repository;

import com.staybook.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    // Search hotels by city
    List<Hotel> findByCityIgnoreCase(String city);

    List<Hotel> findByVendorId(Long vendorId);
    List<Hotel> findByApprovedTrue();
    List<Hotel> findByApprovedFalse();
    List<Hotel> findByApprovedTrueOrderByIdDesc();
    long countByApprovedTrue();
    long countByApprovedFalse();


}
