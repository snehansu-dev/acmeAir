package com.acme.booking_service.service;

import com.acme.booking_service.exception.FlightNotFoundException;
import com.acme.booking_service.model.Booking;
import com.acme.booking_service.model.BookingStatus;
import com.acme.booking_service.repository.BookingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final WebClient flightWebClient;

    public BookingService(BookingRepository bookingRepository,
                          WebClient flightWebClient) {
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
            log.warn("Flight ID {} not found", booking.getFlightId());
            throw new FlightNotFoundException("Flight not found: " + booking.getFlightId());
        }

        // Assign correlation ID if not set (assuming caller may have set it already)
        if (booking.getCorrelationId() == null) {
            booking.setCorrelationId(UUID.randomUUID().toString());
        }

        booking.setStatus(BookingStatus.PENDING_PAYMENT); // Initial state
        Booking saved = bookingRepository.save(booking);

        log.info("Booking created with ID={} and correlationId={}", saved.getId(), saved.getCorrelationId());
        return saved;
    }


    @CachePut(value = "bookingById", key = "#id")
    public Optional<Booking> update(String id, Booking updated) {
        return bookingRepository.findById(id)
                .map(existing -> {
                    updated.setId(id);

                    // Preserve existing fields if not provided
                    if (updated.getStatus() == null) {
                        updated.setStatus(existing.getStatus());
                    }
                    if (updated.getCorrelationId() == null) {
                        updated.setCorrelationId(existing.getCorrelationId());
                    }

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

    @Retry(name = "flightServiceRetry")
    @CircuitBreaker(name = "flightServiceCircuitBreaker", fallbackMethod = "fallbackValidateFlight")
    @Cacheable(value = "flight-validation", key = "#flightId")
    public boolean validateFlightCached(String flightId) {
        log.info("Validating flightId={} via Flight Service", flightId);
        try {
            flightWebClient.get()
                    .uri("/api/flights/{id}", flightId)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block(); // blocking only for sync demo
            return true;
        } catch (WebClientResponseException.NotFound e) {
            return false;
        }
    }

    public boolean fallbackValidateFlight(String flightId, Throwable ex) {
        log.error("Fallback triggered for flightId={}, error={}", flightId, ex.getMessage());
        throw new RuntimeException("Flight Service temporarily unavailable", ex);
    }
}
