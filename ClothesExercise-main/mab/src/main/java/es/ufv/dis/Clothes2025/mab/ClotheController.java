package es.ufv.dis.Clothes2025.mab;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
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
@RequestMapping("/api/clothes")
public class ClotheController {

    private static final Logger logger = LoggerFactory.getLogger(ClotheController.class);

    private static final String BASE_PATH = System.getProperty("user.dir") + "/mab/src/main/resources/";
    private static final String JSON_PATH = BASE_PATH + "shop.json";
    private static final String EXPORTS_FOLDER = BASE_PATH + "exports/";
    private static final String CSV_PATH = EXPORTS_FOLDER + "clothes.csv";

    private List<Clothe> clothes = new ArrayList<>();

    public ClotheController() {
        new File(EXPORTS_FOLDER).mkdirs();

        File jsonFile = new File(JSON_PATH);
        if (jsonFile.exists()) {
            try (Reader reader = new FileReader(JSON_PATH)) {
                clothes = new Gson().fromJson(reader, new TypeToken<List<Clothe>>() {}.getType());
            } catch (IOException e) {
                logger.error("Error reading clothes from JSON", e);
            }
        } else {
            initializeJsonFile();
        }
    }

    @GetMapping
    public List<Clothe> getAllClothes() {
        return clothes;
    }

    @PostMapping
    public String addClothe(@RequestBody Clothe clothe) {
        clothe.setId(UUID.randomUUID().toString());
        clothes.add(clothe);
        saveClothesToFile();
        return "Clothe added successfully.";
    }

    @PutMapping("/{id}")
    public String updateClothe(@PathVariable String id, @RequestBody Clothe updatedClothe) {
        for (Clothe clothe : clothes) {
            if (clothe.getId().equals(id)) {
                clothe.setType(updatedClothe.getType());
                clothe.setPrice(updatedClothe.getPrice());
                clothe.setSize(updatedClothe.getSize());
                clothe.setAvailability(updatedClothe.getAvailability());
                saveClothesToFile();
                return "Clothe updated successfully.";
            }
        }
        return "Clothe not found.";
    }

    @DeleteMapping("/{id}")
    public String deleteClothe(@PathVariable String id) {
        boolean removed = clothes.removeIf(clothe -> clothe.getId().equals(id));
        if (removed) {
            saveClothesToFile();
            return "Clothe deleted successfully.";
        } else {
            return "Clothe not found.";
        }
    }

    @GetMapping("/reset-csv")
    public String resetCsv() {
        File csvFile = new File(CSV_PATH);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            writer.write("ID,Type,Price,Size,Availability\n");
            for (Clothe clothe : clothes) {
                writer.write(String.format("%s,%s,%.2f,%s,%d\n",
                        clothe.getId(),
                        clothe.getType(),
                        clothe.getPrice(),
                        clothe.getSize(),
                        clothe.getAvailability()));
            }
        } catch (IOException e) {
            logger.error("Error resetting CSV file", e);
            return "Error resetting CSV.";
        }
        return "CSV reset successfully.";
    }

    @GetMapping("/export-pdf")
    public String exportToPdf() {
        File pdfFile = new File(EXPORTS_FOLDER, "clothes.pdf");
        try (Document document = new Document(PageSize.A4)) {
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();
            document.add(new Paragraph("Clothes Data"));
            for (Clothe clothe : clothes) {
                document.add(new Paragraph(String.format("ID: %s, Type: %s, Price: %.2f, Size: %s, Availability: %d",
                        clothe.getId(),
                        clothe.getType(),
                        clothe.getPrice(),
                        clothe.getSize(),
                        clothe.getAvailability())));
            }
            document.close();
        } catch (Exception e) {
            logger.error("Error exporting clothes to PDF", e);
            return "Error exporting PDF.";
        }
        return "PDF exported successfully.";
    }

    private void saveClothesToFile() {
        try (Writer writer = new FileWriter(JSON_PATH)) {
            new Gson().toJson(clothes, writer);
        } catch (IOException e) {
            logger.error("Error saving clothes to JSON", e);
        }
    }

    private void initializeJsonFile() {
        try (Writer writer = new FileWriter(JSON_PATH)) {
            new Gson().toJson(new ArrayList<>(), writer);
        } catch (IOException e) {
            logger.error("Error initializing shop.json", e);
        }
    }
}

