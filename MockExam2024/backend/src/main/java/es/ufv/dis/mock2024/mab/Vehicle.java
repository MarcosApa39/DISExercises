package es.ufv.dis.mock2024.mab;

import java.util.UUID;

public class Vehicle {
    private String make;
    private String model;
    private int year;
    private String type;
    private String licensePlate;
    private boolean available;
    private final String id;

    public Vehicle() {
        // Genera un UUID único para cada vehículo
        this.id = UUID.randomUUID().toString();
    }

    public Vehicle(String make, String model, int year, String type, String licensePlate, boolean available) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.type = type;
        this.licensePlate = licensePlate;
        this.available = available;
        this.id = UUID.randomUUID().toString();
    }

    // Getters y setters
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
}

