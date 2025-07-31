package com.acme.booking_service.repository;

import com.acme.booking_service.model.Booking;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryBookingRepository implements BookingRepository {

    private final Map<String, Booking> store = new ConcurrentHashMap<>();

    @Override
    public Booking save(Booking booking) {
        String id = UUID.randomUUID().toString();
        booking.setId(id);
        store.put(id, booking);
        return booking;
    }

    @Override
    public Optional<Booking> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Booking update(String id, Booking updated) {
        updated.setId(id);
        store.put(id, updated);
        return updated;
    }

    @Override
    public boolean delete(String id) {
        return store.remove(id) != null;
    }
}

