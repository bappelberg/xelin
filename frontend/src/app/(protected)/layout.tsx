import Sidebar from "@/components/Sidebar";

// Delad layout för alla inloggningskrävande sidor (dashboard, portal).
// Route-skyddet i sig sker i proxy.ts (KR-804: bindande kontroll ligger i backend).
export default function ProtectedLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="min-h-screen flex bg-zinc-50">
      <Sidebar />
      <div className="flex-1 min-w-0 flex flex-col">{children}</div>
    </div>
  );
}
