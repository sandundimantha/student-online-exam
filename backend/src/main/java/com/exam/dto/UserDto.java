package com.exam.dto;

import com.exam.entity.User;

public record UserDto(
    Long id,
    String fullName,
    String email,
    User.Role role,
    User.Status status
) {}
