package com.exam.controller;

import com.exam.dto.*;
import com.exam.security.CustomUserDetails;
import com.exam.service.LecturerService;
import com.exam.util.AuditLogger;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/lecturer")
public class LecturerController {
    private final LecturerService lecturerService;
    private final AuditLogger auditLogger;

    public LecturerController(LecturerService lecturerService, AuditLogger auditLogger) {
        this.lecturerService = lecturerService;
        this.auditLogger = auditLogger;
    }

    private CustomUserDetails getPrincipal() {
        return (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private Long getLecturerId() {
        return getPrincipal().getUser().getId();
    }

    private String getLecturerEmail() {
        return getPrincipal().getUsername();
    }

    // --- Exam CRUD ---
    @GetMapping("/exams")
    public ResponseEntity<List<ExamDto>> getMyExams() {
        return ResponseEntity.ok(lecturerService.getExamsByLecturer(getLecturerId()));
    }

    @PostMapping("/exams")
    public ResponseEntity<ExamDto> createExam(@Valid @RequestBody CreateExamRequest request) {
        ExamDto created = lecturerService.createExam(getLecturerId(), request);
        auditLogger.log("CREATE_EXAM", getLecturerEmail(), "Created Exam: " + created.title());
        return ResponseEntity.ok(created);
    }

    @PutMapping("/exams/{id}")
    public ResponseEntity<ExamDto> updateExam(@PathVariable Long id, @Valid @RequestBody CreateExamRequest request) {
        ExamDto updated = lecturerService.updateExam(id, request);
        auditLogger.log("UPDATE_EXAM", getLecturerEmail(), "Updated Exam ID: " + id);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/exams/{id}/publish")
    public ResponseEntity<ExamDto> publishExam(@PathVariable Long id) {
        ExamDto published = lecturerService.publishExam(id);
        auditLogger.log("PUBLISH_EXAM", getLecturerEmail(), "Published Exam ID: " + id);
        return ResponseEntity.ok(published);
    }

    @DeleteMapping("/exams/{id}")
    public ResponseEntity<Void> deleteExam(@PathVariable Long id) {
        lecturerService.deleteExam(id);
        auditLogger.log("DELETE_EXAM", getLecturerEmail(), "Deleted Exam ID: " + id);
        return ResponseEntity.noContent().build();
    }

    // --- Question Bank Management ---
    @GetMapping("/questions/subject/{subjectId}")
    public ResponseEntity<List<QuestionBankDto>> getQuestionsBySubject(@PathVariable Long subjectId) {
        return ResponseEntity.ok(lecturerService.getQuestionsBySubject(subjectId));
    }

    @PostMapping("/questions")
    public ResponseEntity<QuestionBankDto> createQuestion(@Valid @RequestBody QuestionBankDto request) {
        QuestionBankDto created = lecturerService.createQuestion(request);
        auditLogger.log("CREATE_QUESTION", getLecturerEmail(), "Created question in subject: " + created.subjectId());
        return ResponseEntity.ok(created);
    }

    @PutMapping("/questions/{id}")
    public ResponseEntity<QuestionBankDto> updateQuestion(@PathVariable Long id, @Valid @RequestBody QuestionBankDto request) {
        QuestionBankDto updated = lecturerService.updateQuestion(id, request);
        auditLogger.log("UPDATE_QUESTION", getLecturerEmail(), "Updated question ID: " + id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/questions/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        lecturerService.deleteQuestion(id);
        auditLogger.log("DELETE_QUESTION", getLecturerEmail(), "Deleted question ID: " + id);
        return ResponseEntity.noContent().build();
    }

    // --- Exam Attempts and Results ---
    @GetMapping("/exams/{examId}/results")
    public ResponseEntity<List<AttemptResultDto>> getExamAttempts(@PathVariable Long examId) {
        return ResponseEntity.ok(lecturerService.getExamAttempts(examId));
    }
}
