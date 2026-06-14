package com.exam.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "student_answers")
public class StudentAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    private ExamAttempt attempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionBank question;

    @Column(name = "selected_answer", columnDefinition = "TEXT")
    private String selectedAnswer;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "marks_awarded")
    private Double marksAwarded;

    public StudentAnswer() {}

    public StudentAnswer(Long id, ExamAttempt attempt, QuestionBank question, String selectedAnswer, Boolean isCorrect, Double marksAwarded) {
        this.id = id;
        this.attempt = attempt;
        this.question = question;
        this.selectedAnswer = selectedAnswer;
        this.isCorrect = isCorrect;
        this.marksAwarded = marksAwarded;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ExamAttempt getAttempt() { return attempt; }
    public void setAttempt(ExamAttempt attempt) { this.attempt = attempt; }

    public QuestionBank getQuestion() { return question; }
    public void setQuestion(QuestionBank question) { this.question = question; }

    public String getSelectedAnswer() { return selectedAnswer; }
    public void setSelectedAnswer(String selectedAnswer) { this.selectedAnswer = selectedAnswer; }

    public Boolean getIsCorrect() { return isCorrect; }
    public void setIsCorrect(Boolean isCorrect) { this.isCorrect = isCorrect; }

    public Double getMarksAwarded() { return marksAwarded; }
    public void setMarksAwarded(Double marksAwarded) { this.marksAwarded = marksAwarded; }

    public static StudentAnswerBuilder builder() {
        return new StudentAnswerBuilder();
    }

    public static class StudentAnswerBuilder {
        private Long id;
        private ExamAttempt attempt;
        private QuestionBank question;
        private String selectedAnswer;
        private Boolean isCorrect;
        private Double marksAwarded;

        public StudentAnswerBuilder id(Long id) { this.id = id; return this; }
        public StudentAnswerBuilder attempt(ExamAttempt attempt) { this.attempt = attempt; return this; }
        public StudentAnswerBuilder question(QuestionBank question) { this.question = question; return this; }
        public StudentAnswerBuilder selectedAnswer(String selectedAnswer) { this.selectedAnswer = selectedAnswer; return this; }
        public StudentAnswerBuilder isCorrect(Boolean isCorrect) { this.isCorrect = isCorrect; return this; }
        public StudentAnswerBuilder marksAwarded(Double marksAwarded) { this.marksAwarded = marksAwarded; return this; }

        public StudentAnswer build() {
            return new StudentAnswer(id, attempt, question, selectedAnswer, isCorrect, marksAwarded);
        }
    }
}
