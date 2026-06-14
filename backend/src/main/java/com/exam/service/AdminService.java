package com.exam.service;

import com.exam.dto.*;
import com.exam.entity.*;
import com.exam.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final ExamRepository examRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(
            UserRepository userRepository,
            SubjectRepository subjectRepository,
            ExamRepository examRepository,
            ExamAttemptRepository examAttemptRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.examRepository = examRepository;
        this.examAttemptRepository = examAttemptRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // --- User CRUD ---
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> getUsersFiltered(String search, String role, String status) {
        return userRepository.findAll().stream()
                .filter(u -> search == null || u.getFullName().toLowerCase().contains(search.toLowerCase()) || u.getEmail().toLowerCase().contains(search.toLowerCase()))
                .filter(u -> role == null || role.isEmpty() || u.getRole().name().equalsIgnoreCase(role))
                .filter(u -> status == null || status.isEmpty() || u.getStatus().name().equalsIgnoreCase(status))
                .map(this::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto createUser(RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        User user = User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .status(User.Status.ACTIVE)
                .build();
        return mapToUserDto(userRepository.save(user));
    }

    public UserDto updateUser(Long id, UserDto request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setStatus(User.Status.valueOf(request.status().name()));
        if (request.role() != null) {
            user.setRole(User.Role.valueOf(request.role().name()));
        }
        return mapToUserDto(userRepository.save(user));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // --- Subject CRUD ---
    public List<SubjectDto> getAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(this::mapToSubjectDto)
                .collect(Collectors.toList());
    }

    public SubjectDto createSubject(SubjectDto dto) {
        if (subjectRepository.findBySubjectCode(dto.subjectCode()).isPresent()) {
            throw new IllegalArgumentException("Subject code already exists");
        }
        Subject subject = Subject.builder()
                .subjectName(dto.subjectName())
                .subjectCode(dto.subjectCode())
                .description(dto.description())
                .build();
        return mapToSubjectDto(subjectRepository.save(subject));
    }

    public SubjectDto updateSubject(Long id, SubjectDto dto) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found"));
        subject.setSubjectName(dto.subjectName());
        subject.setSubjectCode(dto.subjectCode());
        subject.setDescription(dto.description());
        return mapToSubjectDto(subjectRepository.save(subject));
    }

    public void deleteSubject(Long id) {
        subjectRepository.deleteById(id);
    }

    // --- Dashboard Analytics ---
    public AdminDashboardDto getDashboardStats() {
        long students = userRepository.countByRole(User.Role.STUDENT);
        long lecturers = userRepository.countByRole(User.Role.LECTURER);
        long subjects = subjectRepository.count();
        long exams = examRepository.count();
        long activeExams = examRepository.countByStatus(Exam.Status.PUBLISHED);

        List<ExamAttempt> attempts = examAttemptRepository.findAll();
        long completedAttempts = attempts.stream().filter(a -> a.getStatus() == ExamAttempt.Status.COMPLETED).count();
        long passedAttempts = attempts.stream().filter(a -> a.getStatus() == ExamAttempt.Status.COMPLETED && a.getScore() >= a.getExam().getPassingScore()).count();

        double passRate = completedAttempts == 0 ? 0.0 : ((double) passedAttempts / completedAttempts) * 100.0;

        List<AdminDashboardDto.RecentActivityDto> activities = List.of(
                new AdminDashboardDto.RecentActivityDto("Student Registration", "New student registered on the portal.", "5 mins ago"),
                new AdminDashboardDto.RecentActivityDto("Exam Attempt Started", "A student started 'Midterm Examination'.", "15 mins ago"),
                new AdminDashboardDto.RecentActivityDto("Exam Published", "Lecturer published 'Math Quiz 1'.", "1 hour ago")
        );

        return new AdminDashboardDto(
                students,
                lecturers,
                subjects,
                exams,
                activeExams,
                passRate,
                activities
        );
    }

    private UserDto mapToUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus()
        );
    }

    private SubjectDto mapToSubjectDto(Subject subject) {
        return new SubjectDto(
                subject.getId(),
                subject.getSubjectName(),
                subject.getSubjectCode(),
                subject.getDescription()
        );
    }
}
