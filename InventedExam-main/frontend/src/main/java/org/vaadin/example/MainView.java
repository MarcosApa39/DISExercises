package org.vaadin.example;

import java.util.List;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("")
public class MainView extends VerticalLayout {
    private final DataService dataService = new DataService();
    private final Grid<Player> grid = new Grid<>(Player.class);
    private final TextField nameField = new TextField("Name");
    private final TextField numberField = new TextField("Number");
    private final TextField valueField = new TextField("Value");
    private final TextField positionField = new TextField("Position");
    private final TextField matchesPlayedField = new TextField("Matches Played");
    private final TextField previousTeamsField = new TextField("Previous Teams");

    public MainView() {
        setSizeFull();
        configureGrid();
        loadPlayers();

        Button addButton = new Button("Add Player", e -> addPlayer());
        Button exportCsvButton = new Button("Export CSV", e -> exportCsv());
        Button teamValueButton = new Button("Show Team Value", e -> showTeamValue());

        HorizontalLayout formLayout = new HorizontalLayout(nameField, numberField, valueField, positionField, matchesPlayedField, previousTeamsField, addButton);
        formLayout.setWidthFull();

        add(grid, formLayout, exportCsvButton, teamValueButton);
    }

    private void configureGrid() {
        // Ensure the grid is properly configured with custom columns
        grid.removeAllColumns();

        // Add custom columns for player details
        grid.addColumn(Player::getName).setHeader("Name");
        grid.addColumn(Player::getNumber).setHeader("Number");
        grid.addColumn(Player::getValue).setHeader("Value");
        grid.addColumn(Player::getPosition).setHeader("Position");
        grid.addColumn(Player::getMatchesPlayedThisSeason).setHeader("Matches Played");
        grid.addColumn(player -> String.join(", ", player.getPreviousTeams())).setHeader("Previous Teams");

        // Add action buttons for each row
        grid.addComponentColumn(player -> {
            Button editButton = new Button("Edit", e -> editPlayer(player));
            Button deleteButton = new Button("Delete", e -> deletePlayer(player.getId()));
            Button pdfButton = new Button("Generate PDF", e -> generatePdf(player.getName()));
            return new HorizontalLayout(editButton, deleteButton, pdfButton);
        }).setHeader("Actions");
    }

    private void loadPlayers() {
        try {
            List<Player> players = dataService.getPlayers();
            grid.setItems(players);
        } catch (Exception e) {
            Notification.show("Failed to load players: " + e.getMessage());
        }
    }

    private void addPlayer() {
        try {
            Player player = new Player();
            player.setName(nameField.getValue());
            player.setNumber(Integer.parseInt(numberField.getValue()));
            player.setValue(Double.parseDouble(valueField.getValue()));
            player.setPosition(positionField.getValue());
            player.setMatchesPlayedThisSeason(Integer.parseInt(matchesPlayedField.getValue()));
            player.setPreviousTeams(List.of(previousTeamsField.getValue().split(",")));
            dataService.addPlayer(player);
            loadPlayers();
            Notification.show("Player added successfully!");
        } catch (Exception e) {
            Notification.show("Failed to add player: " + e.getMessage());
        }
    }

    private void editPlayer(Player player) {
        try {
            player.setName(nameField.getValue());
            player.setNumber(Integer.parseInt(numberField.getValue()));
            player.setValue(Double.parseDouble(valueField.getValue()));
            player.setPosition(positionField.getValue());
            player.setMatchesPlayedThisSeason(Integer.parseInt(matchesPlayedField.getValue()));
            player.setPreviousTeams(List.of(previousTeamsField.getValue().split(",")));
            dataService.updatePlayer(player.getId(), player);
            loadPlayers();
            Notification.show("Player updated successfully!");
        } catch (Exception e) {
            Notification.show("Failed to update player: " + e.getMessage());
        }
    }

    private void deletePlayer(String id) {
        try {
            dataService.deletePlayer(id);
            loadPlayers();
            Notification.show("Player deleted successfully!");
        } catch (Exception e) {
            Notification.show("Failed to delete player: " + e.getMessage());
        }
    }

    private void generatePdf(String name) {
        try {
            dataService.generatePdf(name);
            Notification.show("PDF generated successfully for " + name + "!");
        } catch (Exception e) {
            Notification.show("Failed to generate PDF for " + name + ": " + e.getMessage());
        }
    }

    private void exportCsv() {
        try {
            String message = dataService.exportCsv();
            Notification.show(message);
        } catch (Exception e) {
            Notification.show("Failed to export CSV: " + e.getMessage());
        }
    }

    private void showTeamValue() {
        try {
            double value = dataService.getTeamValue();
            Notification.show("Total Team Value: " + value);
        } catch (Exception e) {
            Notification.show("Failed to calculate team value: " + e.getMessage());
        }
    }
}
