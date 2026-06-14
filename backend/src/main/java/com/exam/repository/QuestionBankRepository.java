package com.exam.repository;

import com.exam.entity.QuestionBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuestionBankRepository extends JpaRepository<QuestionBank, Long> {
    List<QuestionBank> findBySubjectId(Long subjectId);
    List<QuestionBank> findBySubjectIdAndDifficultyLevel(Long subjectId, QuestionBank.DifficultyLevel difficultyLevel);
}
