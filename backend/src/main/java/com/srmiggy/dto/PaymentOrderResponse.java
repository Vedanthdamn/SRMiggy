package com.srmiggy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrderResponse {
    private String providerOrderId;
    private Double amount;
    private String provider;
    private Long orderId;
}
