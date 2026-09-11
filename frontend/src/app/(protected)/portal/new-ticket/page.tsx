"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";

// Matchar backendens enum-värden (se.foi.xelin.ticket.domain.model).
const PRIORITIES = [
  { value: "LOW", label: "Low" },
  { value: "NORMAL", label: "Normal" },
  { value: "HIGH", label: "High" },
  { value: "CRITICAL", label: "Critical" },
] as const;

const CATEGORIES = [
  { value: "HARDWARE", label: "Hardware" },
  { value: "SOFTWARE", label: "Software" },
  { value: "ACCOUNT", label: "Account & permissions" },
  { value: "NETWORK", label: "Network" },
  { value: "OTHER", label: "Other" },
] as const;

type Result = { id: string } | null;

export default function NewTicketPage() {
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
        setError("Check that all fields are filled in correctly.");
        return;
      }

      if (!res.ok) {
        setError("The ticket could not be submitted right now. Please try again later.");
        return;
      }

      // KR-202: systemet tilldelar ett unikt ärende-ID vid skapande.
      const body = await res.json().catch(() => null);
      const locationId = res.headers.get("Location")?.split("/").pop();
      setResult({ id: body?.id ?? body?.ticketId ?? locationId ?? "unknown" });
    } catch {
      setError("Could not reach the service. Check your connection.");
    } finally {
      setLoading(false);
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
    <main className="flex-1 flex justify-center px-6 py-10">
      <div className="w-full max-w-xl">
          <h1 className="text-2xl font-semibold text-zinc-900 mb-1">
            Report a ticket
          </h1>
          <p className="text-sm text-zinc-500 mb-8">
            Describe your IT support issue and the helpdesk will get back to you.
          </p>

          {result ? (
            <div className="rounded-xl border border-green-200 bg-green-50 p-6">
              <h2 className="text-lg font-semibold text-green-900">
                Ticket submitted
              </h2>
              <p className="mt-1 text-sm text-green-800">
                Your ticket ID is{" "}
                <span className="font-mono font-semibold">{result.id}</span>. You
                can follow its status via email.
              </p>
              <button
                onClick={resetForm}
                className="mt-5 rounded-lg bg-[#1e3d8c] hover:bg-[#162e6a] px-4 py-2 text-sm font-medium text-white cursor-pointer transition-colors"
              >
                Submit another ticket
              </button>
            </div>
          ) : (
            <form onSubmit={handleSubmit} className="flex flex-col gap-5">
              <label className="flex flex-col gap-1.5 text-sm font-medium text-zinc-700">
                Title
                <input
                  type="text"
                  required
                  maxLength={200}
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  placeholder="Short summary"
                  className="rounded-lg border border-zinc-300 bg-white px-3 py-2.5 text-zinc-900 outline-none focus:border-[#1e3d8c] focus:ring-2 focus:ring-[#1e3d8c]/20 transition-colors"
                />
              </label>

              <label className="flex flex-col gap-1.5 text-sm font-medium text-zinc-700">
                Description
                <textarea
                  required
                  rows={6}
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  placeholder="What happened? Which equipment or service is affected? Any error messages?"
                  className="rounded-lg border border-zinc-300 bg-white px-3 py-2.5 text-zinc-900 outline-none focus:border-[#1e3d8c] focus:ring-2 focus:ring-[#1e3d8c]/20 transition-colors resize-y"
                />
              </label>

              <div className="grid gap-5 sm:grid-cols-2">
                <label className="flex flex-col gap-1.5 text-sm font-medium text-zinc-700">
                  Category
                  <select
                    required
                    value={category}
                    onChange={(e) => setCategory(e.target.value)}
                    className="rounded-lg border border-zinc-300 bg-white px-3 py-2.5 text-zinc-900 outline-none focus:border-[#1e3d8c] focus:ring-2 focus:ring-[#1e3d8c]/20 transition-colors"
                  >
                    <option value="" disabled>
                      Select a category
                    </option>
                    {CATEGORIES.map((k) => (
                      <option key={k.value} value={k.value}>
                        {k.label}
                      </option>
                    ))}
                  </select>
                </label>

                <label className="flex flex-col gap-1.5 text-sm font-medium text-zinc-700">
                  Priority
                  <select
                    required
                    value={priority}
                    onChange={(e) => setPriority(e.target.value)}
                    className="rounded-lg border border-zinc-300 bg-white px-3 py-2.5 text-zinc-900 outline-none focus:border-[#1e3d8c] focus:ring-2 focus:ring-[#1e3d8c]/20 transition-colors"
                  >
                    {PRIORITIES.map((p) => (
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
                {loading ? "Submitting…" : "Submit ticket"}
              </button>
            </form>
          )}
      </div>
    </main>
  );
}
