package com.acme.flight_service;

import com.acme.flight_service.exception.NoFlightsFoundException;
import com.acme.flight_service.model.Flight;
import com.acme.flight_service.repository.FlightRepository;
import com.acme.flight_service.service.FlightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FlightServiceTests {

    private FlightRepository flightRepository;
    private FlightService flightService;

    @BeforeEach
    void setUp() {
        flightRepository = mock(FlightRepository.class);
        flightService = new FlightService(flightRepository);
    }

    @Test
    void testGetFlightById_found() {
        Flight flight = new Flight();
        flight.setId("F001");
        when(flightRepository.findById("F001")).thenReturn(Optional.of(flight));

        Optional<Flight> result = flightService.getFlightById("F001");

        assertTrue(result.isPresent());
        assertEquals("F001", result.get().getId());
    }

    @Test
    void testGetFlightById_notFound() {
        when(flightRepository.findById("INVALID")).thenReturn(Optional.empty());

        Optional<Flight> result = flightService.getFlightById("INVALID");

        assertTrue(result.isEmpty());
    }

    @Test
    void testSearchFlights_found() {
        Flight flight = createFlight("F001", "Auckland", "Wellington", List.of());
        when(flightRepository.search("Auckland", "Wellington")).thenReturn(List.of(flight));

        List<Flight> results = flightService.searchFlights("Auckland", "Wellington", null);

        assertEquals(1, results.size());
        assertEquals("Auckland", results.get(0).getOrigin());
    }

    @Test
    void testSearchFlights_directOnly_filterApplied() {
        Flight direct = createFlight("F002", "Auckland", "Christchurch", List.of());
        Flight indirect = createFlight("F009", "Auckland", "Christchurch", List.of("Wellington"));
        when(flightRepository.search("Auckland", "Christchurch")).thenReturn(List.of(direct, indirect));

        List<Flight> results = flightService.searchFlights("Auckland", "Christchurch", true);

        assertEquals(1, results.size());
        assertTrue(results.get(0).isDirect());
    }

    @Test
    void testSearchFlights_notFound() {
        when(flightRepository.search("X", "Y")).thenReturn(List.of());

        assertThrows(NoFlightsFoundException.class, () ->
                flightService.searchFlights("X", "Y", false));
    }

    @Test
    void testSearchFlights_invalidInput() {
        assertThrows(IllegalArgumentException.class, () ->
                flightService.searchFlights("", "Y", null));

        assertThrows(IllegalArgumentException.class, () ->
                flightService.searchFlights("X", "  ", null));
    }

    private Flight createFlight(String id, String origin, String destination, List<String> stopovers) {
        Flight flight = new Flight();
        flight.setId(id);
        flight.setOrigin(origin);
        flight.setDestination(destination);
        flight.setDepartureDate(LocalDate.now());
        flight.setDepartureTime(LocalTime.NOON);
        flight.setArrivalDate(LocalDate.now());
        flight.setArrivalTime(LocalTime.of(14, 0));
        flight.setStopovers(stopovers);
        return flight;
    }
}
