package com.acme.flight_service.service;

import com.acme.flight_service.model.Flight;
import com.acme.flight_service.repository.FlightRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FlightService {

    private final FlightRepository flightRepository;

    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    @Cacheable("flights-all")
    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    @Cacheable(cacheNames = "flights", key = "#id")
    public Optional<Flight> getFlightById(String id) {
        return flightRepository.findById(id);
    }

    @Cacheable(value = "searchFlights", key = "#origin + '_' + #destination")
    public List<Flight> searchFlights(String origin, String destination) {
        return flightRepository.search(origin, destination);
    }

    @Cacheable(value = "searchFlights", key = "#origin + '_' + #destination + '_' + #directOnly")
    public List<Flight> searchFlights(String origin, String destination, boolean directOnly) {
        return flightRepository.search(origin, destination).stream()
                .filter(flight -> !directOnly || flight.isDirect())
                .collect(Collectors.toList());
    }
}
