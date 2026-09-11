import Navbar from "@/components/Navbar";

// Delad layout för alla inloggningskrävande sidor (dashboard, portal).
// Route-skyddet i sig sker i proxy.ts (KR-804: bindande kontroll ligger i backend).
export default function ProtectedLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="min-h-screen flex flex-col">
      <Navbar />
      {children}
    </div>
  );
}
