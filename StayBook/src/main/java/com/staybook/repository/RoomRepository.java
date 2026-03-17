package com.staybook.repository;

import com.staybook.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByHotelIdAndAvailableTrue(Long hotelId);
    List<Room> findByHotelId(Long hotelId);
}
