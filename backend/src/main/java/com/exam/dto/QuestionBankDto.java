package com.exam.dto;

import com.exam.entity.QuestionBank;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record QuestionBankDto(
    Long id,

    @NotNull(message = "Subject ID is required")
    Long subjectId,

    String subjectName,

    @NotBlank(message = "Question text is required")
    String questionText,

    @NotNull(message = "Question type is required")
    QuestionBank.QuestionType questionType,

    String optionA,
    String optionB,
    String optionC,
    String optionD,

    @NotBlank(message = "Correct answer is required")
    String correctAnswer,

    @NotNull(message = "Difficulty level is required")
    QuestionBank.DifficultyLevel difficultyLevel
) {}
