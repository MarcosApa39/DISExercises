package es.ufv.dis.invent2025.mab;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private static final Logger logger = LoggerFactory.getLogger(PlayerController.class);

    private static final String RESOURCES_FOLDER = "backend/src/main/resources/";
    private static final String JSON_PATH = RESOURCES_FOLDER + "datos.json";
    private static final String EXPORTS_FOLDER = RESOURCES_FOLDER + "exports/";
    private static final String SUMMARY_FILE = EXPORTS_FOLDER + "pdf_summary.json";

    private Map<String, Integer> pdfSummary = new HashMap<>();
    private List<Player> players = new ArrayList<>();

    public PlayerController() {
        // Ensure the exports directory exists
        new File(EXPORTS_FOLDER).mkdirs();

        // Load players from JSON file
        File jsonFile = new File(JSON_PATH);
        if (jsonFile.exists()) {
            try (Reader reader = new FileReader(JSON_PATH)) {
                players = new Gson().fromJson(reader, new TypeToken<List<Player>>() {}.getType());
            } catch (IOException e) {
                logger.error("Error reading players from JSON", e);
            }
        }

        // Load or initialize PDF summary
        File summaryFile = new File(SUMMARY_FILE);
        if (summaryFile.exists()) {
            try (Reader reader = new FileReader(SUMMARY_FILE)) {
                pdfSummary = new Gson().fromJson(reader, new TypeToken<Map<String, Integer>>() {}.getType());
            } catch (IOException e) {
                logger.error("Error reading summary.json", e);
            }
        }
    }

    @GetMapping
    public List<Player> getAllPlayers() {
        return players;
    }

    @PostMapping
    public String addPlayer(@RequestBody Player player) {
        player.setId(UUID.randomUUID().toString());
        players.add(player);
        savePlayersToFile();
        return "Player added successfully.";
    }

    @PutMapping("/{id}")
    public String updatePlayer(@PathVariable String id, @RequestBody Player updatedPlayer) {
        for (Player player : players) {
            if (player.getId().equals(id)) {
                player.setName(updatedPlayer.getName());
                player.setNumber(updatedPlayer.getNumber());
                player.setValue(updatedPlayer.getValue());
                player.setPosition(updatedPlayer.getPosition());
                player.setMatchesPlayedThisSeason(updatedPlayer.getMatchesPlayedThisSeason());
                player.setPreviousTeams(updatedPlayer.getPreviousTeams());
                savePlayersToFile();
                return "Player updated successfully.";
            }
        }
        return "Player not found.";
    }

    @DeleteMapping("/{id}")
    public String deletePlayer(@PathVariable String id) {
        boolean removed = players.removeIf(player -> player.getId().equals(id));
        if (removed) {
            savePlayersToFile();
            return "Player deleted successfully.";
        } else {
            return "Player not found.";
        }
    }

    @GetMapping("/team-value")
    public Map<String, Double> getTeamValue() {
        double totalValue = players.stream()
                .mapToDouble(Player::getValue)
                .sum();
        return Collections.singletonMap("teamValue", totalValue);
    }

    @PostMapping("/generate-pdf")
    public String generatePdf(@RequestBody String playerName) {
        String cleanedName = playerName.trim().replace("\"", "");
        logger.info("Generating PDF for '{}'", cleanedName);

        // Find player
        Player player = players.stream()
                .filter(p -> p.getName().equalsIgnoreCase(cleanedName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Player not found"));

        // Generate PDF
        File pdfFile = new File(EXPORTS_FOLDER, cleanedName + ".pdf");
        try (Document document = new Document(PageSize.A4)) {
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();
            document.add(new Paragraph("Player Details"));
            document.add(new Paragraph("Name: " + player.getName()));
            document.add(new Paragraph("Number: " + player.getNumber()));
            document.add(new Paragraph("Value: " + player.getValue()));
            document.add(new Paragraph("Position: " + player.getPosition()));
            document.add(new Paragraph("Matches Played: " + player.getMatchesPlayedThisSeason()));
            document.add(new Paragraph("Previous Teams: " + String.join(", ", player.getPreviousTeams())));
            document.close();
        } catch (Exception e) {
            logger.error("Error generating PDF for '{}'", cleanedName, e);
            return "Error generating PDF.";
        }

        // Update summary
        pdfSummary.put(cleanedName, pdfSummary.getOrDefault(cleanedName, 0) + 1);
        saveSummaryToFile();

        return "PDF generated successfully for " + cleanedName;
    }

    @GetMapping("/export-csv")
    public String exportToCsv() {
        File csvFile = new File(EXPORTS_FOLDER, "players.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            writer.write("ID,Name,Number,Value,Position,MatchesPlayed,PreviousTeams\n");
            for (Player player : players) {
                writer.write(String.format("%s,%s,%d,%.2f,%s,%d,%s\n",
                        player.getId(),
                        player.getName(),
                        player.getNumber(),
                        player.getValue(),
                        player.getPosition(),
                        player.getMatchesPlayedThisSeason(),
                        String.join(";", player.getPreviousTeams())));
            }
        } catch (IOException e) {
            logger.error("Error exporting players to CSV", e);
            return "Error exporting CSV.";
        }
        return "CSV exported successfully.";
        //return "CSV exported successfully to " + csvFile.getAbsolutePath();
    }

    private void savePlayersToFile() {
        try (Writer writer = new FileWriter(JSON_PATH)) {
            new Gson().toJson(players, writer);
        } catch (IOException e) {
            logger.error("Error saving players to JSON", e);
        }
    }

    private void saveSummaryToFile() {
        try (Writer writer = new FileWriter(SUMMARY_FILE)) {
            new Gson().toJson(pdfSummary, writer);
        } catch (IOException e) {
            logger.error("Error saving summary to JSON", e);
        }
    }
}
