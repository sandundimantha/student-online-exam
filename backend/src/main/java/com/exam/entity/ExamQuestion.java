package com.exam.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "exam_questions")
public class ExamQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionBank question;

    @Column(nullable = false)
    private Double marks;

    public ExamQuestion() {}

    public ExamQuestion(Long id, Exam exam, QuestionBank question, Double marks) {
        this.id = id;
        this.exam = exam;
        this.question = question;
        this.marks = marks;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Exam getExam() { return exam; }
    public void setExam(Exam exam) { this.exam = exam; }

    public QuestionBank getQuestion() { return question; }
    public void setQuestion(QuestionBank question) { this.question = question; }

    public Double getMarks() { return marks; }
    public void setMarks(Double marks) { this.marks = marks; }

    public static ExamQuestionBuilder builder() {
        return new ExamQuestionBuilder();
    }

    public static class ExamQuestionBuilder {
        private Long id;
        private Exam exam;
        private QuestionBank question;
        private Double marks;

        public ExamQuestionBuilder id(Long id) { this.id = id; return this; }
        public ExamQuestionBuilder exam(Exam exam) { this.exam = exam; return this; }
        public ExamQuestionBuilder question(QuestionBank question) { this.question = question; return this; }
        public ExamQuestionBuilder marks(Double marks) { this.marks = marks; return this; }

        public ExamQuestion build() {
            return new ExamQuestion(id, exam, question, marks);
        }
    }
}
