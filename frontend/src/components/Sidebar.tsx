"use client";

import Image from "next/image";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { useSyncExternalStore } from "react";
import { logout } from "@/app/actions";

const STORAGE_KEY = "xelin.sidebar.collapsed";

// Hopfällt läge är en per-viewer bekvämlighet i localStorage (kan saknas/kastas
// i privat läge), aldrig delad eller kritisk state. useSyncExternalStore läser
// den säkert kring hydrering: servern renderar alltid utfälld (getServerSnapshot),
// och webbläsaren synkas till det sparade värdet direkt efter hydrering utan att
// trigga en hydreringsvarning.
const collapsedListeners = new Set<() => void>();

function getCollapsedSnapshot(): boolean {
  try {
    return localStorage.getItem(STORAGE_KEY) === "1";
  } catch {
    return false;
  }
}

function getServerCollapsedSnapshot(): boolean {
  return false;
}

function subscribeCollapsed(listener: () => void) {
  collapsedListeners.add(listener);
  return () => collapsedListeners.delete(listener);
}

function setCollapsedPersisted(value: boolean) {
  try {
    localStorage.setItem(STORAGE_KEY, value ? "1" : "0");
  } catch {
    // Går inte att spara — läget gäller bara den här sidladdningen.
  }
  collapsedListeners.forEach((listener) => listener());
}

// isActive matchar även undersidor (t.ex. /dashboard/tickets/12 ska markera
// "Ticket queue"), men aldrig ett syskon (/portal/new-ticket ska inte markera
// "My tickets").
type NavLink = {
  href: string;
  label: string;
  icon: (props: { className?: string }) => React.ReactElement;
  isActive: (pathname: string) => boolean;
};

// Navigeringen är sektionsanpassad: handläggarvyn (/dashboard) och
// serviceportalen (/portal) har olika länkar. Vilken roll den inloggade har
// avgörs bindande av backend per API-anrop (KR-804) — det här styr bara vilka
// genvägar som visas.
function linksFor(pathname: string): NavLink[] {
  if (pathname.startsWith("/portal")) {
    return [
      {
        href: "/portal",
        label: "My tickets",
        icon: TicketsIcon,
        isActive: (p) => p === "/portal" || p.startsWith("/portal/tickets"),
      },
      {
        href: "/portal/new-ticket",
        label: "New ticket",
        icon: PlusIcon,
        isActive: (p) => p === "/portal/new-ticket",
      },
    ];
  }

  if (pathname.startsWith("/dashboard")) {
    return [
      { href: "/dashboard", label: "Ticket queue", icon: QueueIcon, isActive: (p) => p.startsWith("/dashboard") },
    ];
  }

  return [];
}

export default function Sidebar() {
  const pathname = usePathname();
  const links = linksFor(pathname);

  const collapsed = useSyncExternalStore(subscribeCollapsed, getCollapsedSnapshot, getServerCollapsedSnapshot);

  function toggle() {
    setCollapsedPersisted(!collapsed);
  }

  return (
    <aside
      className={`sticky top-0 h-screen shrink-0 flex flex-col border-r border-zinc-200 bg-white transition-[width] duration-200 ease-in-out ${
        collapsed ? "w-16" : "w-60"
      }`}
    >
      <div className={`flex items-center h-14 shrink-0 border-b border-zinc-200 ${collapsed ? "justify-center" : "justify-between px-4"}`}>
        {!collapsed && (
          <Link href="/" className="flex items-center gap-2.5 min-w-0">
            <Image src="/foi-weapon.png" alt="FOI coat of arms" width={18} height={30} className="shrink-0" />
            <span className="text-sm font-semibold text-[#1e3d8c] tracking-wide truncate">Xelin</span>
          </Link>
        )}

        <button
          type="button"
          onClick={toggle}
          aria-label={collapsed ? "Expand sidebar" : "Collapse sidebar"}
          aria-expanded={!collapsed}
          className="flex items-center justify-center h-8 w-8 rounded-md text-zinc-400 hover:text-zinc-900 hover:bg-zinc-100 cursor-pointer transition-colors"
        >
          {collapsed ? <ChevronRightIcon className="h-4 w-4" /> : <ChevronLeftIcon className="h-4 w-4" />}
        </button>
      </div>

      <nav className="flex-1 flex flex-col gap-1 px-2.5 py-4 overflow-y-auto overflow-x-hidden">
        {links.map((link) => {
          const active = link.isActive(pathname);
          const Icon = link.icon;

          return (
            <Link
              key={link.href}
              href={link.href}
              title={collapsed ? link.label : undefined}
              aria-current={active ? "page" : undefined}
              className={`group flex items-center gap-3 rounded-md px-2.5 py-2 text-sm font-medium transition-colors ${
                collapsed ? "justify-center" : ""
              } ${active ? "bg-[#1e3d8c]/10 text-[#1e3d8c]" : "text-zinc-500 hover:text-zinc-900 hover:bg-zinc-100"}`}
            >
              <Icon className="h-[18px] w-[18px] shrink-0" />
              {!collapsed && <span className="truncate">{link.label}</span>}
            </Link>
          );
        })}
      </nav>

      <div className="shrink-0 border-t border-zinc-200 px-2.5 py-3">
        <form action={logout}>
          <button
            type="submit"
            title={collapsed ? "Log out" : undefined}
            className={`group flex w-full items-center gap-3 rounded-md px-2.5 py-2 text-sm font-medium text-zinc-500 hover:text-zinc-900 hover:bg-zinc-100 cursor-pointer transition-colors ${
              collapsed ? "justify-center" : ""
            }`}
          >
            <LogoutIcon className="h-[18px] w-[18px] shrink-0" />
            {!collapsed && <span>Log out</span>}
          </button>
        </form>
      </div>
    </aside>
  );
}

type IconProps = { className?: string };

function iconProps(className?: string) {
  return {
    className,
    viewBox: "0 0 24 24",
    fill: "none",
    stroke: "currentColor",
    strokeWidth: 1.8,
    strokeLinecap: "round" as const,
    strokeLinejoin: "round" as const,
  };
}

function ChevronLeftIcon({ className }: IconProps) {
  return (
    <svg {...iconProps(className)}>
      <polyline points="15 6 9 12 15 18" />
    </svg>
  );
}

function ChevronRightIcon({ className }: IconProps) {
  return (
    <svg {...iconProps(className)}>
      <polyline points="9 6 15 12 9 18" />
    </svg>
  );
}

function QueueIcon({ className }: IconProps) {
  return (
    <svg {...iconProps(className)}>
      <line x1="4" y1="6" x2="20" y2="6" />
      <line x1="4" y1="12" x2="20" y2="12" />
      <line x1="4" y1="18" x2="14" y2="18" />
    </svg>
  );
}

function TicketsIcon({ className }: IconProps) {
  return (
    <svg {...iconProps(className)}>
      <path d="M3 8a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2v2a2 2 0 0 0 0 4v2a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-2a2 2 0 0 0 0-4Z" />
      <line x1="12" y1="7" x2="12" y2="17" strokeDasharray="2.4 2.4" />
    </svg>
  );
}

function PlusIcon({ className }: IconProps) {
  return (
    <svg {...iconProps(className)}>
      <line x1="12" y1="5" x2="12" y2="19" />
      <line x1="5" y1="12" x2="19" y2="12" />
    </svg>
  );
}

function LogoutIcon({ className }: IconProps) {
  return (
    <svg {...iconProps(className)}>
      <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
      <polyline points="16 17 21 12 16 7" />
      <line x1="21" y1="12" x2="9" y2="12" />
    </svg>
  );
}
