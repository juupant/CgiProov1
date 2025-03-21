package com.flightbooking.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

//seat data model
@Data
@Entity
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String number;
    private String status; // available or occupied
    private boolean isWindow;
    private boolean extraLegroom;
    private boolean isNearExit;
    
    @Enumerated(EnumType.STRING)
    private SeatClass seatClass;

    private double price;

    @ManyToOne
    private Flight flight;

    public enum SeatClass {
        FIRST("First Class", 2.5), //seat classes
        BUSINESS("Business Class", 1.8),
        ECONOMY("Economy Class", 1.0);
        
        private final String displayName;
        private final double priceMultiplier;
        
        SeatClass(String displayName, double priceMultiplier) {
            this.displayName = displayName;
            this.priceMultiplier = priceMultiplier;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public double getPriceMultiplier() {
            return priceMultiplier;
        }
    }
} 