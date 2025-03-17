package com.flightbooking.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String destination;
    private LocalDate date;
    private String departureTime;
    private double price;
    private int totalSeats;
    private int availableSeats;
    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seats;

        // explicit getter and setter for departureTime just in case
        public String getDepartureTime() {
            return departureTime;
        }
    
        public void setDepartureTime(String departureTime) {
            this.departureTime = departureTime;
        }
    
        // helper method for some two-direction stuff
        public void addSeat(Seat seat) {
            seats.add(seat);
            seat.setFlight(this);
        }
    
        public void removeSeat(Seat seat) {
            seats.remove(seat);
            seat.setFlight(null);
        }
} 