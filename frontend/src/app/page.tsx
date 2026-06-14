"use client";

import { useAuthContext } from "@/features/auth/context/AuthContext";

export default function Home() {
  const { user } = useAuthContext();

  return (
    <div className="p-8">
      <h1 className="text-2xl font-semibold text-zinc-900">
        {user ? `Välkommen, ${user}` : "Home"}
      </h1>
    </div>
  );
}
