package com.exam.dto;

import com.exam.entity.Exam;
import java.time.LocalDateTime;
import java.util.List;

public record ExamDto(
    Long id,
    String title,
    String description,
    Integer durationMinutes,
    Double passingScore,
    Long subjectId,
    String subjectName,
    String subjectCode,
    Long lecturerId,
    String lecturerName,
    Exam.Status status,
    LocalDateTime createdAt,
    Integer questionsCount,
    Double totalMarks,
    List<QuestionDetailDto> questions
) {
    public record QuestionDetailDto(
        Long id,
        String questionText,
        String questionType,
        String optionA,
        String optionB,
        String optionC,
        String optionD,
        Double marks,
        String difficultyLevel
    ) {}
}
