package com.flightbooking.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flightbooking.model.Seat;
//siin on repo mis hoiab istekohti
@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    void deleteByFlightId(Long flightId);
    long countByFlightIdAndStatus(Long flightId, String status);
    java.util.List<Seat> findByFlightId(Long flightId);
} 

//vscode nonstop jebib java importidega ma tahaks päris ide kasutada