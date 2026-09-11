import { redirect } from "next/navigation";

export default function PortalHome() {
  // Serviceportalen har idag en enda vy: registrera nytt ärende.
  redirect("/portal/new-ticket");
}
