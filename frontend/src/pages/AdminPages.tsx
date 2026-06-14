import React, { useEffect, useState } from 'react';
import api from '../services/api';
import { AdminDashboard as DashboardType, User, Subject } from '../types';
import { 
  Users, BookOpen, GraduationCap, Award, 
  Trash2, Plus, Edit2, AlertCircle 
} from 'lucide-react';

export const AdminDashboard: React.FC = () => {
  const [stats, setStats] = useState<DashboardType | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get('api/admin/dashboard')
      .then(res => setStats(res.data))
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="text-center py-20"><div className="animate-spin rounded-full h-10 w-10 border-b-2 border-blue-500 mx-auto"></div></div>;

  return (
    <div className="space-y-8">
      <div>
        <h2 className="text-3xl font-extrabold tracking-tight">Analytics Dashboard</h2>
        <p className="text-slate-500 dark:text-slate-400 mt-2">Overall administrative stats overview</p>
      </div>

      {stats && (
        <>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            <StatCard label="Total Students" value={stats.totalStudents} icon={GraduationCap} />
            <StatCard label="Total Lecturers" value={stats.totalLecturers} icon={Users} />
            <StatCard label="Total Subjects" value={stats.totalSubjects} icon={BookOpen} />
            <StatCard label="Pass Rate" value={`${stats.passRate.toFixed(1)}%`} icon={Award} />
          </div>

          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-sm">
            <h3 className="text-lg font-bold mb-4">Recent Audit Activity</h3>
            <div className="divide-y divide-slate-100 dark:divide-slate-800">
              {stats.recentActivities.map((act, index) => (
                <div key={index} className="py-4 flex justify-between items-center">
                  <div>
                    <h4 className="font-semibold text-sm">{act.title}</h4>
                    <p className="text-xs text-slate-500 dark:text-slate-400">{act.message}</p>
                  </div>
                  <span className="text-xs text-slate-400 font-medium">{act.timeAgo}</span>
                </div>
              ))}
            </div>
          </div>
        </>
      )}
    </div>
  );
};

const StatCard: React.FC<{ label: string; value: any; icon: any }> = ({ label, value, icon: Icon }) => (
  <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 flex items-center justify-between shadow-sm">
    <div>
      <span className="text-xs font-bold text-slate-400 uppercase tracking-wider">{label}</span>
      <h3 className="text-3xl font-bold mt-2">{value}</h3>
    </div>
    <div className="w-12 h-12 rounded-2xl bg-blue-50 dark:bg-blue-950/20 text-blue-600 dark:text-blue-400 flex items-center justify-center">
      <Icon size={24} />
    </div>
  </div>
);

export const UserManagement: React.FC = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadUsers();
  }, [search]);

  const loadUsers = () => {
    api.get(`api/admin/users?search=${search}`)
      .then(res => setUsers(res.data))
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('Are you sure you want to delete this user?')) {
      try {
        await api.delete(`api/admin/users/${id}`);
        loadUsers();
      } catch (err) {
        alert('Failed to delete user');
      }
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h2 className="text-2xl font-extrabold tracking-tight">User Directories</h2>
          <p className="text-slate-500 dark:text-slate-400 mt-1">Manage Students and Lecturers directories</p>
        </div>
      </div>

      <div className="flex gap-4">
        <input
          type="text"
          placeholder="Search users by name or email..."
          value={search}
          onChange={e => setSearch(e.target.value)}
          className="flex-1 px-4 py-3 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
      </div>

      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl overflow-hidden shadow-sm">
        <table className="w-full text-left border-collapse">
          <thead>
            <tr className="border-b border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-900/50">
              <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">Full Name</th>
              <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">Email</th>
              <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">Role</th>
              <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">Status</th>
              <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider text-right">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-150 dark:divide-slate-800">
            {users.map(u => (
              <tr key={u.id}>
                <td className="px-6 py-4 text-sm font-medium">{u.fullName}</td>
                <td className="px-6 py-4 text-sm text-slate-500">{u.email}</td>
                <td className="px-6 py-4 text-sm font-semibold">{u.role}</td>
                <td className="px-6 py-4 text-sm">
                  <span className={`px-2.5 py-1 rounded-full text-xs font-semibold ${
                    u.status === 'ACTIVE' ? 'bg-green-50 text-green-600' : 'bg-red-50 text-red-600'
                  }`}>
                    {u.status}
                  </span>
                </td>
                <td className="px-6 py-4 text-right">
                  <button onClick={() => handleDelete(u.id)} className="text-red-500 hover:text-red-700">
                    <Trash2 size={18} />
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export const SubjectManagement: React.FC = () => {
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadSubjects();
  }, []);

  const loadSubjects = () => {
    api.get('api/admin/subjects')
      .then(res => setSubjects(res.data))
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('Are you sure you want to delete this subject?')) {
      try {
        await api.delete(`api/admin/subjects/${id}`);
        loadSubjects();
      } catch {
        alert('Failed to delete subject');
      }
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h2 className="text-2xl font-extrabold tracking-tight">Academic Subjects</h2>
          <p className="text-slate-500 dark:text-slate-400 mt-1">Configure subjects curriculum directories</p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {subjects.map(sub => (
          <div key={sub.id} className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 shadow-sm flex flex-col">
            <span className="text-xs font-bold text-blue-500 uppercase tracking-wider">{sub.subjectCode}</span>
            <h3 className="text-lg font-bold mt-2">{sub.subjectName}</h3>
            <p className="text-sm text-slate-500 mt-2 flex-1">{sub.description}</p>
            <div className="flex justify-end gap-2 mt-4 pt-4 border-t border-slate-100 dark:border-slate-800">
              <button onClick={() => sub.id && handleDelete(sub.id)} className="text-red-500 hover:text-red-700">
                <Trash2 size={18} />
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
