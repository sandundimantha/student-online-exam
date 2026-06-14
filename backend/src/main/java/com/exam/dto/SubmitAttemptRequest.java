package com.exam.dto;

import java.util.List;

public record SubmitAttemptRequest(
    List<SaveAnswerRequest> answers
) {}
