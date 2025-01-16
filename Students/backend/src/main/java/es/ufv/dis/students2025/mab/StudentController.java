package es.ufv.dis.students2025.mab;

import java.io.*;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    private static final String JSON_FILE = "datos.json";
    private static final String EXPORTS_FOLDER = "exports/";
    private static final String STUDENTS_FOLDER = "students/";
    private static final String SUMMARY_FILE = STUDENTS_FOLDER + "summary.json";

    private final Gson gson = new Gson();
    private Map<String, Integer> summaryMap = new HashMap<>();

    public StudentController() {
        try {
            ensureFolderExists(getResourcePath(EXPORTS_FOLDER));
            ensureFolderExists(getResourcePath(STUDENTS_FOLDER));

            File summaryFile = new File(getResourcePath(SUMMARY_FILE));
            if (!summaryFile.exists()) {
                try (Writer writer = new FileWriter(summaryFile)) {
                    gson.toJson(summaryMap, writer);
                }
            } else {
                try (Reader reader = new FileReader(summaryFile)) {
                    summaryMap = gson.fromJson(reader, new TypeToken<Map<String, Integer>>() {}.getType());
                }
            }
        } catch (Exception e) {
            logger.error("Error initializing the controller", e);
            throw new RuntimeException("Initialization error", e);
        }
    }

    @GetMapping
    public List<Student> getAllStudents() {
        try (FileReader reader = new FileReader(getResourcePath(JSON_FILE))) {
            return gson.fromJson(reader, new TypeToken<List<Student>>() {}.getType());
        } catch (Exception e) {
            logger.error("Error reading students", e);
            throw new RuntimeException("Error reading students", e);
        }
    }

    @PostMapping("/generate-pdf")
    public String generatePdf(@RequestBody String studentName) {
        String cleanedName = studentName.trim().replace("\"", "");
        logger.info("Received request to generate PDF for '{}'", cleanedName);

        try (FileReader reader = new FileReader(getResourcePath(JSON_FILE))) {
            List<Student> students = gson.fromJson(reader, new TypeToken<List<Student>>() {}.getType());

            Student selectedStudent = students.stream()
                    .filter(s -> s.getName().equalsIgnoreCase(cleanedName))
                    .findFirst()
                    .orElseThrow(() -> {
                        logger.warn("Student not found: '{}'", cleanedName);
                        return new RuntimeException("Student not found");
                    });

            ensureFolderExists(getResourcePath(EXPORTS_FOLDER));

            String pdfPath = getResourcePath(EXPORTS_FOLDER) + cleanedName.replace(" ", "_") + ".pdf";
            File pdfFile = new File(pdfPath);

            try (Document document = new Document(PageSize.A4)) {
                PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
                document.open();
                document.add(new Paragraph("Student Details"));
                document.add(new Paragraph("Name: " + selectedStudent.getName()));
                document.add(new Paragraph("Enrollment ID: " + selectedStudent.getEnrollmentId()));
                document.add(new Paragraph("Career: " + selectedStudent.getCareer()));
                document.add(new Paragraph("GPA: " + selectedStudent.getGpa()));
                document.add(new Paragraph("Subjects: " + String.join(", ", selectedStudent.getSubjects())));
                document.close();
            }

            summaryMap.put(cleanedName, summaryMap.getOrDefault(cleanedName, 0) + 1);
            try (Writer writer = new FileWriter(getResourcePath(SUMMARY_FILE))) {
                gson.toJson(summaryMap, writer);
            }

            return "PDF generated successfully for " + cleanedName;
        } catch (Exception e) {
            logger.error("Error generating PDF for {}", cleanedName, e);
            return "Error generating PDF for " + cleanedName;
        }
    }

    private String getResourcePath(String relativePath) throws Exception {
        URL resource = getClass().getClassLoader().getResource(".");
        if (resource == null) {
            throw new RuntimeException("Resource path not found");
        }
        return Paths.get(resource.toURI()).resolve(relativePath).toString();
    }

    private void ensureFolderExists(String path) throws Exception {
        File folder = new File(path);
        if (!folder.exists() && !folder.mkdirs()) {
            throw new RuntimeException("Failed to create folder: " + path);
        }
    }
}
