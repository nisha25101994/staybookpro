package com.staybook.service;

import com.staybook.entity.Hotel;
import com.staybook.repository.HotelRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelService {

    private final HotelRepository hotelRepo;

    public HotelService(HotelRepository hotelRepo) {
        this.hotelRepo = hotelRepo;
    }

    // ADD HOTEL
    public Hotel addHotel(Hotel hotel) {
        return hotelRepo.save(hotel);
    }

    // GET ALL HOTELS
    public List<Hotel> getAllHotels() {
        return hotelRepo.findAll();
    }
}
