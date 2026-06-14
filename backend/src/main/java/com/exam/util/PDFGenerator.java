package com.exam.util;

import com.exam.dto.AttemptResultDto;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Component;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Component
public class PDFGenerator {

    public byte[] generateResultPDF(AttemptResultDto result) throws IOException {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Main Header
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 24);
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("Online Examination Management System");
                contentStream.endText();

                // Horizontal Line
                contentStream.moveTo(50, 730);
                contentStream.lineTo(550, 730);
                contentStream.stroke();

                // Exam Title
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16);
                contentStream.newLineAtOffset(50, 700);
                contentStream.showText("EXAM REPORT CARD");
                contentStream.endText();

                // Content Block
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                contentStream.newLineAtOffset(50, 660);
                contentStream.setLeading(18f);

                contentStream.showText("Student Name:    " + result.studentName());
                contentStream.newLine();
                contentStream.showText("Student Email:   " + result.studentEmail());
                contentStream.newLine();
                contentStream.showText("Exam Title:      " + result.examTitle());
                contentStream.newLine();
                contentStream.showText("Started At:      " + result.startedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                contentStream.newLine();
                contentStream.showText("Submitted At:    " + (result.submittedAt() != null ? result.submittedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "N/A"));
                contentStream.newLine();
                
                contentStream.endText();

                // Score Card Block (Box)
                contentStream.setNonStrokingColor(0.95f, 0.95f, 0.95f);
                contentStream.addRect(50, 420, 500, 120);
                contentStream.fill();
                contentStream.setNonStrokingColor(0f, 0f, 0f);

                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
                contentStream.newLineAtOffset(70, 500);
                contentStream.setLeading(22f);
                contentStream.showText("Score Obtained:  " + String.format("%.2f", result.score()) + " / " + String.format("%.2f", result.totalMarks()));
                contentStream.newLine();
                contentStream.showText("Passing Marks:   " + String.format("%.2f", result.passingScore()));
                contentStream.newLine();
                
                String resultStatus = result.passed() ? "PASSED" : "FAILED";
                contentStream.showText("Exam Status:     " + resultStatus);
                contentStream.endText();

                // Footer
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE), 10);
                contentStream.newLineAtOffset(50, 100);
                contentStream.showText("Generated automatically by the Online Examination Management System. All Rights Reserved.");
                contentStream.endText();
            }

            document.save(out);
            return out.toByteArray();
        }
    }
}
