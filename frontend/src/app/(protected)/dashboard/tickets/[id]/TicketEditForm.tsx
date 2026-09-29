"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import type { TicketDetail } from "./page";

// Matchar TicketCommentResponse (se.foi.xelin.ticket.infrastructure.web).
type TicketComment = {
  id: number;
  author: string;
  body: string;
  internal: boolean;
  createdAt: string;
};

// Matchar backendens enum-värden (se.foi.xelin.ticket.domain.model).
const STATUSES = [
  { value: "NEW", label: "New" },
  { value: "ASSIGNED", label: "Assigned" },
  { value: "ONGOING", label: "Ongoing" },
  { value: "WAITING", label: "Waiting" },
  { value: "SOLVED", label: "Solved" },
  { value: "CLOSED", label: "Closed" },
] as const;

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

export default function TicketEditForm({ ticket }: { ticket: TicketDetail }) {
  const router = useRouter();

  const [status, setStatus] = useState(ticket.status);
  const [priority, setPriority] = useState(ticket.priority);
  const [category, setCategory] = useState(ticket.category);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [comments, setComments] = useState<TicketComment[]>([]);
  const [commentsLoading, setCommentsLoading] = useState(true);
  const [commentBody, setCommentBody] = useState("");
  const [commentInternal, setCommentInternal] = useState(false);
  const [commentSaving, setCommentSaving] = useState(false);
  const [commentError, setCommentError] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;

    async function loadComments() {
      try {
        const res = await fetch(`/api/tickets/${ticket.id}/comments`);
        if (res.status === 401) {
          router.push(`/login?next=/dashboard/tickets/${ticket.id}`);
          return;
        }
        if (!res.ok) return;
        const data = (await res.json()) as TicketComment[];
        if (!cancelled) setComments(data);
      } catch {
        // Tyst fel — kommentarslistan lämnas tom, resten av sidan fungerar ändå.
      } finally {
        if (!cancelled) setCommentsLoading(false);
      }
    }

    loadComments();
    return () => {
      cancelled = true;
    };
  }, [ticket.id, router]);

  async function handleCommentSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setCommentSaving(true);
    setCommentError(null);

    try {
      const res = await fetch(`/api/tickets/${ticket.id}/comments`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ body: commentBody, internal: commentInternal }),
      });

      if (res.status === 401) {
        router.push(`/login?next=/dashboard/tickets/${ticket.id}`);
        return;
      }

      if (!res.ok) {
        setCommentError("The comment could not be saved. Please try again.");
        return;
      }

      const created = (await res.json()) as TicketComment;
      setComments((prev) => [created, ...prev]);
      setCommentBody("");
      setCommentInternal(false);
    } catch {
      setCommentError("Could not reach the service. Check your connection.");
    } finally {
      setCommentSaving(false);
    }
  }

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setSaving(true);
    setError(null);

    try {
      const res = await fetch(`/api/tickets/${ticket.id}`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ status, priority, category }),
      });

      if (res.status === 401) {
        router.push(`/login?next=/dashboard/tickets/${ticket.id}`);
        return;
      }

      if (!res.ok) {
        setError("The ticket could not be updated. Please try again.");
        return;
      }

      router.push("/dashboard");
      router.refresh();
    } catch {
      setError("Could not reach the service. Check your connection.");
    } finally {
      setSaving(false);
    }
  }

  return (
    <div>
      <Link href="/dashboard" className="text-sm text-zinc-500 hover:text-zinc-900 transition-colors">
        ← Back to queue
      </Link>

      <p className="mt-4 text-xs font-mono text-zinc-400 mb-1">Ticket #{ticket.id}</p>
      <h1 className="text-2xl font-semibold text-zinc-900 mb-1">{ticket.title}</h1>
      <p className="text-sm text-zinc-500 mb-6">
        Reported by {ticket.reporter} · {new Date(ticket.createdAt).toLocaleString("en-GB")}
      </p>

      <p className="text-sm text-zinc-700 whitespace-pre-wrap bg-zinc-50 border border-zinc-200 rounded-lg px-4 py-3 mb-6">
        {ticket.description}
      </p>

      <form onSubmit={handleSubmit} className="flex flex-col gap-5">
        <div className="grid gap-5 sm:grid-cols-3">
          <label className="flex flex-col gap-1.5 text-sm font-medium text-zinc-700">
            Category
            <select
              value={category}
              onChange={(e) => setCategory(e.target.value)}
              className="rounded-lg border border-zinc-300 bg-white px-3 py-2.5 text-zinc-900 outline-none focus:border-[#1e3d8c] focus:ring-2 focus:ring-[#1e3d8c]/20 transition-colors"
            >
              {CATEGORIES.map((c) => (
                <option key={c.value} value={c.value}>
                  {c.label}
                </option>
              ))}
            </select>
          </label>

          <label className="flex flex-col gap-1.5 text-sm font-medium text-zinc-700">
            Status
            <select
              value={status}
              onChange={(e) => setStatus(e.target.value)}
              className="rounded-lg border border-zinc-300 bg-white px-3 py-2.5 text-zinc-900 outline-none focus:border-[#1e3d8c] focus:ring-2 focus:ring-[#1e3d8c]/20 transition-colors"
            >
              {STATUSES.map((s) => (
                <option key={s.value} value={s.value}>
                  {s.label}
                </option>
              ))}
            </select>
          </label>

          <label className="flex flex-col gap-1.5 text-sm font-medium text-zinc-700">
            Priority
            <select
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
          disabled={saving}
          className="mt-1 self-start rounded-lg bg-[#1e3d8c] hover:bg-[#162e6a] active:bg-[#0f2050] px-6 py-2.5 text-sm font-medium text-white disabled:opacity-50 transition-colors cursor-pointer"
        >
          {saving ? "Saving…" : "Save changes"}
        </button>
      </form>

      <section className="mt-10">
        <h2 className="text-sm font-semibold text-zinc-900 mb-3">Comments</h2>

        {commentsLoading && <p className="text-sm text-zinc-500">Loading comments…</p>}

        {!commentsLoading && comments.length === 0 && (
          <p className="text-sm text-zinc-500">No comments yet.</p>
        )}

        <ul className="flex flex-col gap-3">
          {comments.map((c) => (
            <li
              key={c.id}
              className={`rounded-lg border px-4 py-3 ${
                c.internal ? "border-amber-200 bg-amber-50" : "border-zinc-200 bg-zinc-50"
              }`}
            >
              <div className="flex items-center justify-between gap-2 mb-1">
                <span className="text-xs font-medium text-zinc-700">{c.author}</span>
                <div className="flex items-center gap-2">
                  {c.internal && (
                    <span className="text-[10px] font-semibold uppercase tracking-wide text-amber-700">
                      Internal
                    </span>
                  )}
                  <span className="text-xs text-zinc-400">
                    {new Date(c.createdAt).toLocaleString("en-GB")}
                  </span>
                </div>
              </div>
              <p className="text-sm text-zinc-700 whitespace-pre-wrap">{c.body}</p>
            </li>
          ))}
        </ul>

        <form onSubmit={handleCommentSubmit} className="mt-4 flex flex-col gap-3">
          <textarea
            value={commentBody}
            onChange={(e) => setCommentBody(e.target.value)}
            required
            rows={3}
            placeholder="Add a comment…"
            className="rounded-lg border border-zinc-300 bg-white px-3 py-2.5 text-sm text-zinc-900 outline-none focus:border-[#1e3d8c] focus:ring-2 focus:ring-[#1e3d8c]/20 transition-colors"
          />

          <div className="flex items-center justify-between gap-3">
            <label className="flex items-center gap-2 text-sm text-zinc-700">
              <input
                type="checkbox"
                checked={commentInternal}
                onChange={(e) => setCommentInternal(e.target.checked)}
                className="rounded border-zinc-300"
              />
              Internal comment (only visible to agents and admins)
            </label>

            <button
              type="submit"
              disabled={commentSaving}
              className="self-start rounded-lg bg-[#1e3d8c] hover:bg-[#162e6a] active:bg-[#0f2050] px-5 py-2 text-sm font-medium text-white disabled:opacity-50 transition-colors cursor-pointer"
            >
              {commentSaving ? "Posting…" : "Post comment"}
            </button>
          </div>

          {commentError && (
            <p className="text-sm text-red-600 bg-red-50 border border-red-200 rounded-lg px-3 py-2">
              {commentError}
            </p>
          )}
        </form>
      </section>
    </div>
  );
}
