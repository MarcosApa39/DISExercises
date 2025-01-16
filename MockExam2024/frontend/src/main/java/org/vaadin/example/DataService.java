package org.vaadin.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class DataService {
    private static final String BASE_URL = "http://localhost:8081/vehicles"; // Cambiado a puerto 8081
    private final Gson gson;
    private static final Logger logger = Logger.getLogger(DataService.class.getName());

    public DataService() {
        this.gson = new Gson();
    }

    public List<Vehicle> getAllVehicles() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(BASE_URL).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (InputStream inputStream = connection.getInputStream();
                     Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

                    Type listType = new TypeToken<List<Vehicle>>() {}.getType();
                    return gson.fromJson(reader, listType);
                }
            } else {
                logger.log(Level.SEVERE, "Failed to fetch vehicles. HTTP error code: {0}", connection.getResponseCode());
                throw new IOException("HTTP error code: " + connection.getResponseCode());
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error retrieving vehicles: {0}", e.getMessage());
            throw new RuntimeException("Error retrieving vehicles: " + e.getMessage(), e);
        }
    }

    public void addVehicle(Vehicle vehicle) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(BASE_URL).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream outputStream = connection.getOutputStream();
                 Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
                gson.toJson(vehicle, writer);
            }

            if (connection.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                logger.log(Level.SEVERE, "Failed to add vehicle. HTTP error code: {0}", connection.getResponseCode());
                throw new IOException("HTTP error code: " + connection.getResponseCode());
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error adding vehicle: {0}", e.getMessage());
            throw new RuntimeException("Error adding vehicle: " + e.getMessage(), e);
        }
    }

    public void exportVehicles() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(BASE_URL + "/export").openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                logger.log(Level.SEVERE, "Failed to export vehicles. HTTP error code: {0}", connection.getResponseCode());
                throw new IOException("HTTP error code: " + connection.getResponseCode());
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error exporting vehicles: {0}", e.getMessage());
            throw new RuntimeException("Error exporting vehicles: " + e.getMessage(), e);
        }
    }
}
