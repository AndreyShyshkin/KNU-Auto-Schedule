import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

export function middleware(request: NextRequest) {
  const token = request.cookies.get('token')?.value;
  const role = request.cookies.get('role')?.value;

  const isLoginPage = request.nextUrl.pathname === '/login';
  const isPublicPage = request.nextUrl.pathname.startsWith('/results');
  const isApi = request.nextUrl.pathname.startsWith('/api');
  const isStatic = request.nextUrl.pathname.match(/\.(.*)$/);

  if (isLoginPage || isPublicPage || isApi || isStatic) {
    return NextResponse.next();
  }

  if (!token || role !== 'ADMIN') {
    return NextResponse.redirect(new URL('/results', request.url));
  }

  return NextResponse.next();
}

export const config = {
  matcher: ['/((?!_next|favicon.ico).*)'],
};
