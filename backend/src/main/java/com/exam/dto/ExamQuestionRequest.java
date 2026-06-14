package com.exam.dto;

import jakarta.validation.constraints.NotNull;

public record ExamQuestionRequest(
    @NotNull(message = "Question ID is required")
    Long questionId,

    @NotNull(message = "Question marks is required")
    Double marks
) {}
