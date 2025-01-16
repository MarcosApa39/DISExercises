package es.ufv.dis.mock2024.mab;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {
    // Cambiar la ubicación del archivo JSON al directorio del backend
    private static final String BACKEND_DIRECTORY = "json_files"; // Nombre del directorio
    private static final String JSON_FILE = Paths.get(BACKEND_DIRECTORY, "vehicles.json").toString();
    private final Gson gson = new Gson();

    // Método para agregar un vehículo
    @PostMapping
    public ResponseEntity<String> addVehicle(@RequestBody Vehicle vehicle) {
        try {
            List<Vehicle> vehicles = readVehiclesFromFile();
            vehicles.add(vehicle);
            saveVehiclesToFile(vehicles);
            return ResponseEntity.status(HttpStatus.CREATED).body("Vehicle added successfully!");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving vehicle.");
        }
    }

    // Método para obtener todos los vehículos
    @GetMapping
    public ResponseEntity<List<Vehicle>> getVehicles() {
        try {
            List<Vehicle> vehicles = readVehiclesFromFile();
            return ResponseEntity.ok(vehicles);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Método para exportar vehículos a un archivo CSV
    @PostMapping("/export")
    public ResponseEntity<String> exportVehiclesToCsv() {
        try {
            List<Vehicle> vehicles = readVehiclesFromFile();
            Path exportPath = Paths.get(BACKEND_DIRECTORY, "exports", "fleet.csv");
            File file = exportPath.toFile();
            file.getParentFile().mkdirs();

            try (Writer writer = new FileWriter(file);
                 com.opencsv.CSVWriter csvWriter = new com.opencsv.CSVWriter(writer)) {

                csvWriter.writeNext(new String[]{"Make", "Model", "Year", "Type", "License Plate", "Available"});
                for (Vehicle vehicle : vehicles) {
                    csvWriter.writeNext(new String[]{
                            vehicle.getMake(),
                            vehicle.getModel(),
                            String.valueOf(vehicle.getYear()),
                            vehicle.getType(),
                            vehicle.getLicensePlate(),
                            String.valueOf(vehicle.isAvailable())
                    });
                }
            }

            return ResponseEntity.ok("Fleet data exported successfully!");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error exporting CSV.");
        }
    }

    // Leer vehículos desde el archivo JSON
    private List<Vehicle> readVehiclesFromFile() throws IOException {
        File file = Paths.get(JSON_FILE).toFile();
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<Vehicle>>() {}.getType();
            return gson.fromJson(reader, listType);
        }
    }

    // Guardar vehículos en el archivo JSON
    private void saveVehiclesToFile(List<Vehicle> vehicles) throws IOException {
        File file = Paths.get(JSON_FILE).toFile();
        file.getParentFile().mkdirs(); // Asegurarse de que el directorio existe
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(vehicles, writer);
        }
    }
}
