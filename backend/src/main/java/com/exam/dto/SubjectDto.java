package com.exam.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SubjectDto(
    Long id,

    @NotBlank(message = "Subject name is required")
    @Size(max = 100, message = "Subject name must be less than 100 characters")
    String subjectName,

    @NotBlank(message = "Subject code is required")
    @Size(max = 20, message = "Subject code must be less than 20 characters")
    String subjectCode,

    String description
) {}
