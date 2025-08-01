package com.acme.booking_service;

import com.acme.booking_service.model.Booking;
import com.acme.booking_service.model.BookingStatus;
import com.acme.booking_service.repository.InMemoryBookingRepository;
import com.acme.booking_service.service.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;

public class BookingServiceIntegrationTest {

    @Test
    void testCreateBooking_withGeneratedId() {
        var repository = new InMemoryBookingRepository();
        WebClient dummyClient = WebClient.builder().baseUrl("http://localhost").build();
        BookingService service = new BookingService(repository, dummyClient) {
            @Override
            public boolean validateFlightCached(String flightId) {
                return true; // Pretend flight is always valid
            }
        };

        Booking bookingRequest = new Booking(null, "Snehansu Sahoo", "sahoo.developer@gmail.com", "F002", BookingStatus.PENDING_PAYMENT, null);

        // Act
        Booking saved = service.create(bookingRequest);

        // Assert
        assertNotNull(saved.getId());
        assertEquals("F002", saved.getFlightId());
        assertEquals("Snehansu Sahoo", saved.getPassengerName());
        assertEquals("sahoo.developer@gmail.com", saved.getContactEmail());
        assertEquals(BookingStatus.PENDING_PAYMENT, saved.getStatus());
        assertNotNull(saved.getCorrelationId());
    }
}
