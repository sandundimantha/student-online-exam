import React, { useEffect, useState } from 'react';
import api from '../services/api';
import { Exam, QuestionBank, Subject } from '../types';
import { Plus, Trash2, Eye, Award, CheckCircle } from 'lucide-react';

export const LecturerDashboard: React.FC = () => {
  const [exams, setExams] = useState<Exam[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadExams();
  }, []);

  const loadExams = () => {
    api.get('api/lecturer/exams')
      .then(res => setExams(res.data))
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  };

  const handlePublish = async (id: number) => {
    try {
      await api.post(`api/lecturer/exams/${id}/publish`);
      loadExams();
    } catch {
      alert('Failed to publish exam');
    }
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('Delete this exam draft?')) {
      try {
        await api.delete(`api/lecturer/exams/${id}`);
        loadExams();
      } catch {
        alert('Failed to delete exam');
      }
    }
  };

  return (
    <div className="space-y-8">
      <div className="flex justify-between items-center">
        <div>
          <h2 className="text-2xl font-extrabold tracking-tight">Lecturer Console</h2>
          <p className="text-slate-500 dark:text-slate-400 mt-1">Manage exam assemblies and publishing</p>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {exams.map(exam => (
          <div key={exam.id} className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-sm flex flex-col justify-between">
            <div>
              <div className="flex justify-between items-start">
                <span className="text-xs font-bold text-blue-500 uppercase tracking-wider">{exam.subjectCode}</span>
                <span className={`px-2.5 py-0.5 rounded-full text-xs font-semibold ${
                  exam.status === 'PUBLISHED' ? 'bg-green-50 text-green-600' : 'bg-yellow-50 text-yellow-600'
                }`}>
                  {exam.status}
                </span>
              </div>
              <h3 className="text-lg font-bold mt-2">{exam.title}</h3>
              <p className="text-sm text-slate-500 mt-2">{exam.description}</p>
              <div className="mt-4 text-xs text-slate-400 font-medium space-y-1">
                <p>Duration: {exam.durationMinutes} minutes</p>
                <p>Passing Threshold: {exam.passingScore} marks</p>
              </div>
            </div>

            <div className="flex justify-end gap-2 mt-6 pt-4 border-t border-slate-100 dark:border-slate-800">
              {exam.status === 'DRAFT' && (
                <button
                  onClick={() => exam.id && handlePublish(exam.id)}
                  className="px-4 py-2 bg-green-600 hover:bg-green-700 text-white rounded-xl text-xs font-bold transition-all"
                >
                  Publish
                </button>
              )}
              <button
                onClick={() => exam.id && handleDelete(exam.id)}
                className="p-2 border border-slate-200 dark:border-slate-800 text-red-500 hover:bg-red-50 rounded-xl"
              >
                <Trash2 size={16} />
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export const CreateExam: React.FC = () => {
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [duration, setDuration] = useState(60);
  const [passingScore, setPassingScore] = useState(50);
  const [subjectId, setSubjectId] = useState<number>(1);
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    api.get('api/admin/subjects')
      .then(res => {
        setSubjects(res.data);
        if (res.data.length > 0) setSubjectId(res.data[0].id);
      });
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      await api.post('api/lecturer/exams', {
        title, description, durationMinutes: duration, passingScore, subjectId, questions: []
      });
      setSuccess(true);
    } catch {
      alert('Failed to assemble exam');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-8 shadow-sm">
      <h2 className="text-2xl font-bold mb-6">Assemble New Examination</h2>
      {success ? (
        <div className="text-center space-y-4">
          <p className="text-green-600 font-semibold">Exam draft has been saved successfully!</p>
          <a href="/lecturer" className="text-blue-500 font-bold text-sm block">Return to Dashboard</a>
        </div>
      ) : (
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="text-xs font-semibold text-slate-500 block mb-2">Exam Title</label>
            <input
              type="text"
              required
              value={title}
              onChange={e => setTitle(e.target.value)}
              placeholder="E.g. Calculus Midterm 2026"
              className="w-full px-4 py-3 bg-slate-50 dark:bg-slate-850 border border-slate-200 dark:border-slate-800 rounded-2xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <div>
            <label className="text-xs font-semibold text-slate-500 block mb-2">Instructions</label>
            <textarea
              value={description}
              onChange={e => setDescription(e.target.value)}
              placeholder="Enter instructions for the student..."
              className="w-full px-4 py-3 bg-slate-50 dark:bg-slate-850 border border-slate-200 dark:border-slate-800 rounded-2xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 h-28"
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="text-xs font-semibold text-slate-500 block mb-2">Duration (Minutes)</label>
              <input
                type="number"
                required
                value={duration}
                onChange={e => setDuration(Number(e.target.value))}
                className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-2xl text-sm focus:outline-none"
              />
            </div>
            <div>
              <label className="text-xs font-semibold text-slate-500 block mb-2">Passing Threshold</label>
              <input
                type="number"
                required
                value={passingScore}
                onChange={e => setPassingScore(Number(e.target.value))}
                className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-2xl text-sm focus:outline-none"
              />
            </div>
          </div>

          <div>
            <label className="text-xs font-semibold text-slate-500 block mb-2">Subject Link</label>
            <select
              value={subjectId}
              onChange={e => setSubjectId(Number(e.target.value))}
              className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-2xl text-sm focus:outline-none"
            >
              {subjects.map(sub => (
                <option key={sub.id} value={sub.id}>{sub.subjectName}</option>
              ))}
            </select>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full py-3.5 bg-blue-600 hover:bg-blue-700 text-white rounded-2xl font-semibold text-sm transition-all shadow-md shadow-blue-500/20"
          >
            {loading ? 'Creating...' : 'Save Draft Exam'}
          </button>
        </form>
      )}
    </div>
  );
};

export const QuestionBankBuilder: React.FC = () => {
  const [text, setText] = useState('');
  const [type, setType] = useState<'MCQ' | 'TRUE_FALSE' | 'SHORT_ANSWER'>('MCQ');
  const [optionA, setOptionA] = useState('');
  const [optionB, setOptionB] = useState('');
  const [optionC, setOptionC] = useState('');
  const [optionD, setOptionD] = useState('');
  const [correctAnswer, setCorrectAnswer] = useState('');
  const [difficultyLevel, setDifficultyLevel] = useState<'EASY' | 'MEDIUM' | 'HARD'>('MEDIUM');
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      await api.post('api/lecturer/questions', {
        subjectId: 1, // Defaults to first subject
        questionText: text,
        questionType: type,
        optionA: type === 'MCQ' ? optionA : null,
        optionB: type === 'MCQ' ? optionB : null,
        optionC: type === 'MCQ' ? optionC : null,
        optionD: type === 'MCQ' ? optionD : null,
        correctAnswer,
        difficultyLevel
      });
      setSuccess(true);
      setText('');
      setCorrectAnswer('');
    } catch {
      alert('Failed to insert question');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-8 shadow-sm">
      <h2 className="text-2xl font-bold mb-6">Add Question to Bank</h2>
      {success && <p className="text-green-600 font-semibold mb-4">Question saved successfully!</p>}
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="text-xs font-semibold text-slate-500 block mb-2">Question Text</label>
          <input
            type="text"
            required
            value={text}
            onChange={e => setText(e.target.value)}
            className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-2xl text-sm focus:outline-none"
          />
        </div>

        <div>
          <label className="text-xs font-semibold text-slate-500 block mb-2">Question Type</label>
          <select
            value={type}
            onChange={e => setType(e.target.value as any)}
            className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-2xl text-sm focus:outline-none"
          >
            <option value="MCQ">Multiple Choice (MCQ)</option>
            <option value="TRUE_FALSE">True / False</option>
            <option value="SHORT_ANSWER">Short Answer</option>
          </select>
        </div>

        {type === 'MCQ' && (
          <div className="space-y-3">
            <input type="text" placeholder="Option A" value={optionA} onChange={e => setOptionA(e.target.value)} className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-2xl text-sm focus:outline-none" />
            <input type="text" placeholder="Option B" value={optionB} onChange={e => setOptionB(e.target.value)} className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-2xl text-sm focus:outline-none" />
            <input type="text" placeholder="Option C" value={optionC} onChange={e => setOptionC(e.target.value)} className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-2xl text-sm focus:outline-none" />
            <input type="text" placeholder="Option D" value={optionD} onChange={e => setOptionD(e.target.value)} className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-2xl text-sm focus:outline-none" />
          </div>
        )}

        <div>
          <label className="text-xs font-semibold text-slate-500 block mb-2">Correct Answer</label>
          <input
            type="text"
            required
            placeholder="e.g. Option text, 'True' or model text"
            value={correctAnswer}
            onChange={e => setCorrectAnswer(e.target.value)}
            className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-2xl text-sm focus:outline-none"
          />
        </div>

        <button
          type="submit"
          disabled={loading}
          className="w-full py-3.5 bg-blue-600 hover:bg-blue-700 text-white rounded-2xl font-semibold text-sm transition-all"
        >
          {loading ? 'Saving...' : 'Save Question'}
        </button>
      </form>
    </div>
  );
};
