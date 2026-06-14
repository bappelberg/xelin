"use client";

import { useState } from "react";
import { login } from "../api/auth.api";

export function useAuth() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  async function handleLogin(username: string, password: string): Promise<boolean> {
    setLoading(true);
    setError("");
    try {
      await login(username, password);
      return true;
    } catch (e) {
      setError(e instanceof Error ? e.message : "Inloggning misslyckades");
      return false;
    } finally {
      setLoading(false);
    }
  }

  return { handleLogin, loading, error };
}
