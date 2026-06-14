package com.exam.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exam_attempts")
public class ExamAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    private Double score;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.ONGOING;

    public enum Status {
        ONGOING,
        COMPLETED
    }

    public ExamAttempt() {}

    public ExamAttempt(Long id, User student, Exam exam, LocalDateTime startedAt, LocalDateTime submittedAt, Double score, Status status) {
        this.id = id;
        this.student = student;
        this.exam = exam;
        this.startedAt = startedAt;
        this.submittedAt = submittedAt;
        this.score = score;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }

    public Exam getExam() { return exam; }
    public void setExam(Exam exam) { this.exam = exam; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public static ExamAttemptBuilder builder() {
        return new ExamAttemptBuilder();
    }

    public static class ExamAttemptBuilder {
        private Long id;
        private User student;
        private Exam exam;
        private LocalDateTime startedAt;
        private LocalDateTime submittedAt;
        private Double score;
        private Status status = Status.ONGOING;

        public ExamAttemptBuilder id(Long id) { this.id = id; return this; }
        public ExamAttemptBuilder student(User student) { this.student = student; return this; }
        public ExamAttemptBuilder exam(Exam exam) { this.exam = exam; return this; }
        public ExamAttemptBuilder startedAt(LocalDateTime startedAt) { this.startedAt = startedAt; return this; }
        public ExamAttemptBuilder submittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; return this; }
        public ExamAttemptBuilder score(Double score) { this.score = score; return this; }
        public ExamAttemptBuilder status(Status status) { this.status = status; return this; }

        public ExamAttempt build() {
            return new ExamAttempt(id, student, exam, startedAt, submittedAt, score, status);
        }
    }
}
