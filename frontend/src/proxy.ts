import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

// Route-skydd (Next 16: middleware → proxy). Utan giltig sessionskaka skickas
// användaren till inloggningen. Detta är endast UX — den bindande
// behörighetskontrollen sker server-side i backend per API-anrop (KR-804).
export function proxy(request: NextRequest) {
  const jsessionid = request.cookies.get('JSESSIONID');
  const { pathname } = request.nextUrl;

  const isProtected =
    pathname.startsWith('/dashboard') || pathname.startsWith('/portal');

  if (!jsessionid && isProtected) {
    const loginUrl = new URL('/login', request.url);
    loginUrl.searchParams.set('next', pathname);
    return NextResponse.redirect(loginUrl);
  }

  return NextResponse.next();
}

export const config = {
  matcher: ['/dashboard/:path*', '/portal/:path*'],
};
