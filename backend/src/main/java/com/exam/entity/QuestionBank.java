package com.exam.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "question_bank")
public class QuestionBank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false, length = 30)
    private QuestionType questionType;

    @Column(name = "option_a")
    private String optionA;

    @Column(name = "option_b")
    private String optionB;

    @Column(name = "option_c")
    private String optionC;

    @Column(name = "option_d")
    private String optionD;

    @Column(name = "correct_answer", nullable = false, columnDefinition = "TEXT")
    private String correctAnswer;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level", nullable = false, length = 20)
    private DifficultyLevel difficultyLevel;

    public enum QuestionType {
        MCQ,
        TRUE_FALSE,
        SHORT_ANSWER
    }

    public enum DifficultyLevel {
        EASY,
        MEDIUM,
        HARD
    }

    public QuestionBank() {}

    public QuestionBank(Long id, Subject subject, String questionText, QuestionType questionType, String optionA, String optionB, String optionC, String optionD, String correctAnswer, DifficultyLevel difficultyLevel) {
        this.id = id;
        this.subject = subject;
        this.questionText = questionText;
        this.questionType = questionType;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
        this.difficultyLevel = difficultyLevel;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public QuestionType getQuestionType() { return questionType; }
    public void setQuestionType(QuestionType questionType) { this.questionType = questionType; }

    public String getOptionA() { return optionA; }
    public void setOptionA(String optionA) { this.optionA = optionA; }

    public String getOptionB() { return optionB; }
    public void setOptionB(String optionB) { this.optionB = optionB; }

    public String getOptionC() { return optionC; }
    public void setOptionC(String optionC) { this.optionC = optionC; }

    public String getOptionD() { return optionD; }
    public void setOptionD(String optionD) { this.optionD = optionD; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    public DifficultyLevel getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(DifficultyLevel difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public static QuestionBankBuilder builder() {
        return new QuestionBankBuilder();
    }

    public static class QuestionBankBuilder {
        private Long id;
        private Subject subject;
        private String questionText;
        private QuestionType questionType;
        private String optionA;
        private String optionB;
        private String optionC;
        private String optionD;
        private String correctAnswer;
        private DifficultyLevel difficultyLevel;

        public QuestionBankBuilder id(Long id) { this.id = id; return this; }
        public QuestionBankBuilder subject(Subject subject) { this.subject = subject; return this; }
        public QuestionBankBuilder questionText(String questionText) { this.questionText = questionText; return this; }
        public QuestionBankBuilder questionType(QuestionType questionType) { this.questionType = questionType; return this; }
        public QuestionBankBuilder optionA(String optionA) { this.optionA = optionA; return this; }
        public QuestionBankBuilder optionB(String optionB) { this.optionB = optionB; return this; }
        public QuestionBankBuilder optionC(String optionC) { this.optionC = optionC; return this; }
        public QuestionBankBuilder optionD(String optionD) { this.optionD = optionD; return this; }
        public QuestionBankBuilder correctAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; return this; }
        public QuestionBankBuilder difficultyLevel(DifficultyLevel difficultyLevel) { this.difficultyLevel = difficultyLevel; return this; }

        public QuestionBank build() {
            return new QuestionBank(id, subject, questionText, questionType, optionA, optionB, optionC, optionD, correctAnswer, difficultyLevel);
        }
    }
}
