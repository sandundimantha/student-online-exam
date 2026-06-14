package com.exam.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreateExamRequest(
    @NotBlank(message = "Exam title is required")
    String title,

    String description,

    @NotNull(message = "Duration in minutes is required")
    Integer durationMinutes,

    @NotNull(message = "Passing score is required")
    Double passingScore,

    @NotNull(message = "Subject ID is required")
    Long subjectId,

    List<ExamQuestionRequest> questions
) {}
