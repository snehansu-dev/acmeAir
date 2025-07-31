package com.acme.booking_service.controller;

import com.acme.booking_service.exception.FlightNotFoundException;
import com.acme.booking_service.model.Booking;
import com.acme.booking_service.service.BookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getById(@PathVariable String id) {
        return bookingService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Booking booking) {
        try {
            Booking created = bookingService.create(booking);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (FlightNotFoundException ex) {
            log.warn("Flight not found for ID: {}", booking.getFlightId());
            return ResponseEntity.badRequest().body("Invalid flight ID: " + booking.getFlightId());
        } catch (Exception ex) {
            log.error("Unexpected error while creating booking", ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody Booking booking) {
        return bookingService.update(id, booking)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable String id) {
        return bookingService.cancel(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
