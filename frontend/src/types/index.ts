export type Role = 'ADMIN' | 'LECTURER' | 'STUDENT';
import { Status } from '../types';

export interface User {
  id: number;
  fullName: string;
  email: string;
  role: Role;
  status: 'ACTIVE' | 'INACTIVE';
}

export interface Subject {
  id?: number;
  subjectName: string;
  subjectCode: string;
  description: string;
}

export interface Exam {
  id?: number;
  title: string;
  description: string;
  durationMinutes: number;
  passingScore: number;
  subjectId: number;
  subjectName?: string;
  subjectCode?: string;
  lecturerId?: number;
  lecturerName?: string;
  status?: 'DRAFT' | 'PUBLISHED';
  questionsCount?: number;
  totalMarks?: number;
  questions?: QuestionDetail[];
}

export interface QuestionDetail {
  id: number;
  questionText: string;
  questionType: 'MCQ' | 'TRUE_FALSE' | 'SHORT_ANSWER';
  optionA?: string;
  optionB?: string;
  optionC?: string;
  optionD?: string;
  marks: number;
  difficultyLevel: 'EASY' | 'MEDIUM' | 'HARD';
}

export interface QuestionBank {
  id?: number;
  subjectId: number;
  subjectName?: string;
  questionText: string;
  questionType: 'MCQ' | 'TRUE_FALSE' | 'SHORT_ANSWER';
  optionA?: string;
  optionB?: string;
  optionC?: string;
  optionD?: string;
  correctAnswer: string;
  difficultyLevel: 'EASY' | 'MEDIUM' | 'HARD';
}

export interface AdminDashboard {
  totalStudents: number;
  totalLecturers: number;
  totalSubjects: number;
  totalExams: number;
  activeExams: number;
  passRate: number;
  recentActivities: RecentActivity[];
}

export interface RecentActivity {
  title: string;
  message: string;
  timeAgo: string;
}

export interface StartAttemptResponse {
  attemptId: number;
  examTitle: string;
  durationMinutes: number;
  questions: AttemptQuestion[];
}

export interface AttemptQuestion {
  id: number;
  questionText: string;
  questionType: 'MCQ' | 'TRUE_FALSE' | 'SHORT_ANSWER';
  optionA?: string;
  optionB?: string;
  optionC?: string;
  optionD?: string;
  marks: number;
  savedAnswer?: string;
}

export interface AttemptResult {
  attemptId: number;
  examId: number;
  examTitle: string;
  studentName: string;
  studentEmail: string;
  score: number;
  totalMarks: number;
  passingScore: number;
  passed: boolean;
  status: 'ONGOING' | 'COMPLETED';
  startedAt: string;
  submittedAt?: string;
  answers?: StudentAnswerDetail[];
}

export interface StudentAnswerDetail {
  questionId: number;
  questionText: string;
  questionType: 'MCQ' | 'TRUE_FALSE' | 'SHORT_ANSWER';
  optionA?: string;
  optionB?: string;
  optionC?: string;
  optionD?: string;
  correctAnswer?: string;
  selectedAnswer: string;
  isCorrect: boolean;
  marksAwarded: number;
  maxMarks: number;
}
