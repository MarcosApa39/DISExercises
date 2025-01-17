package es.ufv.dis.Clothes2025.mab;


import java.util.UUID;

public class Clothe {
    private String id;
    private String type;
    private double price;
    private String size;
    private int availability;

    public Clothe() {
        this.id = UUID.randomUUID().toString();
    }

    public Clothe(String type, double price, String size, int availability) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.price = price;
        this.size = size;
        this.availability = availability;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }
}

