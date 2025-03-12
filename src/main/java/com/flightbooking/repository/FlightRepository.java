package com.flightbooking.repository;

import com.flightbooking.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    List<Flight> findByDestinationAndDateAndPriceLessThanEqual(
        String destination, 
        LocalDate date, 
        double maxPrice
    );
} 