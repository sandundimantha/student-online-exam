package com.exam.controller;

import com.exam.dto.*;
import com.exam.service.AdminService;
import com.exam.util.AuditLogger;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;
    private final AuditLogger auditLogger;

    public AdminController(AdminService adminService, AuditLogger auditLogger) {
        this.adminService = adminService;
        this.auditLogger = auditLogger;
    }

    private String getAdminEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    // --- Dashboard ---
    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardDto> getDashboardStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    // --- User Management ---
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status
    ) {
        return ResponseEntity.ok(adminService.getUsersFiltered(search, role, status));
    }

    @PostMapping("/users")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody RegisterRequest request) {
        UserDto created = adminService.createUser(request);
        auditLogger.log("CREATE_USER", getAdminEmail(), "Created user: " + created.email());
        return ResponseEntity.ok(created);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserDto request) {
        UserDto updated = adminService.updateUser(id, request);
        auditLogger.log("UPDATE_USER", getAdminEmail(), "Updated user: " + updated.email());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        auditLogger.log("DELETE_USER", getAdminEmail(), "Deleted user ID: " + id);
        return ResponseEntity.noContent().build();
    }

    // --- Subject Management ---
    @GetMapping("/subjects")
    public ResponseEntity<List<SubjectDto>> getAllSubjects() {
        return ResponseEntity.ok(adminService.getAllSubjects());
    }

    @PostMapping("/subjects")
    public ResponseEntity<SubjectDto> createSubject(@Valid @RequestBody SubjectDto request) {
        SubjectDto created = adminService.createSubject(request);
        auditLogger.log("CREATE_SUBJECT", getAdminEmail(), "Created subject code: " + created.subjectCode());
        return ResponseEntity.ok(created);
    }

    @PutMapping("/subjects/{id}")
    public ResponseEntity<SubjectDto> updateSubject(@PathVariable Long id, @Valid @RequestBody SubjectDto request) {
        SubjectDto updated = adminService.updateSubject(id, request);
        auditLogger.log("UPDATE_SUBJECT", getAdminEmail(), "Updated subject code: " + updated.subjectCode());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/subjects/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        adminService.deleteSubject(id);
        auditLogger.log("DELETE_SUBJECT", getAdminEmail(), "Deleted subject ID: " + id);
        return ResponseEntity.noContent().build();
    }
}
