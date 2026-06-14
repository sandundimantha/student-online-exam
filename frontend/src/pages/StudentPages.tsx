import React, { useEffect, useState, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../services/api';
import { Exam, StartAttemptResponse, AttemptResult } from '../types';
import { Award, Timer, CheckCircle2, XCircle } from 'lucide-react';

export const StudentDashboard: React.FC = () => {
  const [exams, setExams] = useState<Exam[]>([]);
  const [history, setHistory] = useState<AttemptResult[]>([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    api.get('api/student/exams')
      .then(res => setExams(res.data));

    api.get('api/student/attempts')
      .then(res => setHistory(res.data))
      .finally(() => setLoading(false));
  }, []);

  const handleStartExam = (id: number) => {
    if (window.confirm('Do you want to start this exam now? The timer will begin immediately.')) {
      navigate(`/student/exams/${id}`);
    }
  };

  return (
    <div className="space-y-8">
      <div>
        <h2 className="text-2xl font-extrabold tracking-tight">Student Portal</h2>
        <p className="text-slate-500 mt-1">Access available exams and view your score history</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        {/* Active Exams List */}
        <div className="space-y-4">
          <h3 className="text-lg font-bold">Available Exams</h3>
          {exams.length === 0 ? (
            <p className="text-sm text-slate-500 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6">No examinations available right now.</p>
          ) : (
            exams.map(exam => (
              <div key={exam.id} className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-sm flex items-center justify-between">
                <div>
                  <h4 className="font-bold text-base">{exam.title}</h4>
                  <p className="text-xs text-slate-500 mt-1">Duration: {exam.durationMinutes} mins | Questions: {exam.questionsCount}</p>
                </div>
                <button
                  onClick={() => exam.id && handleStartExam(exam.id)}
                  className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-xl text-xs font-bold transition-all shadow-md shadow-blue-500/10"
                >
                  Start
                </button>
              </div>
            ))
          )}
        </div>

        {/* History List */}
        <div className="space-y-4">
          <h3 className="text-lg font-bold">Exam Attempts History</h3>
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl overflow-hidden shadow-sm divide-y divide-slate-100 dark:divide-slate-800">
            {history.map((att) => (
              <div key={att.attemptId} className="p-4 flex items-center justify-between">
                <div>
                  <h4 className="font-semibold text-sm">{att.examTitle}</h4>
                  <p className="text-xs text-slate-500 mt-0.5">Score: {att.score}/{att.totalMarks}</p>
                </div>
                <button
                  onClick={() => navigate(`/student/results/${att.attemptId}`)}
                  className="px-3.5 py-1.5 border border-slate-200 dark:border-slate-800 rounded-lg text-xs font-bold hover:bg-slate-50"
                >
                  Result Card
                </button>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export const TakeExam: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [attempt, setAttempt] = useState<StartAttemptResponse | null>(null);
  const [timeLeft, setTimeLeft] = useState(0);
  const [answers, setAnswers] = useState<Record<number, string>>({});
  const [submitting, setSubmitting] = useState(false);
  
  const answersRef = useRef(answers);
  answersRef.current = answers;

  useEffect(() => {
    if (id) {
      api.post(`api/student/exams/${id}/start`)
        .then(res => {
          setAttempt(res.data);
          setTimeLeft(res.data.durationMinutes * 60);
        })
        .catch(() => {
          alert('Failed to start attempt.');
          navigate('/student');
        });
    }
  }, [id]);

  // Timer Countdown
  useEffect(() => {
    if (timeLeft <= 0) return;
    const timer = setInterval(() => {
      setTimeLeft(prev => {
        if (prev <= 1) {
          clearInterval(timer);
          handleSubmit();
          return 0;
        }
        return prev - 1;
      });
    }, 1000);
    return () => clearInterval(timer);
  }, [timeLeft]);

  // Periodic Auto-save draft every 15 seconds
  useEffect(() => {
    const autoSave = setInterval(() => {
      saveAnswersProgress();
    }, 15000);
    return () => clearInterval(autoSave);
  }, [attempt]);

  const handleSelectAnswer = (qId: number, ans: string) => {
    setAnswers(prev => ({ ...prev, [qId]: ans }));
    if (attempt) {
      api.post(`api/student/exams/attempts/${attempt.attemptId}/save`, {
        questionId: qId,
        selectedAnswer: ans
      });
    }
  };

  const saveAnswersProgress = () => {
    const currentAttempt = attempt;
    if (currentAttempt) {
      Object.entries(answersRef.current).forEach(([qId, val]) => {
        api.post(`api/student/exams/attempts/${currentAttempt.attemptId}/save`, {
          questionId: Number(qId),
          selectedAnswer: val
        });
      });
    }
  };

  const handleSubmit = async () => {
    if (submitting) return;
    setSubmitting(true);
    saveAnswersProgress();
    
    try {
      const attemptId = attempt?.attemptId;
      if (attemptId) {
        await api.post(`api/student/exams/attempts/${attemptId}/submit`);
        navigate(`/student/results/${attemptId}`);
      }
    } catch {
      alert('Failed to submit exam');
    } finally {
      setSubmitting(false);
    }
  };

  if (!attempt) return <div className="text-center py-20">Initializing Secure Examination Module...</div>;

  const min = Math.floor(timeLeft / 60);
  const sec = timeLeft % 60;

  return (
    <div className="max-w-3xl mx-auto space-y-6">
      <div className="glass-panel border border-slate-200 dark:border-slate-800 rounded-3xl p-6 flex justify-between items-center sticky top-4 z-10 shadow-lg">
        <div>
          <h2 className="font-extrabold text-xl">{attempt.examTitle}</h2>
          <span className="text-xs text-slate-500">Secure Exam Engine Mode</span>
        </div>
        <div className="flex items-center gap-2 px-4 py-2 bg-red-50 dark:bg-red-950/20 text-red-600 dark:text-red-400 rounded-2xl">
          <Timer size={18} />
          <span className="font-bold tracking-wider">{String(min).padStart(2, '0')}:{String(sec).padStart(2, '0')}</span>
        </div>
      </div>

      <div className="space-y-6">
        {attempt.questions.map((q, idx) => (
          <div key={q.id} className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-sm">
            <h4 className="font-bold text-base mb-4">Question {idx + 1}: {q.questionText}</h4>
            
            {q.questionType === 'MCQ' && (
              <div className="space-y-2">
                {[q.optionA, q.optionB, q.optionC, q.optionD].map((opt, i) => opt && (
                  <label key={i} className="flex items-center gap-3 p-3 border border-slate-150 dark:border-slate-800 hover:bg-slate-50 rounded-2xl cursor-pointer">
                    <input
                      type="radio"
                      name={`q-${q.id}`}
                      checked={answers[q.id] === opt}
                      onChange={() => handleSelectAnswer(q.id, opt)}
                      className="accent-blue-500"
                    />
                    <span className="text-sm">{opt}</span>
                  </label>
                ))}
              </div>
            )}

            {q.questionType === 'TRUE_FALSE' && (
              <div className="flex gap-4">
                {['True', 'False'].map(opt => (
                  <label key={opt} className="flex-1 flex items-center justify-center gap-2 p-3 border border-slate-150 dark:border-slate-800 rounded-2xl cursor-pointer">
                    <input
                      type="radio"
                      name={`q-${q.id}`}
                      checked={answers[q.id] === opt}
                      onChange={() => handleSelectAnswer(q.id, opt)}
                    />
                    <span className="text-sm font-semibold">{opt}</span>
                  </label>
                ))}
              </div>
            )}

            {q.questionType === 'SHORT_ANSWER' && (
              <textarea
                value={answers[q.id] || ''}
                onChange={e => handleSelectAnswer(q.id, e.target.value)}
                placeholder="Type your response answer text..."
                className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-2xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 h-28"
              />
            )}
          </div>
        ))}
      </div>

      <button
        onClick={handleSubmit}
        disabled={submitting}
        className="w-full py-4 bg-blue-600 hover:bg-blue-700 text-white rounded-2xl font-bold tracking-wide transition-all shadow-lg"
      >
        Submit Examination
      </button>
    </div>
  );
};

export const ResultsDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [result, setResult] = useState<AttemptResult | null>(null);

  useEffect(() => {
    if (id) {
      api.get(`api/student/attempts/${id}`)
        .then(res => setResult(res.data));
    }
  }, [id]);

  if (!result) return <div className="text-center py-20">Loading Scorecard...</div>;

  return (
    <div className="max-w-2xl mx-auto space-y-6">
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-sm text-center">
        <h2 className="text-xl font-bold mb-1">{result.examTitle}</h2>
        <p className="text-xs text-slate-400">Student Result Card Summary</p>

        <div className="my-6">
          <span className="text-xs text-slate-400 font-bold uppercase block tracking-wider">Score Obtained</span>
          <h3 className="text-4xl font-extrabold text-blue-600 dark:text-blue-400 mt-2">{result.score} / {result.totalMarks}</h3>
        </div>

        <div className={`inline-flex items-center gap-2 px-4 py-2 rounded-full font-bold text-sm ${
          result.passed ? 'bg-green-50 text-green-600' : 'bg-red-50 text-red-600'
        }`}>
          {result.passed ? <CheckCircle2 size={18} /> : <XCircle size={18} />}
          <span>{result.passed ? 'PASSED' : 'FAILED'}</span>
        </div>
      </div>

      <div className="space-y-4">
        <h3 className="text-lg font-bold">Answer Sheet Review</h3>
        {result.answers?.map((ans) => (
          <div key={ans.questionId} className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-sm">
            <h4 className="font-bold text-sm mb-3">{ans.questionText}</h4>
            <div className="space-y-1 text-xs font-semibold">
              <p className={ans.isCorrect ? 'text-green-600' : 'text-red-500'}>Your Answer: {ans.selectedAnswer}</p>
              <p className="text-slate-500">Correct Answer: {ans.correctAnswer}</p>
              <p className="text-slate-400 pt-2">Points Awarded: {ans.marksAwarded} / {ans.maxMarks}</p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
