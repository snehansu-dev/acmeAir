package com.acme.flight_service.repository;

import com.acme.flight_service.model.Flight;

import java.util.List;
import java.util.Optional;

public interface FlightRepository {
    List<Flight> findAll();
    Optional<Flight> findById(String id);
    List<Flight> search(String origin, String destination);
}
