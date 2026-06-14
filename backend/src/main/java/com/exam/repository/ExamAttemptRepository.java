package com.exam.repository;

import com.exam.entity.ExamAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {
    List<ExamAttempt> findByStudentId(Long studentId);
    List<ExamAttempt> findByExamId(Long examId);
    Optional<ExamAttempt> findByStudentIdAndExamIdAndStatus(Long studentId, Long examId, ExamAttempt.Status status);
    Optional<ExamAttempt> findByStudentIdAndExamId(Long studentId, Long examId);
    long countByStatus(ExamAttempt.Status status);
}
