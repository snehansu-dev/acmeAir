# Acme Air Flight Booking System (Microservices)

This project is a microservice-based Flight Booking System built with Spring Boot using two independent services:

- `flight-service` — Manages flight information.
- `booking-service` — Handles bookings for available flights.

---

## Technologies Used

- Java 21
- Spring Boot 3
- Gradle 8.4
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

### Flight Service  (For test, used Port 8081)

- `GET /api/flights/search?origin=Auckland&destination=Christchurch` — Search all the flights by route.
- `GET /api/flights/search?origin=Auckland&destination=Christchurch&direct=false` — Search flights by route and optionally filter for direct only.
- `GET /api/flights/{id}` — Get flight details by ID

### Booking Service  (For test, used Port 8082)

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
## Build and Run the application
Run in root folder

```bash
./gradlew clean build

## Running the Services

Ensure ports don't conflict:

- `flight-service` runs on port `8081`
- `booking-service` runs on port `8082`

**Run both the services in 2 terminals inside root folder:**

```bash
.\gradlew :flight-service:bootRun
```

```bash
.\gradlew :booking-service:bootRun
```

## Data - JSON Data (flight-service/resources/data/flights.json)

If adding or modifying, make sure the data matches your model structure (includes fares, stopovers, times etc).

---

## Git Setup

-  `.gitignore` is placed at root level
- `.idea/` folders and build outputs are ignored

---

## Notes

- Date is formatted as `yyyy-MM-dd`
- Time uses default `LocalTime` 24-hour format (e.g., `23:05:01`)
- Pricing supports multiple fare classes (economy, business) with currency code

## Future Improvements
- For now Caffeine is used as Cache, but once containterized Redis distruted cache will be used
- Docker will implemented
- Per service Database will be implemented
- Notification service will be implemented to send booking confirmation
- Payment Service will be integrated, till then booking status would remain "PENDING_PAYMENT"
- Saga pattern will be implemented for compensation transaction
- Opentelemetry Observability will implemented for tracing
- Elastic search will be implemented for distributed logging
- CorrelationId will be used for tracing and would be added to logging
- Some addon to booking service will be done
- Identity service will be implemented


