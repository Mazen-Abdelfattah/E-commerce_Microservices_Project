package com.mazen.ecommerce.inventory.product.dto;

import jakarta.validation.constraints.Min;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateProductRequest {

    private String name;
    private String description;

    @Min(0)
    private BigDecimal price;

    @Min(0)
    private Integer stockQuantity;
}
