package com.srmiggy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    private Long vendorId;
    private Long slotId;
    private String deliveryAddress;
    private String customerPhone;
    private List<CartItemRequest> items;
    private Boolean useLoyaltyPoints = false;
}
