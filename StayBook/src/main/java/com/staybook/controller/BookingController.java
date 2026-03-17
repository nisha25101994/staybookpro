package com.staybook.controller;

import com.staybook.entity.*;
import com.staybook.repository.*;
import com.staybook.service.BookingService;
import com.staybook.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final InvoiceRepository invoiceRepository;
    private final BookingRepository bookingRepo;
    private final UserRepository userRepo;
    private final HotelRepository hotelRepo;
    private final CouponRepository couponRepo;
    private final UserService userService;
    private final BookingService bookingService;

    public BookingController(
            InvoiceRepository invoiceRepository,
            BookingRepository bookingRepo,
            UserRepository userRepo,
            HotelRepository hotelRepo,
            CouponRepository couponRepo,
            UserService userService,
            BookingService bookingService
    ) {
        this.invoiceRepository = invoiceRepository;
        this.bookingRepo = bookingRepo;
        this.userRepo = userRepo;
        this.hotelRepo = hotelRepo;
        this.couponRepo = couponRepo;
        this.userService = userService;
        this.bookingService = bookingService;
    }

    private String generateInvoiceNumber() {
        return "INV-" + System.currentTimeMillis();
    }

    // ✅ UPDATED RAW SAVE (Enhanced but flow same)
    @PostMapping
    public Booking createBooking(@RequestBody Booking booking) {

        if (booking.getGuests() <= 0) {
            throw new RuntimeException("Guests must be greater than 0");
        }

        if (booking.getRoomQuantity() <= 0) {
            throw new RuntimeException("Room quantity must be greater than 0");
        }

        booking.setBookingDate(java.time.LocalDate.now());
        booking.setStatus(com.staybook.entity.BookingStatus.PENDING);

        if (booking.getCouponCode() != null &&
                !booking.getCouponCode().trim().isEmpty()) {

            Coupon coupon = couponRepo
                    .findByCodeAndActiveTrue(booking.getCouponCode())
                    .orElse(null);

            if (coupon != null) {

                double base = booking.getTotalPrice();
                double discount =
                        (base * coupon.getDiscountPercentage()) / 100;

                booking.setDiscountAmount(discount);
                booking.setTotalPrice(base - discount);
                booking.setExpiryDate(coupon.getExpiryDate());
            }
        }

        return bookingRepo.save(booking);
    }

       

    // ✅ Confirm Booking (unchanged)
    @PutMapping("/confirm/{id}")
    public ResponseEntity<?> confirmBooking(@PathVariable Long id) {

        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            return ResponseEntity.badRequest()
                    .body("Booking already confirmed");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepo.save(booking);

        Optional<Invoice> existingInvoice =
                invoiceRepository.findByBookingId(booking.getId());

        if (existingInvoice.isEmpty()) {

            Invoice invoice = new Invoice();
            invoice.setBooking(booking);
            invoice.setInvoiceNumber(generateInvoiceNumber());
            invoice.setAmount(booking.getTotalPrice());
            invoice.setStatus("GENERATED");
            invoice.setCreatedAt(LocalDateTime.now());

            invoiceRepository.save(invoice);
        }

        return ResponseEntity.ok("Booking Confirmed & Invoice Generated");
    }

    // ✅ Admin - View All
    @GetMapping
    public List<Booking> allBookings() {
        return bookingRepo.findAll();
    }

    // ✅ My Bookings
    @GetMapping("/my")
    public List<Booking> myBookings(Authentication auth) {

        if (auth == null) {
            throw new RuntimeException("User not logged in");
        }

        User user = userRepo.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return bookingRepo.findByUser_Id(user.getId());
    }

    // ✅ Cancel Booking
    @DeleteMapping("/{bookingId}")
    public void cancelBooking(
            @PathVariable Long bookingId,
            Authentication auth
    ) {

        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getUser().getEmail().equals(auth.getName())) {
            throw new RuntimeException("Unauthorized");
        }

        bookingRepo.delete(booking);
    }

    // ✅ Preview (unchanged logic)
    @PostMapping("/preview")
    public Map<String, Object> previewBooking(@RequestBody Map<String, Object> req) {

        Long hotelId = Long.valueOf(req.get("hotelId").toString());
        int guests = Integer.parseInt(req.get("guests").toString());
        int roomQty = Integer.parseInt(req.get("roomQuantity").toString());
        String coupon = req.get("couponCode") != null
                ? req.get("couponCode").toString()
                : null;

        LocalDate checkIn = LocalDate.parse(req.get("checkIn").toString());
        LocalDate checkOut = LocalDate.parse(req.get("checkOut").toString());

        Hotel hotel = hotelRepo.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));

        long days = ChronoUnit.DAYS.between(checkIn, checkOut);

        double total = bookingService
                .calculatePrice(hotel, roomQty, guests, coupon, days);

        Map<String, Object> response = new HashMap<>();
        response.put("totalPrice", total);
        response.put("days", days);

        return response;
    }
}