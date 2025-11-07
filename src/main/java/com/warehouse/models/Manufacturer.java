package com.warehouse.models;

import lombok.Data;

@Data
public class Manufacturer {
    private Long id;
    private String name;
    private String country;
    private String contactPhone;
}