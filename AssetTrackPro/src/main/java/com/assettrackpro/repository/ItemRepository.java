package com.assettrackpro.repository;

import com.assettrackpro.entity.Item;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT i FROM Item i WHERE i.vendor.id = :vendorId AND i.deleted = false")
    Page<Item> findByVendorId(Long vendorId, Pageable pageable);

    @Query("SELECT i FROM Item i WHERE (LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(i.description) LIKE LOWER(CONCAT('%', :name, '%'))) AND i.deleted = false")
    Page<Item> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT i FROM Item i WHERE i.deleted = false")
    Page<Item> findAllActive(Pageable pageable);
    
    List<Item> findByVendorIdAndQuantityAvailableLessThan(
            Long vendorId, int threshold);
}
