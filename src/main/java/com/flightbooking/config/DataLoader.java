package com.flightbooking.config;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.flightbooking.model.Flight;
import com.flightbooking.repository.FlightRepository;
import com.flightbooking.service.FlightService;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private FlightService flightService;

    @Override
    public void run(String... args) {
        // delete existing data
        flightRepository.deleteAll();
        
        // create sample flights
        List<Flight> flights = Arrays.asList(
            createFlight("London", 299.99),
            createFlight("Paris", 249.99),
            createFlight("New York", 599.99),
            createFlight("Tokyo", 899.99)
        );

        // save flights and generate seats
        for (Flight flight : flights) {
            Flight savedFlight = flightRepository.save(flight);
            flightService.generateRandomSeats(savedFlight);
        }
    }

    private Flight createFlight(String destination, double price) {
        Flight flight = new Flight();
        flight.setDestination(destination);
        flight.setDate(LocalDate.now().plusDays(7)); // flight in 7 days
        
        // format time as string "HH:mm"
        String departureTime = LocalTime.of(10, 0).format(DateTimeFormatter.ofPattern("HH:mm"));
        flight.setDepartureTime(departureTime);
        
        flight.setPrice(price);
        flight.setTotalSeats(180);
        flight.setAvailableSeats(180); // initially all seats are available
        return flight;
    }
} 