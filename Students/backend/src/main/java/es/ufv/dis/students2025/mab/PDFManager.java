package es.ufv.dis.students2025.mab;


import java.io.FileOutputStream;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

public class PDFManager {

    public static void createStudentPdf(Student student, String filePath) throws Exception {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            document.add(new Paragraph("Student Information"));
            document.add(new Paragraph("----------------------------"));
            document.add(new Paragraph("Name: " + student.getName()));
            document.add(new Paragraph("Enrollment ID: " + student.getEnrollmentId()));
            document.add(new Paragraph("Career: " + student.getCareer()));
            document.add(new Paragraph("GPA: " + student.getGpa()));
            document.add(new Paragraph("Subjects: " + String.join(", ", student.getSubjects())));

        } finally {
            document.close();
        }
    }
}

