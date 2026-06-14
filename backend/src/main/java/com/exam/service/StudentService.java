package com.exam.service;

import com.exam.dto.*;
import com.exam.entity.*;
import com.exam.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final ExamRepository examRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final StudentAnswerRepository studentAnswerRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public StudentService(
            ExamRepository examRepository,
            ExamAttemptRepository examAttemptRepository,
            ExamQuestionRepository examQuestionRepository,
            StudentAnswerRepository studentAnswerRepository,
            UserRepository userRepository,
            NotificationRepository notificationRepository
    ) {
        this.examRepository = examRepository;
        this.examAttemptRepository = examAttemptRepository;
        this.examQuestionRepository = examQuestionRepository;
        this.studentAnswerRepository = studentAnswerRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    public List<ExamDto> getAvailableExams(Long studentId) {
        // Find all published exams
        List<Exam> publishedExams = examRepository.findByStatus(Exam.Status.PUBLISHED);

        // Filter out exams that student has already attempted
        List<Long> attemptedExamIds = examAttemptRepository.findByStudentId(studentId).stream()
                .map(attempt -> attempt.getExam().getId())
                .collect(Collectors.toList());

        return publishedExams.stream()
                .filter(exam -> !attemptedExamIds.contains(exam.getId()))
                .map(this::mapToBasicExamDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public StartAttemptResponse startAttempt(Long studentId, Long examId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Exam not found"));

        if (exam.getStatus() != Exam.Status.PUBLISHED) {
            throw new IllegalStateException("This exam is not published yet");
        }

        // Check if student has an existing attempt
        Optional<ExamAttempt> existing = examAttemptRepository.findByStudentIdAndExamId(studentId, examId);
        if (existing.isPresent()) {
            throw new IllegalStateException("You have already attempted or started this exam");
        }

        ExamAttempt attempt = ExamAttempt.builder()
                .student(student)
                .exam(exam)
                .startedAt(LocalDateTime.now())
                .status(ExamAttempt.Status.ONGOING)
                .build();

        ExamAttempt savedAttempt = examAttemptRepository.save(attempt);

        List<ExamQuestion> examQuestions = examQuestionRepository.findByExamId(examId);
        List<StartAttemptResponse.AttemptQuestionDto> qDtos = examQuestions.stream()
                .map(eq -> new StartAttemptResponse.AttemptQuestionDto(
                        eq.getQuestion().getId(),
                        eq.getQuestion().getQuestionText(),
                        eq.getQuestion().getQuestionType().name(),
                        eq.getQuestion().getOptionA(),
                        eq.getQuestion().getOptionB(),
                        eq.getQuestion().getOptionC(),
                        eq.getQuestion().getOptionD(),
                        eq.getMarks(),
                        null
                ))
                .collect(Collectors.toList());

        return new StartAttemptResponse(
                savedAttempt.getId(),
                exam.getTitle(),
                exam.getDurationMinutes(),
                qDtos
        );
    }

    @Transactional
    public void saveAnswer(Long studentId, Long attemptId, SaveAnswerRequest request) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new IllegalArgumentException("Attempt not found"));

        if (!attempt.getStudent().getId().equals(studentId)) {
            throw new SecurityException("Unauthorized access to this attempt");
        }
        if (attempt.getStatus() == ExamAttempt.Status.COMPLETED) {
            throw new IllegalStateException("Attempt has already been submitted");
        }

        // Check if question is in the exam
        List<ExamQuestion> examQuestions = examQuestionRepository.findByExamId(attempt.getExam().getId());
        ExamQuestion currentEq = examQuestions.stream()
                .filter(eq -> eq.getQuestion().getId().equals(request.questionId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Question is not part of this exam"));

        Optional<StudentAnswer> existingAnswerOpt = studentAnswerRepository
                .findByAttemptIdAndQuestionId(attemptId, request.questionId());

        StudentAnswer answer;
        if (existingAnswerOpt.isPresent()) {
            answer = existingAnswerOpt.get();
            answer.setSelectedAnswer(request.selectedAnswer());
        } else {
            answer = StudentAnswer.builder()
                    .attempt(attempt)
                    .question(currentEq.getQuestion())
                    .selectedAnswer(request.selectedAnswer())
                    .build();
        }

        studentAnswerRepository.save(answer);
    }

    @Transactional
    public AttemptResultDto submitAttempt(Long studentId, Long attemptId) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new IllegalArgumentException("Attempt not found"));

        if (!attempt.getStudent().getId().equals(studentId)) {
            throw new SecurityException("Unauthorized access to this attempt");
        }
        if (attempt.getStatus() == ExamAttempt.Status.COMPLETED) {
            return getAttemptResult(studentId, attemptId);
        }

        List<ExamQuestion> examQuestions = examQuestionRepository.findByExamId(attempt.getExam().getId());
        double totalScore = 0.0;
        double totalMarks = 0.0;

        for (ExamQuestion eq : examQuestions) {
            totalMarks += eq.getMarks();
            Optional<StudentAnswer> answerOpt = studentAnswerRepository
                    .findByAttemptIdAndQuestionId(attemptId, eq.getQuestion().getId());

            if (answerOpt.isPresent()) {
                StudentAnswer answer = answerOpt.get();
                boolean isCorrect = evaluateAnswer(eq.getQuestion(), answer.getSelectedAnswer());
                answer.setIsCorrect(isCorrect);
                double marksAwarded = isCorrect ? eq.getMarks() : 0.0;
                answer.setMarksAwarded(marksAwarded);
                studentAnswerRepository.save(answer);
                totalScore += marksAwarded;
            } else {
                // If student didn't answer
                StudentAnswer emptyAnswer = StudentAnswer.builder()
                        .attempt(attempt)
                        .question(eq.getQuestion())
                        .selectedAnswer("")
                        .isCorrect(false)
                        .marksAwarded(0.0)
                        .build();
                studentAnswerRepository.save(emptyAnswer);
            }
        }

        attempt.setScore(totalScore);
        attempt.setSubmittedAt(LocalDateTime.now());
        attempt.setStatus(ExamAttempt.Status.COMPLETED);
        examAttemptRepository.save(attempt);

        // Send Notification
        String message = String.format("You completed the exam '%s'. Your score: %.2f / %.2f.",
                attempt.getExam().getTitle(), totalScore, totalMarks);
        Notification notification = Notification.builder()
                .user(attempt.getStudent())
                .title("Exam Result Released")
                .message(message)
                .isRead(false)
                .build();
        notificationRepository.save(notification);

        return getAttemptResult(studentId, attemptId);
    }

    public AttemptResultDto getAttemptResult(Long studentId, Long attemptId) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new IllegalArgumentException("Attempt not found"));

        if (!attempt.getStudent().getId().equals(studentId) && 
            attempt.getExam().getLecturer().getId() != studentId) {
            // Only the student themselves or the lecturer can view details
            throw new SecurityException("Unauthorized view access to this attempt");
        }

        List<ExamQuestion> examQuestions = examQuestionRepository.findByExamId(attempt.getExam().getId());
        double totalMarks = examQuestions.stream().mapToDouble(ExamQuestion::getMarks).sum();

        List<AttemptResultDto.StudentAnswerDetailDto> details = examQuestions.stream()
                .map(eq -> {
                    Optional<StudentAnswer> saOpt = studentAnswerRepository
                            .findByAttemptIdAndQuestionId(attemptId, eq.getQuestion().getId());
                    String selected = saOpt.map(StudentAnswer::getSelectedAnswer).orElse("");
                    Boolean correct = saOpt.map(StudentAnswer::getIsCorrect).orElse(false);
                    Double awarded = saOpt.map(StudentAnswer::getMarksAwarded).orElse(0.0);

                    return new AttemptResultDto.StudentAnswerDetailDto(
                            eq.getQuestion().getId(),
                            eq.getQuestion().getQuestionText(),
                            eq.getQuestion().getQuestionType().name(),
                            eq.getQuestion().getOptionA(),
                            eq.getQuestion().getOptionB(),
                            eq.getQuestion().getOptionC(),
                            eq.getQuestion().getOptionD(),
                            eq.getQuestion().getCorrectAnswer(),
                            selected,
                            correct,
                            awarded,
                            eq.getMarks()
                    );
                })
                .collect(Collectors.toList());

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
                details
        );
    }

    public List<AttemptResultDto> getAttemptHistory(Long studentId) {
        return examAttemptRepository.findByStudentId(studentId).stream()
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

    private boolean evaluateAnswer(QuestionBank question, String selected) {
        if (selected == null || selected.trim().isEmpty()) {
            return false;
        }
        return question.getCorrectAnswer().trim().equalsIgnoreCase(selected.trim());
    }

    private ExamDto mapToBasicExamDto(Exam exam) {
        List<ExamQuestion> examQuestions = examQuestionRepository.findByExamId(exam.getId());
        double totalMarks = examQuestions.stream().mapToDouble(ExamQuestion::getMarks).sum();

        return new ExamDto(
                exam.getId(),
                exam.getTitle(),
                exam.getDescription(),
                exam.getDurationMinutes(),
                exam.getPassingScore(),
                exam.getSubject().getId(),
                exam.getSubject().getSubjectName(),
                exam.getSubject().getSubjectCode(),
                null,
                null,
                null,
                null,
                examQuestions.size(),
                totalMarks,
                null
        );
    }
}
