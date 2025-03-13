package com.flightbooking.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flightbooking.model.Flight;
import com.flightbooking.model.Seat;
import com.flightbooking.repository.FlightRepository;
import com.flightbooking.repository.SeatRepository;

@Service
public class FlightService {        //this is the service for flights
    
    private static final Logger logger = LoggerFactory.getLogger(FlightService.class);
    
    @Autowired
    private FlightRepository flightRepository;
    
    @Autowired
    private SeatRepository seatRepository;
    
    public List<Flight> searchFlights(String destination, LocalDate date, double maxPrice) {
        if (destination == null || date == null || maxPrice < 0) {
            throw new IllegalArgumentException("Invalid search parameters");
        }

        logger.info("Searching for flights to {} on {} with max price {}", destination, date, maxPrice);
        
        // convert destination to lowercase for case-insensitive search
        String searchDestination = destination.toLowerCase();
        
        List<Flight> flights = flightRepository.findAll().stream()
            .filter(flight -> flight.getDestination().toLowerCase().contains(searchDestination))
            .filter(flight -> flight.getDate().equals(date))
            .filter(flight -> flight.getPrice() <= maxPrice)
            .toList();
        
        logger.info("Found {} matching flights", flights.size());
        return flights;
    }
    
    public List<Seat> getFlightSeats(Long flightId) {
        if (flightId == null) {
            throw new IllegalArgumentException("Flight ID cannot be null");
        }

        logger.info("Getting seats for flight {}", flightId);
        
        // verify flight exists
        Optional<Flight> flight = flightRepository.findById(flightId);
        if (flight.isEmpty()) {
            logger.warn("Flight not found with ID: {}", flightId);
            throw new IllegalArgumentException("Flight not found");
        }

        List<Seat> seats = seatRepository.findByFlightId(flightId);
        logger.info("Found {} seats for flight {}", seats.size(), flightId);
        return seats;
    }
    
    @Transactional
    public void generateRandomSeats(Flight flight) {
        if (flight == null || flight.getId() == null) {
            throw new IllegalArgumentException("Invalid flight");
        }

        logger.info("Generating random seats for flight {}", flight.getId());
        Random random = new Random();
        int rows = 30;
        int seatsPerRow = 6;
        
        // delete existing seats for this flight
        seatRepository.deleteByFlightId(flight.getId());
        
        for (int row = 1; row <= rows; row++) {
            for (int seatNum = 0; seatNum < seatsPerRow; seatNum++) {
                Seat seat = new Seat();
                char letter = (char) ('A' + seatNum);
                seat.setNumber(row + String.valueOf(letter));
                seat.setStatus(random.nextDouble() < 0.7 ? "AVAILABLE" : "OCCUPIED");
                seat.setWindow(seatNum == 0 || seatNum == seatsPerRow - 1);
                seat.setHasExtraLegroom(row == 1 || row == 16);
                seat.setNearExit(row == 1 || row == 16 || row == rows);
                seat.setFlight(flight);
                seatRepository.save(seat); //man writing this was tedious
            }
        }
        
        // Update available seats count
        long availableSeats = seatRepository.countByFlightIdAndStatus(flight.getId(), "AVAILABLE");
        flight.setAvailableSeats((int) availableSeats);
        flight.setTotalSeats(rows * seatsPerRow);
        flightRepository.save(flight);
        
        logger.info("Generated {} seats for flight {}", rows * seatsPerRow, flight.getId());
    }
} 