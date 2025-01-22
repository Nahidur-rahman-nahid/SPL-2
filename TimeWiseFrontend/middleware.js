import { NextResponse } from 'next/server';

const VERIFICATION_COOKIE = 'timewise-verification';
const USER_DATA_COOKIE = 'timewise-user-data';

export async function middleware(req) {
  const token = req.cookies.get('timewise-auth-token');
  const isVerified = req.cookies.get(VERIFICATION_COOKIE);

  // Redirect unauthenticated users to /welcome if they access /home
  if (req.nextUrl.pathname.startsWith('/home') && !token) {
    console.log(' no token found and hence Redirecting to /welcome');
    return NextResponse.redirect(new URL('/welcome', req.url));
  }

  // If token exists but user isn't verified yet, verify once
  if (token && !isVerified && req.nextUrl.pathname.startsWith('/home')) {
    try {
      const userResponse = await fetch('http://localhost:3000/api/users/account/personal', {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${token.value}`,
        },
      });

      if (!userResponse.ok) {
        // Invalid token, redirect to /welcome and delete the auth token
        const response = NextResponse.redirect(new URL('/welcome', req.url));
        response.cookies.delete('timewise-auth-token');
        return response;
      }

      const userData = await userResponse.json();

      // Set session-based verification cookie
      const response = NextResponse.redirect(new URL('/home', req.url));
      response.cookies.set(VERIFICATION_COOKIE, 'true', {
        httpOnly: true,
        secure: process.env.NODE_ENV === 'production',
        sameSite: 'strict',
      });

      // Store user data in a secure cookie
      response.cookies.set(USER_DATA_COOKIE, JSON.stringify(userData), {
        httpOnly: true, // Prevents JavaScript access to the cookie
        secure: process.env.NODE_ENV === 'production', // Only send over HTTPS in production
        sameSite: 'strict', // Prevents CSRF attacks
        path: '/', // Makes the cookie available across the app
      });

      return response;
    } catch (error) {
      // Redirect to /welcome in case of an error and delete the auth token
      const response = NextResponse.redirect(new URL('/welcome', req.url));
      response.cookies.delete('timewise-auth-token');
      return response;
    }
  }

  // For any other URL that doesn't match '/home', '/welcome', '/login', '/register', etc.
  // redirect them to the '/welcome' route.
  return NextResponse.redirect(new URL('/welcome', req.url));
}

export const config = {
  matcher: [
    '/home/:path*',  // All home routes
    '/api/:path*',     // All API routes
    '/((?!welcome|login|register|about-us|contact-us|_next/static|favicon.ico).*)',  // All other routes
  ],
};
