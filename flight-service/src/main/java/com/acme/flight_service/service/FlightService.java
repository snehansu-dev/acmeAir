package com.acme.flight_service.service;

import com.acme.flight_service.exception.NoFlightsFoundException;
import com.acme.flight_service.model.Flight;
import com.acme.flight_service.repository.FlightRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FlightService {

    private final FlightRepository flightRepository;

    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    /**
     * Fetch all flights (optional use case like admin or audit).
     */
    @SuppressWarnings("unused")
    @Cacheable("flights-all")
    public List<Flight> getAllFlights() {
        log.info("Fetching all flights");
        return flightRepository.findAll();
    }

    /**
     * Fetch a flight by its unique ID.
     */
    @Cacheable(cacheNames = "flights", key = "#id")
    public Optional<Flight> getFlightById(String id) {
        log.info("Fetching flight by ID: {}", id);
        return flightRepository.findById(id);
    }

    /**
     * Search for flights between origin and destination,
     * optionally filtering by whether the flight is direct.
     */
    @Cacheable(value = "searchFlights", key = "#origin + '_' + #destination + '_' + #directOnly")
    public List<Flight> searchFlights(String origin, String destination, Boolean directOnly) {
        log.info("Searching flights from {} to {}, directOnly={}", origin, destination, directOnly);
        validateInput(origin, destination);

        List<Flight> results = flightRepository.search(origin, destination);

        if (directOnly != null) {
            results = filterByDirectFlag(results, directOnly);
        }

        if (results.isEmpty()) {
            throw new NoFlightsFoundException("No " + (Boolean.TRUE.equals(directOnly) ? "direct " : "") +
                    "flights found from " + origin + " to " + destination);
        }

        return results;
    }

    /**
     * Validates that both origin and destination are provided and not blank.
     */
    private void validateInput(String origin, String destination) {
        if (origin == null || origin.isBlank() || destination == null || destination.isBlank()) {
            throw new IllegalArgumentException("Both origin and destination must be provided");
        }
    }

    /**
     * Filters the flights based on the directOnly flag.
     */
    private List<Flight> filterByDirectFlag(List<Flight> flights, boolean directOnly) {
        return flights.stream()
                .filter(flight -> directOnly == flight.isDirect())
                .collect(Collectors.toList());
    }
}
