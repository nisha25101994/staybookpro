package com.staybook.service;

import com.staybook.entity.*;
import com.staybook.repository.BookingRepository;
import com.staybook.repository.CouponRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingService {

    private final CouponRepository couponRepo;
    private final BookingRepository bookingRepo;

    public BookingService(CouponRepository couponRepo,
                          BookingRepository bookingRepo) {
        this.couponRepo = couponRepo;
        this.bookingRepo = bookingRepo;
    }

    public double calculatePrice(Hotel hotel,
                                 int roomQty,
                                 int guests,
                                 String couponCode,
                                 long days) {

        if (hotel == null) throw new IllegalArgumentException("Hotel null");
        if (roomQty <= 0) throw new IllegalArgumentException("Invalid rooms");
        if (days <= 0) throw new IllegalArgumentException("Invalid days");

        double basePrice = hotel.getPricePerNight() * roomQty * days;

        if (guests > roomQty * 2) {
            basePrice += 500 * (guests - (roomQty * 2));
        }

        if (couponCode != null && !couponCode.trim().isEmpty()) {
            Coupon coupon = couponRepo
                    .findByCodeAndActiveTrue(couponCode.trim())
                    .orElse(null);

            if (coupon != null &&
                    (coupon.getExpiryDate() == null ||
                     coupon.getExpiryDate().isAfter(LocalDate.now()))) {

                double discount =
                        (basePrice * coupon.getDiscountPercentage()) / 100;

                basePrice -= discount;
            }
        }

        return Math.max(basePrice, 0);
    }

    public Booking createBooking(User user,
                                 Hotel hotel,
                                 int roomQty,
                                 int guests,
                                 LocalDate checkIn,
                                 LocalDate checkOut,
                                 String couponCode) {

        long days = ChronoUnit.DAYS.between(checkIn, checkOut);

        double finalPrice = calculatePrice(
                hotel, roomQty, guests, couponCode, days
        );

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setHotel(hotel);
        booking.setRoomQuantity(roomQty);
        booking.setGuests(guests);
        booking.setCheckIn(checkIn);
        booking.setCheckOut(checkOut);
        booking.setTotalPrice(finalPrice);
        booking.setBookingDate(LocalDate.now());
        booking.setStatus(BookingStatus.PENDING);

        return bookingRepo.save(booking);
    }

    public List<Booking> getBookingsByUser_Id(Long userId) {
        return bookingRepo.findByUser_Id(userId);
    }
}