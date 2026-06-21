import Link from "next/link";

export default function Home() {
  return (
    <main>
      <h1>Startsida</h1>
      <Link href="/login">Gå till inloggning</Link>
    </main>
  );
}