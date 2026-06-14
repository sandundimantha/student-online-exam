package com.exam.controller;

import com.exam.dto.*;
import com.exam.security.CustomUserDetails;
import com.exam.service.StudentService;
import com.exam.util.AuditLogger;
import com.exam.util.PDFGenerator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    private final StudentService studentService;
    private final PDFGenerator pdfGenerator;
    private final AuditLogger auditLogger;

    public StudentController(StudentService studentService, PDFGenerator pdfGenerator, AuditLogger auditLogger) {
        this.studentService = studentService;
        this.pdfGenerator = pdfGenerator;
        this.auditLogger = auditLogger;
    }

    private CustomUserDetails getPrincipal() {
        return (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private Long getStudentId() {
        return getPrincipal().getUser().getId();
    }

    private String getStudentEmail() {
        return getPrincipal().getUsername();
    }

    @GetMapping("/exams")
    public ResponseEntity<List<ExamDto>> getAvailableExams() {
        return ResponseEntity.ok(studentService.getAvailableExams(getStudentId()));
    }

    @PostMapping("/exams/{examId}/start")
    public ResponseEntity<StartAttemptResponse> startAttempt(@PathVariable Long examId) {
        StartAttemptResponse response = studentService.startAttempt(getStudentId(), examId);
        auditLogger.log("START_EXAM", getStudentEmail(), "Started Attempt ID: " + response.attemptId() + " for Exam ID: " + examId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/exams/attempts/{attemptId}/save")
    public ResponseEntity<Void> saveAnswer(
            @PathVariable Long attemptId,
            @Valid @RequestBody SaveAnswerRequest request
    ) {
        studentService.saveAnswer(getStudentId(), attemptId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/exams/attempts/{attemptId}/submit")
    public ResponseEntity<AttemptResultDto> submitAttempt(@PathVariable Long attemptId) {
        AttemptResultDto response = studentService.submitAttempt(getStudentId(), attemptId);
        auditLogger.log("SUBMIT_EXAM", getStudentEmail(), "Submitted Attempt ID: " + attemptId + " | Score: " + response.score());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/attempts")
    public ResponseEntity<List<AttemptResultDto>> getAttemptHistory() {
        return ResponseEntity.ok(studentService.getAttemptHistory(getStudentId()));
    }

    @GetMapping("/attempts/{attemptId}")
    public ResponseEntity<AttemptResultDto> getAttemptResult(@PathVariable Long attemptId) {
        return ResponseEntity.ok(studentService.getAttemptResult(getStudentId(), attemptId));
    }

    @GetMapping("/attempts/{attemptId}/pdf")
    public ResponseEntity<byte[]> downloadResultPDF(@PathVariable Long attemptId) throws IOException {
        AttemptResultDto result = studentService.getAttemptResult(getStudentId(), attemptId);
        byte[] pdfBytes = pdfGenerator.generateResultPDF(result);

        auditLogger.log("DOWNLOAD_PDF", getStudentEmail(), "Downloaded PDF result card for Attempt ID: " + attemptId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=exam_report_" + attemptId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
