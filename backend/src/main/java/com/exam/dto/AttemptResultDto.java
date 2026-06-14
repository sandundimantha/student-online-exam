package com.exam.dto;

import java.time.LocalDateTime;
import java.util.List;

public record AttemptResultDto(
    Long attemptId,
    Long examId,
    String examTitle,
    String studentName,
    String studentEmail,
    Double score,
    Double totalMarks,
    Double passingScore,
    Boolean passed,
    String status,
    LocalDateTime startedAt,
    LocalDateTime submittedAt,
    List<StudentAnswerDetailDto> answers
) {
    public record StudentAnswerDetailDto(
        Long questionId,
        String questionText,
        String questionType,
        String optionA,
        String optionB,
        String optionC,
        String optionD,
        String correctAnswer,
        String selectedAnswer,
        Boolean isCorrect,
        Double marksAwarded,
        Double maxMarks
    ) {}
}
