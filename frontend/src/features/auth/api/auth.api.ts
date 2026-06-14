export async function login(username: string, password: string): Promise<string> {
  const res = await fetch("/api/auth/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify({ username, password }),
  });

  if (res.status === 401) throw new Error("Fel användarnamn eller lösenord");
  if (!res.ok) throw new Error("Kunde inte nå servern");

  return res.text();
}

export async function logout(): Promise<void> {
  await fetch("/api/auth/logout", {
    method: "POST",
    credentials: "include",
  });
}

export async function checkSession(): Promise<string | null> {
  const res = await fetch("/api/tickets", { credentials: "include" });
  if (res.ok) return "inloggad";
  return null;
}
