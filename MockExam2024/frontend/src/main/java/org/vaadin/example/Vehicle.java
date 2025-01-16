package org.vaadin.example;


public class Vehicle {
    private String make;
    private String model;
    private int year;
    private String type;
    private String licensePlate;
    private boolean available;
    private String id;

    // Default constructor
    public Vehicle() {}

    // Constructor with fields
    public Vehicle(String make, String model, int year, String type, String licensePlate, boolean available, String id) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.type = type;
        this.licensePlate = licensePlate;
        this.available = available;
        this.id = id;
    }

    // Getters and setters
    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
