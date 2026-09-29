"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import type { TicketDetail } from "./page";

// Matchar TicketCommentResponse (se.foi.xelin.ticket.infrastructure.web).
// Interna kommentarer filtreras redan bort server-side för slutanvändare (KR-208).
type TicketComment = {
  id: number;
  author: string;
  body: string;
  internal: boolean;
  createdAt: string;
};

export default function PortalTicketView({ ticket }: { ticket: TicketDetail }) {
  const router = useRouter();

  const [comments, setComments] = useState<TicketComment[]>([]);
  const [commentsLoading, setCommentsLoading] = useState(true);
  const [commentBody, setCommentBody] = useState("");
  const [commentSaving, setCommentSaving] = useState(false);
  const [commentError, setCommentError] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;

    async function loadComments() {
      try {
        const res = await fetch(`/api/tickets/${ticket.id}/comments`);
        if (res.status === 401) {
          router.push(`/login?next=/portal/tickets/${ticket.id}`);
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
        body: JSON.stringify({ body: commentBody }),
      });

      if (res.status === 401) {
        router.push(`/login?next=/portal/tickets/${ticket.id}`);
        return;
      }

      if (!res.ok) {
        setCommentError("The comment could not be saved. Please try again.");
        return;
      }

      const created = (await res.json()) as TicketComment;
      setComments((prev) => [created, ...prev]);
      setCommentBody("");
    } catch {
      setCommentError("Could not reach the service. Check your connection.");
    } finally {
      setCommentSaving(false);
    }
  }

  return (
    <div>
      <Link href="/portal" className="text-sm text-zinc-500 hover:text-zinc-900 transition-colors">
        ← Back to my tickets
      </Link>

      <p className="mt-4 text-xs font-mono text-zinc-400 mb-1">Ticket #{ticket.id}</p>
      <h1 className="text-2xl font-semibold text-zinc-900 mb-1">{ticket.title}</h1>
      <p className="text-sm text-zinc-500 mb-6">
        Submitted {new Date(ticket.createdAt).toLocaleString("en-GB")}
      </p>

      <div className="grid gap-5 sm:grid-cols-3 mb-6">
        <div>
          <p className="text-xs font-medium text-zinc-500 mb-1">Status</p>
          <p className="text-sm text-zinc-900">{ticket.status}</p>
        </div>
        <div>
          <p className="text-xs font-medium text-zinc-500 mb-1">Priority</p>
          <p className="text-sm text-zinc-900">{ticket.priority}</p>
        </div>
        <div>
          <p className="text-xs font-medium text-zinc-500 mb-1">Category</p>
          <p className="text-sm text-zinc-900">{ticket.category}</p>
        </div>
      </div>

      <p className="text-sm text-zinc-700 whitespace-pre-wrap bg-zinc-50 border border-zinc-200 rounded-lg px-4 py-3 mb-10">
        {ticket.description}
      </p>

      <section>
        <h2 className="text-sm font-semibold text-zinc-900 mb-3">Comments</h2>

        {commentsLoading && <p className="text-sm text-zinc-500">Loading comments…</p>}

        {!commentsLoading && comments.length === 0 && (
          <p className="text-sm text-zinc-500">No comments yet.</p>
        )}

        <ul className="flex flex-col gap-3">
          {comments.map((c) => (
            <li key={c.id} className="rounded-lg border border-zinc-200 bg-zinc-50 px-4 py-3">
              <div className="flex items-center justify-between gap-2 mb-1">
                <span className="text-xs font-medium text-zinc-700">{c.author}</span>
                <span className="text-xs text-zinc-400">
                  {new Date(c.createdAt).toLocaleString("en-GB")}
                </span>
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

          <button
            type="submit"
            disabled={commentSaving}
            className="self-start rounded-lg bg-[#1e3d8c] hover:bg-[#162e6a] active:bg-[#0f2050] px-5 py-2 text-sm font-medium text-white disabled:opacity-50 transition-colors cursor-pointer"
          >
            {commentSaving ? "Posting…" : "Post comment"}
          </button>

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
