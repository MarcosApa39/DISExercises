package org.vaadin.example;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class DataService {
    private static final String BASE_URL = "http://localhost:8099/api/clothes";
    private final ObjectMapper objectMapper;

    public DataService() {
        this.objectMapper = new ObjectMapper();
    }

    public List<Clothe> getAllClothes() {
        try {
            URL url = new URL(BASE_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            List<Clothe> clothes = objectMapper.readValue(reader, new TypeReference<List<Clothe>>() {});
            reader.close();

            return clothes;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching clothes", e);
        }
    }

    public void addClothe(Clothe clothe) {
        try {
            URL url = new URL(BASE_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = objectMapper.writeValueAsString(clothe);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
                os.flush();
            }

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed to add clothe: " + conn.getResponseMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error adding clothe", e);
        }
    }

    public void updateClothe(Clothe clothe) {
        try {
            URL url = new URL(BASE_URL + "/" + clothe.getId());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = objectMapper.writeValueAsString(clothe);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
                os.flush();
            }

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed to update clothe: " + conn.getResponseMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error updating clothe", e);
        }
    }

    public void deleteClothe(String id) {
        try {
            URL url = new URL(BASE_URL + "/" + id);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Content-Type", "application/json");

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed to delete clothe: " + conn.getResponseMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error deleting clothe", e);
        }
    }

    public void resetCsv() {
        try {
            URL url = new URL(BASE_URL + "/reset-csv");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed to reset CSV: " + conn.getResponseMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error resetting CSV", e);
        }
    }

    public void exportToPdf() {
        try {
            URL url = new URL(BASE_URL + "/export-pdf");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed to export PDF: " + conn.getResponseMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error exporting PDF", e);
        }
    }
}
