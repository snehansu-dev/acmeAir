package com.acme.flight_service.controller;

import com.acme.flight_service.model.Flight;
import com.acme.flight_service.service.FlightService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
@Slf4j
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping("/search")
    public List<Flight> search(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam(required = false) Boolean direct) {

        log.info("Received search request: origin={}, destination={}, direct={}", origin, destination, direct);
        return flightService.searchFlights(origin, destination, direct);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Flight> getById(@PathVariable String id) {
        log.info("Fetching flight by ID: {}", id);
        return flightService.getFlightById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
