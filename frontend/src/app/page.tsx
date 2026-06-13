"use client";

import { useState, useEffect, useCallback } from "react";

type Ticket = {
  id: number;
  title: string;
  description: string;
  status: string;
  createdAt: string;
};

export default function Home() {
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [submitStatus, setSubmitStatus] = useState<"idle" | "loading" | "ok" | "error">("idle");
  const [tickets, setTickets] = useState<Ticket[]>([]);

  const fetchTickets = useCallback(async () => {
    try {
      const res = await fetch("/api/tickets");
      if (res.ok) setTickets(await res.json());
    } catch {
      // ignore fetch errors silently
    }
  }, []);

  useEffect(() => {
    fetchTickets();
  }, [fetchTickets]);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setSubmitStatus("loading");
    try {
      const res = await fetch("/api/tickets", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ title, description }),
      });
      if (res.ok) {
        setSubmitStatus("ok");
        setTitle("");
        setDescription("");
        fetchTickets();
      } else {
        setSubmitStatus("error");
      }
    } catch {
      setSubmitStatus("error");
    }
  }

  return (
    <div className="flex flex-col items-center gap-8 py-12 px-4 bg-zinc-50 dark:bg-black min-h-screen">
      <form
        onSubmit={handleSubmit}
        className="flex flex-col gap-4 w-full max-w-md bg-white dark:bg-zinc-900 p-8 rounded-xl shadow"
      >
        <h1 className="text-xl font-semibold text-zinc-900 dark:text-zinc-50">
          Skapa ärende
        </h1>

        <label className="flex flex-col gap-1 text-sm text-zinc-700 dark:text-zinc-300">
          Titel
          <input
            type="text"
            required
            maxLength={200}
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            className="rounded-lg border border-zinc-300 dark:border-zinc-700 bg-transparent px-3 py-2 text-zinc-900 dark:text-zinc-50 outline-none focus:ring-2 focus:ring-zinc-500"
          />
        </label>

        <label className="flex flex-col gap-1 text-sm text-zinc-700 dark:text-zinc-300">
          Beskrivning
          <textarea
            required
            maxLength={2000}
            rows={4}
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            className="rounded-lg border border-zinc-300 dark:border-zinc-700 bg-transparent px-3 py-2 text-zinc-900 dark:text-zinc-50 outline-none focus:ring-2 focus:ring-zinc-500 resize-none"
          />
        </label>

        <button
          type="submit"
          disabled={submitStatus === "loading"}
          className="rounded-full bg-zinc-900 dark:bg-zinc-50 px-5 py-2.5 text-sm font-medium text-white dark:text-zinc-900 disabled:opacity-50"
        >
          {submitStatus === "loading" ? "Skickar…" : "Skicka"}
        </button>

        {submitStatus === "ok" && (
          <p className="text-sm text-green-600">Ärende skapat!</p>
        )}
        {submitStatus === "error" && (
          <p className="text-sm text-red-600">Något gick fel. Är backend igång?</p>
        )}
      </form>

      <section className="w-full max-w-md">
        <h2 className="text-lg font-semibold text-zinc-900 dark:text-zinc-50 mb-4">
          Ärenden ({tickets.length})
        </h2>
        {tickets.length === 0 ? (
          <p className="text-sm text-zinc-500">Inga ärenden ännu.</p>
        ) : (
          <ul className="flex flex-col gap-3">
            {tickets.map((t) => (
              <li
                key={t.id}
                className="bg-white dark:bg-zinc-900 rounded-xl shadow px-5 py-4 flex flex-col gap-1"
              >
                <div className="flex items-center justify-between gap-2">
                  <span className="font-medium text-zinc-900 dark:text-zinc-50">{t.title}</span>
                  <span className="text-xs text-zinc-500 uppercase tracking-wide">{t.status}</span>
                </div>
                <p className="text-sm text-zinc-600 dark:text-zinc-400 whitespace-pre-wrap">{t.description}</p>
                <p className="text-xs text-zinc-400">
                  {new Date(t.createdAt).toLocaleString("sv-SE")}
                </p>
              </li>
            ))}
          </ul>
        )}
      </section>
    </div>
  );
}
