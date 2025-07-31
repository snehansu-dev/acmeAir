package com.acme.flight_service.exception;

public class NoFlightsFoundException extends RuntimeException {
    public NoFlightsFoundException(String message) {
        super(message);
    }
}
