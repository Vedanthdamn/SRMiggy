package com.srmiggy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentVerifyRequest {
    private String providerOrderId;
    private String providerPaymentId;
    private String providerSignature;
}
