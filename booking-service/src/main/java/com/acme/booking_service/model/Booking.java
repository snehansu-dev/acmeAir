package com.acme.booking_service.model;

public class Booking {
    private String id;
    private String flightId;
    private String passengerName;
    private String contactEmail;

    public Booking() {}

    public Booking(String id, String flightId, String passengerName, String contactEmail) {
        this.id = id;
        this.flightId = flightId;
        this.passengerName = passengerName;
        this.contactEmail = contactEmail;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFlightId() { return flightId; }
    public void setFlightId(String flightId) { this.flightId = flightId; }
    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
}

