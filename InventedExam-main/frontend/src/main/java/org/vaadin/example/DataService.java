package org.vaadin.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class DataService {
    private static final String BASE_URL = "http://localhost:8089/api/players";

    public List<Player> getPlayers() throws Exception {
        URL url = new URL(BASE_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(connection.getInputStream(), new TypeReference<List<Player>>() {});
    }

    public void addPlayer(Player player) throws Exception {
        sendRequestWithBody("POST", BASE_URL, player);
    }

    public void updatePlayer(String id, Player player) throws Exception {
        sendRequestWithBody("PUT", BASE_URL + "/" + id, player);
    }

    public void deletePlayer(String id) throws Exception {
        URL url = new URL(BASE_URL + "/" + id);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.getResponseCode();
    }

    public void generatePdf(String playerName) throws Exception {
        sendRequestWithBody("POST", BASE_URL + "/generate-pdf", playerName);
    }

    public String exportCsv() throws Exception {
        URL url = new URL(BASE_URL + "/export-csv");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        return connection.getResponseMessage();
    }

    public double getTeamValue() throws Exception {
        URL url = new URL(BASE_URL + "/team-value");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Double> response = mapper.readValue(connection.getInputStream(), new TypeReference<Map<String, Double>>() {});
        return response.get("teamValue");
    }

    private void sendRequestWithBody(String method, String url, Object body) throws Exception {
        URL endpoint = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(connection.getOutputStream(), body);
        connection.getResponseCode();
    }
}
