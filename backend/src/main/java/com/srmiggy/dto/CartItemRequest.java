package com.srmiggy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequest {
    private UUID menuItemId;
    private Integer quantity;
}
