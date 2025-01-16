package es.ufv.dis.mock2024.mab;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VehicleControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final Path JSON_FILE_PATH = Paths.get("json_files", "vehicles.json");
    private static final Path CSV_FILE_PATH = Paths.get("json_files", "exports", "fleet.csv");

    @Test
    void testAddVehicle() {
        // Crear un vehículo
        Vehicle vehicle = new Vehicle("Toyota", "Corolla", 2022, "Sedan", "ABC-1234", true);

        // Enviar el vehículo al endpoint POST /vehicles
        ResponseEntity<String> response = restTemplate.postForEntity("/vehicles", vehicle, String.class);

        // Verificar que la respuesta es exitosa
        assertEquals(201, response.getStatusCode().value());
        assertEquals("Vehicle added successfully!", response.getBody());

        // Verificar que el archivo JSON contiene el vehículo
        assertTrue(Files.exists(JSON_FILE_PATH), "El archivo JSON no fue creado");
    }

    @Test
    void testGetVehicles() {
        // Crear y enviar un vehículo
        Vehicle vehicle = new Vehicle("Honda", "Civic", 2020, "Sedan", "XYZ-5678", false);
        restTemplate.postForEntity("/vehicles", vehicle, String.class);

        // Obtener la lista de vehículos desde el endpoint GET /vehicles
        ResponseEntity<Vehicle[]> response = restTemplate.getForEntity("/vehicles", Vehicle[].class);

        // Verificar que la respuesta es exitosa
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());

        // Verificar que contiene al menos un vehículo
        assertTrue(response.getBody().length > 0, "La lista de vehículos no debería estar vacía");

        // Verificar que el vehículo está presente
        Vehicle retrievedVehicle = response.getBody()[0];
        assertEquals(vehicle.getModel(), retrievedVehicle.getModel());
        assertEquals(vehicle.getLicensePlate(), retrievedVehicle.getLicensePlate());
    }

    @Test
    void testExportVehiclesToCsv() {
        // Crear y enviar vehículos
        restTemplate.postForEntity("/vehicles", new Vehicle("Ford", "Focus", 2019, "Hatchback", "FOC-3456", true), String.class);
        restTemplate.postForEntity("/vehicles", new Vehicle("Chevrolet", "Malibu", 2021, "Sedan", "CHE-7890", false), String.class);

        // Exportar vehículos a CSV
        ResponseEntity<String> response = restTemplate.postForEntity("/vehicles/export", null, String.class);

        // Verificar que la respuesta es exitosa
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Fleet data exported successfully!", response.getBody());

        // Verificar que el archivo CSV existe
        assertTrue(Files.exists(CSV_FILE_PATH), "El archivo CSV no fue creado");

        // Verificar contenido del archivo CSV
        try {
            List<String> lines = Files.readAllLines(CSV_FILE_PATH);
            assertEquals(3, lines.size()); // 1 encabezado + 2 filas de datos
            assertTrue(lines.get(1).contains("Ford"));
            assertTrue(lines.get(2).contains("Chevrolet"));
        } catch (Exception e) {
            throw new RuntimeException("Error al leer el archivo CSV: " + e.getMessage());
        }
    }
}

