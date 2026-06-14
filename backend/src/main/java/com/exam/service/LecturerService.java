package com.exam.service;

import com.exam.dto.*;
import com.exam.entity.*;
import com.exam.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LecturerService {
    private final ExamRepository examRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final QuestionBankRepository questionBankRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final ExamAttemptRepository examAttemptRepository;

    public LecturerService(
            ExamRepository examRepository,
            SubjectRepository subjectRepository,
            UserRepository userRepository,
            QuestionBankRepository questionBankRepository,
            ExamQuestionRepository examQuestionRepository,
            ExamAttemptRepository examAttemptRepository
    ) {
        this.examRepository = examRepository;
        this.subjectRepository = subjectRepository;
        this.userRepository = userRepository;
        this.questionBankRepository = questionBankRepository;
        this.examQuestionRepository = examQuestionRepository;
        this.examAttemptRepository = examAttemptRepository;
    }

    // --- Exam CRUD ---
    public List<ExamDto> getExamsByLecturer(Long lecturerId) {
        return examRepository.findByLecturerId(lecturerId).stream()
                .map(this::mapToExamDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ExamDto createExam(Long lecturerId, CreateExamRequest request) {
        Subject subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new IllegalArgumentException("Subject not found"));
        User lecturer = userRepository.findById(lecturerId)
                .orElseThrow(() -> new IllegalArgumentException("Lecturer not found"));

        Exam exam = Exam.builder()
                .title(request.title())
                .description(request.description())
                .durationMinutes(request.durationMinutes())
                .passingScore(request.passingScore())
                .subject(subject)
                .lecturer(lecturer)
                .status(Exam.Status.DRAFT)
                .build();

        Exam savedExam = examRepository.save(exam);

        if (request.questions() != null) {
            for (ExamQuestionRequest qReq : request.questions()) {
                QuestionBank question = questionBankRepository.findById(qReq.questionId())
                        .orElseThrow(() -> new IllegalArgumentException("Question not found"));
                ExamQuestion eq = ExamQuestion.builder()
                        .exam(savedExam)
                        .question(question)
                        .marks(qReq.marks())
                        .build();
                examQuestionRepository.save(eq);
            }
        }

        return mapToExamDto(savedExam);
    }

    @Transactional
    public ExamDto updateExam(Long id, CreateExamRequest request) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Exam not found"));

        exam.setTitle(request.title());
        exam.setDescription(request.description());
        exam.setDurationMinutes(request.durationMinutes());
        exam.setPassingScore(request.passingScore());

        if (!exam.getSubject().getId().equals(request.subjectId())) {
            Subject subject = subjectRepository.findById(request.subjectId())
                    .orElseThrow(() -> new IllegalArgumentException("Subject not found"));
            exam.setSubject(subject);
        }

        examRepository.save(exam);

        // Update questions
        examQuestionRepository.deleteByExamId(id);
        if (request.questions() != null) {
            for (ExamQuestionRequest qReq : request.questions()) {
                QuestionBank question = questionBankRepository.findById(qReq.questionId())
                        .orElseThrow(() -> new IllegalArgumentException("Question not found"));
                ExamQuestion eq = ExamQuestion.builder()
                        .exam(exam)
                        .question(question)
                        .marks(qReq.marks())
                        .build();
                examQuestionRepository.save(eq);
            }
        }

        return mapToExamDto(exam);
    }

    public ExamDto publishExam(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Exam not found"));
        exam.setStatus(Exam.Status.PUBLISHED);
        return mapToExamDto(examRepository.save(exam));
    }

    public void deleteExam(Long id) {
        examRepository.deleteById(id);
    }

    // --- Question Bank Management ---
    public List<QuestionBankDto> getQuestionsBySubject(Long subjectId) {
        return questionBankRepository.findBySubjectId(subjectId).stream()
                .map(this::mapToQuestionBankDto)
                .collect(Collectors.toList());
    }

    public QuestionBankDto createQuestion(QuestionBankDto dto) {
        Subject subject = subjectRepository.findById(dto.subjectId())
                .orElseThrow(() -> new IllegalArgumentException("Subject not found"));
        QuestionBank question = QuestionBank.builder()
                .subject(subject)
                .questionText(dto.questionText())
                .questionType(dto.questionType())
                .optionA(dto.optionA())
                .optionB(dto.optionB())
                .optionC(dto.optionC())
                .optionD(dto.optionD())
                .correctAnswer(dto.correctAnswer())
                .difficultyLevel(dto.difficultyLevel())
                .build();
        return mapToQuestionBankDto(questionBankRepository.save(question));
    }

    public QuestionBankDto updateQuestion(Long id, QuestionBankDto dto) {
        QuestionBank question = questionBankRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Question not found"));
        question.setQuestionText(dto.questionText());
        question.setQuestionType(dto.questionType());
        question.setOptionA(dto.optionA());
        question.setOptionB(dto.optionB());
        question.setOptionC(dto.optionC());
        question.setOptionD(dto.optionD());
        question.setCorrectAnswer(dto.correctAnswer());
        question.setDifficultyLevel(dto.difficultyLevel());
        return mapToQuestionBankDto(questionBankRepository.save(question));
    }

    public void deleteQuestion(Long id) {
        questionBankRepository.deleteById(id);
    }

    // --- View Student Results ---
    public List<AttemptResultDto> getExamAttempts(Long examId) {
        return examAttemptRepository.findByExamId(examId).stream()
                .map(attempt -> {
                    List<ExamQuestion> examQuestions = examQuestionRepository.findByExamId(attempt.getExam().getId());
                    double totalMarks = examQuestions.stream().mapToDouble(ExamQuestion::getMarks).sum();
                    return new AttemptResultDto(
                            attempt.getId(),
                            attempt.getExam().getId(),
                            attempt.getExam().getTitle(),
                            attempt.getStudent().getFullName(),
                            attempt.getStudent().getEmail(),
                            attempt.getScore(),
                            totalMarks,
                            attempt.getExam().getPassingScore(),
                            attempt.getScore() != null && attempt.getScore() >= attempt.getExam().getPassingScore(),
                            attempt.getStatus().name(),
                            attempt.getStartedAt(),
                            attempt.getSubmittedAt(),
                            null
                    );
                })
                .collect(Collectors.toList());
    }

    private ExamDto mapToExamDto(Exam exam) {
        List<ExamQuestion> examQuestions = examQuestionRepository.findByExamId(exam.getId());
        double totalMarks = examQuestions.stream().mapToDouble(ExamQuestion::getMarks).sum();

        List<ExamDto.QuestionDetailDto> qDtos = examQuestions.stream()
                .map(eq -> new ExamDto.QuestionDetailDto(
                        eq.getQuestion().getId(),
                        eq.getQuestion().getQuestionText(),
                        eq.getQuestion().getQuestionType().name(),
                        eq.getQuestion().getOptionA(),
                        eq.getQuestion().getOptionB(),
                        eq.getQuestion().getOptionC(),
                        eq.getQuestion().getOptionD(),
                        eq.getMarks(),
                        eq.getQuestion().getDifficultyLevel().name()
                ))
                .collect(Collectors.toList());

        return new ExamDto(
                exam.getId(),
                exam.getTitle(),
                exam.getDescription(),
                exam.getDurationMinutes(),
                exam.getPassingScore(),
                exam.getSubject().getId(),
                exam.getSubject().getSubjectName(),
                exam.getSubject().getSubjectCode(),
                exam.getLecturer().getId(),
                exam.getLecturer().getFullName(),
                exam.getStatus(),
                exam.getCreatedAt(),
                qDtos.size(),
                totalMarks,
                qDtos
        );
    }

    private QuestionBankDto mapToQuestionBankDto(QuestionBank question) {
        return new QuestionBankDto(
                question.getId(),
                question.getSubject().getId(),
                question.getSubject().getSubjectName(),
                question.getQuestionText(),
                question.getQuestionType(),
                question.getOptionA(),
                question.getOptionB(),
                question.getOptionC(),
                question.getOptionD(),
                question.getCorrectAnswer(),
                question.getDifficultyLevel()
        );
    }
}
