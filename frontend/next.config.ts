import type { NextConfig } from "next";

// Backend-URL:en injiceras via miljövariabel (KR-904) — ingen hårdkodning av host/port.
// Fallback till localhost används endast vid lokal körning utan Docker/K8s.
const backendUrl = process.env.BACKEND_URL ?? "http://localhost:8080";

const nextConfig: NextConfig = {
  // Frontend kommunicerar uteslutande med det interna REST-API:et (KR-T203).
  // Alla /api-anrop proxas server-side till backend, så webbläsaren håller sig
  // till appens egen origin (samma origin => JSESSIONID fungerar utan CORS).
  async rewrites() {
    return [
      {
        source: "/api/:path*",
        destination: `${backendUrl}/api/:path*`,
      },
    ];
  },
};

export default nextConfig;
