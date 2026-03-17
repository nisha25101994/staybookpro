package com.staybook.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.staybook.entity.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCodeAndActiveTrue(String code);
}