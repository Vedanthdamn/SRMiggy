package com.srmiggy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    private UUID vendorId;
    private UUID slotId;
    private String deliveryAddress;
    private String customerPhone;
    private List<CartItemRequest> items;
    private Boolean useLoyaltyPoints = false;
}
