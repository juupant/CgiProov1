package com.flightbooking.model;

import jakarta.persistence.Entity;
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
    private boolean hasExtraLegroom;
    private boolean isNearExit;
    
    @ManyToOne
    private Flight flight;
} 