import { cookies } from "next/headers";
import { redirect } from "next/navigation";
import { revalidatePath } from "next/cache"; // Importera denna!

export default async function Dashboard() {

  async function handleLogout() {
    "use server";

    const cookieStore = await cookies();
    const jsessionid = cookieStore.get("JSESSIONID")?.value;

    try {
      await fetch("http://localhost:8080/api/auth/logout", {
        method: "POST",
        headers: { "Cookie": `JSESSIONID=${jsessionid}` }
      });
    } catch (e) {
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