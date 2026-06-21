"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";

export default function LoginPage() {
  const router = useRouter();
  const [error, setError] = useState<string | null>(null);

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();

    const formData = new FormData(e.currentTarget);

    const username = formData.get("username");
    const password = formData.get("password");

    const res = await fetch("http://localhost:8080/api/auth/login", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include", // 🔥 VIKTIGT: gör att JSESSIONID sparas
      body: JSON.stringify({
        username,
        password,
      }),
    });

    if (!res.ok) {
      setError("Fel användarnamn eller lösenord");
      return;
    }

    // session-cookie (JSESSIONID) sparas automatiskt av browsern
    router.push("/dashboard");
  }

  return (
    <main>
      <h1>Logga in</h1>

      <form onSubmit={handleSubmit}>
        <input
          type="text"
          name="username"
          placeholder="Username"
          required
        />

        <input
          type="password"
          name="password"
          placeholder="Password"
          required
        />

        <button type="submit">Logga in</button>
      </form>

      {error && <p style={{ color: "red" }}>{error}</p>}
    </main>
  );
}