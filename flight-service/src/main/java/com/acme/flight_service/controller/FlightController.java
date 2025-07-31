package com.acme.flight_service.controller;

import com.acme.flight_service.model.Flight;
import com.acme.flight_service.service.FlightService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
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

        List<Flight> results = flightService.searchFlights(origin, destination);

        if (direct != null) {
            results = results.stream()
                    .filter(flight -> flight.isDirect() == direct)
                    .toList();
        }

        return results;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Flight> getById(@PathVariable String id) {
        return flightService.getFlightById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
