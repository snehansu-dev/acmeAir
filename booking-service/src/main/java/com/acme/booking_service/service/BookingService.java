package com.acme.booking_service.service;

import com.acme.booking_service.exception.FlightNotFoundException;
import com.acme.booking_service.model.Booking;
import com.acme.booking_service.repository.BookingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;


import java.util.Optional;

@Slf4j
@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final WebClient flightWebClient;

    public BookingService(BookingRepository bookingRepository, WebClient flightWebClient) {
        this.bookingRepository = bookingRepository;
        this.flightWebClient = flightWebClient;
    }

    @Cacheable(value = "bookingById", key = "#id")
    public Optional<Booking> getById(String id) {
        return bookingRepository.findById(id);
    }

    public Booking create(Booking booking) {
        log.info("Creating booking for flightId={}", booking.getFlightId());

        if (!validateFlightCached(booking.getFlightId())) {
            log.warn("Flight ID {} not found in Flight Service", booking.getFlightId());
            throw new FlightNotFoundException("Flight not found: " + booking.getFlightId());
        }

        Booking saved = bookingRepository.save(booking);
        log.info("Booking created with ID={}", saved.getId());
        return saved;
    }

    @CachePut(value = "bookingById", key = "#id")
    public Optional<Booking> update(String id, Booking updated) {
        return bookingRepository.findById(id)
                .map(existing -> {
                    updated.setId(id);
                    bookingRepository.update(id, updated);
                    log.info("Updated booking ID={}", id);
                    return updated;
                });
    }

    @CacheEvict(value = "bookingById", key = "#id")
    public boolean cancel(String id) {
        boolean deleted = bookingRepository.delete(id);
        if (deleted) {
            log.info("Cancelled booking ID={}", id);
        } else {
            log.warn("Attempted to cancel non-existing booking ID={}", id);
        }
        return deleted;
    }

    @Cacheable(value = "flight-validation", key = "#flightId")
    public boolean validateFlightCached(String flightId) {
        log.info("Validating flightId={} via Flight Service", flightId);
        try {
            flightWebClient.get()
                    .uri("/api/flights/{id}", flightId)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block(); // blocking since create() is sync

            return true;
        } catch (WebClientResponseException.NotFound e) {
            return false;
        } catch (Exception e) {
            log.error("Flight Service error for flightId={}: {}", flightId, e.getMessage());
            throw new RuntimeException("Flight Service unreachable", e);
        }
    }
}
