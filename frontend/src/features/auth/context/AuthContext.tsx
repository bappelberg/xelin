"use client";

import { createContext, useContext, useEffect, useState } from "react";
import { login as loginApi, logout as logoutApi, checkSession } from "../api/auth.api";

type AuthContextValue = {
  user: string | null;
  login: (username: string, password: string) => Promise<boolean>;
  logout: () => Promise<void>;
};

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<string | null>(null);

  useEffect(() => {
    checkSession().then((name) => {
      if (name) setUser(name);
    });
  }, []);

  async function login(username: string, password: string): Promise<boolean> {
    try {
      const name = await loginApi(username, password);
      setUser(name);
      return true;
    } catch {
      return false;
    }
  }

  async function logout(): Promise<void> {
    await logoutApi();
    setUser(null);
  }

  return (
    <AuthContext.Provider value={{ user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuthContext(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuthContext måste användas inom AuthProvider");
  return ctx;
}
