package com.exam.dto;

import java.util.List;

public record AdminDashboardDto(
    long totalStudents,
    long totalLecturers,
    long totalSubjects,
    long totalExams,
    long activeExams,
    double passRate,
    List<RecentActivityDto> recentActivities
) {
    public record RecentActivityDto(
        String title,
        String message,
        String timeAgo
    ) {}
}
