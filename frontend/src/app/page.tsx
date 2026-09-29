import Image from "next/image";
import Link from "next/link";

// Startsida: väljare mellan handläggarvyn (Xelin) och den publika
// serviceportalen. Sidan i sig är oskyddad — proxy.ts skickar vidare till
// /login om användaren saknar giltig session när knapparna klickas.
export default function Home() {
  return (
    <main className="min-h-screen flex flex-col items-center justify-center bg-zinc-50 px-6 gap-10">
      <div className="flex flex-col items-center gap-4">
        <Image src="/foi-weapon.png" alt="FOI coat of arms" width={64} height={104} priority />
        <div className="text-center">
          <h1 className="text-2xl font-semibold text-[#1e3d8c] tracking-wide">Xelin</h1>
          <p className="mt-1 text-sm text-zinc-500">IT Service Management</p>
        </div>
      </div>

      <div className="flex flex-col sm:flex-row gap-4">
        <Link
          href="/dashboard"
          className="rounded-lg bg-[#1e3d8c] hover:bg-[#162e6a] active:bg-[#0f2050] px-6 py-3 text-sm font-medium text-white text-center transition-colors"
        >
          Xelin
        </Link>
        <Link
          href="/portal"
          className="rounded-lg border border-zinc-300 bg-white hover:bg-zinc-100 active:bg-zinc-200 px-6 py-3 text-sm font-medium text-zinc-900 text-center transition-colors"
        >
          Serviceportalen
        </Link>
      </div>
    </main>
  );
}
