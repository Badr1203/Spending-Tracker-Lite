package com.example.spendingtrackerlite.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DecimalFormat;

public class Product {
    private long id; // Database ID, useful for updates/deletes
    private String barcode;
    private int variant;
    private  String category;
    private String type;
    private String brand;
    private String title;
    private String unit;
    private double quantity;
    private Double percentage; // Can be null
    private String manufacturer; // Can be null
    private String country; // Can be null

    // Constructors
    public Product(long id, String barcode, int variant, String category, String type, String brand, String title,
                   String unit, double quantity, @Nullable Double percentage,
                   @Nullable String manufacturer, @Nullable String country) {
        this.id = id;
        this.barcode = barcode;
        this.variant = variant;
        this.category = category;
        this.type = type;
        this.brand = brand;
        this.title = title;
        this.unit = unit;
        this.quantity = quantity;
        this.percentage = percentage;
        this.manufacturer = manufacturer;
        this.country = country;
    }

    // Getters
    public long getId() {
        return id;
    }

    public String getBarcode() {
        return barcode;
    }

    public int getVariant() {
        return variant;
    }

    public String getCategory() {
        return category;
    }

    public String getType() {
        return type;
    }

    public String getBrand() {
        return brand;
    }

    public String getTitle() {
        return title;
    }

    public String getUnit() {
        return unit;
    }

    public double getQuantity() {
        return quantity;
    }

    @Nullable
    public Double getPercentage() {
        return percentage;
    }

    @Nullable
    public String getManufacturer() {
        return manufacturer;
    }

    @Nullable
    public String getCountry() {
        return country;
    }

    @NonNull
    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("#.##");
        String info = barcode + " " + category + " " + type+ " " +
                brand;
        if (!title.isEmpty()) info += " " + title;
        info += " " + df.format(quantity) + unit;
        if (percentage > 0) info += " " + df.format(percentage) + " " +
                manufacturer + " " + country;
        return info;
    }
}
