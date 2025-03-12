package com.flightbooking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flightbooking.model.Seat;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByFlightId(Long flightId);
    List<Seat> findByFlightIdAndStatus(Long flightId, String status);
    long countByFlightIdAndStatus(Long flightId, String status);
    void deleteByFlightId(Long flightId);
} 

//vscode nonstop jebib java importidega ma tahaks päris ide kasutada