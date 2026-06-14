import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { ThemeProvider } from './context/ThemeContext';
import { Layout } from './components/Layout';
import { Login, Register, ForgotPassword } from './pages/AuthPages';
import { AdminDashboard, UserManagement, SubjectManagement } from './pages/AdminPages';
import { LecturerDashboard, CreateExam, QuestionBankBuilder } from './pages/LecturerPages';
import { StudentDashboard, TakeExam, ResultsDetail } from './pages/StudentPages';

const RoleGuard: React.FC<{ children: React.ReactNode; allowedRoles: string[] }> = ({ children, allowedRoles }) => {
  const { user, loading } = useAuth();
  if (loading) return null;
  if (!user) return <Navigate to="/login" replace />;
  if (!allowedRoles.includes(user.role)) return <Navigate to="/login" replace />;
  return <Layout>{children}</Layout>;
};

const AppRoutes: React.FC = () => {
  return (
    <Routes>
      {/* Auth Public Routes */}
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/forgot-password" element={<ForgotPassword />} />

      {/* Admin Panel */}
      <Route path="/admin" element={<RoleGuard allowedRoles={['ADMIN']}><AdminDashboard /></RoleGuard>} />
      <Route path="/admin/users" element={<RoleGuard allowedRoles={['ADMIN']}><UserManagement /></RoleGuard>} />
      <Route path="/admin/subjects" element={<RoleGuard allowedRoles={['ADMIN']}><SubjectManagement /></RoleGuard>} />

      {/* Lecturer Console */}
      <Route path="/lecturer" element={<RoleGuard allowedRoles={['LECTURER']}><LecturerDashboard /></RoleGuard>} />
      <Route path="/lecturer/exams/create" element={<RoleGuard allowedRoles={['LECTURER']}><CreateExam /></RoleGuard>} />
      <Route path="/lecturer/questions" element={<RoleGuard allowedRoles={['LECTURER']}><QuestionBankBuilder /></RoleGuard>} />

      {/* Student Portal */}
      <Route path="/student" element={<RoleGuard allowedRoles={['STUDENT']}><StudentDashboard /></RoleGuard>} />
      <Route path="/student/exams/:id" element={<RoleGuard allowedRoles={['STUDENT']}><TakeExam /></RoleGuard>} />
      <Route path="/student/results/:id" element={<RoleGuard allowedRoles={['STUDENT']}><ResultsDetail /></RoleGuard>} />

      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
};

const App: React.FC = () => {
  return (
    <ThemeProvider>
      <AuthProvider>
        <BrowserRouter>
          <AppRoutes />
        </BrowserRouter>
      </AuthProvider>
    </ThemeProvider>
  );
};

export default App;
