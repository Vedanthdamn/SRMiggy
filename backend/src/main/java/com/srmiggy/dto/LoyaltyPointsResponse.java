package com.srmiggy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyPointsResponse {
    private Double currentPoints;
    private Double pointsEarnedFromOrder;
}
