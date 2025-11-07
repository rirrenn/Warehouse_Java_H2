package com.warehouse.models;

import lombok.Data;

@Data
public class Supplier {
    private Long id;
    private String name;
    private String address;
    private String email;
    private Long manufacturerId; // Просто ID производителя
}