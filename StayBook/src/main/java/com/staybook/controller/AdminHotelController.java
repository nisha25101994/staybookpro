package com.staybook.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.staybook.entity.Hotel;
import com.staybook.repository.BookingRepository;
import com.staybook.repository.HotelRepository;
import com.staybook.repository.UserRepository;

@RestController
@RequestMapping("/api/admin/hotel")
public class AdminHotelController {

    private final HotelRepository hotelRepository;
    private final UserRepository userRepo;
    private final BookingRepository bookingRepo;

    public AdminHotelController(HotelRepository hotelRepository,
                                UserRepository userRepo,
                                BookingRepository bookingRepo) {
        this.hotelRepository = hotelRepository;
        this.userRepo = userRepo;
        this.bookingRepo = bookingRepo;
    }

    // ✅ VIEW APPROVED HOTELS
    @GetMapping
    public List<Hotel> getApprovedHotels() {
        return hotelRepository.findByApprovedTrue();
    }

    // ✅ VIEW PENDING HOTELS
    @GetMapping("/pending")
    public List<Hotel> getPendingHotels() {
        return hotelRepository.findByApprovedFalse();
    }

    // ✅ APPROVE HOTEL
    @PutMapping("/{id}/approve")
    public Hotel approveHotel(@PathVariable Long id) {
        return hotelRepository.findById(id)
                .map(hotel -> {
                    hotel.setApproved(true);
                    return hotelRepository.save(hotel);
                })
                .orElseThrow(() ->
                        new RuntimeException("Hotel not found with id: " + id)
                );
    }

    // ✅ REJECT HOTEL
    @DeleteMapping("/{id}/reject")
    public void rejectHotel(@PathVariable Long id) {
        hotelRepository.deleteById(id);
    }

    // ✅ DASHBOARD STATS
    @GetMapping("/dashboard/stats")
    public Map<String, Long> getAdminStats() {

        Map<String, Long> stats = new HashMap<>();

        stats.put("totalHotels", hotelRepository.count());
        stats.put("pendingHotels", hotelRepository.countByApprovedFalse());
        stats.put("approvedHotels", hotelRepository.countByApprovedTrue());
        stats.put("totalUsers", userRepo.count());
        stats.put("totalBookings", bookingRepo.count());

        return stats;
    }

}