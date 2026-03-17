package com.staybook.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.staybook.entity.Hotel;
import com.staybook.repository.HotelRepository;

@RestController
@RequestMapping("/api/vendor")
public class VendorController {

    private final HotelRepository hotelRepo;

    public VendorController(HotelRepository hotelRepo) {
        this.hotelRepo = hotelRepo;
    }

    // ✅ ADD HOTEL
    @PostMapping("/hotels")
    public Hotel addHotel(@RequestBody Hotel hotel) {
        hotel.setApproved(false); // vendor hotels are pending
        return hotelRepo.save(hotel);
    }

    // ✅ GET MY HOTELS
    @GetMapping("/hotels")
    public List<Hotel> getMyHotels() {
        return hotelRepo.findAll();
    }

    // ✅ DELETE HOTEL
    @DeleteMapping("/hotels/{id}")
    public void deleteHotel(@PathVariable Long id) {
        hotelRepo.deleteById(id);
    }
}

