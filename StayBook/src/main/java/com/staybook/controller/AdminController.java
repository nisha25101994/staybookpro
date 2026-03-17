package com.staybook.controller;

import com.staybook.entity.Booking;
import com.staybook.entity.BookingStatus;
import com.staybook.entity.User;
import com.staybook.repository.BookingRepository;
import com.staybook.repository.UserRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepo;
    private final BookingRepository bookingRepo;

    public AdminController(
            UserRepository userRepo,
            BookingRepository bookingRepo
    ) {
        this.userRepo = userRepo;
        this.bookingRepo = bookingRepo;
    }

    /* =========================
       USER MANAGEMENT
    ========================= */

    // ✅ VIEW ALL USERS
    @GetMapping("/users")
    public List<User> allUsers() {
        return userRepo.findAll();
    }

    /* =========================
       BOOKING MANAGEMENT
    ========================= */

    // ✅ VIEW ALL BOOKINGS
    @GetMapping("/bookings")
    public List<Booking> allBookings() {
        return bookingRepo.findAll();
    }

    // ✅ UPDATE BOOKING STATUS (Enum Safe)
    @PutMapping("/bookings/{id}/status")
    public Booking updateBookingStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {

        String statusStr = body.get("status");

        if (statusStr == null || statusStr.trim().isEmpty()) {
            throw new RuntimeException("Status is required");
        }

        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        try {
            BookingStatus status =
                    BookingStatus.valueOf(statusStr.toUpperCase());

            booking.setStatus(status);

        } catch (IllegalArgumentException e) {
            throw new RuntimeException(
                    "Invalid status. Allowed: PENDING, CONFIRMED, CANCELLED"
            );
        }

        return bookingRepo.save(booking);
    }
}