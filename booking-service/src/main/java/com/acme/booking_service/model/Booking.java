package com.acme.booking_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    private String id;
    private String passengerName;
    private String contactEmail;
    private String flightId;
    private BookingStatus status;
    private String correlationId;
}
