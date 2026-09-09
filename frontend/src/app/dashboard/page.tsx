import { cookies } from "next/headers";
import { redirect } from "next/navigation";
import { revalidatePath } from "next/cache"; // Importera denna!

export default async function Dashboard() {

  async function handleLogout() {
    "use server";

    const cookieStore = await cookies();
    const jsessionid = cookieStore.get("JSESSIONID")?.value;

    // Server-side anrop → absolut URL från miljövariabel (KR-904). Proxyn i
    // next.config.ts gäller bara webbläsarens relativa /api-anrop.
    const backendUrl = process.env.BACKEND_URL ?? "http://localhost:8080";

    try {
      await fetch(`${backendUrl}/api/auth/logout`, {
        method: "POST",
        headers: { "Cookie": `JSESSIONID=${jsessionid}` }
      });
    } catch {
      console.error("Backend kunde inte nås, men vi rensar lokalt.");
    }

    // Rensa cookien
    cookieStore.delete("JSESSIONID");

    // Tvinga Next.js att tömma routerns cache för dashboarden
    revalidatePath("/dashboard");

    // Skicka till login
    redirect("/login");
  }

  return (
    <main>
      <h1>Skyddad Dashboard</h1>
      <form action={handleLogout}>
        <button type="submit">Logga ut</button>
      </form>
    </main>
  );
}