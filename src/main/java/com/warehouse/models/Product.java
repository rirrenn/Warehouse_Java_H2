package com.warehouse.models;

import lombok.Data;

@Data
public class Product {
    private Long id;
    private String name;
    private double sellingPrice;  // Цена продажи
    private double purchasePrice; // Цена закупки
    private int quantity;
    private String expiryDate;
    private Long manufacturerId;
    private Long supplierId;
}