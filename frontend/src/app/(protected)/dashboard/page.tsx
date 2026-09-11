import { cookies } from "next/headers";
import Link from "next/link";

// Matchar TicketResponse (se.foi.xelin.ticket.infrastructure.web).
type TicketListItem = {
  id: number;
  title: string;
  status: string;
  priority: string;
  category: string;
  reporter: string;
  createdAt: string;
};

type TicketQueue =
  | { state: "ok"; tickets: TicketListItem[] }
  | { state: "forbidden" }
  | { state: "error" };

// KR-301: handläggarens samlade ärendekö. Server-side hämtning mot backend —
// samma mönster som logout i actions.ts (absolut URL, JSESSIONID vidarebefordras).
async function fetchTickets(): Promise<TicketQueue> {
  const cookieStore = await cookies();
  const jsessionid = cookieStore.get("JSESSIONID")?.value;
  const backendUrl = process.env.BACKEND_URL ?? "http://localhost:8080";

  try {
    const res = await fetch(`${backendUrl}/api/tickets`, {
      headers: { Cookie: `JSESSIONID=${jsessionid}` },
      cache: "no-store",
    });

    if (res.status === 403) {
      return { state: "forbidden" };
    }

    if (!res.ok) {
      return { state: "error" };
    }

    const tickets = (await res.json()) as TicketListItem[];
    return { state: "ok", tickets };
  } catch {
    return { state: "error" };
  }
}

export default async function Dashboard() {
  const queue = await fetchTickets();

  return (
    <main className="flex-1 px-6 py-10">
      <div className="mx-auto max-w-5xl">
        <h1 className="text-2xl font-semibold text-zinc-900 mb-6">Ticket queue</h1>

        {queue.state === "forbidden" && (
          <p className="text-sm text-zinc-500">
            You do not have permission to view the ticket queue. This view is for agents and
            administrators.
          </p>
        )}

        {queue.state === "error" && (
          <p className="text-sm text-red-600 bg-red-50 border border-red-200 rounded-lg px-3 py-2">
            Could not load the ticket queue. Please try again later.
          </p>
        )}

        {queue.state === "ok" && queue.tickets.length === 0 && (
          <p className="text-sm text-zinc-500">No tickets yet.</p>
        )}

        {queue.state === "ok" && queue.tickets.length > 0 && (
          <div className="overflow-x-auto rounded-xl border border-zinc-200 bg-white">
            <table className="min-w-full text-sm">
              <thead>
                <tr className="border-b border-zinc-200 text-left text-zinc-500">
                  <th className="px-4 py-3 font-medium">ID</th>
                  <th className="px-4 py-3 font-medium">Title</th>
                  <th className="px-4 py-3 font-medium">Status</th>
                  <th className="px-4 py-3 font-medium">Priority</th>
                  <th className="px-4 py-3 font-medium">Category</th>
                  <th className="px-4 py-3 font-medium">Reporter</th>
                  <th className="px-4 py-3 font-medium">Created</th>
                </tr>
              </thead>
              <tbody>
                {queue.tickets.map((t) => (
                  <tr key={t.id} className="border-b border-zinc-100 last:border-0">
                    <td className="px-4 py-3 font-mono text-zinc-500">{t.id}</td>
                    <td className="px-4 py-3">
                      <Link
                        href={`/dashboard/tickets/${t.id}`}
                        className="text-[#1e3d8c] hover:underline"
                      >
                        {t.title}
                      </Link>
                    </td>
                    <td className="px-4 py-3">{t.status}</td>
                    <td className="px-4 py-3">{t.priority}</td>
                    <td className="px-4 py-3">{t.category}</td>
                    <td className="px-4 py-3">{t.reporter}</td>
                    <td className="px-4 py-3 text-zinc-500">
                      {new Date(t.createdAt).toLocaleString("en-GB")}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </main>
  );
}
