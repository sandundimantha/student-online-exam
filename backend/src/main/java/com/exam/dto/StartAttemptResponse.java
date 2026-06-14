package com.exam.dto;

import java.util.List;

public record StartAttemptResponse(
    Long attemptId,
    String examTitle,
    Integer durationMinutes,
    List<AttemptQuestionDto> questions
) {
    public record AttemptQuestionDto(
        Long id,
        String questionText,
        String questionType,
        String optionA,
        String optionB,
        String optionC,
        String optionD,
        Double marks,
        String savedAnswer
    ) {}
}
