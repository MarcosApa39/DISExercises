package org.vaadin.example;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route
public class MainView extends VerticalLayout {
    private final DataService dataService;
    private final Grid<Vehicle> vehicleGrid;

    public MainView() {
        this.dataService = new DataService();
        this.vehicleGrid = new Grid<>(Vehicle.class);

        // Form fields
        TextField makeField = new TextField("Make");
        TextField modelField = new TextField("Model");
        TextField yearField = new TextField("Year");
        TextField typeField = new TextField("Type");
        TextField licensePlateField = new TextField("License Plate");
        Checkbox availableCheckbox = new Checkbox("Available");

        // Buttons
        Button addButton = new Button("Add Vehicle", event -> {
            Vehicle vehicle = new Vehicle(
                    makeField.getValue(),
                    modelField.getValue(),
                    Integer.parseInt(yearField.getValue()),
                    typeField.getValue(),
                    licensePlateField.getValue(),
                    availableCheckbox.getValue(),
                    null // ID will be auto-generated
            );
            dataService.addVehicle(vehicle);
            Notification.show("Vehicle added successfully!");
            refreshGrid();
        });

        Button exportButton = new Button("Export to CSV", event -> {
            dataService.exportVehicles();
            Notification.show("Vehicles exported successfully!");
        });

        // Grid configuration
        vehicleGrid.setColumns("make", "model", "year", "type", "licensePlate", "available");
        refreshGrid();

        // Layout
        add(makeField, modelField, yearField, typeField, licensePlateField, availableCheckbox, addButton, exportButton, vehicleGrid);
    }

    private void refreshGrid() {
        vehicleGrid.setItems(dataService.getAllVehicles());
    }
}
