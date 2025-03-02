import { NextResponse } from 'next/server';
import { cookies } from 'next/headers';

export async function GET(req) {
  try {
    const cookieStore = await cookies();
    const token = cookieStore.get("timewise-auth-token")?.value;

    if (!token) {
      return NextResponse.redirect(new URL('/login', req.url));
    }

    const response = await fetch(
      `${process.env.BACKEND_SERVER_URL}/api/messages/all`,
      {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
      }
    );

    if (!response.ok) {
      const errorText = await response.text();
      return NextResponse.json(
        { error: errorText || 'Failed to fetch user messages' },
        { status: response.status }
      );
    }

    const messages = await response.json();
    return NextResponse.json(messages, { status: 200 });
  } catch (error) {
    console.error('Messages Fetching Error:', error);
    return NextResponse.json(
      { error: 'Internal server error' },
      { status: 500 }
    );
  }
}