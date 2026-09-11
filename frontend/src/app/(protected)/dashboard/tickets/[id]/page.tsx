import { cookies } from "next/headers";
import { notFound } from "next/navigation";
import TicketEditForm from "./TicketEditForm";

// Matchar TicketResponse (se.foi.xelin.ticket.infrastructure.web), utökad med description.
export type TicketDetail = {
  id: number;
  title: string;
  description: string;
  status: string;
  priority: string;
  category: string;
  reporter: string;
  createdAt: string;
};

type TicketResult =
  | { state: "ok"; ticket: TicketDetail }
  | { state: "forbidden" }
  | { state: "not_found" }
  | { state: "error" };

async function fetchTicket(id: string): Promise<TicketResult> {
  const cookieStore = await cookies();
  const jsessionid = cookieStore.get("JSESSIONID")?.value;
  const backendUrl = process.env.BACKEND_URL ?? "http://localhost:8080";

  try {
    const res = await fetch(`${backendUrl}/api/tickets/${id}`, {
      headers: { Cookie: `JSESSIONID=${jsessionid}` },
      cache: "no-store",
    });

    if (res.status === 403) return { state: "forbidden" };
    if (res.status === 404) return { state: "not_found" };
    if (!res.ok) return { state: "error" };

    const ticket = (await res.json()) as TicketDetail;
    return { state: "ok", ticket };
  } catch {
    return { state: "error" };
  }
}

export default async function TicketDetailPage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = await params;
  const result = await fetchTicket(id);

  if (result.state === "not_found") {
    notFound();
  }

  return (
    <main className="flex-1 px-6 py-10">
      <div className="mx-auto max-w-2xl">
        {result.state === "forbidden" && (
          <p className="text-sm text-zinc-500">
            You do not have permission to view this ticket. This view is for agents and
            administrators.
          </p>
        )}

        {result.state === "error" && (
          <p className="text-sm text-red-600 bg-red-50 border border-red-200 rounded-lg px-3 py-2">
            Could not load the ticket. Please try again later.
          </p>
        )}

        {result.state === "ok" && <TicketEditForm ticket={result.ticket} />}
      </div>
    </main>
  );
}
