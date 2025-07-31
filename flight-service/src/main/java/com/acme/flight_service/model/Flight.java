package com.acme.flight_service.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Flight {
    private String id;
    private String origin;
    private String destination;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate departureDate;
    private LocalTime departureTime;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate arrivalDate;
    private LocalTime arrivalTime;

    private List<String> stopovers;

    private List<FareClass> fares;

    public boolean isDirect() {
        return stopovers == null || stopovers.isEmpty();
    }


}
