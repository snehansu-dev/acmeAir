# Acme Air Flight Booking System (Microservices)

This project is a microservice-based Flight Booking System built with Spring Boot using two independent services:

- `flight-service` — Manages flight information.
- `booking-service` — Handles bookings for available flights.

---

## Technologies Used

- Java 21
- Spring Boot 3
- Gradle
- RESTful API
- In-memory data store (JSON file)
- Caffeine Cache
- Lombok
- WebClient (for inter-service communication)

---

## Project Structure

```
flight/                          # Root folder (multi-module Gradle project)
│
├── flight-service/              # Flight microservice
│   ├── model/                   # Flight and FareClass models
│   ├── repository/              # JsonFlightRepository for in-memory data
│   ├── service/                 # FlightService
│   ├── controller/              # REST controller for search and get-by-id
│   └── resources/data/          # JSON file storing flights
│
├── booking-service/            # Booking microservice
│   ├── model/                   # Booking model
│   ├── repository/              # In-memory BookingRepository
│   ├── service/                 # BookingService with validation
│   ├── controller/              # Booking REST API
│   └── exception/               # Custom exception handling
│
├── settings.gradle             # Declares both modules
└── README.md
```

---

## Endpoints

### Flight Service

- `GET /api/flights/search?origin=Auckland&destination=Christchurch[&direct=true|false]` — Search flights by route and optionally filter for direct only.
- `GET /api/flights/{id}` — Get flight details by ID

### Booking Service

- `POST /api/bookings` — Create a new booking
- `GET /api/bookings/{id}` — Get a booking by ID
- `PUT /api/bookings/{id}` — Update a booking
- `DELETE /api/bookings/{id}` — Cancel a booking

---

## Caching

- Configured using Caffeine via `application.properties`
- `flight-service` caches all/search/findById queries.
- `booking-service` caches flight validation results.

---

## Running the Services

Ensure ports don't conflict:

- `flight-service` runs on port `8081`
- `booking-service` runs on port `8082`

To run individually:

```bash
cd flight-service
./gradlew bootRun
```

```bash
cd booking-service
./gradlew bootRun
```

---

## Sample JSON Data (flight-service/resources/data/flights.json)

If adding or modifying, make sure the data matches your model structure (includes fares, stopovers, times etc).

---

## Git Setup

- Each service has its own `.gitignore`
- `.idea/` folders and build outputs are ignored

---

## Notes

- Date is formatted as `yyyy-MM-dd`
- Time uses default `LocalTime` 24-hour format (e.g., `23:05:01`)
- Pricing supports multiple fare classes (economy, business) with currency code
