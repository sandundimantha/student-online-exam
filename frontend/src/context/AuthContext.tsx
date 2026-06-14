import React, { createContext, useContext, useState, useEffect } from 'react';
import axios from 'axios';

interface UserSession {
  id: number;
  fullName: string;
  email: string;
  role: string;
  token: string;
}

interface AuthContextType {
  user: UserSession | null;
  loading: boolean;
  login: (session: UserSession, refreshToken: string) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<UserSession | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const savedSession = localStorage.getItem('user_session');
    if (savedSession) {
      const parsed = JSON.parse(savedSession) as UserSession;
      setUser(parsed);
      axios.defaults.headers.common['Authorization'] = `Bearer ${parsed.token}`;
    }
    setLoading(false);
  }, []);

  const login = (session: UserSession, refreshToken: string) => {
    setUser(session);
    localStorage.setItem('user_session', JSON.stringify(session));
    localStorage.setItem('refresh_token', refreshToken);
    axios.defaults.headers.common['Authorization'] = `Bearer ${session.token}`;
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem('user_session');
    localStorage.removeItem('refresh_token');
    delete axios.defaults.headers.common['Authorization'];
  };

  return (
    <AuthContext.Provider value={{ user, loading, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within an AuthProvider');
  return context;
};
