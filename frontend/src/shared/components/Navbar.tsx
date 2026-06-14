"use client";

import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { useAuthContext } from "@/features/auth/context/AuthContext";

const navLinks = [
  { href: "/", label: "Home" },
  { href: "/service-requests", label: "Serviceförfrågningar" },
  { href: "/problems", label: "Problem" },
  { href: "/changes", label: "Ändringar" },
];

export function Navbar() {
  const pathname = usePathname();
  const router = useRouter();
  const { user, logout } = useAuthContext();

  async function handleLogout() {
    await logout();
    router.push("/auth");
  }

  if (pathname.startsWith("/auth")) return null;

  return (
    <nav className="w-full border-b border-zinc-200 dark:border-zinc-800 bg-white dark:bg-zinc-900">
      <div className="max-w-7xl mx-auto px-4 h-14 flex items-center gap-8">
        <Link
          href="/"
          className="text-sm font-semibold text-zinc-900 dark:text-zinc-50 shrink-0"
        >
          Xelin
        </Link>

        <ul className="flex items-center gap-1 flex-1">
          {navLinks.map(({ href, label }) => {
            const active = pathname === href;
            return (
              <li key={href}>
                <Link
                  href={href}
                  className={`px-3 py-1.5 rounded-md text-sm transition-colors ${
                    active
                      ? "bg-zinc-100 dark:bg-zinc-800 text-zinc-900 dark:text-zinc-50 font-medium"
                      : "text-zinc-500 hover:text-zinc-900 dark:hover:text-zinc-50"
                  }`}
                >
                  {label}
                </Link>
              </li>
            );
          })}
        </ul>

        {user ? (
          <button
            onClick={handleLogout}
            className="text-sm text-zinc-500 hover:text-zinc-900 dark:hover:text-zinc-50 transition-colors cursor-pointer"
          >
            Logga ut
          </button>
        ) : (
          <Link
            href="/auth"
            className="text-sm text-zinc-500 hover:text-zinc-900 dark:hover:text-zinc-50 transition-colors"
          >
            Logga in
          </Link>
        )}
      </div>
    </nav>
  );
}
