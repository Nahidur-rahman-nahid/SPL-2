import { NextResponse } from 'next/server';
import { cookies } from 'next/headers';

export async function GET(req) {
  try {
    const cookieStore = await cookies();
    const token = cookieStore.get("timewise-auth-token")?.value;

    if (!token) {
      // Redirect to login if no token is found
      return NextResponse.redirect(new URL('/login', req.url));
    }
    const url = new URL(req.url);
    const previousNumberOfDays = url.searchParams.get('previousNumberOfDays');

    const response = await fetch(
      `${process.env.BACKEND_SERVER_URL}/api/statistics/task?previousNumberOfDays=${previousNumberOfDays}`,
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
        { error: errorText || 'Failed to fetch user tasks statistics' },
        { status: response.status }
      );
    }

    const taskStats = await response.json();
    return NextResponse.json(taskStats, { status: 200 });
  } catch (error) {
    console.error('User Tasks Statistics Fetching Error:', error);
    return NextResponse.json(
      { error: 'Internal server error' },
      { status: 500 }
    );
  }
}