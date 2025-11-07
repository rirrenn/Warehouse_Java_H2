package com.warehouse.models;

import lombok.Data;

@Data
public class Customer {
    private Long id;
    private String name;
    private String phone;
    private double discount;  // В процентах (например, 5 для 5%)
}