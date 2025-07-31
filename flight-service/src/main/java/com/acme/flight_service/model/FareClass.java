package com.acme.flight_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class FareClass {
    private String travelClass; // e.g., "Economy", "Business"
    private BigDecimal price;
    private String currency;    // e.g., "NZD", "USD"
}
