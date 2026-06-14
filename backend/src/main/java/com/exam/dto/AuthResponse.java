package com.exam.dto;

public record AuthResponse(
    String token,
    String refreshToken,
    Long id,
    String fullName,
    String email,
    String role
) {}
