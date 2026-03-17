package com.staybook.controller;

import com.staybook.entity.Room;
import com.staybook.entity.Hotel;
import com.staybook.repository.RoomRepository;
import com.staybook.repository.HotelRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "http://localhost:3000")
public class RoomController {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;

    public RoomController(RoomRepository roomRepository,
                          HotelRepository hotelRepository) {
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
    }

    // ✅ GET rooms by hotel
    @GetMapping("/hotel/{hotelId}")
    public List<Room> getRoomsByHotel(@PathVariable Long hotelId) {
        return roomRepository.findByHotelId(hotelId);
    }

    // ✅ Vendor add room
    @PostMapping("/vendor/{hotelId}")
    public Room addRoom(@PathVariable Long hotelId,
                        @RequestBody Room room) {

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));

        room.setHotel(hotel);
        room.setAvailable(true);

        return roomRepository.save(room);
    }

    // ✅ Delete room
    @DeleteMapping("/vendor/{roomId}")
    public void deleteRoom(@PathVariable Long roomId) {
        roomRepository.deleteById(roomId);
    }
}