package com.acme.flight_service.repository;

import com.acme.flight_service.model.Flight;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class JsonFlightRepository implements FlightRepository {

    private final List<Flight> flights;

    public JsonFlightRepository() {
        log.info("Initializing JsonFlightRepository...");
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try (InputStream is = getClass().getResourceAsStream("/data/flights.json")) {
            if (is == null) {
                log.error("flights.json file not found in /data/");
                throw new RuntimeException("flights.json not found in /data/");
            }
            Flight[] array = mapper.readValue(is, Flight[].class);
            flights = Arrays.asList(array);
            log.info("Loaded {} flights from flights.json", flights.size());
        } catch (IOException e) {
            log.error("Error loading flights.json", e);
            throw new RuntimeException("Failed to load flights", e);
        }
    }

    @Override
    public List<Flight> findAll() {
        log.debug("Returning all flights");
        return new ArrayList<>(flights);
    }

    @Override
    public Optional<Flight> findById(String id) {
        log.debug("Searching flight by ID: {}", id);
        return flights.stream()
                .filter(f -> f.getId().equalsIgnoreCase(id))
                .findFirst();
    }

    @Override
    public List<Flight> search(String origin, String destination) {
        log.debug("Searching flights from {} to {}", origin, destination);
        return flights.stream()
                .filter(f -> f.getOrigin().equalsIgnoreCase(origin) &&
                        f.getDestination().equalsIgnoreCase(destination))
                .collect(Collectors.toList());
    }
}
