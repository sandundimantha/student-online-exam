package com.exam.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exams")
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "passing_score", nullable = false)
    private Double passingScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecturer_id", nullable = false)
    private User lecturer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.DRAFT;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum Status {
        DRAFT,
        PUBLISHED
    }

    public Exam() {}

    public Exam(Long id, String title, String description, Integer durationMinutes, Double passingScore, Subject subject, User lecturer, Status status, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.passingScore = passingScore;
        this.subject = subject;
        this.lecturer = lecturer;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Double getPassingScore() { return passingScore; }
    public void setPassingScore(Double passingScore) { this.passingScore = passingScore; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public User getLecturer() { return lecturer; }
    public void setLecturer(User lecturer) { this.lecturer = lecturer; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static ExamBuilder builder() {
        return new ExamBuilder();
    }

    public static class ExamBuilder {
        private Long id;
        private String title;
        private String description;
        private Integer durationMinutes;
        private Double passingScore;
        private Subject subject;
        private User lecturer;
        private Status status = Status.DRAFT;
        private LocalDateTime createdAt;

        public ExamBuilder id(Long id) { this.id = id; return this; }
        public ExamBuilder title(String title) { this.title = title; return this; }
        public ExamBuilder description(String description) { this.description = description; return this; }
        public ExamBuilder durationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; return this; }
        public ExamBuilder passingScore(Double passingScore) { this.passingScore = passingScore; return this; }
        public ExamBuilder subject(Subject subject) { this.subject = subject; return this; }
        public ExamBuilder lecturer(User lecturer) { this.lecturer = lecturer; return this; }
        public ExamBuilder status(Status status) { this.status = status; return this; }
        public ExamBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Exam build() {
            return new Exam(id, title, description, durationMinutes, passingScore, subject, lecturer, status, createdAt);
        }
    }
}
