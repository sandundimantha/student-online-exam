package com.exam.repository;

import com.exam.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findByLecturerId(Long lecturerId);
    List<Exam> findByStatus(Exam.Status status);
    List<Exam> findBySubjectId(Long subjectId);
    long countByStatus(Exam.Status status);
}
