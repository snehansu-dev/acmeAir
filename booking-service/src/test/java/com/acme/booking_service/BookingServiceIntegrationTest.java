package com.acme.booking_service;

import com.acme.booking_service.model.Booking;
import com.acme.booking_service.repository.InMemoryBookingRepository;
import com.acme.booking_service.service.BookingService;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class BookingServiceIntegrationTest {

    @Test
    void testCreateBooking_withGeneratedId() {
        var repository = new InMemoryBookingRepository();
        BookingService service = new BookingService(repository, null) {
            @Override
            public boolean validateFlightCached(String flightId) {
                return true; // Pretend flight is always valid
            }
        };

        Booking bookingRequest = new Booking(null, "F002", "Alice", "alice@example.com");

        // Act
        Booking saved = service.create(bookingRequest);

        // Assert
        assertNotNull(saved.getId());
        assertEquals("F002", saved.getFlightId());
        assertEquals("Alice", saved.getPassengerName());
        assertEquals("alice@example.com", saved.getContactEmail());
    }
}
