package org.vaadin.example;

import java.util.List;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("")
public class MainView extends VerticalLayout {
    private final DataService dataService;
    private final Grid<Clothe> grid;
    private final TextField typeField;
    private final NumberField priceField;
    private final TextField sizeField;
    private final NumberField availabilityField;

    public MainView() {
        this.dataService = new DataService();
        this.grid = new Grid<>(Clothe.class);

        this.typeField = new TextField("Type");
        this.priceField = new NumberField("Price");
        this.sizeField = new TextField("Size");
        this.availabilityField = new NumberField("Availability");

        setupGrid();
        setupForm();

        add(grid, new HorizontalLayout(typeField, priceField, sizeField, availabilityField, createAddButton()));
    }

    private void setupGrid() {
        grid.setColumns("id", "type", "price", "size", "availability");
        grid.setItems(fetchClothes());

        grid.addComponentColumn(item -> {
            Button resetCsvButton = new Button("Reset CSV", event -> resetCsv());
            Button exportPdfButton = new Button("Export PDF", event -> exportToPdf());
            return new HorizontalLayout(resetCsvButton, exportPdfButton);
        });

        grid.addItemClickListener(event -> {
            Clothe clothe = event.getItem();
            typeField.setValue(clothe.getType());
            priceField.setValue(clothe.getPrice());
            sizeField.setValue(clothe.getSize());
            availabilityField.setValue((double) clothe.getAvailability());
        });
    }

    private void setupForm() {
        add(new Div(new Button("Update", e -> updateClothe())));
        add(new Div(new Button("Delete", e -> deleteClothe())));
    }

    private List<Clothe> fetchClothes() {
        return dataService.getAllClothes();
    }

    private void updateClothe() {
        Clothe selected = grid.asSingleSelect().getValue();
        if (selected != null) {
            selected.setType(typeField.getValue());
            selected.setPrice(priceField.getValue());
            selected.setSize(sizeField.getValue());
            selected.setAvailability(availabilityField.getValue().intValue());
            dataService.updateClothe(selected);
            refreshGrid();
            Notification.show("Clothe updated!");
        }
    }

    private void deleteClothe() {
        Clothe selected = grid.asSingleSelect().getValue();
        if (selected != null) {
            dataService.deleteClothe(selected.getId());
            refreshGrid();
            Notification.show("Clothe deleted!");
        }
    }

    private void resetCsv() {
        dataService.resetCsv();
        Notification.show("CSV reset successfully!");
    }

    private void exportToPdf() {
        dataService.exportToPdf();
        Notification.show("PDF exported successfully!");
    }

    private void refreshGrid() {
        grid.setItems(fetchClothes());
    }

    private Button createAddButton() {
        return new Button("Add Clothe", event -> {
            Clothe clothe = new Clothe();
            clothe.setType(typeField.getValue());
            clothe.setPrice(priceField.getValue());
            clothe.setSize(sizeField.getValue());
            clothe.setAvailability(availabilityField.getValue().intValue());
            dataService.addClothe(clothe);
            refreshGrid();
            Notification.show("Clothe added!");
        });
    }
}
