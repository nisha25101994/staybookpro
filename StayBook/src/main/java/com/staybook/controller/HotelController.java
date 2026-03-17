package com.staybook.controller;

import com.staybook.entity.Hotel;
import com.staybook.entity.User;
import com.staybook.repository.HotelRepository;
import com.staybook.repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;   // ✅ correct import
import java.util.List;





@RestController
@RequestMapping("/api/hotels")   // ✅ FIXED (plural)
@CrossOrigin(origins = "http://localhost:3000")
public class HotelController {

    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;

    public HotelController(HotelRepository hotelRepository,
                           UserRepository userRepository) {
        this.hotelRepository = hotelRepository;
        this.userRepository = userRepository;
    }

    // ===============================
    // 👤 USER PANEL
    // ===============================

    // 🔹 USER → Get approved hotels
    @GetMapping
    public List<Hotel> getApprovedHotels() {
        return hotelRepository.findByApprovedTrue();
    }

    
    @GetMapping("/{id}")
    public Hotel getHotelById(@PathVariable Long id) {
        return hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + id));
    }
    
    
    // 🔹 USER → Search hotels by city
    @GetMapping("/city/{city}")
    public List<Hotel> getHotelsByCity(@PathVariable String city) {
        return hotelRepository.findByCityIgnoreCase(city);
    }

    // ===============================
    // 🧑‍💼 VENDOR PANEL
    // ===============================

    // 🔹 VENDOR → Add hotel
    @PostMapping("/vendor")
    public Hotel addHotelByVendor(
            @RequestBody Hotel hotel,
            Authentication auth
    ) {
        String email = auth.getName();

        User vendor = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        hotel.setVendor(vendor);
        hotel.setApproved(false);

        return hotelRepository.save(hotel);
    }

    // 🔹 VENDOR → View own hotels
    @GetMapping("/vendor")
    public List<Hotel> getMyHotels(Authentication authentication) {

        String email = authentication.getName();

        User vendor = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        return hotelRepository.findByVendorId(vendor.getId());
    }
    
    @PostMapping("/vendor/upload/{hotelId}")
    public Hotel uploadHotelImage(
            @PathVariable Long hotelId,
            @RequestParam("image") MultipartFile file
    ) throws IOException {

    	String uploadDir = "C:/staybook_uploads/";

        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);

        Files.write(filePath, file.getBytes());

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));

        hotel.setImageUrl(fileName);

        return hotelRepository.save(hotel);
    }

}
