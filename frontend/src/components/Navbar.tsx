import Image from "next/image";
import { logout } from "@/app/actions";

export default function Navbar() {
  return (
    <header className="flex items-center justify-between border-b border-zinc-200 bg-white px-6 py-3">
      <div className="flex items-center gap-3">
        <Image src="/foi-weapon.png" alt="FOI coat of arms" width={22} height={36} />
        <span className="text-sm font-semibold text-[#1e3d8c]">Xelin</span>
      </div>
      <form action={logout}>
        <button
          type="submit"
          className="text-sm text-zinc-500 hover:text-zinc-900 cursor-pointer transition-colors"
        >
          Log out
        </button>
      </form>
    </header>
  );
}
