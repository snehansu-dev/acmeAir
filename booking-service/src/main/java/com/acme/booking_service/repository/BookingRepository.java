package com.acme.booking_service.repository;

import com.acme.booking_service.model.Booking;

import java.util.Optional;

public interface BookingRepository {
    Booking save(Booking booking);
    Optional<Booking> findById(String id);
    Booking update(String id, Booking updated);
    boolean delete(String id);
}