"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import Image from "next/image";

export default function LoginPage() {
  const router = useRouter();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();

    setLoading(true);
    setError(null);

    try {
      const res = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify({
          username,
          password,
        }),
      });

      if (!res.ok) {
        setError("Fel användarnamn eller lösenord");
        return;
      }

      router.push("/dashboard");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="min-h-screen flex">
      {/* Left panel */}
      <div className="hidden lg:flex lg:w-1/2 bg-[#1e3d8c] flex-col items-center justify-center gap-8 p-12">
        <Image
          src="/foi-weapon.png"
          alt="FOI vapensköld"
          width={180}
          height={300}
          priority
        />

        <div className="text-center">
          <h1 className="text-3xl font-semibold text-white tracking-wide">
            Xelin
          </h1>
          <p className="mt-2 text-sm text-[#c9a227] uppercase tracking-widest">
            IT Service Management
          </p>
        </div>
      </div>

      {/* Right panel */}
      <div className="flex-1 flex items-center justify-center bg-zinc-50 px-6">
        <div className="w-full max-w-sm">
          {/* Mobile logo */}
          <div className="flex flex-col items-center mb-8 lg:hidden">
            <Image
              src="/foi-vapen.png"
              alt="FOI vapensköld"
              width={80}
              height={130}
              priority
            />
            <h1 className="mt-4 text-xl font-semibold text-[#1e3d8c]">
              Xelin
            </h1>
          </div>

          <h2 className="text-2xl font-semibold text-zinc-900 mb-1">
            Logga in
          </h2>
          <p className="text-sm text-zinc-500 mb-8">
            Ange dina LDAP-uppgifter
          </p>

          <form onSubmit={handleSubmit} className="flex flex-col gap-5">
            <label className="flex flex-col gap-1.5 text-sm font-medium text-zinc-700">
              Användarnamn
              <input
                type="text"
                required
                autoComplete="username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                className="rounded-lg border border-zinc-300 bg-white px-3 py-2.5 text-zinc-900 outline-none focus:border-[#1e3d8c] focus:ring-2 focus:ring-[#1e3d8c]/20 transition-colors"
              />
            </label>

            <label className="flex flex-col gap-1.5 text-sm font-medium text-zinc-700">
              Lösenord
              <input
                type="password"
                required
                autoComplete="current-password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="rounded-lg border border-zinc-300 bg-white px-3 py-2.5 text-zinc-900 outline-none focus:border-[#1e3d8c] focus:ring-2 focus:ring-[#1e3d8c]/20 transition-colors"
              />
            </label>

            {error && (
              <p className="text-sm text-red-600 bg-red-50 border border-red-200 rounded-lg px-3 py-2">
                {error}
              </p>
            )}

            <button
              type="submit"
              disabled={loading}
              className="mt-1 rounded-lg bg-[#1e3d8c] hover:bg-[#162e6a] active:bg-[#0f2050] px-5 py-2.5 text-sm font-medium text-white disabled:opacity-50 transition-colors cursor-pointer"
            >
              {loading ? "Loggar in…" : "Logga in"}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}