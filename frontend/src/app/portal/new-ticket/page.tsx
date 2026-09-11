"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import Image from "next/image";

// Matchar backendens enum-värden (se.foi.xelin.ticket.domain.model).
const PRIORITETER = [
  { value: "LAG", label: "Låg" },
  { value: "NORMAL", label: "Normal" },
  { value: "HOG", label: "Hög" },
  { value: "KRITISK", label: "Kritisk" },
] as const;

const KATEGORIER = [
  { value: "HARDVARA", label: "Hårdvara" },
  { value: "MJUKVARA", label: "Programvara" },
  { value: "KONTO", label: "Konto & behörighet" },
  { value: "NATVERK", label: "Nätverk" },
  { value: "OVRIGT", label: "Övrigt" },
] as const;

type Result = { id: string } | null;

export default function NyttArendePage() {
  const router = useRouter();

  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [priority, setPriority] = useState<string>("NORMAL");
  const [category, setCategory] = useState<string>("");

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [result, setResult] = useState<Result>(null);

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const res = await fetch("/api/tickets", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ title, description, priority, category }),
      });

      if (res.status === 401) {
        router.push("/login?next=/portal/new-ticket");
        return;
      }

      if (res.status === 400) {
        setError("Kontrollera att alla fält är korrekt ifyllda.");
        return;
      }

      if (!res.ok) {
        setError("Ärendet kunde inte registreras just nu. Försök igen senare.");
        return;
      }

      // KR-202: systemet tilldelar ett unikt ärende-ID vid skapande.
      const body = await res.json().catch(() => null);
      const locationId = res.headers.get("Location")?.split("/").pop();
      setResult({ id: body?.id ?? body?.ticketId ?? locationId ?? "okänt" });
    } catch {
      setError("Kunde inte nå tjänsten. Kontrollera din uppkoppling.");
    } finally {
      setLoading(false);
    }
  }

  async function handleLogout() {
    try {
      await fetch("/api/auth/logout", { method: "POST" });
    } finally {
      router.push("/login");
    }
  }

  function resetForm() {
    setTitle("");
    setDescription("");
    setPriority("NORMAL");
    setCategory("");
    setResult(null);
    setError(null);
  }

  return (
    <div className="min-h-screen flex flex-col">
      <header className="flex items-center justify-between border-b border-zinc-200 bg-white px-6 py-3">
        <div className="flex items-center gap-3">
          <Image
            src="/foi-weapon.png"
            alt="FOI vapensköld"
            width={22}
            height={36}
          />
          <span className="text-sm font-semibold text-[#1e3d8c]">
            Xelin Serviceportal
          </span>
        </div>
        <button
          onClick={handleLogout}
          className="text-sm text-zinc-500 hover:text-zinc-900 cursor-pointer transition-colors"
        >
          Logga ut
        </button>
      </header>

      <main className="flex-1 flex justify-center px-6 py-10">
        <div className="w-full max-w-xl">
          <h1 className="text-2xl font-semibold text-zinc-900 mb-1">
            Registrera ärende
          </h1>
          <p className="text-sm text-zinc-500 mb-8">
            Beskriv ditt IT-supportärende så återkommer helpdesk.
          </p>

          {result ? (
            <div className="rounded-xl border border-green-200 bg-green-50 p-6">
              <h2 className="text-lg font-semibold text-green-900">
                Ärendet är registrerat
              </h2>
              <p className="mt-1 text-sm text-green-800">
                Ditt ärende-ID är{" "}
                <span className="font-mono font-semibold">{result.id}</span>. Du
                kan följa status via din e-post.
              </p>
              <button
                onClick={resetForm}
                className="mt-5 rounded-lg bg-[#1e3d8c] hover:bg-[#162e6a] px-4 py-2 text-sm font-medium text-white cursor-pointer transition-colors"
              >
                Registrera ett till ärende
              </button>
            </div>
          ) : (
            <form onSubmit={handleSubmit} className="flex flex-col gap-5">
              <label className="flex flex-col gap-1.5 text-sm font-medium text-zinc-700">
                Titel
                <input
                  type="text"
                  required
                  maxLength={200}
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  placeholder="Kort sammanfattning"
                  className="rounded-lg border border-zinc-300 bg-white px-3 py-2.5 text-zinc-900 outline-none focus:border-[#1e3d8c] focus:ring-2 focus:ring-[#1e3d8c]/20 transition-colors"
                />
              </label>

              <label className="flex flex-col gap-1.5 text-sm font-medium text-zinc-700">
                Beskrivning
                <textarea
                  required
                  rows={6}
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  placeholder="Vad har hänt? Vilken utrustning eller tjänst gäller det? Felmeddelanden?"
                  className="rounded-lg border border-zinc-300 bg-white px-3 py-2.5 text-zinc-900 outline-none focus:border-[#1e3d8c] focus:ring-2 focus:ring-[#1e3d8c]/20 transition-colors resize-y"
                />
              </label>

              <div className="grid gap-5 sm:grid-cols-2">
                <label className="flex flex-col gap-1.5 text-sm font-medium text-zinc-700">
                  Kategori
                  <select
                    required
                    value={category}
                    onChange={(e) => setCategory(e.target.value)}
                    className="rounded-lg border border-zinc-300 bg-white px-3 py-2.5 text-zinc-900 outline-none focus:border-[#1e3d8c] focus:ring-2 focus:ring-[#1e3d8c]/20 transition-colors"
                  >
                    <option value="" disabled>
                      Välj kategori
                    </option>
                    {KATEGORIER.map((k) => (
                      <option key={k.value} value={k.value}>
                        {k.label}
                      </option>
                    ))}
                  </select>
                </label>

                <label className="flex flex-col gap-1.5 text-sm font-medium text-zinc-700">
                  Prioritet
                  <select
                    required
                    value={priority}
                    onChange={(e) => setPriority(e.target.value)}
                    className="rounded-lg border border-zinc-300 bg-white px-3 py-2.5 text-zinc-900 outline-none focus:border-[#1e3d8c] focus:ring-2 focus:ring-[#1e3d8c]/20 transition-colors"
                  >
                    {PRIORITETER.map((p) => (
                      <option key={p.value} value={p.value}>
                        {p.label}
                      </option>
                    ))}
                  </select>
                </label>
              </div>

              {error && (
                <p className="text-sm text-red-600 bg-red-50 border border-red-200 rounded-lg px-3 py-2">
                  {error}
                </p>
              )}

              <button
                type="submit"
                disabled={loading}
                className="mt-1 self-start rounded-lg bg-[#1e3d8c] hover:bg-[#162e6a] active:bg-[#0f2050] px-6 py-2.5 text-sm font-medium text-white disabled:opacity-50 transition-colors cursor-pointer"
              >
                {loading ? "Skickar…" : "Skicka in ärende"}
              </button>
            </form>
          )}
        </div>
      </main>
    </div>
  );
}
