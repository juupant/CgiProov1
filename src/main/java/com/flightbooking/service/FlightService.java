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
import com.flightbooking.model.Seat.SeatClass;
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
        int totalRows = 30;
        int seatsPerRow = 6;
        
        
        // class configuration
        int firstClassRows = 2;     // Rows 1-2
        int businessClassRows = 3;   // Rows 3-5
        // Rest are economy class    // Rows 6-30
        
        // clear existing seats
        flight.getSeats().clear();
        
        for (int row = 1; row <= totalRows; row++) {
            // Determine seat class based on row number
            SeatClass seatClass;
            if (row <= firstClassRows) {
                seatClass = SeatClass.FIRST;
            } else if (row <= firstClassRows + businessClassRows) {
                seatClass = SeatClass.BUSINESS;
            } else {
                seatClass = SeatClass.ECONOMY;
            }
            
            for (int seatNum = 0; seatNum < seatsPerRow; seatNum++) {
                Seat seat = new Seat();
                char letter = (char) ('A' + seatNum);
                seat.setNumber(row + String.valueOf(letter));
                seat.setStatus(random.nextDouble() < 0.7 ? "AVAILABLE" : "OCCUPIED");
                seat.setWindow(seatNum == 0 || seatNum == seatsPerRow - 1);
                seat.setExtraLegroom(row == 1 || row == firstClassRows + 1 || row == totalRows);
                seat.setNearExit(row == 1 || row == firstClassRows + 1 || row == totalRows);
                seat.setSeatClass(seatClass);
                seat.setFlight(flight);
                
                // calculate seat price based on class and features
                double basePrice = flight.getPrice();
                double classMultiplier = seatClass.getPriceMultiplier();
                double extraFeaturesMultiplier = 1.0;
                if (seat.isWindow()) extraFeaturesMultiplier += 0.1;
                if (seat.isExtraLegroom()) extraFeaturesMultiplier += 0.15;
                if (seat.isNearExit()) extraFeaturesMultiplier += 0.05;
                
                seat.setPrice(basePrice * classMultiplier * extraFeaturesMultiplier);
                flight.addSeat(seat); // use the helper method
            }
        }
        
        // Update available seats count
        long availableSeats = seatRepository.countByFlightIdAndStatus(flight.getId(), "AVAILABLE");
        flight.setAvailableSeats((int) availableSeats);
        flight.setTotalSeats(totalRows * seatsPerRow);
        flightRepository.save(flight);
        
        logger.info("Generated {} seats for flight {}", totalRows * seatsPerRow, flight.getId());
    }
} 