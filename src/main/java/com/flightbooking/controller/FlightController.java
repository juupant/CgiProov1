package com.flightbooking.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flightbooking.model.Flight;
import com.flightbooking.model.Seat;
import com.flightbooking.service.FlightService;


//https://spring.io/guides/gs/accessing-data-mysql springboot docs has so far been the main source for info
 //every time i save vscode blasts everything red for a second for no reason
 //regarding imports in java i mainly spammed different ones until it worked

 //this is the controller to make things work
@RestController
@RequestMapping("/api/flights")
@CrossOrigin(origins = "*")
public class FlightController {
    
    private static final Logger logger = LoggerFactory.getLogger(FlightController.class);
    
    @Autowired
    private FlightService flightService;
    
    @PostMapping("/search")
    public ResponseEntity<?> searchFlights(@RequestBody Map<String, String> searchParams) {
        try {
            String destination = searchParams.get("destination");
            String dateStr = searchParams.get("date");
            String maxPriceStr = searchParams.get("maxPrice");
            
            logger.info("Received search request with params: {}", searchParams);

            if (destination == null || dateStr == null || maxPriceStr == null) { //added error messages for debugging comfort, previously had issues when launching
                logger.warn("Missing required parameters in request");        //and didnt see shit so needed actual visual error indications
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Missing required parameters",
                                "message", "Destination, date, and maxPrice are required"));
            }

            LocalDate date;
            double maxPrice;
            
            try {
                date = LocalDate.parse(dateStr);
                maxPrice = Double.parseDouble(maxPriceStr);
            } catch (Exception e) {
                logger.warn("Invalid parameter format: {}", e.getMessage());
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid parameter format",
                                "message", "Please check date format (YYYY-MM-DD) and price value")); //added error messages for debugging comfort
            }
            
            List<Flight> flights = flightService.searchFlights(destination, date, maxPrice);
            logger.info("Found {} flights matching criteria", flights.size());
            
            return ResponseEntity.ok(flights);
        } catch (Exception e) {
            logger.error("Error searching flights: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal server error",
                            "message", "An unexpected error occurred while searching flights")); //added error messages for debugging comfort
        }
    }
    
    @GetMapping("/{flightId}/seats")
    public ResponseEntity<?> getFlightSeats(@PathVariable Long flightId) {
        try {
            logger.info("Getting seats for flight {}", flightId);
            List<Seat> seats = flightService.getFlightSeats(flightId);
            return ResponseEntity.ok(seats);
        } catch (Exception e) {
            logger.error("Error getting seats for flight {}: {}", flightId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal server error",
                            "message", "An unexpected error occurred while fetching seats")); //added error messages for debugging comfort
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        logger.error("Unhandled exception: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of("error", "Internal server error",
                        "message", "An unexpected error occurred")); //added error messages for debugging comfort
    }
} 