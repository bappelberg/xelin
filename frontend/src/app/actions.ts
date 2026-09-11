"use server";

import { cookies } from "next/headers";
import { redirect } from "next/navigation";
import { revalidatePath } from "next/cache";

// Delad server action för utloggning — används av Navbar i både server- och klientkomponenter.
export async function logout() {
  const cookieStore = await cookies();
  const jsessionid = cookieStore.get("JSESSIONID")?.value;

  // Server-side anrop → absolut URL från miljövariabel (KR-904). Proxyn i
  // next.config.ts gäller bara webbläsarens relativa /api-anrop.
  const backendUrl = process.env.BACKEND_URL ?? "http://localhost:8080";

  try {
    await fetch(`${backendUrl}/api/auth/logout`, {
      method: "POST",
      headers: { Cookie: `JSESSIONID=${jsessionid}` },
    });
  } catch {
    console.error("Backend kunde inte nås, men vi rensar lokalt.");
  }

  cookieStore.delete("JSESSIONID");

  revalidatePath("/dashboard");
  revalidatePath("/portal");

  redirect("/login");
}
