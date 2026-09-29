import { cookies } from "next/headers";
import Link from "next/link";

// Matchar TicketResponse (se.foi.xelin.ticket.infrastructure.web).
type TicketListItem = {
  id: number;
  title: string;
  status: string;
  priority: string;
  category: string;
  createdAt: string;
};

type MyTickets =
  | { state: "ok"; tickets: TicketListItem[] }
  | { state: "forbidden" }
  | { state: "error" };

// KR-207: slutanvändaren ser sina egna ärenden. Server-side hämtning mot backend —
// samma mönster som ärendekön i dashboard/page.tsx (absolut URL, JSESSIONID vidarebefordras).
async function fetchMyTickets(): Promise<MyTickets> {
  const cookieStore = await cookies();
  const jsessionid = cookieStore.get("JSESSIONID")?.value;
  const backendUrl = process.env.BACKEND_URL ?? "http://localhost:8080";

  try {
    const res = await fetch(`${backendUrl}/api/tickets/mine`, {
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

export default async function PortalHome() {
  const myTickets = await fetchMyTickets();

  return (
    <main className="flex-1 px-6 py-10">
      <div className="mx-auto max-w-3xl">
        <div className="flex items-center justify-between mb-6">
          <h1 className="text-2xl font-semibold text-zinc-900">My tickets</h1>
          <Link
            href="/portal/new-ticket"
            className="rounded-lg bg-[#1e3d8c] hover:bg-[#162e6a] active:bg-[#0f2050] px-4 py-2 text-sm font-medium text-white transition-colors"
          >
            New ticket
          </Link>
        </div>

        {myTickets.state === "forbidden" && (
          <p className="text-sm text-zinc-500">
            You do not have permission to view this page.
          </p>
        )}

        {myTickets.state === "error" && (
          <p className="text-sm text-red-600 bg-red-50 border border-red-200 rounded-lg px-3 py-2">
            Could not load your tickets. Please try again later.
          </p>
        )}

        {myTickets.state === "ok" && myTickets.tickets.length === 0 && (
          <p className="text-sm text-zinc-500">
            You have not reported any tickets yet.
          </p>
        )}

        {myTickets.state === "ok" && myTickets.tickets.length > 0 && (
          <div className="overflow-x-auto rounded-xl border border-zinc-200 bg-white">
            <table className="min-w-full text-sm">
              <thead>
                <tr className="border-b border-zinc-200 text-left text-zinc-500">
                  <th className="px-4 py-3 font-medium">ID</th>
                  <th className="px-4 py-3 font-medium">Title</th>
                  <th className="px-4 py-3 font-medium">Status</th>
                  <th className="px-4 py-3 font-medium">Priority</th>
                  <th className="px-4 py-3 font-medium">Category</th>
                  <th className="px-4 py-3 font-medium">Created</th>
                </tr>
              </thead>
              <tbody>
                {myTickets.tickets.map((t) => (
                  <tr key={t.id} className="border-b border-zinc-100 last:border-0">
                    <td className="px-4 py-3 font-mono text-zinc-500">{t.id}</td>
                    <td className="px-4 py-3">
                      <Link
                        href={`/portal/tickets/${t.id}`}
                        className="text-[#1e3d8c] hover:underline"
                      >
                        {t.title}
                      </Link>
                    </td>
                    <td className="px-4 py-3">{t.status}</td>
                    <td className="px-4 py-3">{t.priority}</td>
                    <td className="px-4 py-3">{t.category}</td>
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
