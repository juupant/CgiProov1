package com.flightbooking.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flightbooking.model.Flight;
//siin on repo mis hoiab lendude infot
@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    List<Flight> findByDestinationAndDateAndPriceLessThanEqual(
        String destination, 
        LocalDate date, 
        double maxPrice
    );
} 